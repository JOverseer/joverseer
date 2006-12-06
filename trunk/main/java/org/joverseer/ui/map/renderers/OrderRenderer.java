package org.joverseer.ui.map.renderers;

import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.domain.Army;
import org.joverseer.domain.Order;
import org.springframework.richclient.application.Application;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Íןו 2006
 * Time: 10:01:28 לל
 * To change this template use File | Settings | File Templates.
 */
public class OrderRenderer implements Renderer {
    MapMetadata mapMetadata = null;
    OrderVisualizationData orderVisualizationData = null;

    private OrderVisualizationData getOrderVisualizationData() {
        if (orderVisualizationData == null) {
            orderVisualizationData = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
        }
        return orderVisualizationData;
    }

    public boolean appliesTo(Object obj) {
        return Order.class.isInstance(obj) && !((Order)obj).isBlank();// && orderVisualizationData.contains((Order)obj);
    }

    private void init() {
        mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
    }

    public void render(Object obj, Graphics2D g, int x, int y) {
        if (mapMetadata == null) init();

        // todo render order
        Order order = (Order)obj;
        if (order.getOrderNo() == 810) {
            // movChar
            if (order.getParameter(0) == null) {
                return;
            }
            try {
                String hexNoStr = order.getParameter(0);
                int hexNo = Integer.parseInt(hexNoStr);
                Point p1 = MapPanel.instance().getHexCenter(hexNo);
                Point p2 = MapPanel.instance().getHexCenter(Integer.parseInt(order.getCharacter().getHexNo()));
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            catch (Exception exc) {
                // parse or some other error, return
                return;
            }
        }
    }
}
