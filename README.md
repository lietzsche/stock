# LoopLens‑Lite (루프렌즈‑라이트)

> **GPU·LLM 없이 동작하는 자기‑튜닝 트레이딩 코치**
> Java 17 + Spring Boot 예제 프로젝트로, **시장→실행→메타** 3단 피드백 루프를 단순 규칙·통계·크론 잡만으로 구현합니다.

---

## 1 프로젝트 목표

| 목표          | 설명                                        |
| ----------- | ----------------------------------------- |
| **Zero‑AI** | 노트북 CPU만으로 동작, GPU·클라우드 추론 비용 없음          |
| **투명성**     | 전략 파라미터가 *YAML* 파일에 저장 → 사용자가 직접 열람·수정 가능 |
| **자가 튜닝**   | 주 1회 배치 작업이 승률·손실률을 계산해 YAML을 덮어써 루프를 완결  |
| **브로커 독립**  | REST 브로커(Alpaca, Futu, 키움 등)에 어댑터 방식으로 연결 |

---

## 2 아키텍처 개요

```
┌────────── MarketData Puller ──────────┐                      
│   (크론/웹소켓 가격 수집)             │                      
└───────────┬──────────────────────────┘                      
            │ OHLC JSON                                           
            ▼                                                    
┌───────────── Signal Engine ───────────┐                       
│  (rules.yml 평가, ta‑lib‑java)         │                       
└───────────┬─────────────┬─────────────┘                       
            │             │ signals.yml (파라미터)                
            │             ▼                                     
            │    ┌────── Meta‑Tuner (주간 배치) ───────┐          
            │    │ YAML 덮어쓰기 + ParamRevision 저장 │          
            │    └───────────────────────────────────┘          
            ▼                                                   
┌──────────── Trade Executor ──────────┐ orders  ┌─ Broker API ─┐
│  (BrokerClient 어댑터)                │────────►│ REST 호출    │
└───────────┬──────────────────────────┘ fills    └─────────────┘
            │ fills                                            
            ▼                                                   
┌────── PostgreSQL / TimescaleDB ───────┐                       
│ signals, trades, 통계 저장              │                       
└───────────┬───────────────────────────┘                       
            ▼                                                   
   Review Service (일/주간 P&L JSON)                            
```

모듈별 정리

| 레이어 | 모듈               | 부트 앱                       | 역할                           |
| --- | ---------------- | -------------------------- | ---------------------------- |
| 마켓  | `market-poller`  | `MarketPollerApplication`  | OHLC·펀더멘털 수집, TimescaleDB 저장 |
| 시그널 | `signal-engine`  | `SignalEngineApplication`  | `signals.yml` 규칙 평가 (ta‑lib) |
| 실행  | `trade-executor` | `TradeExecutorApplication` | 주문 실행, `BrokerClient` SPI    |
| 리뷰  | `review-service` | `ReviewServiceApplication` | `/api/v1/review/*` 일·주간 리포트  |
| 메타  | `meta-tuner`     | Spring Batch               | 매주 일 03:00 YAML 파라미터 자동 조정   |

---

## 3 도메인 엔티티

```
User(id, email, risk_profile)
SignalRun(id, user_id, date, rule_name, outcome {LONG|SHORT|FLAT}, confidence, created_at)
Trade(id, user_id, signal_run_id, broker_order_id, side {BUY|SELL}, qty, fill_price, status {FILLED|REJECTED}, filled_at)
DailyPnL(id, user_id, date, realized_pnl, exposure, win_rate, max_drawdown)
ParamRevision(id, revision_ts, file_sha256, hit_rate, avg_drawdown)
```

---

## 4 설정 파일 예시

### 4.1 `signals.yml`

```yaml
# EMA 교차 + 변동성 필터
ema_cross:
  fast: 12          # Meta‑Tuner가 자동 조정
  slow: 26
  min_price_move: 1.0  # % 변화량
  time_window: 30m

# RSI 밴드
rsi_band:
  period: 14
  oversold: 30
  overbought: 70
```

### 4.2 `broker.yml`

```yaml
alpaca:
  base-url: https://paper-api.alpaca.markets
  api-key: ${ALPACA_KEY}
  api-secret: ${ALPACA_SECRET}
```

---

## 5 REST API 스펙

| 메서드  | 경로                       | 파라미터/바디                                  | 용도                           |
| ---- | ------------------------ | ---------------------------------------- | ---------------------------- |
| POST | `/api/v1/signals/run`    | `{ "userId":"u1", "date":"2025-07-01" }` | 당일 규칙 평가 강제 실행 (기본 09:00 크론) |
| POST | `/api/v1/trades/execute` | `{ "signalRunId":123 }`                  | 해당 시그널로 주문 실행                |
| GET  | `/api/v1/review/daily`   | `userId`, `date`                         | 일일 P\&L, 승률, 차트 URL          |
| GET  | `/api/v1/review/weekly`  | `userId`, `week`                         | 주간 통계 및 파라미터 변화              |
| GET  | `/api/stocks/{code}`     | -                                        | 현재 주가 조회                        |

모든 엔드포인트는 `Authorization: Bearer <JWT>` 필요 (Spring Security HS256).

---

## 6 메타 튜닝 로직

```
입력   : 최근 20거래일 SignalRun + DailyPnL
지표   : HitRate = WIN / TOTAL
규칙   : HitRate < 0.55 && drawdown > 5% → fast += 2
출력   : signals.yml 덮어쓰기 + ParamRevision 레코드
```

Spring Batch `Tasklet` + SnakeYAML로 구현.

---

## 7 개발 환경 실행

```bash
# 필수: Java 17, Docker Compose, Gradle 8+
cp .env.sample .env   # DB·브로커 키·JWT 시크릿 입력
./gradlew clean build

# 인프라 기동
cd infra && docker compose up -d  # Postgres+Timescale, Kafka, Grafana

# 각 모듈 실행 (터미널 분리)
./gradlew :market-poller:bootRun
./gradlew :signal-engine:bootRun
./gradlew :trade-executor:bootRun
./gradlew :review-service:bootRun
./gradlew :meta-tuner:bootRun   # 수동 실행 또는 일요일 대기
```

Grafana 대시보드 → [http://localhost:3000](http://localhost:3000) (`admin` / `admin`).

---

## 8 테스트

* **단위**: `SignalEngineTests` – 규칙 평가 검증
* **통합**: Testcontainers(Postgres+Kafka)
* **E2E**: `scripts/e2e-demo.sh` – 2일 히스토리 데이터로 전체 루프 실행

---

## 9 확장 가이드

1. **새 규칙** 추가 → `SignalRule` 구현 후 `signals.yml` 블록 삽입
2. **새 브로커** → `BrokerClient` 어댑터 구현
3. **다른 데이터 피드** → `MarketFeedAdapter` 연결
4. **고급 튜닝** → 유전 알고리즘·그리드 서치 (CPU만 사용)

---

## 10 디렉터리 구조

```
looplens-lite/
├── build.gradle.kts
├── settings.gradle.kts
├── market-poller/
├── signal-engine/
├── trade-executor/
├── review-service/
├── meta-tuner/
├── config/
│   ├── signals.yml
│   └── broker.yml
├── infra/
│   └── docker-compose.yml
└── README.md
```

---

## 라이선스 & 크레딧

* **Apache‑2.0**
* 기술 지표: [ta‑lib](https://ta-lib.org/)
* TimescaleDB·Grafana는 각 소유자의 상표입니다.

---

### Happy looping 🚀
