package org.joverseer.ui.listviews;

import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.application.Application;
import org.springframework.context.MessageSource;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.domain.IBelongsToNation;

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
    }

    protected Object getValueAtInternal(Object object, int i) {
        GameMetadata gm = (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata");
        if (IBelongsToNation.class.isInstance(object) && getColumnPropertyNames()[i].equals("nationNo")) {
            int nationNo = ((IBelongsToNation)object).getNationNo();
            return gm.getNationByNum(nationNo).getShortName();
        }
        return super.getValueAtInternal(object, i);
    }

    protected boolean isCellEditableInternal(Object object, int i) {
        return false;
    }
}
