package org.joverseer.ui.listviews;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.joverseer.domain.NationMessage;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.EnemyCharacterRumorWrapper;
import org.joverseer.domain.Character;


public class EnemyCharacterRumorListView extends BaseItemListView {


    public EnemyCharacterRumorListView() {
        super(EnemyCharacterRumorTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{120, 64, 64, 240};
    }
    
    
    protected JComponent createControlImpl() {
        JComponent comp = super.createControlImpl();
        table.setDefaultRenderer(Boolean.class, new JTable().getDefaultRenderer(Boolean.class));
        return comp;
    }

    protected void setItems() {
        Container thieves = EnemyCharacterRumorWrapper.getAgentWrappers();
        tableModel.setRows(thieves.getItems());
        tableModel.fireTableDataChanged();
    }
}
