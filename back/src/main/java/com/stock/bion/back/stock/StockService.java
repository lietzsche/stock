package com.stock.bion.back.stock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    public StockInfo fetchStock(String code) throws Exception {
        String url = "https://finance.naver.com/item/main.naver?code=" + code;
        Document doc = Jsoup.connect(url).get();
        String text = doc.selectFirst("p.no_today span.blind").text();
        long price = Long.parseLong(text.replace(",", ""));
        return new StockInfo(code, price);
    }
}
