package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.joverseer.domain.Army;
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
import org.joverseer.support.infoSources.DoubleAgentInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromRevealCharacterInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.NarrationForm;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.CharacterRangeMapItem;
import org.joverseer.ui.listviews.ArtifactInfoTableModel;
import org.joverseer.ui.listviews.ItemTableModel;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.ColorPicker;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.PopupMenuActionListener;
import org.joverseer.ui.support.TableUtils;
import org.joverseer.ui.support.transferHandlers.CharIdTransferHandler;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
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
import org.springframework.richclient.table.BeanTableModel;


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

    JButton btnMenu;

    JComponent order1comp;
    JComponent order2comp;
    OrderViewer order1;
    OrderViewer order2;

    boolean showArtifacts = false;
    boolean showSpells = false;
    boolean showOrders = false;

    ActionCommand showArtifactsCommand = new ShowArtifactsCommand();
    ActionCommand showSpellsCommand = new ShowSpellsCommand();
    ActionCommand showOrdersCommand = new ShowOrdersCommand();
    ActionCommand showResultsCommand = new ShowResultsCommand();
    ActionCommand showCharacterRangeOnMapCommand = new ShowCharacterRangeOnMapCommand();
    ActionCommand deleteCharacterCommand = new DeleteCharacterCommand();


    public CharacterViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }
    
    public boolean appliesTo(Object obj) {
        return Character.class.isInstance(obj);
    }

    public void setFormObject(Object object) {
        boolean showStartingInfo = false;
        Character startingChar = null;
        Character c = (Character) object;
        if (object != getFormObject()) {
            showArtifacts = false;
            showOrders = !c.getOrders()[0].isBlank() || !c.getOrders()[1].isBlank();
            showSpells = false;
        }
        super.setFormObject(object);
        if (object == null)
            return;
        if (statsTextBox != null) {
            characterName.setCaretPosition(0);

            String txt = getStatLine(c);
            
            if (txt.equals("")) {
                // character is enemy
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
            
            ArrayList artis = new ArrayList();
            if (showArtifacts) {
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
                order1comp.setVisible(true);
                order2comp.setVisible(true);
            } else {
                order1comp.setVisible(false);
                order2comp.setVisible(false);
            }
            
            if (getShowColor()) {
                Game g = GameHolder.instance().getGame();
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
        characterName.setTransferHandler(new CharIdTransferHandler("text"));
        characterName.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                TransferHandler handler = characterName.getTransferHandler();
                handler.exportAsDrag(characterName, e, TransferHandler.COPY);
            }
        });
        c.setBorder(null);
        c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
        c.setPreferredSize(new Dimension(160, 12));
        
        bf.bindControl(c, "name");
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
        
        artifactsTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2&& e.getButton() == MouseEvent.BUTTON1) {
                    ArtifactInfo a = (ArtifactInfo)tableModel.getRow(artifactsTable.getSelectedRow());
                    if (a == null) return;
                    final String descr = "#" + a.getNo() + " - " + a.getName() + "\n" +
                                    a.getAlignment() + "\n" +
                                    a.getPower1() + ", " + a.getPower2(); 
                    MessageDialog dlg = new MessageDialog("Artifact Info", descr);
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

        order1 = new OrderViewer(FormModelHelper.createFormModel(new Order(new Character())));
        glb.append(order1comp = order1.createFormControl(), 2, 1);
        glb.nextLine();
        order2 = new OrderViewer(FormModelHelper.createFormModel(new Order(new Character())));
        glb.append(order2comp = order2.createFormControl(), 2, 1);
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
            Character c = (Character) getFormObject();
            String result = c.getName() + "\n" + c.getOrderResults().replaceAll("\n", "");
            result = result.replaceAll(" He was ordered", "\nHe was ordered");
            result = result.replaceAll(" She was ordered", "\nShe was ordered");

            MessageDialog msg = new MessageDialog("Order results", result);
            msg.showDialog();
        }
    }

    private class ShowCharacterRangeOnMapCommand extends ActionCommand {

        protected void doExecuteCommand() {
            CharacterRangeMapItem crmi = new CharacterRangeMapItem((Character) getFormObject());
            AbstractMapItem.add(crmi);

            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance()
                            .getSelectedHex(), this));
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
                    new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance()
                            .getSelectedHex(), this));
        }
    }

    private JPopupMenu createCharacterPopupContextMenu() {
        Character c = (Character) getFormObject();
        showArtifactsCommand.setEnabled(c != null && c.getArtifacts().size() > 0);
        showSpellsCommand.setEnabled(c != null && c.getSpells().size() > 0);
        showResultsCommand.setEnabled(c != null && c.getOrderResults() != null && !c.getOrderResults().equals(""));
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "armyCommandGroup",
                new Object[] {showArtifactsCommand, showSpellsCommand, showOrdersCommand, showResultsCommand,
                        "separator", showCharacterRangeOnMapCommand, "separator", deleteCharacterCommand});
        return group.createPopupMenu();
    }
    
    public boolean getShowColor() {
        return showColor;
    }

    
    public void setShowColor(boolean showColor) {
        this.showColor = showColor;
    }
    

}
