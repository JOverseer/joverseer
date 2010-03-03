package org.joverseer.support.readers.orders;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joverseer.domain.Character;
import org.joverseer.domain.Note;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;

/**
 * Reads orders from a text representation. The format supported is that used by
 * Automagic or the Generate Order Report command.
 * 
 * @author Marios Skounakis
 */
public class OrderTextReader {
	public static int STANDARD_ORDER_TEXT = 1;
	public static int ORDERCHECKER_ORDER_TEXT = 2;
	
	public int textType = 2;
	String orderText;

	Game game;
	
	

	ArrayList<String> lineResults = new ArrayList<String>();

	int chars = 0;
	int orders = 0;

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String getOrderText() {
		return orderText;
	}

	public void setOrderText(String orderText) {
		this.orderText = orderText;
	}

	protected boolean isCharacterLine(String line) {
		if (getTextType() == STANDARD_ORDER_TEXT) {
			String charPattern = "^[\\p{L}\\d\\?]+([\\-\\s'][\\p{L}\\d\\?]+)*\\s+\\([\\w\\-\\s' ]{3,5}\\) @ \\d{4}.*";
			Pattern chP = Pattern.compile(charPattern, Pattern.CASE_INSENSITIVE
					| Pattern.UNICODE_CASE);
			return chP.matcher(line).matches();
		} else if (getTextType() == ORDERCHECKER_ORDER_TEXT) {
			String charPattern = "^[\\p{L}\\d\\?]+([\\-\\s'][\\p{L}\\d\\?]+)*\\s+\\([\\w\\-\\s',\\//\\d ]*\\) @ .*";
			Pattern chP = Pattern.compile(charPattern, Pattern.CASE_INSENSITIVE
					| Pattern.UNICODE_CASE);
			return chP.matcher(line).matches();
		} else {
			return false;
		}
		
	}

	protected boolean isOrderLine(String line) {
		if (getTextType() == STANDARD_ORDER_TEXT) {
			String orderPattern = "^\\d{3}\\s{1,2}\\w{5,7}.*";
			return Pattern.matches(orderPattern, line);
		} else if (getTextType() == ORDERCHECKER_ORDER_TEXT) {
			String orderPattern = "^\\d{3}\\s{1,2}\\(\\w{5,7}\\).*";
			return Pattern.matches(orderPattern, line);
		}
		return false;
	}

	protected String getCharacterNameFromLine(String line) {
		if (getTextType() == STANDARD_ORDER_TEXT) {
			int j1 = line.indexOf("(");
			int j2 = line.indexOf(")");
			int j3 = line.indexOf("@ ");
			if (j1 > -1 && j2 > -1 && j3 > -1) {
				return line.substring(j1 + 1, j2);
			}
		} else if (getTextType() == ORDERCHECKER_ORDER_TEXT) {
			int j1 = line.indexOf("(");
			int j2 = line.indexOf(",");
			if (j1 > -1 && j2 > -1) {
				return line.substring(j1 + 1, j2);
			}
		}
		return null;
	}

	protected String getCharacterLocationFromLine(String line) {
		if (getTextType() == STANDARD_ORDER_TEXT) {
			int j1 = line.indexOf("(");
			int j2 = line.indexOf(")");
			int j3 = line.indexOf("@ ");
			if (j1 > -1 && j2 > -1 && j3 > -1) {
				return line.substring(j3 + 2, j3 + 6);
			}
		} else if (getTextType() == ORDERCHECKER_ORDER_TEXT) {
			String locationPattern = ".*(\\d{4}).*";
			Pattern chP = Pattern.compile(locationPattern, Pattern.CASE_INSENSITIVE
					| Pattern.UNICODE_CASE);
			Matcher m =chP.matcher(line); 
			if (m.find()) {
				return m.group(1);
			}
		}
		return null;
	}

