package org.joverseer.ui.listviews;

import org.springframework.context.MessageSource;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.domain.Order;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Íןו 2006
 * Time: 10:02:41 לל
 * To change this template use File | Settings | File Templates.
 */
public class OrderTableModel extends ItemTableModel {
    public OrderTableModel(MessageSource messageSource) {
        super(Order.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"character.name", "character.hexNo", "noAndCode", "parameters"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class};  //To change body of implemented methods use File | Settings | File Templates.
    }


}
