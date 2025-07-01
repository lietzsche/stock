## 1. 상승 추세(Uptrend) 추종

* **목표**: 강한 상승 모멘텀을 가진 종목을 포착하여 상승 추세에 올라탄다.
* **참고 지표**: 이동평균선(EMA/SMA), ADX 등

---

## 2. 부화뇌동(Chasing) 금지

* **정의**: 이미 과도하게 급등한 구간에 추격 매수하지 않음
* **방법**:

    * 고점 돌파 직후(쨉 돌파) 매수 지양
    * 거래량 스파이크 → 잠재적 거품 신호

---

## 3. 소신파(non-herd) 상승 추세 시그널

1. **거래량 최고점 피함**

    * 오늘 거래량이 지정 기간(`TimeFrame`) 내 최대치가 아니어야 함
    * → 과도한 과열 구간(거래량 스파이크) 탈피

2. **전고점(Previous High) 돌파**

    * 오늘 종가 > 해당 기간 이전 종가 최고치
    * → 실질적인 돌파 신뢰도 확보

3. **최근 3일 연속 저점 & 고점 상승**

    * 매일의 저가(Low)와 고가(High)가 직전일 대비 모두 상승
    * → 연속적 모멘텀 확인

4. **전고점 기준별 전략**

    * 단기: 최근 1개월(≈22 거래일) 신고가
    * 중기/장기: 최근 6개월, 1년(≈250 거래일) 신고가
    * → 각 TimeFrame별 시그널 따로 생성

---

### 시그널 의사결정 흐름도

1. 데이터 수집

    * `prices[0]` = 오늘, `prices[1..N-1]` = 과거 N일치 데이터
2. 조건 #1 확인:

   ```java
   maxVolume = max(prices.volume)
   if (today.volume >= maxVolume) signal = false;
   ```
3. 조건 #2 확인:

   ```java
   prevMaxClose = max(prices[1..].close)
   if (today.close <= prevMaxClose) signal = false;
   ```
4. 조건 #3 확인:

   ```java
   for i in {3,2,1}:
       if not (prices[i-1].low  > prices[i].low  &&
               prices[i-1].high > prices[i].high):
           signal = false;
   ```
5. `signal == true` 이면 매수 고려

---

## 4. 트레일링 스톱 전략

* **하한 손절(lowerLimit)**: 예) −5%
* **상한 기준 갱신(upperLimit)**: 예) +10%
* **로직**:

    1. **진입**: `entryPrice = 최초매수가`, `highestPrice = entryPrice`
    2. **가격 갱신**

        * `price > highestPrice`:

            * `if price ≥ entryPrice·(1+upperLimit) → entryPrice = price`
            * `highestPrice = price`
    3. **손절 체크**

        * `if price ≤ highestPrice·(1−lowerLimit) → 청산`

---

## 5. 미분(변화량) 조건 추가 아이디어

* **목적**: 변곡점을 포착하거나 모멘텀이 꺾이는 지점 조기 탐지
* **방법 예시**:

    1. **1차 미분(일일 수익률)**

        * `d1[i] = (close[i] − close[i−1]) / close[i−1]`
        * **조건**: `d1[0] ≥ α` (오늘 모멘텀 최소값)
    2. **2차 미분(모멘텀 변화 속도)**

        * `d2[i] = d1[i] − d1[i−1]`
        * **조건**: `d2[0] ≥ β` (모멘텀이 점점 강해지는 구간)
* **활용 예**:

    * 시그널 발동 직전 3일간 `d1`과 `d2` 모두 양수인 종목만 필터
    * 변동성 높은 장에서 거짓 시그널 제거

---

> **Tip**: `α`, `β` 값은 백테스트를 통해 최적화하세요.
> 또한 이동평균 기울기(slope)나 ADX 기울기 등도 유사하게 미분 개념으로 활용할 수 있습니다.