	public void readOrders(int pass) {
		try {
			if (pass == 0) {
				lineResults.clear();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(getOrderText().getBytes())));
			String line;
			String charId = null;
			String location = null;
			int i = 0;
			int charLine = 0;
			String[] orderText = new String[] { null, null, null };
			int[] orderLines = new int[] { 0, 0, 0 };
			String notes = "";

			int lineCounter = 0;
			while ((line = reader.readLine()) != null) {
				if (isCharacterLine(line)) {
					if (charId != null) {
						addOrders(charId, location, charLine, orderText,
								orderLines, notes, pass);
						orderText = new String[] { null, null, null };
						orderLines = new int[] { 0, 0, 0 };
						notes = "";
					}
					charId = getCharacterNameFromLine(line);
					location = getCharacterLocationFromLine(line);
					if (location != null && charId != null) {
						charLine = lineCounter;
						i = 0;
						notes = "";
						lineResults.add("Character line (char id: " + charId
								+ ").");
					} else {
						lineResults
								.add("Line ignored. Looks like character line but parsing failed.");
					}
				} else if (isOrderLine(line)) {
					if (charId != null) {
						orderLines[i] = lineCounter;
						orderText[i] = line;
						lineResults.add("Order line.");
						i++;
					} else {
						lineResults.add("Order line ignored.");
					}
				} else if (!line.trim().equals("") && !line.trim().equals("--")) {
					// comments
					notes += (line.equals("") ? "" : "\n") + line;
					lineResults.add("Order notes.");
				} else {
					lineResults.add("Line ignored.");
				}
				lineCounter++;
			}
			if (charId != null) {
				addOrders(charId, location, charLine, orderText, orderLines,
						notes, pass);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private void addOrders(String charId, String location, int charLine,
			String[] orderText, int[] orderLines, String notes, int pass) {
		Character c = (Character) getGame().getTurn().getContainer(
				TurnElementsEnum.Character).findFirstByProperty("id", charId);
		if (c == null) {
			c = (Character) getGame().getTurn().getContainer(
					TurnElementsEnum.Character).findFirstByProperty("id",
					charId.trim());
		}
		if (c == null) {
			String lineRes = lineResults.get(charLine);
			lineRes += " " + "Character was not found in game.";
			lineResults.set(charLine, lineRes.trim());
			return;
		}
		if (c.getHexNo() != Integer.parseInt(location)) {
			String lineRes = lineResults.get(charLine);
			lineRes += " "
					+ "Character was found but at a different location - ignoring.";
			lineResults.set(charLine, lineRes.trim());
			return;
		}

		if (pass == 1) {
			// clear old notes
			for (Note nd : (ArrayList<Note>) getGame().getTurn().getContainer(
					TurnElementsEnum.Notes).findAllByProperty("target", c)) {
				if (nd.getTags().indexOf("Order") > -1) {
					getGame().getTurn().getContainer(TurnElementsEnum.Notes)
							.removeItem(nd);
				}
			}
			if (notes != null && !notes.equals("")) {
				Note n = new Note();
				n.setTarget(c);
				n.setNationNo(c.getNationNo());
				n.setText(notes.trim());
				n.setTags("Order");
				getGame().getTurn().getContainer(TurnElementsEnum.Notes).addItem(n);
			}
			
			
		}

		chars++;
		Order[] orders = c.getOrders();
		for (int i = 0; i < c.getNumberOfOrders(); i++) {
			if (orderText[i] == null) {
				orders[i].clear();
			} else {
				this.orders++;
				String repString = "  ";
				if (orderText[i].trim().indexOf(repString) == -1) {
					repString = " ";
				}
				String[] parts = orderText[i].trim().replace(repString,
						Order.DELIM).split(Order.DELIM);
				String parameters = "";
				int orderNo = -1;
				if (!parts[0].equals("")) {
					orderNo = Integer.parseInt(parts[0]);
					for (int j = 2; j < parts.length; j++) {
						String part = parts[j].trim();// .replace(" ", "");
						if (!part.equals("--") && !part.equals("")) {
							parameters = parameters
									+ (parameters.equals("") ? "" : Order.DELIM)
									+ part;
						}
					}
				}
				String lineRes = lineResults.get(orderLines[i]);
				lineRes += " Parsed order: " + orderNo
						+ ". Order will be added to " + c.getName() + ".";
				lineResults.set(orderLines[i], lineRes.trim());
				if (pass == 1) {
					orders[i].setOrderNo(orderNo);
					orders[i].setParameters(parameters);
					// check mov army orders and swap params if applicable
					if (orderNo == 830 || orderNo == 850 || orderNo == 860) {
						// String paramTemp =
						// orders[i].getParameter(orders[i].getLastParamIndex());
						String paramZero = orders[i].getParameter(0);
						if (paramZero.equals("no") || paramZero.equals("ev")) {
							for (int ii = 0; ii < orders[i].getLastParamIndex(); ii++) {
								orders[i].setParameter(ii, orders[i]
										.getParameter(ii + 1));
							}
							orders[i].setParameter(orders[i]
									.getLastParamIndex(), paramZero);
						}
					}
				}
			}
		}
	}

	public int getChars() {
		return chars;
	}

	public void setChars(int chars) {
		this.chars = chars;
	}

	public ArrayList<String> getLineResults() {
		return lineResults;
	}

	public int getOrders() {
		return orders;
	}

	public void setOrders(int orders) {
		this.orders = orders;
	}

	public int getTextType() {
		return textType;
	}

	public void setTextType(int textType) {
		this.textType = textType;
	}

}
