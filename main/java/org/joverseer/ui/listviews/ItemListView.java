package org.joverseer.ui.listviews;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.table.TableUtils;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.ApplicationEvent;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import org.joverseer.domain.IHasMapLocation;
import org.joverseer.metadata.GameMetadata;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import sun.management.MethodInfo;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 13 Οκτ 2006
 * Time: 9:24:42 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ItemListView extends AbstractView implements ApplicationListener, MouseListener {
    BeanTableModel tableModel;
    TurnElementsEnum turnElementType = null;
    String metadataProperty;

    JTable table;
    Class tableModelClass;
    private SelectHexCommandExecutor selectHexCommandExecutor = new SelectHexCommandExecutor();

    public ItemListView(TurnElementsEnum turnElementType, Class tableModelClass) {
        this.turnElementType = turnElementType;
        this.tableModelClass = tableModelClass;
    }

    public ItemListView(String metadataProperty, Class tableModelClass) {
        this.metadataProperty = metadataProperty;
        this.tableModelClass = tableModelClass;
        this.turnElementType = null;
    }

    private void setItems() {
        if (this.turnElementType != null) {
            Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (!Game.isInitialized(g)) return;
            Container items = g.getTurn().getContainer(turnElementType);
            tableModel.setRows(items.getItems());
        } else {
            Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (!Game.isInitialized(g)) return;
            GameMetadata gm = g.getMetadata();
            try {
                Container items = (Container) PropertyUtils.getProperty(gm, metadataProperty);
                tableModel.setRows(items.getItems());
            }
            catch (Exception exc) {
                // todo fix
                int a = 1;
            }
        }
    }

    protected void registerLocalCommandExecutors(PageComponentContext pageComponentContext) {
        pageComponentContext.register("selectHexCommand", selectHexCommandExecutor);
        selectHexCommandExecutor.setEnabled(GameHolder.hasInitializedGame());
    }

    protected JComponent createControl() {

        // fetch the messageSource instance from the application context
        MessageSource messageSource = (MessageSource) getApplicationContext().getBean("messageSource");

        // create the table model
        try {
            tableModel = (BeanTableModel)tableModelClass.getConstructor(new Class[]{MessageSource.class}).newInstance(new Object[]{messageSource});
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        setItems();

        // create the JTable instance
        table = TableUtils.createStandardSortableTable(tableModel);
        org.joverseer.ui.support.TableUtils.setTableColumnWidths(table,
                new int[]{32, 40, 120,
                        32, 32, 32, 32,
                        32, 32, 32, 32,
                        32, 32, 32, 32, 32});

        table.addMouseListener(this);
        JScrollPane scrollPane = new JScrollPane(table);
        //scrollPane.getViewport().setOpaque(true);
        //scrollPane.getViewport().setBackground(table.getBackground());
        return scrollPane;
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                setItems();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == 1) {
            selectHexCommandExecutor.execute();
        }
    }

    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private class SelectHexCommandExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Object obj = tableModel.getRow(((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row));
                if (!IHasMapLocation.class.isInstance(obj)) return;
                IHasMapLocation selectedItem = (IHasMapLocation)obj;
                Point selectedHex = new Point(selectedItem.getX(), selectedItem.getY());
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selectedHex, this));
            }
        }
    }
}

