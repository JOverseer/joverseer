package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.joverseer.domain.Army;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Company;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.Order;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.DoubleAgentInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromRevealCharacterInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.command.ShowCharacterFastStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterLongStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterMovementRangeCommand;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.CharacterRangeMapItem;
import org.joverseer.ui.listviews.ArtifactInfoTableModel;
import org.joverseer.ui.listviews.ItemTableModel;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.orderEditor.OrderEditorAutoNations;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.controls.TableUtils;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.ui.support.transferHandlers.ParamTransferHandler;
import org.joverseer.ui.support.transferHandlers.CharIdTransferHandler;
import org.joverseer.ui.views.NarrationForm;
import org.joverseer.ui.views.OrderResultsForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;

import sun.awt.geom.AreaOp.AddOp;


public class CharacterViewer extends ObjectViewer {

    public static final String FORM_PAGE = "CharacterViewer";

    boolean showColor = true;
    
    JTextField characterName;
    JTextField statsTextBox;
    JTextField infoSourcesTextBox;
    JTextField nationTextBox;
    JTextField companyMembersTextBox;

    JTable artifactsTable;
    JTable spellsTable;
    
    JLabel swapOrdersIconCmd;

    JButton btnMenu;

    JComponent order1comp;
    JComponent order2comp;
    OrderViewer order1;
    OrderViewer order2;
    JPanel orderPanel;

    boolean showArtifacts = false;
    boolean showSpells = false;
    boolean showOrders = false;

    ActionCommand showArtifactsCommand = new ShowArtifactsCommand();
    ActionCommand showSpellsCommand = new ShowSpellsCommand();
    ActionCommand showOrdersCommand = new ShowOrdersCommand();
    ActionCommand showResultsCommand = new ShowResultsCommand();
    ActionCommand deleteCharacterCommand = new DeleteCharacterCommand();
    ActionCommand addRefuseChallengesCommand = new AddOrderCommand(215, "");

