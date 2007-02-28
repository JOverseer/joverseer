package org.joverseer.tools.orderHandler;

import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.messages.Message;
import org.joverseer.support.messages.MessageTypeEnum;


public class ImprovePopulationCenterHandler extends BaseOrderHandler {
    int[] improveCosts = new int[]{0, 4000, 6000, 8000, 10000};
    
    public ImprovePopulationCenterHandler() {
        super(new int[]{550});
    }

    public OrderResult getOrderResult(Character c, int orderNo) {
        Game g = getGame();
        if (g == null) return null;
        PopulationCenter pc = (PopulationCenter)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", c.getHexNo());
        OrderResult res = new OrderResult();
        if (pc == null) {
            res.addMessage(new Message(MessageTypeEnum.Error, String.format("No population center exists at hex %s.", c.getHexNo())));
            return res;
        }
        if (pc.getSize() == PopulationCenterSizeEnum.city) {
            res.addMessage(new Message(MessageTypeEnum.Error, String.format("Population center is a city and cannot be improved.")));
            return res;
        }
        res.getOrderCost().setProduct(ProductEnum.Gold, improveCosts[(Integer)pc.getSize().getCode()]);
        return res;
    }
    
    
    
}
