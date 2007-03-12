package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;


public class OrderEditor extends AbstractForm implements ApplicationListener {
    public static final String FORM_PAGE = "orderEditorForm";
    JComboBox orderCombo;
    JTextField parameters;
    JTextField character;
    JTextArea description;
    JPanel subeditorPanel;
    JPanel myPanel;
    
    ArrayList<JComponent> subeditorComponents = new ArrayList<JComponent>(); 
    
    Container orderEditorData = null;
    
    GameMetadata gm;
    
    public OrderEditor() {
        super(FormModelHelper.createFormModel(new Order(new Character())), FORM_PAGE);
    }
    
    public String getParameters() {
        return parameters.getText();
    }
    
    private Container getOrderEditorData() {
        if (orderEditorData == null) {
            orderEditorData = new Container(new String[]{"orderNo"});
            try {
                GameMetadata gm = (GameMetadata)Application.instance().getApplicationContext().getBean("gameMetadata");
                Resource resource = gm.getResource("orderEditorData.csv");
    
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
    
                String ln;
                while ((ln = reader.readLine()) != null) {
                    try {
                    String[] partsL = ln.split(";");
                    String[] parts = new String[]{"", "", "", "",
                        "", "", "", "", 
                        "", "", "", "", 
                        "", "", "", ""
                    };
                    for (int i=0; i<partsL.length; i++) {
                        parts[i] = partsL[i];
                    }
                    OrderEditorData oed = new OrderEditorData();
                    oed.setOrderNo(Integer.parseInt(parts[0]));
                    oed.setOrderDescr(parts[1]);
                    oed.setParameterDescription(parts[2]);
                    oed.setOrderType(parts[3]);
                    oed.getParamTypes().add(parts[4]);
                    oed.getParamTypes().add(parts[5]);
                    oed.getParamTypes().add(parts[6]);
                    oed.getParamTypes().add(parts[7]);
                    oed.getParamTypes().add(parts[8]);
                    oed.getParamTypes().add(parts[9]);
                    oed.getParamTypes().add(parts[10]);
                    oed.setMajorSkill(parts[11]);
                    oed.setSkill(parts[12]);
                    orderEditorData.addItem(oed);
                    }
                    catch (Exception exc) {
                        System.out.println(ln);
                    }
                }
            }
            catch (Exception exc) {
                System.out.println(exc.getMessage());
                orderEditorData = null;
            }
        }
        return orderEditorData;
    }
    
    private GameMetadata getGameMetadata() {
        if (gm == null) {
            Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (!Game.isInitialized(g)) return null;
            gm = g.getMetadata();
        }
        return gm;
    }
    
