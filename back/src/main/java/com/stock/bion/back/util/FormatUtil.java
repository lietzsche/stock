package com.stock.bion.back.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormatUtil {

	public static LocalDate stringToLocalDate(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		}

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			return LocalDate.parse(str, formatter);
		} catch (Exception e) {
			log.info("stringToLocalDate : str >>>> {}", str, e);
			return null;
		}
	}

	public static long stringToLong(String str) {
		long result = 0L;
		str = str.isEmpty() ? "0" : str;
		try {
			str = str.replaceAll(",", "");
			result = Long.parseLong(str);
		} catch (Exception e) {
			log.info(String.format("stringToLong : str >>>> %s", str));
		}
		return result;
	}

	public static long parseDiffText(String diffText) {
		if (diffText == null || diffText.isBlank())
			return 0;

		boolean isMinus = diffText.contains("하한가") || diffText.contains("하락");
		String clean = diffText.replace("상한가", "").replace("하한가", "").replace("상승", "").replace("하락", "")
				.replace("보합", "").trim();

		long diff = FormatUtil.stringToLong(clean);
		return isMinus ? -diff : diff;
	}
}