    public CharacterViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }
    
    public boolean appliesTo(Object obj) {
        return Character.class.isInstance(obj);
    }

    public void setFormObject(Object object) {
        Character c = (Character) object;
        if (object != getFormObject()) {
            showArtifacts = false;
            showOrders = !c.getOrders()[0].isBlank() || !c.getOrders()[1].isBlank() ||
                OrderEditorAutoNations.instance().containsNation(c.getNationNo());
            showSpells = false;
        }
        super.setFormObject(object);
        if (object == null)
            return;
        reset(c);
    }
    
    
    
    public void reset(Character c) {
        Game g = GameHolder.instance().getGame();
        
        boolean showStartingInfo = false;
        ArrayList artis = new ArrayList();
        Character startingChar = null;
        characterName.setText(GraphicUtils.parseName(c.getName()));
        if (statsTextBox != null) {
            characterName.setCaretPosition(0);

            String txt = getStatLine(c);
            
            if (txt.equals("")) {
                // character is enemy
                
                // retrieve info from CharacterInfoCollector if possible
//                AdvancedCharacterWrapper acw = CharacterInfoCollector.instance().getCharacterForTurn(c.getName(), g.getCurrentTurn());
//                if (acw != null) {
//                    startingChar = new Character();
//                    startingChar.setName(acw.getName());
//                    startingChar.setId(acw.getId());
//                    startingChar.setNationNo(acw.getNationNo());
//                    txt = getStatLineFromCharacterWrapper(acw);
//                    if (!txt.equals("")) {
//                        txt += "(collected info";
//                        if (acw.getStartChar()) {
//                            txt += " - start char";
//                        }
//                        txt += ")";
//                    }
//                    // artifacts
//                    if (showArtifacts) {
//                        for (ArtifactWrapper aw : acw.getArtifacts()) {
//                            // find artifact in metadata
//                            ArtifactInfo a = (ArtifactInfo)g.getMetadata().getArtifacts().findFirstByProperty("no", aw.getNumber());
//                            // copy into new object to change the name and add the turn - hack but for now it works
//                            ArtifactInfo na = new ArtifactInfo();
//                            na.setNo(a.getNo());
//                            na.setAlignment(a.getAlignment());
//                            na.setOwner(acw.getName());
//                            na.setPowers(a.getPowers());
//                            na.setName(a.getName() + " (t" + aw.getTurnNo() + ")");
//                            artis.add(na);
//                        }
//                    }
//                    
//                    
//                    
//                    showStartingInfo = true;
//                } else 
            	{
                    // retrieve starting info
                    Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder"))
                            .getGame();
                    GameMetadata gm = game.getMetadata();
                    Container startChars = gm.getCharacters();
                    if (startChars != null) {
                        startingChar = (Character) startChars.findFirstByProperty("id", c.getId());
                        showStartingInfo = true;
                        if (startingChar != null) {
                            txt = getStatLine(startingChar) + "(start info)";
                        }
                    }
                }
            }
            statsTextBox.setText(txt);
            statsTextBox.setCaretPosition(0);
            
            // show info sources, if needed
            InfoSource is = c.getInfoSource();
            if (DerivedFromLocateArtifactInfoSource.class.isInstance(is) ||
                    DerivedFromRevealCharacterInfoSource.class.isInstance(is)) {
                DerivedFromSpellInfoSource sis = (DerivedFromSpellInfoSource)is;
                String infoSourcesStr = sis.getSpell() + " at " + sis.getHexNo();
                infoSourcesTextBox.setVisible(true);
                for (InfoSource dsis : sis.getOtherInfoSources()) {
                    if (DerivedFromSpellInfoSource.class.isInstance(dsis)) {
                        infoSourcesStr += ", " + ((DerivedFromSpellInfoSource)dsis).getSpell() + " at " + ((DerivedFromSpellInfoSource)dsis).getHexNo();
                    }
                }
                infoSourcesTextBox.setText(infoSourcesStr);
            } else if (DoubleAgentInfoSource.class.isInstance(is)) {
                infoSourcesTextBox.setVisible(true);
                //TODO show nation name
                infoSourcesTextBox.setText("Double agent for nation " + ((DoubleAgentInfoSource)is).getNationNo());
            } else {
                infoSourcesTextBox.setVisible(false);
            }

            Font f = GraphicUtils.getFont(statsTextBox.getFont().getName(), (showStartingInfo ? Font.ITALIC
                    : Font.PLAIN), statsTextBox.getFont().getSize());
            statsTextBox.setFont(f);

            Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (game == null)
                return;
            GameMetadata gm = game.getMetadata();
            int nationNo = (showStartingInfo && startingChar != null ? startingChar.getNationNo() : c.getNationNo());
            nationTextBox.setText(gm.getNationByNum(nationNo).getShortName());
            
            if (showArtifacts && !showStartingInfo) {
                ArrayList<Integer> artifacts = (!showStartingInfo ? c.getArtifacts()
                        : startingChar != null ? startingChar.getArtifacts() : null);
                if (artifacts != null) {
                    for (Integer no : artifacts) {
                        ArtifactInfo arti = (ArtifactInfo) gm.getArtifacts().findFirstByProperty("no", no);
                        if (arti == null) {
                            arti = new ArtifactInfo();
                            arti.setNo(no);
                            arti.setName("---");
                        }
                        artis.add(arti);
                    }
                }
            } else {
                // do nothing, we have already populated the artis table
            }
            
            ((BeanTableModel) artifactsTable.getModel()).setRows(artis);
            artifactsTable.setPreferredSize(new Dimension(artifactsTable.getWidth(), 16 * artis.size()));

            ArrayList spells = new ArrayList();
            if (showSpells) {
                spells.addAll(c.getSpells());
            }
            ((BeanTableModel) spellsTable.getModel()).setRows(spells);
            spellsTable.setPreferredSize(new Dimension(spellsTable.getWidth(), 16 * spells.size()));

            Container companies = game.getTurn().getContainer(TurnElementsEnum.Company);
            Company company = (Company)companies.findFirstByProperty("commander", c.getName());
            if (company != null) {
                String members = company.getMemberStr();
                
                companyMembersTextBox.setText("Company: " + members);
                companyMembersTextBox.setCaretPosition(0);
                companyMembersTextBox.setVisible(true);
            } else {
                companyMembersTextBox.setVisible(false);
            }
            order1.setFormObject(c.getOrders()[0]);
            order2.setFormObject(c.getOrders()[1]);
            if (showOrders) {
            	orderPanel.setVisible(true);
            	swapOrdersIconCmd.setVisible(true);
            } else {
            	orderPanel.setVisible(false);
            	swapOrdersIconCmd.setVisible(false);
            }
            
            if (getShowColor()) {
                Turn t = g.getTurn();
                
                NationRelations nr = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", nationNo);
                Color col;
                if (nr == null) {
                    col = ColorPicker.getInstance().getColor(NationAllegianceEnum.Neutral.toString());
                } else {
                    col = ColorPicker.getInstance().getColor(nr.getAllegiance().toString());
                }
                characterName.setForeground(col);
            }
        }

    }
    