    private void refreshOrderCombo() {
        Container orderMetadata = getGameMetadata().getOrders();
        Order o = (Order)getFormObject();
        ListListModel orders = new ListListModel();
        orders.add(Order.NA);
        for (OrderMetadata om : (ArrayList<OrderMetadata>)orderMetadata.getItems()) {
            if (o.getCharacter() != null && om.charHasRequiredSkill(o.getCharacter())) {
                orders.add(om.getNumber() + " " + om.getCode());
            }
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
        character.setPreferredSize(new Dimension(70, 18));

        JButton btnSave = new JButton();
        btnSave.setPreferredSize(new Dimension(18, 18));
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

        orderCombo.setPreferredSize(new Dimension(70, 18));
        orderCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                refreshOrder();
                refreshDescription();
                refreshSubeditor();
            }
        });
        
        
        glb.nextLine();
        
        glb.append(new JLabel("Parameters :"));
        glb.append(parameters = new JTextField());
        parameters.setEditable(false);
        parameters.setPreferredSize(new Dimension(70, 18));
        glb.nextLine();
        
        JLabel l;
        glb.append(l = new JLabel("Description :"));
        glb.append(description = new JTextArea());
        l.setVerticalAlignment(JLabel.NORTH);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setPreferredSize(new Dimension(200, 50));
        description.setEditable(false);
        description.setBorder(parameters.getBorder());
        Font f = new Font(description.getFont().getName(),
                            Font.ITALIC,
                            description.getFont().getSize()-1);
        description.setFont(f);
        
        glb.nextLine();
        glb.append(subeditorPanel = new JPanel(), 2, 1);
        subeditorPanel.setBackground(Color.white);
        glb.nextLine();
        
        
        myPanel = glb.getPanel();
        myPanel.setBackground(Color.white);
        
        return myPanel;
    }
    
    private void refreshOrder() {
        Order o = (Order)getFormObject();
        if (getSelectedOrderNo().equals("") || o.getOrderNo() != Integer.parseInt(getSelectedOrderNo())) {
            o.setParameters("");
            parameters.setText(o.getParameters());
        }
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
        subeditorPanel.removeAll();
        subeditorComponents.clear();
        Order o = (Order)getFormObject();
        boolean paramsEditable = true;
        if (!getSelectedOrderNo().equals("") && getOrderEditorData() != null) {
            OrderEditorData oed = (OrderEditorData)getOrderEditorData().findFirstByProperty("orderNo", Integer.parseInt(getSelectedOrderNo()));
            if (oed != null) {
                paramsEditable = false;
                TableLayoutBuilder tlb = new TableLayoutBuilder(subeditorPanel);
                int paramNo = 0;
                if (oed.getOrderNo() == 850 || oed.getOrderNo() == 860 || oed.getOrderNo() == 840) {
                    AbstractOrderSubeditor sub = new MoveArmyOrderSubeditor(o);
                    sub.addComponents(tlb, subeditorComponents, o, 0);
                    sub.setEditor(this);
                } else {
                    for (String paramType : oed.getParamTypes()) {
                        AbstractOrderSubeditor sub = null;
                        if (paramType.equals("hex")) {
                            sub = new SingleParameterOrderSubeditor("Hex : ", o);
                        } else if (paramType.equals("cid") || paramType.equals("xid")) {
                            sub = new SingleParameterOrderSubeditor("Char : ", o);
                        } else if (paramType.equals("tac")) {
                            sub = new DropDownParameterOrderSubeditor("Tactic : ", o, new String[]{"ch", "fl", "st", "su", "hr", "am"}, new String[]{"Charge", "Flank", "Standard", "Surround", "Hit and Run", "Ambush"});
                        } else if (paramType.equals("prd")) {
                            sub = new DropDownParameterOrderSubeditor("Product : ", o, new String[]{"le", "br", "st", "mi", "fo", "ti", "mo"}, new String[]{"Leather", "Bronze", "Steel", "Mithril", "Food", "Timber", "Mounts"});
                        } else if (paramType.equals("pro")) {
                            sub = new DropDownParameterOrderSubeditor("Product : ", o, new String[]{"le", "br", "st", "mi", "fo", "ti", "mo", "go"}, new String[]{"Leather", "Bronze", "Steel", "Mithril", "Food", "Timber", "Mounts", "Gold"});
                        } else if (paramType.equals("%")) {
                            sub = new NumberParameterOrderSubeditor("Percent : ", o);
                        } else if (paramType.equals("g")) {
                            sub = new NumberParameterOrderSubeditor("No : ", o);
                        } else if (paramType.equals("yn")) {
                            sub = new DropDownParameterOrderSubeditor("Yes/No : ", o, new String[]{"y", "n"}, new String[]{"Yes", "No"});
                        } else if (paramType.equals("wep")) {
                            sub = new DropDownParameterOrderSubeditor("Weapon : ", o, new String[]{"wo", "br", "st", "mi"}, new String[]{"Wooden", "Bronze", "Steel", "Mithril"});
                        } else if (paramType.equals("arm")) {
                            sub = new DropDownParameterOrderSubeditor("Armor : ", o, new String[]{"no", "le", "br", "st", "mi"}, new String[]{"None", "Leather", "Bronze", "Steel", "Mithril"});
                        } else if (paramType.equals("trp")) {
                            sub = new DropDownParameterOrderSubeditor("Troop Type : ", o, new String[]{"hc", "lc", "hi", "li", "ar", "ma"}, new String[]{"Heavy Cavalry", "Light Cavalry", "Heavy Infantry", "Light Infantry", "Archers", "Men at Arms"});
                        } else if (paramType.equals("gen")) {
                            sub = new DropDownParameterOrderSubeditor("Gender : ", o, new String[]{"m", "f"}, new String[]{"Male", "Female"});
                        } else if (paramType.equals("alg")) {
                            sub = new DropDownParameterOrderSubeditor("Allegiance : ", o, new String[]{"g", "e"}, new String[]{"Good", "Evil"});
                        } else if (paramType.equals("nam")) {
                            sub = new SingleParameterOrderSubeditor("Name : ", o);
                        } else if (paramType.equals("rsp")) {
                            sub = new SingleParameterOrderSubeditor("Response : ", o);
                        } else if (paramType.equals("a")) {
                            sub = new NumberParameterOrderSubeditor("Skill : ", o);
                        } else if (paramType.equals("b")) {
                            sub = new NumberParameterOrderSubeditor("No : ", o);
                        } else if (paramType.equals("e")) {
                            sub = new NumberParameterOrderSubeditor("No : ", o);
                        } else if (paramType.equals("d")) {
                            sub = new NumberParameterOrderSubeditor("No : ", o);
                        } else if (paramType.equals("dir") || paramType.equals("dirx")) {
                            sub = new SingleParameterOrderSubeditor("Dir : ", o);
                        } else if (paramType.equals("nat")) {
                            sub = new NationParameterOrderSubeditor("Nation : ", o);
                        } else if (paramType.equals("spc") || paramType.equals("sph") || paramType.equals("spz") || paramType.equals("spm") || paramType.equals("spl")) {
                            sub = new SpellNumberParameterOrderSubeditor("Spell : ", o, oed.getOrderNo());
                        }
                        if (sub != null) {
                            sub.addComponents(tlb, subeditorComponents, o, paramNo);
                            sub.setEditor(this);
                        }
                        paramNo++;
                    }
                }
                //hack in order to show all of the subeditor fields
                tlb.cell(new JLabel(" "));
                tlb.row();
                tlb.cell(new JLabel(" "));
                tlb.row();
                tlb.cell(new JLabel(" "));
                tlb.row();
                tlb.cell(new JLabel(" "));
                tlb.row();
                tlb.cell(new JLabel(" "));
                tlb.row();
                tlb.getPanel();
                //subeditorPanel.invalidate();
                //myPanel.invalidate();
            }
        }
        parameters.setEditable(paramsEditable);
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
        refreshOrderCombo();
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
                //OrderEditorView oev = (OrderEditorView)Application.instance().getApplicationContext().getBean("orderEditorView");
                // mscoon
                //DockingManager.display(DockingManager.getDockable("orderEditorView"));
            }
        }
    }
    
    public void updateParameters() {
        String v = "";
        for (JComponent c : subeditorComponents) {
            String val = "";
            if (JTextField.class.isInstance(c)) {
                val = ((JTextField)c).getText();
            } else if (JComboBox.class.isInstance(c)) {
                if (((JComboBox)c).getSelectedItem() == null) {
                    val = "";
                } else {
                    val = ((JComboBox)c).getSelectedItem().toString();
                }
            }
            if (val.equals("")) {
                val = "-";
            }
            v += (v.equals("") ? "" : " ") + val;
        }
        parameters.setText(v);
    }
}
