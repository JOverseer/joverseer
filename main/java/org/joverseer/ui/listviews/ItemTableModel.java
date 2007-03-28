package org.joverseer.ui.listviews;

import java.util.prefs.Preferences;

import javax.swing.JPopupMenu;

import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.application.Application;
import org.springframework.context.MessageSource;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.JOverseerClient;


public abstract class ItemTableModel extends BeanTableModel {
	
    public ItemTableModel(Class aClass, MessageSource messageSource) {
        super(aClass, messageSource);
        setRowNumbers(false);
    }

    
    protected Object getValueAtInternal(Object object, int i) {
        try {
            String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.showNationAs");
            if (pval.equals("number")) return super.getValueAtInternal(object, i);
            Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (game == null) return "";
            if (IBelongsToNation.class.isInstance(object) && getColumnPropertyNames()[i].equals("nationNo")) {
                GameMetadata gm = game.getMetadata();
                Integer nationNo = ((IBelongsToNation)object).getNationNo();
                if (nationNo == null) return "";
                return gm.getNationByNum(nationNo).getShortName();
            }
            return super.getValueAtInternal(object, i);
        }
        catch (Exception exc) {
            return "";
        }
        
    }

    protected boolean isCellEditableInternal(Object object, int i) {
        return false;
    }
    

}
