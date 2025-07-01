package com.stock.bion.back.data;

import com.stock.bion.back.calculate.TrailingStopStrategyService;
import com.stock.bion.back.exception.DocumentFetchException;
import com.stock.bion.back.util.FormatUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataService {

	/**
	 * 조회 기간(TimeFrame)을 나타내는 열거형입니다. SHORT_TERM: 단기(약 1개월) LONG_TERM : 장기(약 1년)
	 */
	public enum TimeFrame {
		SHORT_TERM, // 약 1개월
		MEDIUM_TERM,
		LONG_TERM; // 약 1년

		/**
		 * 각 기간별 대략적인 거래일 수를 반환합니다.
		 */
		public int getLookbackDays() {
			return switch (this) {
				case SHORT_TERM -> 22; // 약 1개월
				case MEDIUM_TERM -> 120; // 약 1년
				case LONG_TERM -> 250; // 약 1년
				default -> throw new IllegalArgumentException("지원하지 않는 기간: " + this);
			};
		}
	}

	/**
	 * 조회 기간(TimeFrame)에 따라 과거 가격 데이터를 페이지별로 조회해 반환합니다.
	 */
	public List<Price> fetchPricesForTimeframe(String code, TimeFrame timeframe) {
		int days = timeframe.getLookbackDays();
		List<Price> allPrices = new ArrayList<>();
		int page = 1;
		while (allPrices.size() < days) {
			List<Price> pagePrices = getPriceInfo(code, page++);
			if (pagePrices.isEmpty()) {
				break;
			}
			allPrices.addAll(pagePrices);
		}
		// API가 최신 데이터를 먼저 반환한다고 가정하고 최근 'days'개만 반환
		return allPrices.stream().limit(days).toList();
	}

	/// 아래 통신 로직

	private static final String COMPANIES_URL = "http://kind.krx.co.kr/corpgeneral/corpList.do?method=download&searchType=13";
	private static final String PRICES_URL = "http://finance.naver.com/item/sise_day.nhn?code=%s&page=%d";
	private static final String MARKET_URL = "https://finance.naver.com/item/main.naver?code=%s";

	public List<Stock> getCompanyInfo() { // 기본정보 가져오기

		Document doc = getDocumentByUrl(COMPANIES_URL);

		Elements infoList = doc.select("tr");
		List<Stock> stocks = new ArrayList<>();
		for (int i = 1; i < infoList.size(); i++) {
			Elements info = infoList.get(i).select("td");
			Stock stock = new Stock();
			stock.setName(info.get(0).text());
			stock.setCode(info.get(1).text());
			stocks.add(stock);
		}

		return stocks.parallelStream().filter(s -> isIdentifier(s.getCode())).collect(Collectors.toList());
	}

	public Boolean isIdentifier(String code) {
		Document doc = getDocumentByUrl(String.format(MARKET_URL, code));

		Elements kospiList = doc.select("img.kospi");

		for (Element img : kospiList) {
			String altText = img.attr("alt");
			if ("코스피".equals(altText))
				return true;
		}

		Elements kosdaqList = doc.select("img.kosdaq");
		for (Element img : kosdaqList) {
			String altText = img.attr("alt");
			if ("코스닥".equals(altText))
				return true;
		}

		return false;
	}

	public List<Price> getPriceInfo(String code, int page) { // 종목 및 페이지로 가격 정보 가져오기
		Document doc = getDocumentByUrl(String.format(PRICES_URL, code, page));

		Elements infoList = doc.select("tr");

		List<Price> prices = new ArrayList<>();

		for (int i = 2; i < infoList.size() - 2; i++) {
			if (i >= 7 && i <= 9) {
				continue;
			}
			Price price = new Price();

			Elements info = infoList.get(i).select("td");
			price.setDate(FormatUtil.stringToLocalDate(info.get(0).text()));
			price.setClose(FormatUtil.stringToLong(info.get(1).text()));
			price.setOpen(FormatUtil.stringToLong(info.get(3).text()));
			price.setHigh(FormatUtil.stringToLong(info.get(4).text()));
			price.setLow(FormatUtil.stringToLong(info.get(5).text()));
			price.setVolume(FormatUtil.stringToLong(info.get(6).text()));
			price.setDiff(FormatUtil.parseDiffText(info.get(2).text()));

			prices.add(price);
		}

		return prices;
	}

	private Document getDocumentByUrl(String url) {
		try {
			return Jsoup.connect(url).userAgent("Mozilla/5.0").ignoreHttpErrors(true) // HTTP 404, 500 오류 무시 (null 반환)
					.ignoreContentType(true) // 응답 내용 타입 무시
					.timeout(10000) // 10초까지 대기
					.get();
		} catch (IOException e) {
			log.warn("getDocumentByUrl failed: {}", url, e);
			throw new DocumentFetchException("URL 가져오기 실패: " + url, e);
		}
	}
}
