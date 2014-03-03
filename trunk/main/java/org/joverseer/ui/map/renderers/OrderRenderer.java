package org.joverseer.ui.map.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

import org.joverseer.domain.Army;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.drawing.Arrow;
import org.springframework.richclient.application.Application;

/**
 * Draws orders on the map Sometimes to draw an order you need some parameters
 * (e.g. fed and cavalry for a new army) To find those params, it uses the
 * OrderVisualizationData container
 * 
 * @author Marios Skounakis
 */
public class OrderRenderer extends DefaultHexRenderer {
	MapMetadata mapMetadata = null;
	OrderVisualizationData orderVisualizationData = null;

	private OrderVisualizationData getOrderVisualizationData() {
		if (this.orderVisualizationData == null) {
			this.orderVisualizationData = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
		}
		return this.orderVisualizationData;
	}

	private boolean drawOrders() {
		HashMap mapOptions1 = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
		Object obj = mapOptions1.get(MapOptionsEnum.DrawOrders);
		if (obj == null)
			return false;
		if (obj == MapOptionValuesEnum.DrawOrdersOn) {
			return true;
		}
		return false;
	}

	@Override
	public boolean appliesTo(Object obj) {
		return Order.class.isInstance(obj) && !((Order) obj).isBlank() && drawOrders() && (getOrderVisualizationData().contains((Order) obj) || getOrderVisualizationData().getOrderEditorOrder() == obj);
	}

	@Override
	public void render(Object obj, Graphics2D g, int x, int y) {
		render(obj, g, x, y, true);
	}

	public boolean canRender(Object obj) {
		return render(obj, null, 1, 1, false);
	}

