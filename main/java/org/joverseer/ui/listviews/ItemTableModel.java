package org.joverseer.ui.listviews;

import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.application.Application;
import org.springframework.context.MessageSource;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Οκτ 2006
 * Time: 7:21:52 μμ
 * To change this template use File | Settings | File Templates.
 */
public abstract class ItemTableModel extends BeanTableModel {
    public ItemTableModel(Class aClass, MessageSource messageSource) {
        super(aClass, messageSource);
        setRowNumbers(false);
    }

    protected Object getValueAtInternal(Object object, int i) {
        Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (game == null) return "";
        if (IBelongsToNation.class.isInstance(object) && getColumnPropertyNames()[i].equals("nationNo")) {
            GameMetadata gm = game.getMetadata();
            int nationNo = ((IBelongsToNation)object).getNationNo();
            return gm.getNationByNum(nationNo).getShortName();
        }
        return super.getValueAtInternal(object, i);
    }

    protected boolean isCellEditableInternal(Object object, int i) {
        return false;
    }
    
    

}
