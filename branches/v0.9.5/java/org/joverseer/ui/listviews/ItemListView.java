package org.joverseer.ui.listviews;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.beanutils.PropertyUtils;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;


public abstract class ItemListView extends BaseItemListView {
    TurnElementsEnum turnElementType = null;
    String metadataProperty;

    public ItemListView(TurnElementsEnum turnElementType, Class tableModelClass) {
        super(tableModelClass);
        this.turnElementType = turnElementType;
    }

    public ItemListView(String metadataProperty, Class tableModelClass) {
        super(tableModelClass);
        this.metadataProperty = metadataProperty;
        this.turnElementType = null;
    }

    protected void setItems() {
        if (this.turnElementType != null) {
            Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (!Game.isInitialized(g)) return;
            Container items = g.getTurn().getContainer(turnElementType);
            ArrayList filteredItems = new ArrayList();
            AbstractListViewFilter filter = getActiveFilter();
            for (Object o : items.getItems()) {
                if (filter == null || filter.accept(o)) filteredItems.add(o);
            };
            tableModel.setRows(filteredItems);
        } else {
            Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (!Game.isInitialized(g)) return;
            GameMetadata gm = g.getMetadata();
            try {
                Container items = (Container) PropertyUtils.getProperty(gm, metadataProperty);
                ArrayList filteredItems = new ArrayList();
                AbstractListViewFilter filter = getActiveFilter();
                for (Object o : items.getItems()) {
                    if (filter == null || filter.accept(o)) filteredItems.add(o);
                };
                tableModel.setRows(filteredItems);
            }
            catch (Exception exc) {
                // todo fix
            	tableModel.setRows(new ArrayList());
            }
        }
        //tableModel.fireTableDataChanged();
    }

    
}