	public boolean render(Object obj, Graphics2D g, int x, int y, boolean doRender) {
		if (this.mapMetadata == null)
			init();

		// todo render order
		Order order = (Order) obj;
		if (order.getOrderNo() == 810 || order.getOrderNo() == 870 || order.getOrderNo() == 820) {
			if (doRender) {
				renderCharacterMovementOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 825) {
			if (doRender) {
				renderMovementSpellOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 850 || order.getOrderNo() == 860 || order.getOrderNo() == 830) {
			if (doRender) {
				renderArmyMovementOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 840) {
			if (doRender) {
				renderStandAndDefendOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 947) {
			if (doRender) {
				renderNatTranOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 948) {
			if (doRender) {
				renderTranCarOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 925) {
			if (doRender) {
				renderReconOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 910) {
			if (doRender) {
				renderScoAreaOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 935) {
			if (doRender) {
				renderScryingArtifactOrder(order, g);
			}
			return true;
		} else if (order.getOrderNo() == 940 && order.getParameter(0) != null && order.getParameter(0).equals("415")) {
			if (doRender) {
				renderScryAreaOrder(order, g);
			}
			return true;
		}
		return false;
	}

	private void renderStandAndDefendOrder(Order order, Graphics2D g) {
		if (order.getParameter(0) == null || order.getParameter(0).equals("")) {
			return;
		}
		try {
			int currentHexNo = order.getCharacter().getHexNo();
			String param = order.getParameter(0);

			MovementDirection[] mds = new MovementDirection[] { null, null, null };
			while (true) {
				for (MovementDirection md : MovementDirection.values()) {
					if (md != MovementDirection.Home) {
						mds[0] = mds[1];
						mds[1] = mds[2];
						mds[2] = md;
					}
					if (mds[0] != null && mds[2] != null && mds[1].getDir().equals(param)) {
						break;
					}
					if (param.equals(""))
						break;
				}
				if (mds[0] != null && mds[2] != null && mds[1].getDir().equals(param)) {
					break;
				}
				if (param.equals(""))
					break;
			}

			for (MovementDirection md : mds) {
				int nextHexNo = MovementUtils.getHexNoAtDir(currentHexNo, md);
				Point p1;
				Point p2 = null;
				p1 = MapPanel.instance().getHexCenter(currentHexNo);
				p2 = MapPanel.instance().getHexCenter(nextHexNo);
				g.setStroke(GraphicUtils.getDashStroke(3, 8));
				g.setColor(Color.RED);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				double theta = Math.atan2((p2.y - p1.y), (p2.x - p1.x));
				Shape a = Arrow.getArrowHead(p2.x, p2.y, 10, 15, theta);
				g.fill(a);
			}
		} catch (Exception exc) {
			// parse or some other error, return
			return;
		}
	}

	private void renderArmyMovementOrder(Order order, Graphics2D g) {
		if (order.getParameter(0) == null) {
			return;
		}

		OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
		Object d = ovd.getAdditionalInfo(order, "displacement");
		if (d == null)
			d = 0;
		int df = (Integer) d;
		int displacementX = 0;
		int displacementY = 0;
		if (df == 1) {
			displacementX = -11;
			displacementY = -10;
		} else if (df == 2) {
			displacementX = 11;
			displacementY = -10;
		} else if (df == 3) {
			displacementX = 11;
			displacementY = 10;
		} else if (df == 4) {
			displacementX = -11;
			displacementY = 10;
		}

		boolean isNavy = order.getOrderNo() == 830;
		try {
			int maxCost = (order.getOrderNo() == 850 ? 12 : 14);
			int currentHexNo = order.getCharacter().getHexNo();
			int startHexNo = currentHexNo;
			String[] params = order.getParameters().split(Order.DELIM);
			Point p1;
			Point p2 = null;
			int cost = 0;

			Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();

			Army army = (Army) game.getTurn().getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", order.getCharacter().getName());
			Boolean cav = null;
			Boolean fed = null;
			if (army != null) {
				cav = army.computeCavalry();
				fed = army.computeFed();
			} else {
				try {
					cav = (Boolean) ovd.getAdditionalInfo(order, "cavalry");
					fed = (Boolean) ovd.getAdditionalInfo(order, "fed");
				} catch (Exception exc) {
				}
				;
			}
			if (cav == null)
				cav = false;
			if (fed == null)
				fed = false;

			boolean evasive = false;
			if (order.getParameter(order.getLastParamIndex()).equals("ev")) {
				evasive = true;
			}

			int lastUsefulParamIndex = order.getLastMovementIndex();
			// TODO: check that the +1 is correct
			for (int i = 0; i < lastUsefulParamIndex+1; i++) {
				String dir = params[i];
				MovementDirection md = MovementDirection.getDirectionFromString(dir);
				int nextHexNo = MovementUtils.getHexNoAtDir(currentHexNo, md);
				if (nextHexNo == currentHexNo) {
					cost += 1;
					if (evasive) {
						cost += 1;
					}
					continue;
				}
				p1 = MapPanel.instance().getHexCenter(currentHexNo);
				p2 = MapPanel.instance().getHexCenter(nextHexNo);
				p1.x += displacementX;
				p1.y += displacementY;

				p2.x += displacementX;
				p2.y += displacementY;
				g.setStroke(GraphicUtils.getDashStroke(3, 8));
				int curCost = 0;
				if (!isNavy) {
					curCost = MovementUtils.calculateMovementCostForArmy(currentHexNo, dir, cav, fed, true, null, currentHexNo);
					if (evasive && curCost > 0) {
						curCost = curCost * 2;
					}
				} else {
					curCost = MovementUtils.calculateMovementCostForNavy(currentHexNo, dir, fed, startHexNo);
					if (evasive && curCost > 0) {
						curCost = curCost * 2;
					}
					if (curCost == -2) {
						// consume all remaining mps
						curCost = maxCost - cost;
					}
				}
				if (cost + curCost <= maxCost && cost >= 0 && curCost > 0) {
					g.setColor(Color.RED);
				} else {
					g.setColor(Color.PINK);
				}
				g.drawLine(p1.x, p1.y, p2.x, p2.y);

				if (i == lastUsefulParamIndex - 2) {
					// last segment
					double theta = Math.atan2((p2.y - p1.y), (p2.x - p1.x));
					Shape a = Arrow.getArrowHead(p2.x, p2.y, 10, 15, theta);
					g.fill(a);
				}
				if (i > 0 && cost > 0) {
					// draw distance so far
					drawString(g, String.valueOf(cost), p1, p1);
				}

				if (curCost == -1 || cost == -1) {
					cost = -1;
				} else {
					cost += curCost;
				}

				if (drawCharNames()) {
					if (i == (lastUsefulParamIndex - 1) / 2) {
						// middle segment
						drawString(g, order.getCharacter().getName(), p1, p2);
					}
				}

				currentHexNo = nextHexNo;
			}
			// draw last distance
			if (cost > 0) {
				drawString(g, String.valueOf(cost), p2, p2);
			}
		} catch (Exception exc) {
			// parse or some other error, return
			return;
		}

	}

	private void drawString(Graphics2D g, String str, Point p1, Point p2) {
		// calculate and prepare character name rendering
		Point p = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
		Font f = GraphicUtils.getFont("Microsoft Sans Serif", Font.PLAIN, 9);
		FontMetrics fm = g.getFontMetrics(f);
		Rectangle2D bb = fm.getStringBounds(str, g);
		Rectangle b = new Rectangle(((Double) bb.getX()).intValue(), ((Double) bb.getY()).intValue(), ((Double) bb.getWidth()).intValue(), ((Double) bb.getHeight()).intValue());
		int xt = p.x - new Double(b.getWidth() / 2).intValue();
		int yt = p.y;
		b.translate(xt, yt);
		RoundRectangle2D rr = new RoundRectangle2D.Double(b.getX(), b.getY(), b.getWidth() + 2, b.getHeight() + 2, 3, 3);
		g.setFont(f);

		g.setColor(Color.BLACK);
		// fill rectangle behind char name
		g.fill(rr);
		// draw char name
		g.setColor(Color.WHITE);
		g.drawString(str, xt + 1, yt + 1);

	}

	private void renderMovementSpellOrder(Order order, Graphics2D g) {
		if (order.getParameter(0) == null) {
			return;
		}
		try {
			String hexNoStr = order.getParameter(1);
			String spellId = order.getParameter(0);
			int hexNo = Integer.parseInt(hexNoStr);
			int spellNo = Integer.parseInt(spellId);
			Point p1 = MapPanel.instance().getHexCenter(hexNo);
			Point p2 = MapPanel.instance().getHexCenter(order.getCharacter().getHexNo());

			int distance = MovementUtils.distance(order.getCharacter().getHexNo(), hexNo);
			boolean distanceOk = false;
			PopulationCenter popCenter = (PopulationCenter) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", hexNo);
			if (spellNo == 302) {
				distanceOk = distance <= 14;
			} else if (spellNo == 304) {
				distanceOk = distance <= 16;
			} else if (spellNo == 306) {
				distanceOk = distance <= 19;
			} else if (spellNo == 308) {
				// capital only
				distanceOk = popCenter != null && popCenter.getNationNo() == order.getCharacter().getNationNo() && popCenter.getCapital();
			} else if (spellNo == 310) {
				// mt or city only
				distanceOk = popCenter != null && popCenter.getNationNo() == order.getCharacter().getNationNo() && popCenter.getSize().getCode() >= PopulationCenterSizeEnum.majorTown.getCode();
			} else if (spellNo == 312) {
				distanceOk = popCenter != null && popCenter.getNationNo() == order.getCharacter().getNationNo();
			} else if (spellNo == 314) {
				// teleport
				distanceOk = true;
			}
			Color color;
			if (distanceOk) {
				color = Color.BLACK;
			} else {
				color = Color.GRAY;
			}
			g.setColor(color);
			Stroke s = GraphicUtils.getDashStroke(3, 8);
			Arrow.renderArrow(p1, p2, color, s, g);
			if (drawCharNames()) {
				String name = order.getCharacter().getName();
				if (order.getOrderNo() == 820) {
					name += " & co";
				}
				drawString(g, name, p1, p2);
			}
		} catch (Exception exc) {
			// parse or some other error, return
			return;
		}

	}

	private void renderCharacterMovementOrder(Order order, Graphics2D g) {
		if (order.getParameter(0) == null) {
			return;
		}
		try {
			String hexNoStr = order.getParameter(0);
			int hexNo = Integer.parseInt(hexNoStr);
			Point p1 = MapPanel.instance().getHexCenter(hexNo);
			Point p2 = MapPanel.instance().getHexCenter(order.getCharacter().getHexNo());

			int distance = MovementUtils.distance(order.getCharacter().getHexNo(), hexNo);

			if (distance <= 12) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(Color.GRAY);
			}
			// draw arrowhead
			double theta = Math.atan2((p1.y - p2.y), (p1.x - p2.x));
			g.setStroke(new BasicStroke(1));
			Shape arrowHead = Arrow.getArrowHead(p1.x, p1.y, 10, 15, theta);
			g.fill(arrowHead);

			Stroke s = GraphicUtils.getDashStroke(3, 8);
			g.setStroke(s);
			// draw line
			g.drawLine(p1.x, p1.y, p2.x, p2.y);

			if (drawCharNames()) {
				String name = order.getCharacter().getName();
				if (order.getOrderNo() == 820) {
					name += " & co";
				}
				drawString(g, name, p1, p2);
			}
		} catch (Exception exc) {
			// parse or some other error, return
			return;
		}
	}

	private void renderNatTranOrder(Order order, Graphics2D g) {
		if (order.getParameter(0) == null) {
			return;
		}
		try {
			String hexNoStr = order.getParameter(0);
			int hexNo = Integer.parseInt(hexNoStr);
			Point p1 = MapPanel.instance().getHexCenter(hexNo);
			int hexNo2 = MovementUtils.getHexNoAtDir(hexNo, MovementDirection.NorthWest);
			Point p2 = MapPanel.instance().getHexCenter(hexNo2);

			ProductEnum product = ProductEnum.getFromCode(order.getParameter(1));
			String pctStr = order.getParameter(2);
			//int pct = Integer.parseInt(pctStr);

			// draw arrowhead
			double theta = Math.atan2((p1.y - p2.y), (p1.x - p2.x));
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.GREEN);
			Shape arrowHead = Arrow.getArrowHead(p1.x, p1.y, 10, 15, theta);
			g.fill(arrowHead);

			Stroke s = GraphicUtils.getDashStroke(3, 6);
			g.setStroke(s);
			g.setColor(Color.GREEN);
			// draw line
			g.drawLine(p1.x, p1.y, p2.x, p2.y);

			String descr = product.getCode() + " " + pctStr + "%";
			drawString(g, descr, p2, p2);

		} catch (Exception exc) {
			// parse or some other error, return
			return;
		}
	}

	private void renderTranCarOrder(Order order, Graphics2D g) {
		if (order.getParameter(0) == null) {
			return;
		}
		try {
			String hexNoStr = order.getParameter(0);
			int hexNo = Integer.parseInt(hexNoStr);
			Point p2 = MapPanel.instance().getHexCenter(hexNo);
			String hexNoStr2 = order.getParameter(1);
			int hexNo2 = Integer.parseInt(hexNoStr2);
			Point p1 = MapPanel.instance().getHexCenter(hexNo2);

			ProductEnum product = ProductEnum.getFromCode(order.getParameter(2));
			String unitsStr = order.getParameter(3);

			// draw arrowhead
			double theta = Math.atan2((p1.y - p2.y), (p1.x - p2.x));
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.GREEN);
			Shape arrowHead = Arrow.getArrowHead(p1.x, p1.y, 10, 15, theta);
			g.fill(arrowHead);

			Stroke s = GraphicUtils.getDashStroke(3, 6);
			g.setStroke(s);
			g.setColor(Color.GREEN);
			// draw line
			g.drawLine(p1.x, p1.y, p2.x, p2.y);

			String descr = product.getCode() + " " + unitsStr;
			drawString(g, descr, p1, p2);

		} catch (Exception exc) {
			// parse or some other error, return
			return;
		}
	}

	private void renderReconTypeOrder(int hexNo, String text, Graphics2D g) {
		int[] xs = new int[18];
		int[] ys = new int[18];
		MovementDirection[] sides = new MovementDirection[] { MovementDirection.NorthWest, MovementDirection.NorthEast, MovementDirection.East, MovementDirection.SouthEast, MovementDirection.SouthWest, MovementDirection.West };
		int i = 0;
		for (MovementDirection md : sides) {
			// for each direction
			// get the hex at the dir
			int h = MovementUtils.getHexNoAtDir(hexNo, md);
			Point pc = MapPanel.instance().getHexLocation(h);
			// get the three appropriate sides
			for (int j = 0; j < 3; j++) {
				// get side - start from side 4, and add j and i
				// j to get the next three sides
				// and i to index by hex
				int hsi = (4 + i + j) % 6;
				Point p = new Point(this.xPoints[hsi], this.yPoints[hsi]);
				p.translate(pc.x, pc.y);
				xs[i * 3 + j] = p.x;
				ys[i * 3 + j] = p.y;
			}
			i++;
		}
		Stroke s = g.getStroke();
		g.setStroke(GraphicUtils.getBasicStroke(3));
		Polygon pl = new Polygon(xs, ys, 18);
		g.setColor(Color.blue);
		g.drawPolygon(pl);
		Point p = MapPanel.instance().getHexCenter(hexNo);
		if (drawCharNames()) {
			drawString(g, text, p, p);
		}
		g.setStroke(s);
	}

	private void renderReconOrder(Order o, Graphics2D g) {
		OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
		try {
			int hexNo = Integer.parseInt((String) (ovd.getAdditionalInfo(o, "hexNo")));
			renderReconTypeOrder(hexNo, o.getCharacter().getName() + "'s recon", g);
		} catch (Exception exc) {
		}
		;
	}

	private void renderScoAreaOrder(Order o, Graphics2D g) {
		OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
		try {
			int hexNo = Integer.parseInt((String) (ovd.getAdditionalInfo(o, "hexNo")));
			renderReconTypeOrder(hexNo, o.getCharacter().getName() + "'s ScoArea", g);
		} catch (Exception exc) {
		}
		;
	}

	private void renderScryingArtifactOrder(Order o, Graphics2D g) {
		try {
			int hexNo = Integer.parseInt(o.getParameter(1));
			renderReconTypeOrder(hexNo, o.getCharacter().getName() + "'s scry arti", g);
		} catch (Exception exc) {
		}
		;
	}

	private void renderScryAreaOrder(Order o, Graphics2D g) {
		try {
			int hexNo = Integer.parseInt(o.getParameter(1));
			renderReconTypeOrder(hexNo, o.getCharacter().getName() + "'s Scry Area", g);
		} catch (Exception exc) {
		}
		;
	}

	private boolean drawCharNames() {
		HashMap mapOptions1 = (HashMap) Application.instance().getApplicationContext().getBean("mapOptions");
		Object obj = mapOptions1.get(MapOptionsEnum.DrawNamesOnOrders);
		if (obj == null)
			return false;
		if (obj == MapOptionValuesEnum.DrawNamesOnOrdersOn) {
			return true;
		}
		return false;
	}

}
