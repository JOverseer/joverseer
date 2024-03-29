package org.joverseer.support.readers.orders;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.joverseer.domain.Character;
import org.joverseer.domain.Note;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.tools.OrderParameterValidator;
import org.joverseer.tools.OrderValidationResult;

/**
 * Reads orders from a text representation. The format supported is that used by
 * Automagic or the Generate Order Report command.
 * 
 * @author Marios Skounakis
 */
public class OrderTextReader implements OrderTextReaderInterface {
	public static final int STANDARD_ORDER_TEXT = 1;
	public static final int ORDERCHECKER_ORDER_TEXT = 2;
	public static final int AUTOMAGIC_ORDER_TEXT = 3;
	
	public int textType = 2; // note not final

	final Game game;
	
	ArrayList<String> lineResults = new ArrayList<String>();

	int chars = 0;
	int orders = 0;

	public OrderTextReader(Game game) {
		this(game,STANDARD_ORDER_TEXT); 
	}
	public OrderTextReader(Game game,int type) {
		this.game= game;
		this.textType = type;
	}
	public Game getGame() {
		return this.game;
	}
	public static OrderTextReader Factory(String text,Game game) {
		if (text.equals("Standard")) { //$NON-NLS-1$
			return new OrderTextReader(game);
		} else if (text.equals("Order Checker")) { //$NON-NLS-1$
			return new OrderCheckerOrderTextReader(game);
		} else {
			return new AutoMagicOrderTextReader(game);
		}
	}
	public static OrderTextReaderInterface Factory2(String text,Game game) {
		if (text.equals("Standard")) { //$NON-NLS-1$
			return new OrderTextReader(game);
		} else if (text.equals("Order Checker")) { //$NON-NLS-1$
			return new OrderCheckerOrderTextReader(game);
		} else {
			return new OrderFileReader(game);
		}
	}
		
	protected boolean isCharacterLine(String line) {
		String charPattern = "^[\\p{L}\\d\\?]+([\\-\\s'][\\p{L}\\d\\?]+)*\\s+\\([\\w\\-\\s' ]{3,5}\\) @ \\d{4}.*";
		Pattern chP = Pattern.compile(charPattern, Pattern.CASE_INSENSITIVE
				| Pattern.UNICODE_CASE);
		return chP.matcher(line).matches();
	}

	protected boolean isOrderLine(String line) {
		String orderPattern = "^\\d{3}\\s{1,2}\\w{5,7}.*";
		return Pattern.matches(orderPattern, line);
	}

	protected String getCharacterNameFromLine(String line) {
		int j1 = line.indexOf("(");
		int j2 = line.indexOf(")");
		int j3 = line.indexOf("@ ");
		if (j1 > -1 && j2 > -1 && j3 > -1) {
			return line.substring(j1 + 1, j2);
		}
		return null;
	}

