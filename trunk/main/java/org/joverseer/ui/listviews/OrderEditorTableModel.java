package org.joverseer.ui.listviews;

import java.awt.Point;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.joverseer.domain.Character;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;

/**
 * Order editor table model Supports the display of orders in a table and the
 * editing of the order parameters
 * 
 * Fires the appropriate events when the order fields are changed
 * 
 * @author Marios Skounakis
 */
public class OrderEditorTableModel extends ItemTableModel {

	public static int iStats = 3;
	public static int iNoAndCode = 4;
	public static int iParamStart = 5;
	public static int iParamEnd = 19;
	public static int iDraw = 20;
	public static int iResults = 21;
	public static int iCost = 22;
	public static int iProfit = 23;
	public static int iNation = 0;
	public static int iHexNo = 2;

	public OrderEditorTableModel(MessageSource messageSource) {
		super(Order.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "character.nationNo", "character.name", "character.hexNo", "characterStats", "noAndCode", "p0", "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10", "p11", "p12", "p13", "p14", "draw", "results", "cost", "profit" };
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, Integer.class, String.class, String.class,

		String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,

		Boolean.class, ImageIcon.class, String.class, String.class };
	}

	@Override
	protected boolean isCellEditableInternal(Object object, int i) {
		return i == iNoAndCode || (i >= iParamStart && i <= iParamEnd) || i == iDraw;
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (i == iNation) {
			Order order = (Order) object;
			if (game == null)
				return "";
			if (IBelongsToNation.class.isInstance(order.getCharacter()) && getColumnPropertyNames()[i].endsWith("nationNo")) {
				GameMetadata gm = game.getMetadata();
				Integer nationNo = ((IBelongsToNation) order.getCharacter()).getNationNo();
				if (nationNo == null)
					return "";
				String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.showNationAs");
				if (pval.equals("number"))
					return nationNo;
				return gm.getNationByNum(nationNo).getShortName();
			}
			return super.getValueAtInternal(object, i);
		} else if (i == iDraw) {
			OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
			return ovd.contains((Order) object);
		} else if (i == iResults) {
			OrderResultContainer container = (OrderResultContainer) Application.instance().getApplicationContext().getBean("orderResultContainer");
			OrderResultTypeEnum orderResultType = container.getResultTypeForOrder((Order) object);
			Icon ico = null;
			if (orderResultType != null) {
				ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
				if (orderResultType == null) {
					ico = null;
				} else if (orderResultType == OrderResultTypeEnum.Info) {
					ico = new ImageIcon(imgSource.getImage("orderresult.info.icon"));
				} else if (orderResultType == OrderResultTypeEnum.Help) {
					ico = new ImageIcon(imgSource.getImage("orderresult.help.icon"));
				} else if (orderResultType == OrderResultTypeEnum.Warning) {
					ico = new ImageIcon(imgSource.getImage("orderresult.warn.icon"));
				} else if (orderResultType == OrderResultTypeEnum.Error) {
					ico = new ImageIcon(imgSource.getImage("orderresult.error.icon"));
				} else if (orderResultType == OrderResultTypeEnum.Okay) {
					ico = new ImageIcon(imgSource.getImage("orderresult.okay.icon"));
				}
			}
			return ico;
		} else if (i == iCost) {
			Order order = (Order) object;
			OrderCostCalculator calc = new OrderCostCalculator();
			int cost = calc.getOrderCost(order, game.getTurn());
			if (cost > 0) {
				return cost;
			} else if (cost == -1) {
				return "Error";
			} else {
				return "";
			}
		} else if (i == iProfit) {
			Order order = (Order) object;
			OrderCostCalculator calc = new OrderCostCalculator();
			int cost = calc.getOrderCost(order, game.getTurn());
			if (cost < 0) {
				return -cost;
			} else if (cost == -1) {
				return "Error";
			} else {
				return "";
			}
		} else if (i >= iParamStart && i <= iParamEnd) {
			Order order = (Order) object;
			String v = order.getParameter(i - iParamStart);
			if (v == null || v.equals("--") || v.equals("-")) {
				v = "";
			}
			;
			return v;
		} else if (i == iStats) {
			Order order = (Order) object;
			Character c = order.getCharacter();

			String txt = "";
			if (c.getCommandTotal() > 0) {
				txt += (txt.equals("") ? "" : ", ") + String.valueOf(c.getCommandTotal()) + "C";
			}
			if (c.getAgentTotal() > 0) {
				txt += (txt.equals("") ? "" : ", ") + String.valueOf(c.getAgentTotal()) + "A";
			}
			if (c.getEmmisaryTotal() > 0) {
				txt += (txt.equals("") ? "" : ", ") + String.valueOf(c.getEmmisaryTotal()) + "E";
			}
			if (c.getMageTotal() > 0) {
				txt += (txt.equals("") ? "" : ", ") + String.valueOf(c.getMageTotal()) + "M";
			}
			return txt;
		}
		return super.getValueAtInternal(object, i);
	}

	@Override
	protected void setValueAtInternal(Object v, Object obj, int col) {
		if (col == iDraw) {
			OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
			if (ovd.contains((Order) obj)) {
				ovd.removeOrder((Order) obj);
			} else {
				ovd.addOrder((Order) obj);
			}
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));

			return;
		} else if (col == iNoAndCode) {
			Order o = (Order) obj;
			int oldNo = o.getOrderNo();

			o.setNoAndCode(v.toString());
			if (oldNo != o.getOrderNo()) {
				if ((oldNo == 860 || oldNo == 850) && (o.getOrderNo() == 860 || o.getOrderNo() == 850)) {
					// keep same parameters
				} else {
					o.setParameters("");
				}
			}
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), o, o));
			return;
		} else if (col >= iParamStart && col <= iParamEnd) {
			Order o = (Order) obj;
			String paramTxt = "";
			String paramTxt2 = "";
			for (int i = iParamStart; i <= iParamEnd; i++) {
				String txt = (String) getValueAtInternal(obj, i);
				if (i == col) {
					txt = (String) v;
				}
				if (txt.trim().equals("")) {
					txt = "";
				}
				if (txt == null || txt.equals("")) {
					txt = "-";
				}
				paramTxt += (paramTxt.equals("") ? "" : "#") + txt;
				if (!txt.equals("-")) {
					paramTxt2 = paramTxt;
				}
			}
			o.setParameters(paramTxt2);
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), o, o));
			return;
		}
		super.setValueAtInternal(v, obj, col);
	}

	@Override
	public void fireTableCellUpdated(int row, int column) {
		// super.fireTableCellUpdated(row, column);
		Point selectedHex = MapPanel.instance().getSelectedHex();
		if (selectedHex != null) {
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshHexItems.toString(), selectedHex, this));
		}
	}
}