//    private String getStatTextFromCharacterWrapper(String prefix, CharacterAttributeWrapper caw) {
//        String v = caw == null || caw.getValue() == null ? "" : caw.getValue().toString();
//        if (v.equals("0")) v = "";
//        if (caw != null && caw.getValue() != null) {
//                InfoSource is = caw.getInfoSource();
//                if (DerivedFromTitleInfoSource.class.isInstance(is)) {
//                        v += "+";
//                } else if (RumorActionInfoSource.class.isInstance(is)) {
//                        v += "+";
//                }
//        }
//        if (caw != null && caw.getTotalValue() != null) {
//                if (!caw.getTotalValue().toString().equals(
//                                caw.getValue().toString())
//                                && !caw.getTotalValue().toString().equals("0")) {
//                        v += "(" + caw.getTotalValue().toString() + ")";
//                }
//        }
//        if (!v.equals("")) {
//            v = prefix + v + " ";
//        }
//        return v;
//    }
//    
//    public String getStatLineFromCharacterWrapper(AdvancedCharacterWrapper acw) {
//        String txt = "";
//        txt += getStatTextFromCharacterWrapper("C", acw.getCommand());
//        txt += getStatTextFromCharacterWrapper("A", acw.getAgent());
//        txt += getStatTextFromCharacterWrapper("E", acw.getEmmisary());
//        txt += getStatTextFromCharacterWrapper("M", acw.getMage());
//        txt += getStatTextFromCharacterWrapper("S", acw.getStealth());
//        txt += getStatTextFromCharacterWrapper("Cr", acw.getChallenge());
//        txt += getStatTextFromCharacterWrapper("H", acw.getHealth());
//        if (acw.getDeathReason() != null && acw.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
//            txt += " (" + acw.getDeathReason().toString() + ")";
//        }
//        return txt;
//    }

    private String getStatLine(Character c) {
        String txt = "";
        txt += getStatText("C", c.getCommand(), c.getCommandTotal());
        txt += getStatText("A", c.getAgent(), c.getAgentTotal());
        txt += getStatText("E", c.getEmmisary(), c.getEmmisaryTotal());
        txt += getStatText("M", c.getMage(), c.getMageTotal());
        txt += getStatText("S", c.getStealth(), c.getStealthTotal());
        txt += getStatText("Cr", c.getChallenge(), c.getChallenge());
        txt += (c.getHealth() == null ? "" : "H" + c.getHealth());
        if (c.getDeathReason() != null && c.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
            txt += " (" + c.getDeathReason().toString() + ")";
        }
        return txt;
    }

    private String getStatText(String prefix, int skill, int skillTotal) {
        if (skillTotal == 0 && skill == 0)
            return "";
        return prefix + skill + (skillTotal > skill ? "(" + skillTotal + ")" : "") + " ";
    }


    protected JComponent createFormControl() {
        getFormModel().setValidating(false);
        BindingFactory bf = getBindingFactory();
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));

        JComponent c;

        glb.append(c = new JTextField());
        characterName = (JTextField) c;
        characterName.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                characterName.setTransferHandler(new CharIdTransferHandler("text"));
                TransferHandler handler = characterName.getTransferHandler();
                handler.exportAsDrag(characterName, e, TransferHandler.COPY);
            }
        });
        c.setBorder(null);
        c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
        c.setPreferredSize(new Dimension(160, 12));
        
        glb.append(c = new JTextField());
        c.setBorder(null);
        nationTextBox = (JTextField) c;
        // bf.bindControl(c, "nationNo");
        c.setPreferredSize(new Dimension(50, 12));

        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");

        btnMenu = new JButton();
        Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
        btnMenu.setIcon(ico);
        btnMenu.setPreferredSize(new Dimension(16, 16));
        glb.append(btnMenu);
        btnMenu.addActionListener(new PopupMenuActionListener() {

            public JPopupMenu getPopupMenu() {
                return createCharacterPopupContextMenu();
            }
        });
        
        btnMenu = new JButton();
        ico = new ImageIcon(imgSource.getImage("showCharDetails.icon"));
        btnMenu.setIcon(ico);
        btnMenu.setPreferredSize(new Dimension(16, 16));
        glb.append(btnMenu);
        btnMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                showArtifacts = !showOrders;
                showSpells = !showOrders;
                showOrders = !showOrders;
                reset((Character)getFormObject());
            }
            
        });

        glb.nextLine();

        glb.append(statsTextBox = new JTextField(), 2, 1);
        statsTextBox.setBorder(null);
        statsTextBox.setPreferredSize(new Dimension(100, 12));
        glb.nextLine();
        
        glb.append(companyMembersTextBox = new JTextField(), 3, 1);
        companyMembersTextBox.setBorder(null);
        companyMembersTextBox.setPreferredSize(new Dimension(150, 12));
        glb.nextLine();
        
        glb.append(infoSourcesTextBox = new JTextField(), 2, 1);
        infoSourcesTextBox.setBorder(null);
        infoSourcesTextBox.setPreferredSize(new Dimension(100, 12));
        glb.nextLine();

        glb.append(artifactsTable = new JTable(), 2, 1);
        artifactsTable.setPreferredSize(new Dimension(150, 20));
        final ArtifactInfoTableModel tableModel = new ArtifactInfoTableModel(this.getMessageSource()) {

            protected String[] createColumnPropertyNames() {
                return new String[] {"no", "name"};
            }

            protected Class[] createColumnClasses() {
                return new Class[] {String.class, String.class};
            }
        };
        
        artifactsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	if (e.getClickCount() == 1) {
	                ArtifactInfo a = (ArtifactInfo)tableModel.getRow(artifactsTable.getSelectedRow());
	                if (a == null) return;
	                TransferHandler handler = new ParamTransferHandler(a.getNo());
	                artifactsTable.setTransferHandler(handler);
	                handler.exportAsDrag(artifactsTable, e, TransferHandler.COPY);
            	} else if (e.getClickCount() == 2&& e.getButton() == MouseEvent.BUTTON1) {
                    ArtifactInfo a = (ArtifactInfo)tableModel.getRow(artifactsTable.getSelectedRow());
                    if (a == null) return;
                    final String descr = "#" + a.getNo() + " - " + a.getName() + "\n" +
                                    a.getAlignment() + "\n" +
                                    a.getPower1() + ", " + a.getPower2(); 
                    MessageDialog dlg = new MessageDialog("Artifact Info", descr);
                    dlg.showDialog();
                }
            }

			
        });
        
        tableModel.setRowNumbers(false);
        artifactsTable.setModel(tableModel);
        // todo think about this
        TableUtils.setTableColumnWidths(artifactsTable, new int[] {30, 120});
        artifactsTable.setBorder(null);

        glb.nextLine();

        glb.append(spellsTable = new JTable(), 2, 1);
        spellsTable.setPreferredSize(new Dimension(150, 12));
        final ItemTableModel spellModel = new ItemTableModel(SpellProficiency.class, this.getMessageSource()) {

            protected String[] createColumnPropertyNames() {
                return new String[] {"spellId", "name", "proficiency"};
            }

            protected Class[] createColumnClasses() {
                return new Class[] {Integer.class, String.class, String.class};
            }
        };
        spellModel.setRowNumbers(false);
        spellsTable.setModel(spellModel);
        TableUtils.setTableColumnWidths(spellsTable, new int[] {30, 90, 30});
        spellsTable.setBorder(null);
        
        
