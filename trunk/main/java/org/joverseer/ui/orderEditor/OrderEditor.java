package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;


public class OrderEditor extends AbstractForm implements ApplicationListener {
    JComboBox orderCombo;
    JTextField parameters;
    JTextField character;
    JTextArea description;
    JPanel subeditorPanel;
    JPanel myPanel;
    
    GameMetadata gm;
    
    public OrderEditor() {
        super(FormModelHelper.createFormModel(new Order(new Character())));
    }
    
    private GameMetadata getGameMetadata() {
        if (gm == null) {
            Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (!Game.isInitialized(g)) return null;
            return g.getMetadata();
        }
        return gm;
    }
    
    private void refreshOrderCombo() {
        Container orderMetadata = getGameMetadata().getOrders();
        ListListModel orders = new ListListModel();
        orders.add(Order.NA);
        for (OrderMetadata om : (ArrayList<OrderMetadata>)orderMetadata.getItems()) {
            orders.add(om.getNumber() + " " + om.getCode());
        }
        SortedListModel slm = new SortedListModel(orders);
        orderCombo.setModel(new ComboBoxListModelAdapter(slm));
    }
    
    protected JComponent createFormControl() {
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(5, 5, 0, 0));
        glb.append(new JLabel("Character :"));
        
        glb.append(character = new JTextField());
        character.setEditable(false);
        character.setPreferredSize(new Dimension(50, 16));

        JButton btnSave = new JButton();
        btnSave.setPreferredSize(new Dimension(16, 16));
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("SaveGameCommand.icon"));
        btnSave.setIcon(ico);
        glb.append(btnSave);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                saveOrder();
            }
        });

        glb.nextLine();
        
        glb.append(new JLabel("Order :"));
        
        glb.append(orderCombo = new JComboBox());

        orderCombo.setPreferredSize(new Dimension(50, 16));
        orderCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                refreshDescription();
                refreshSubeditor();
            }
        });
        
        
        glb.nextLine();
        
        glb.append(new JLabel("Parameters :"));
        glb.append(parameters = new JTextField());
        parameters.setEditable(false);
        parameters.setPreferredSize(new Dimension(50, 16));
        glb.nextLine();
        
        JLabel l;
        glb.append(l = new JLabel("Description :"));
        glb.append(description = new JTextArea());
        l.setVerticalAlignment(JLabel.NORTH);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setPreferredSize(new Dimension(250, 40));
        description.setEditable(false);
        description.setBorder(parameters.getBorder());
        Font f = new Font(description.getFont().getName(),
                            Font.ITALIC,
                            description.getFont().getSize());
        description.setFont(f);
        
        glb.nextLine();
        glb.append(subeditorPanel = new JPanel(), 2, 1);
        subeditorPanel.setBackground(Color.white);
        glb.nextLine();
        
        
        myPanel = glb.getPanel();
        myPanel.setBackground(Color.white);
        
        return myPanel;
    }
    
    private String getSelectedOrderNo() {
        String noAndCode = orderCombo.getSelectedItem().toString();
        int i = noAndCode.indexOf(" ");
        if (i > 0) {
            return noAndCode.substring(0, i);
        } else {
            return "";
        }
    }
    
    private void refreshSubeditor() {
        AbstractOrderSubeditor mcs = null;
        if (getSelectedOrderNo().equals("810")) {
            mcs = new SingleParameterOrderSubeditor("Destination :", (Order)getFormObject());
        } else if (getSelectedOrderNo().equals("615")) {
            mcs = new SingleParameterOrderSubeditor("Target :", (Order)getFormObject());
        }
        if (mcs != null) {
            mcs.setEditor(this);
            subeditorPanel.removeAll();
            subeditorPanel.add(mcs.getControl());
            subeditorPanel.updateUI();
            mcs.setFormObject(getFormObject());
            parameters.setEditable(false);
        } else {
            parameters.setEditable(true);
        }
    }
    
    private void refreshDescription() {
        String txt = "";
        try {
            String selOrder = (String)orderCombo.getSelectedItem();
            int i = selOrder.indexOf(' ');
            int no = Integer.parseInt(selOrder.substring(0, i));
            Container orderMetadata = getGameMetadata().getOrders();

            OrderMetadata om = (OrderMetadata)orderMetadata.findFirstByProperty("number", no);
            txt = om.getName() + ", " + om.getDifficulty() + ", " + om.getRequirement() + "\n" + om.getParameters();
        }
        catch (Exception exc) {
            // do nothing
        }
        description.setText(txt);
    }
    
    private void saveOrder() {
        Order o = (Order)getFormObject();
        o.setNoAndCode(orderCombo.getSelectedItem().toString());
        o.setParameters(parameters.getText());
        // throw an order changed event
        Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), o, this));
        Point selectedHex = new Point(o.getCharacter().getX(), o.getCharacter().getY());
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selectedHex, this));
        
    }
    
    public void setFormObject(Object obj) {
        super.setFormObject(obj);
        Order o = (Order)obj;
        parameters.setText(o.getParameters());
        orderCombo.setSelectedItem(o.getNoAndCode());
        Character c = o.getCharacter();
        if (c == null) {
            character.setText("");
        } else {
            character.setText(c.getName() + " - " + c.getHexNo());
        }
    }
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                refreshOrderCombo();
            } else if (e.getEventType().equals(LifecycleEventsEnum.EditOrderEvent.toString())) {
                setFormObject(e.getObject());
            }
        }
    }
    
    public void setParameters(String v) {
        parameters.setText(v);
    }
}
