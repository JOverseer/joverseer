package org.joverseer.support.readers.orders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joverseer.game.Game;

public class OrderCheckerOrderTextReader extends OrderTextReader {

	public OrderCheckerOrderTextReader(Game game) {
		super(game,ORDERCHECKER_ORDER_TEXT);
	}
	@Override
	protected boolean isCharacterLine(String line) {
		String charPattern = "^[\\p{L}\\d\\?]+([\\-\\s'][\\p{L}\\d\\?]+)*\\s+\\([\\w\\-\\s',\\//\\d ]*\\) @ .*";
		Pattern chP = Pattern.compile(charPattern, Pattern.CASE_INSENSITIVE
				| Pattern.UNICODE_CASE);
		return chP.matcher(line).matches();
	}
	@Override
	protected boolean isOrderLine(String line) {
		String orderPattern = "^\\d{3}\\s{1,2}\\(\\w{5,7}\\).*";
		return Pattern.matches(orderPattern, line);
	}
	@Override
	protected String getCharacterNameFromLine(String line) {
		int j1 = line.indexOf("(");
		int j2 = line.indexOf(",");
		if (j1 > -1 && j2 > -1) {
			return line.substring(j1 + 1, j2);
		}
		return null;
	}
	@Override
	protected String getCharacterLocationFromLine(String line) {
		String locationPattern = ".*(\\d{4}).*";
		Pattern chP = Pattern.compile(locationPattern, Pattern.CASE_INSENSITIVE
				| Pattern.UNICODE_CASE);
		Matcher m =chP.matcher(line); 
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
}