//        spellsTable.addMouseListener(new MouseAdapter() {
//            public void mousePressed(MouseEvent e) {
//                SpellProficiency sp = (SpellProficiency)tableModel.getRow(spellsTable.getSelectedRow());
//                if (sp == null) return;
//                TransferHandler handler = new ParamTransferHandler(sp.getSpellId());
//                spellsTable.setTransferHandler(handler);
//                handler.exportAsDrag(spellsTable, e, TransferHandler.COPY);
//            }
//        });
        spellsTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2&& e.getButton() == MouseEvent.BUTTON1) {
                    SpellProficiency sp = (SpellProficiency)spellModel.getRow(spellsTable.getSelectedRow());
                    if (sp == null) return;
                    
                    Game g = GameHolder.instance().getGame();
                    SpellInfo si = (SpellInfo)g.getMetadata().getSpells().findFirstByProperty("number", sp.getSpellId());
                    
                    String descr = si.getNumber() + " - " + si.getName() + "\n" +
                                    "Description: " + si.getDescription() + "\n" +
                                    "Difficulty: " + si.getDifficulty() + "\n" +
                                    "Required Info: " + si.getRequiredInfo() + "\n" +
                                    "Requirements: " + si.getRequirements() + "\n" +
                                    "Order: " + si.getOrderNumber();
                    
                    MessageDialog dlg = new MessageDialog("Spell Info", descr);
                    dlg.showDialog();
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });
        
        glb.nextLine();

        TableLayoutBuilder tlb = new TableLayoutBuilder();
        order1 = new OrderViewer(FormModelHelper.createFormModel(new Order(new Character())));
        tlb.cell(order1comp = order1.createFormControl());
        tlb.row();
        order2 = new OrderViewer(FormModelHelper.createFormModel(new Order(new Character())));
        tlb.cell(order2comp = order2.createFormControl());
        tlb.row();
        orderPanel = tlb.getPanel();
        orderPanel.setBackground(Color.white);
        glb.append(orderPanel, 2, 1);
        glb.append(swapOrdersIconCmd = new JLabel(new ImageIcon(imgSource.getImage("swapOrders.icon"))));
        swapOrdersIconCmd.setToolTipText("Swap orders");
        
        swapOrdersIconCmd.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Character c = (Character)getFormObject();
				c.setOrders(new Order[]{c.getOrders()[1], c.getOrders()[0]});
		          Application.instance().getApplicationContext().publishEvent(
		                  new JOverseerEvent(LifecycleEventsEnum.RefreshHexItems.toString(), MapPanel.instance().getSelectedHex(), this));
			}
        });
        
        glb.nextLine();

        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }

    private void refresh() {
        setFormObject(getFormObject());
    }

    private class ShowArtifactsCommand extends ActionCommand {

        protected void doExecuteCommand() {
            showArtifacts = !showArtifacts;
            refresh();
        }
    }

    private class ShowSpellsCommand extends ActionCommand {

        protected void doExecuteCommand() {
            showSpells = !showSpells;
            refresh();
        }
    }

    private class ShowOrdersCommand extends ActionCommand {

        protected void doExecuteCommand() {
            showOrders = !showOrders;
            refresh();
        }
    }

    private class ShowResultsCommand extends ActionCommand {

        protected void doExecuteCommand() {
        	final OrderResultsForm f = new OrderResultsForm(FormModelHelper.createFormModel(getFormObject()));
        	FormBackedDialogPage pg = new FormBackedDialogPage(f);
        	TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
		protected boolean onFinish() {
			return true;
		}

		protected void onAboutToShow() {
			super.onAboutToShow();
			f.setFormObject(getFormObject());
		}
		
		protected Object[] getCommandGroupMembers() {
                    return new AbstractCommand[] {
                            getFinishCommand()
                    };
                }

        	};
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            dlg.setTitle(ms.getMessage("orderResultsDialog.title", new Object[]{}, Locale.getDefault()));
        	dlg.showDialog();
        	
        }
    }

    
    private class AddRefuseChallengeCommand extends AddOrderCommand {
        public AddRefuseChallengeCommand() {
            super(215, "");
        }
    }
    
    private class AddCreateCampCommand extends AddOrderCommand {
        public AddCreateCampCommand() {
            super(555, "");
        }
    }
    
    private class AddInfYourCommand extends AddOrderCommand {
        public AddInfYourCommand() {
            super(520, "");
        }
    }
    
    private class AddInfOtherCommand extends AddOrderCommand {
        public AddInfOtherCommand() {
            super(525, "");
        }
    }
    
    private class AddPrenticeCommand extends AddOrderCommand {
        public AddPrenticeCommand() {
            super(710, "");
        }
    }
    
    private class AddReconCommand extends AddOrderCommand {
        public AddReconCommand() {
            super(925, "");
        }
    }
    
    private class AddImprovePopCommand extends AddOrderCommand {
        public AddImprovePopCommand() {
            super(550, "");
        }
    }
    
    
    private class AddOrderCommand extends ActionCommand {
        int orderNo;
        String params;
        
        public AddOrderCommand(int orderNo, String params) {
            super();
            this.orderNo = orderNo;
            this.params = params;
        }

        protected void doExecuteCommand() {
            Character c = (Character)getFormObject();
            for (int i=0; i<2; i++) {
                if (c.getOrders()[i].isBlank()) {
                    c.getOrders()[i].setOrderNo(orderNo);
                    c.getOrders()[i].setParameters(params);
                    setFormObject(getFormObject());
                    showOrders = true;
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), c.getOrders()[i], this));
                    return;
                }
            }
        }
        
    }
    
    private class DeleteCharacterCommand extends ActionCommand {
        protected void doExecuteCommand() {
            Character c = (Character)getFormObject();
            Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            Turn t = g.getTurn();
            Container armies = t.getContainer(TurnElementsEnum.Army);
            Army a = (Army)armies.findFirstByProperty("commanderName", c.getName());
            if (a != null) {
                MessageDialog dlg = new MessageDialog("Error", "The character commands an army and cannot be deleted.\nDelete the army first if you want to delete the character.");
                dlg.showDialog();
                return;
            }
            Container characters = t.getContainer(TurnElementsEnum.Character);
            characters.removeItem(c);
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance()
                            .getSelectedHex(), this));
        }
    }
    
    private JPopupMenu createCharacterPopupContextMenu() {
        Character c = (Character) getFormObject();
        ActionCommand showCharacterRangeOnMapCommand = new ShowCharacterMovementRangeCommand(c.getHexNo(), 12);
        ActionCommand showCharacterLongStrideRangeCommand = new ShowCharacterLongStrideRangeCommand(c.getHexNo());
        ActionCommand showCharacterFastStrideRangeCommand = new ShowCharacterFastStrideRangeCommand(c.getHexNo());
        boolean hasFastStride = false;
        boolean hasLongStride = false;
        for (SpellProficiency sp : c.getSpells()) {
        	if (sp.getSpellId() == 304) {
        		hasFastStride = true;
        	}
        	if (sp.getSpellId() == 302) {
        		hasLongStride = true;
        	}
        }
        showCharacterFastStrideRangeCommand.setEnabled(hasFastStride);
        showCharacterLongStrideRangeCommand.setEnabled(hasLongStride);
        
        showArtifactsCommand.setEnabled(c != null);
        showSpellsCommand.setEnabled(c != null && c.getSpells().size() > 0);
        showResultsCommand.setEnabled(c != null && c.getOrderResults() != null && !c.getOrderResults().equals(""));
        
        CommandGroup quickOrders = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("quickOrdersCommandGroup",
                new Object[]{
                new AddRefuseChallengeCommand(),
                "separator",
                new AddReconCommand(),
                "separator",
                new AddCreateCampCommand(),
                new AddInfYourCommand(),
                new AddInfOtherCommand(),
                new AddImprovePopCommand(),
                "separator",
                new AddPrenticeCommand()
        });
        
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "armyCommandGroup",
                new Object[] {showArtifactsCommand, showSpellsCommand, showOrdersCommand, showResultsCommand,
                        "separator", showCharacterRangeOnMapCommand, showCharacterFastStrideRangeCommand, showCharacterLongStrideRangeCommand, "separator", deleteCharacterCommand,
                        "separator", quickOrders});
        return group.createPopupMenu();
    }
    
    public boolean getShowColor() {
        return showColor;
    }

    
    public void setShowColor(boolean showColor) {
        this.showColor = showColor;
    }
    

}