	protected String getCharacterLocationFromLine(String line) {
		int j1 = line.indexOf("(");
		int j2 = line.indexOf(")");
		int j3 = line.indexOf("@ ");
		if (j1 > -1 && j2 > -1 && j3 > -1) {
			return line.substring(j3 + 2, j3 + 6);
		}
		return null;
	}
	/***
	 * Parse the text as orders
	 * Clears the lineResults array.
	 * May change TextType if AutoMagic format detected.
	 * @param reader
	 * @throws Exception
	 */
	public void parseOrders(BufferedReader reader) throws Exception {
		this.lineResults.clear();
		internalReadOrders(reader, 0);
	}
	@Override
	public void readOrders(BufferedReader reader) throws Exception {
		internalReadOrders(reader, 1);
	}
	public void internalReadOrders(BufferedReader reader,int pass) throws Exception {
		try {
			String line;
			String charId = null;
			String location = null;
			int i = 0;
			int charLine = 0;
			String[] orderText1 = new String[] { null, null, null };
			int[] orderLines = new int[] { 0, 0, 0 };
			String notes = "";

			int lineCounter = 0;
			while ((line = reader.readLine()) != null) {
                if (line.startsWith("BEGINMEAUTOINPUT") && (pass == 0)) {
                	// its a JO or automagic format!
                	this.lineResults.add("Joverseer or Automagic orders found.");
                	this.setTextType(AUTOMAGIC_ORDER_TEXT);
                	return;
                }

				if (isCharacterLine(line)) {
					if (charId != null) {
						// start of next character, so flush out the previous character's orders
						addOrders(charId, location, charLine, orderText1,
								orderLines, notes, pass);
						orderText1 = new String[] { null, null, null };
						orderLines = new int[] { 0, 0, 0 };
						notes = "";
					}
					charId = getCharacterNameFromLine(line);
					location = getCharacterLocationFromLine(line);
					if (location != null && charId != null) {
						charLine = lineCounter;
						i = 0;
						notes = "";
						this.lineResults.add("Character line (char id: " + charId
								+ ").");
					} else {
						this.lineResults
								.add("Line ignored. Looks like character line but parsing failed.");
					}
				} else if (isOrderLine(line)) {
					if (charId != null) {
						orderLines[i] = lineCounter;
						orderText1[i] = line;
						this.lineResults.add("Order line.");
						i++;
					} else {
						this.lineResults.add("Order line ignored.");
					}
				} else if (!line.trim().equals("") && !line.trim().equals("--")) {
					// comments
					notes += (line.equals("") ? "" : "\n") + line;
					this.lineResults.add("Order notes.");
				} else {
					this.lineResults.add("Line ignored.");
				}
				lineCounter++;
			}
			if (charId != null) {
				// flush last character
				addOrders(charId, location, charLine, orderText1, orderLines,
						notes, pass);
			}
        } catch (Exception exc) {
            throw exc;
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
	}

	private void addOrders(String charId, String location, int charLine,
			String[] orderText1, int[] orderLines, String notes, int pass) {
		Turn t = getGame().getTurn();
		if (t == null) {
			this.lineResults.add("No turn found");
			return;
		}
		
		Character c = (Character) t.getContainer(
				TurnElementsEnum.Character).findFirstByProperty("id", charId);
		if (c == null) {
			c = (Character) t.getContainer(
					TurnElementsEnum.Character).findFirstByProperty("id",
					charId.trim());
		}
		if (c == null) {
			String lineRes = this.lineResults.get(charLine);
			lineRes += " " + "Character was not found in game.";
			this.lineResults.set(charLine, lineRes.trim());
			return;
		}
		if (c.getHexNo() != Integer.parseInt(location)) {
			String lineRes = this.lineResults.get(charLine);
			lineRes += " "
					+ "Character was found but at a different location - ignoring.";
			this.lineResults.set(charLine, lineRes.trim());
			return;
		}

		if (pass == 1) {
			// clear old notes
			for (Note nd : (ArrayList<Note>) getGame().getTurn().getContainer(
					TurnElementsEnum.Notes).findAllByProperty("target", c)) {
				if (nd.getTags()!=null && nd.getTags().indexOf("Order") > -1) {
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

		this.chars++;
		Order[] orders1 = c.getOrders();
		for (int i = 0; i < c.getNumberOfOrders(); i++) {
			if (orderText1[i] == null) {
				orders1[i].clear();
			} else {
				this.orders++;
				String repString = "  ";
				if (orderText1[i].trim().indexOf(repString) == -1) {
					repString = " ";
				}
				String[] parts = orderText1[i].trim().replace(repString,
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
				String lineRes = this.lineResults.get(orderLines[i]);
				lineRes += " Parsed order: " + orderNo
						+ ". Order will be added to " + c.getName() + ".";
				this.lineResults.set(orderLines[i], lineRes.trim());
				if (pass == 1) {
					orders1[i].setOrderNo(orderNo);
					orders1[i].setParameters(parameters);
					// check mov army orders and swap params if applicable
					if (orders1[i].isArmyMovementOrderCapableOfEvasion()) {
						// String paramTemp =
						// orders[i].getParameter(orders[i].getLastParamIndex());
						String paramZero = orders1[i].getParameter(0);
						if (paramZero.equals("no") || paramZero.equals("ev")) {
							for (int ii = 0; ii < orders1[i].getLastParamIndex(); ii++) {
								orders1[i].setParameter(ii, orders1[i]
										.getParameter(ii + 1));
							}
							orders1[i].setParameter(orders1[i]
									.getLastParamIndex(), paramZero);
						}
					}
					OrderParameterValidator opv = new OrderParameterValidator();
					int j = 0;
					Order o = orders1[i];
					if (o.getOrderNo() == 728 || o.getOrderNo() == 731 || o.getOrderNo() == 734 || o.getOrderNo() == 737) {
						o.checkForDefaultGenderAndName();
					}

					while (j <= o.getLastParamIndex()) {
						int length = o.getParameter(j).length(); 
						// special handling for char id parameters that get messed up due to spaces (trailing or in the middle)
						if (length < 5 && length > 1) {
							OrderValidationResult ovr = opv.checkParam(o, j);
							if (ovr != null && ovr.getMessage() != null && ovr.getMessage().startsWith("must be 5 lowercase chars")) {
								// check next parameter - if string too, and error, concatenate
								if (j+1 <= o.getLastParamIndex()) {
									ovr = opv.checkParam(o, j+1);
									if (ovr != null && ovr.getLevel() == OrderValidationResult.ERROR) {
										o.setParameter(j, o.getParameter(j) + " " + o.getParameter(j+1));
										for (int k=j+1; k<o.getLastParamIndex(); k++) {
											o.setParameter(k, o.getParameter(k+1));
										}
										o.setParameter(o.getLastParamIndex(), "");
									}
								}
								// if needed, append trailing spaces to make a 5-length char id
								String txt = o.getParameter(j);
								while (txt.length() < 5) txt += " ";
								o.setParameter(j, txt);
							}
						}
						if (o.getOrderNo() == 725 || o.getOrderNo() == 728 || o.getOrderNo() == 731 || o.getOrderNo() == 734 || o.getOrderNo() == 737) {
							// name order
							// check if first the name is composed of multiple words
							int paramNo = o.getOrderNo() == 725 ? 6 : 2;
							while (o.getLastParamIndex() >= paramNo) {
								o.setParameter(0, o.getParameter(0) + " " + o.getParameter(1));
								for (int k=1; k<o.getLastParamIndex(); k++) {
									o.setParameter(k, o.getParameter(k+1));
								}
								o.setParameter(o.getLastParamIndex(), "");
							}
						}
						j++;
					}
				}
			}
		}
	}

	public int getChars() {
		return this.chars;
	}

	public void setChars(int chars) {
		this.chars = chars;
	}

	public ArrayList<String> getLineResults() {
		return this.lineResults;
	}

	@Override
	public int getOrdersRead() {
		return this.orders;
	}

	public int getTextType() {
		return this.textType;
	}

	public void setTextType(int textType) {
		this.textType = textType;
	}

}
