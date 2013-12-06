package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.Company;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.Note;
import org.joverseer.domain.Order;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.InfoUtils;
import org.joverseer.support.infoSources.DoubleAgentInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromRevealCharacterInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.tools.infoCollectors.characters.AdvancedCharacterWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterInfoCollector;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.command.AddEditNoteCommand;
import org.joverseer.ui.command.ShowCharacterFastStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterLongStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterMovementRangeCommand;
import org.joverseer.ui.command.ShowCharacterPathMasteryRangeCommand;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HexArrowMapItem;
import org.joverseer.ui.listviews.ArtifactInfoTableModel;
import org.joverseer.ui.listviews.ItemTableModel;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.orderEditor.OrderEditorAutoNations;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.commands.DialogsUtility;
import org.joverseer.ui.support.commands.ShowInfoSourcePopupCommand;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.controls.TableUtils;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.ui.support.transferHandlers.ArtifactInfoExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.CharacterExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.ParamTransferHandler;
import org.joverseer.ui.views.EditCharacterForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;

/**
 * Shows characters in the Current Hex View
 * 
 * @author Marios Skounakis
 */
public class CharacterViewer extends ObjectViewer {

	public static final String FORM_PAGE = "CharacterViewer";

	boolean showColor = true;

	JTextField characterName;
	JTextField statsTextBox;
	JTextField infoSourcesTextBox;
	JTextField nationTextBox;
	JTextField companyMembersTextBox;
	JTextArea notes;
	JTextArea hostages;
	JScrollPane hostagesPane;
	JScrollPane notesPane;
	NotesViewer notesViewer;

	JTable artifactsTable;
	JTable spellsTable;

	JLabelButton swapOrdersIconCmd;

	JButton btnMenu;

	JComponent order1comp;
	JComponent order2comp;
	JComponent order3comp;
	OrderViewer order1;
	OrderViewer order2;
	OrderViewer order3;
	JPanel orderPanel;

	boolean showArtifacts = false;
	boolean showSpells = false;
	boolean showOrders = false;
	boolean showHostages = false;

	ActionCommand showArtifactsCommand = new ShowArtifactsCommand();
	ActionCommand showSpellsCommand = new ShowSpellsCommand();
	ActionCommand showOrdersCommand = new ShowOrdersCommand();
	ActionCommand showResultsCommand = new ShowResultsCommand();
	ActionCommand deleteCharacterCommand = new DeleteCharacterCommand();
	ActionCommand addRefuseChallengesCommand = new AddOrderCommand(215, "");
	ActionCommand editCharacterCommand = new EditCharacterCommand();
	ActionCommand sendOrdersByChatCommand = new SendOrdersByChatCommand();

	public CharacterViewer(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	@Override
	public boolean appliesTo(Object obj) {
		return Character.class.isInstance(obj);
	}

	@Override
	public void setFormObject(Object object) {
		Character c = (Character) object;
		if (object != getFormObject()) {
			this.showArtifacts = false;
			this.showOrders = !c.getOrders()[0].isBlank() || !c.getOrders()[1].isBlank() || !c.getOrders()[0].isBlank() || OrderEditorAutoNations.instance().containsNation(c.getNationNo());
			resetOrderPanel(c.getNumberOfOrders());
			this.showSpells = false;
			this.showHostages = false;
		}
		super.setFormObject(object);
		if (object == null)
			return;
		reset(c);
	}

	private void resetOrderPanel(int numberOfOrders) {
		this.orderPanel.removeAll();
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(this.order1comp, "rowspec=top:20px");
		tlb.row();
		tlb.cell(this.order2comp, "rowspec=top:20px");
		tlb.row();
		if (numberOfOrders == 3) {
			tlb.cell(this.order3comp, "rowspec=top:20px");
			tlb.row();
		}
		JPanel pnl = tlb.getPanel();
		pnl.setBackground(Color.white);
		this.orderPanel.add(pnl);
	}

	public void reset(Character c) {
		Game g = GameHolder.instance().getGame();

		boolean showStartingInfo = false;
		ArrayList<ArtifactInfo> artis = new ArrayList<ArtifactInfo>();
		Character startingChar = null;
		this.characterName.setText(GraphicUtils.parseName(c.getName()));
		this.characterName.setTransferHandler(new CharacterExportTransferHandler(c));
		if (this.statsTextBox != null) {
			this.characterName.setCaretPosition(0);
			String txt = c.getStatString();

			if (txt.equals("")) {
				// character is enemy

				// retrieve info from CharacterInfoCollector if possible
				AdvancedCharacterWrapper acw = CharacterInfoCollector.instance().getCharacterForTurn(c.getName(), g.getCurrentTurn());
				if (acw != null) {
					startingChar = new Character();
					startingChar.setName(acw.getName());
					startingChar.setId(acw.getId());
					startingChar.setNationNo(acw.getNationNo());
					txt = getStatLineFromCharacterWrapper(acw);
					if (!txt.equals("")) {
						txt += "(collected info";
						if (acw.getStartChar()) {
							txt += " - start char";
						}
						txt += ")";
					}
					// artifacts
					if (this.showArtifacts) {
						for (ArtifactWrapper aw : acw.getArtifacts()) {
							// find artifact in metadata
							ArtifactInfo a = g.getMetadata().getArtifacts().findFirstByProperty("no", aw.getNumber());
							// copy into new object to change the name and add
							// the turn - hack but for now it works
							ArtifactInfo na = new ArtifactInfo();
							na.setNo(a.getNo());
							na.setAlignment(a.getAlignment());
							na.setOwner(acw.getName());
							na.setPowers(a.getPowers());
							na.setName(a.getName() + " (t" + aw.getTurnNo() + ")");
							artis.add(na);
						}
					}

					showStartingInfo = true;
				} else {
					// retrieve starting info
					Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
					GameMetadata gm = game.getMetadata();
					Container<?> startChars = gm.getCharacters();
					if (startChars != null) {
						startingChar = (Character) startChars.findFirstByProperty("id", c.getId());
						showStartingInfo = true;
						if (startingChar != null) {
							txt = startingChar.getStatString() + "(start info)";
						}
					}
				}
			}
			this.statsTextBox.setText(txt);
			this.statsTextBox.setCaretPosition(0);

			// show info sources, if needed
			InfoSource is = c.getInfoSource();
			if (DerivedFromLocateArtifactInfoSource.class.isInstance(is) || DerivedFromRevealCharacterInfoSource.class.isInstance(is)) {
				DerivedFromSpellInfoSource sis = (DerivedFromSpellInfoSource) is;
				String infoSourcesStr = sis.getSpell() + " at " + sis.getHexNo();
				this.infoSourcesTextBox.setVisible(true);
				for (InfoSource dsis : sis.getOtherInfoSources()) {
					if (DerivedFromSpellInfoSource.class.isInstance(dsis)) {
						infoSourcesStr += ", " + ((DerivedFromSpellInfoSource) dsis).getSpell() + " at " + ((DerivedFromSpellInfoSource) dsis).getHexNo();
					}
				}
				this.infoSourcesTextBox.setText(infoSourcesStr);
			} else if (DoubleAgentInfoSource.class.isInstance(is)) {
				this.infoSourcesTextBox.setVisible(true);
				// TODO show nation name
				this.infoSourcesTextBox.setText("Double agent for nation " + ((DoubleAgentInfoSource) is).getNationNo());
			} else {
				this.infoSourcesTextBox.setVisible(false);
			}

			Font f = GraphicUtils.getFont(this.statsTextBox.getFont().getName(), (showStartingInfo ? Font.ITALIC : Font.PLAIN), this.statsTextBox.getFont().getSize());
			this.statsTextBox.setFont(f);

			Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
			if (game == null)
				return;
			GameMetadata gm = game.getMetadata();
			int nationNo = (showStartingInfo && startingChar != null ? startingChar.getNationNo() : c.getNationNo());
			Nation charNation = gm.getNationByNum(nationNo);
			String nationShortName = charNation.getShortName();
			String nationName = charNation.getName();
			if (nationNo == 0) {
				// check if it is a dragon
				if (InfoUtils.isDragon(c.getName())) {
					nationShortName = "Dragon";
					nationName = "This character is a dragon";
				}
			}
			this.nationTextBox.setText(nationShortName);
			this.nationTextBox.setCaretPosition(0);
			this.nationTextBox.setToolTipText(nationName);
			if (this.showArtifacts && !showStartingInfo) {
				ArrayList<Integer> artifacts = (!showStartingInfo ? c.getArtifacts() : startingChar != null ? startingChar.getArtifacts() : null);
				if (artifacts != null) {
					for (Integer no : artifacts) {
						ArtifactInfo arti = gm.getArtifacts().findFirstByProperty("no", no);
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

			((BeanTableModel) this.artifactsTable.getModel()).setRows(artis);
			this.artifactsTable.setPreferredSize(new Dimension(180, 16 * artis.size()));
			TableUtils.setTableColumnWidths(this.artifactsTable, new int[] { 10, 120, 40 });

			ArrayList<SpellProficiency> spells = new ArrayList<SpellProficiency>();
			if (this.showSpells) {
				spells.addAll(c.getSpells());
			}
			((BeanTableModel) this.spellsTable.getModel()).setRows(spells);
			this.spellsTable.setPreferredSize(new Dimension(this.spellsTable.getWidth(), 16 * spells.size()));
			TableUtils.setTableColumnWidths(this.spellsTable, new int[] { 10, 120, 40 });
			Container<Company> companies = game.getTurn().getCompanies();
			Company company = companies.findFirstByProperty("commander", c.getName());
			if (company != null) {
				String members = company.getMemberStr();

				this.companyMembersTextBox.setText("Company: " + members);
				this.companyMembersTextBox.setCaretPosition(0);
				this.companyMembersTextBox.setVisible(true);
				this.companyMembersTextBox.setFont(GraphicUtils.getFont(this.companyMembersTextBox.getFont().getName(), Font.ITALIC, this.companyMembersTextBox.getFont().getSize()));
			} else {
				// check if he is travelling with a company or an army...
				boolean found = false;
				for (Company comp : companies.getItems()) {
					if (comp.getMembers().contains(c.getName())) {
						this.companyMembersTextBox.setVisible(true);
						this.companyMembersTextBox.setText("In " + comp.getCommander() + "'s company");
						this.companyMembersTextBox.setCaretPosition(0);
						this.companyMembersTextBox.setFont(GraphicUtils.getFont(this.companyMembersTextBox.getFont().getName(), Font.ITALIC, this.companyMembersTextBox.getFont().getSize()));
						found = true;
						break;
					}
				}
				if (!found) {
					if (c.getNationNo() != null && c.getNationNo() > 0) {
						ArrayList<Army> armies = game.getTurn().getArmies().findAllByProperty("nationNo", c.getNationNo());
						for (Army a : armies) {
							if (a.getCharacters().contains(c.getName())) {
								this.companyMembersTextBox.setVisible(true);
								this.companyMembersTextBox.setText("In " + a.getCommanderName() + "'s army");
								this.companyMembersTextBox.setCaretPosition(0);
								this.companyMembersTextBox.setFont(GraphicUtils.getFont(this.companyMembersTextBox.getFont().getName(), Font.ITALIC, this.companyMembersTextBox.getFont().getSize()));
								found = true;
								break;
							}
						}
					}
				}
				if (!found) {
					this.companyMembersTextBox.setVisible(false);
				}

			}
			refreshOrders(c);

			if (getShowColor()) {
				Turn t = g.getTurn();

				NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", nationNo);
				Color col;
				if (nr == null) {
					col = ColorPicker.getInstance().getColor(NationAllegianceEnum.Neutral.toString());
				} else {
					col = ColorPicker.getInstance().getColor(nr.getAllegiance().toString());
				}
				this.characterName.setForeground(col);
			}
			if (this.showHostages && c.getHostages().size() > 0) {
				this.hostagesPane.setVisible(true);
				String htxt = "";
				for (String h : c.getHostages()) {
					htxt += (htxt.equals("") ? "" : ", ") + h;
					Character hostage = (Character) g.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", h);
					if (hostage != null) {
						Integer hNationNo = hostage.getNationNo();
						String hNationName = gm.getNationByNum(hNationNo).getShortName();
						htxt += "(" + hNationName + ")";
					}
				}
				this.hostages.setText(("Hostages: " + htxt));
			} else {
				this.hostagesPane.setVisible(false);
			}

			ArrayList<Note> notes1 = g.getTurn().getNotes().findAllByProperty("target", c);
			this.notesViewer.setFormObject(notes1);
			this.characterName.setCaretPosition(0);
		}

	}

	public void refreshOrders(Character c) {
		this.order1.setFormObject(c.getOrders()[0]);
		this.order2.setFormObject(c.getOrders()[1]);
		this.order3.setFormObject(c.getOrders()[2]);
		if (this.showOrders) {
			this.orderPanel.setVisible(true);
			if (c.getNumberOfOrders() == 3) {
				this.order3comp.setVisible(true);
			} else {
				this.order3comp.setVisible(false);
			}
			this.swapOrdersIconCmd.setVisible(true);
		} else {
			this.orderPanel.setVisible(false);
			this.swapOrdersIconCmd.setVisible(false);
		}

	}

	public String getStatLineFromCharacterWrapper(AdvancedCharacterWrapper acw) {
		return acw.getStatString();

	}

	@Override
	protected JComponent createFormControl() {
		getFormModel().setValidating(false);
		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		glb.setDefaultInsets(new Insets(0, 0, 0, 5));

		JComponent c;

		glb.append(c = new JTextField());
		this.characterName = (JTextField) c;
		this.characterName.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// characterName.setTransferHandler(new
				// CharIdTransferHandler("text"));
				TransferHandler handler = CharacterViewer.this.characterName.getTransferHandler();
				handler.exportAsDrag(CharacterViewer.this.characterName, e, TransferHandler.COPY);
			}
		});
		c.setBorder(null);
		c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));

		c.setPreferredSize(new Dimension(160, 12));

		glb.append(c = new JTextField());
		c.setBorder(null);
		this.nationTextBox = (JTextField) c;
		// bf.bindControl(c, "nationNo");
		c.setPreferredSize(new Dimension(50, 12));

		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");

		final JButton btnMainMenu = new JButton();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
		btnMainMenu.setIcon(ico);
		btnMainMenu.setPreferredSize(new Dimension(16, 16));
		glb.append(btnMainMenu);
		btnMainMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				return createCharacterPopupContextMenu();
			}
		});

		final JButton btnCharDetails = new JButton();
		ico = new ImageIcon(imgSource.getImage("showCharDetails.icon"));
		btnCharDetails.setIcon(ico);
		btnCharDetails.setPreferredSize(new Dimension(16, 16));
		glb.append(btnCharDetails);
		btnCharDetails.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CharacterViewer.this.showArtifacts = !CharacterViewer.this.showOrders;
				CharacterViewer.this.showSpells = !CharacterViewer.this.showOrders;
				CharacterViewer.this.showHostages = !CharacterViewer.this.showOrders;
				CharacterViewer.this.showOrders = !CharacterViewer.this.showOrders;
				reset((Character) getFormObject());
			}

		});

		glb.nextLine();

		glb.append(this.statsTextBox = new JTextField(), 2, 1);
		this.statsTextBox.setBorder(null);
		this.statsTextBox.setPreferredSize(new Dimension(100, 12));
		glb.nextLine();

		glb.append(this.companyMembersTextBox = new JTextField(), 3, 1);
		this.companyMembersTextBox.setBorder(null);
		this.companyMembersTextBox.setPreferredSize(new Dimension(150, 12));
		Font f = GraphicUtils.getFont(this.companyMembersTextBox.getFont().getName(), Font.ITALIC, 11);
		this.companyMembersTextBox.setFont(f);
		glb.nextLine();

		glb.append(this.infoSourcesTextBox = new JTextField(), 2, 1);
		this.infoSourcesTextBox.setBorder(null);
		this.infoSourcesTextBox.setPreferredSize(new Dimension(100, 12));
		glb.nextLine();

		glb.append(this.artifactsTable = new JTable(), 2, 1);
		this.artifactsTable.setPreferredSize(new Dimension(150, 20));
		final ArtifactInfoTableModel tableModel = new ArtifactInfoTableModel(this.getMessageSource()) {

			@Override
			protected String[] createColumnPropertyNames() {
				return new String[] { "no", "name", "stats" };
			}

			@Override
			protected Class<?>[] createColumnClasses() {
				return new Class[] { String.class, String.class, String.class };
			}

		};

		this.artifactsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 1) {
					if (CharacterViewer.this.artifactsTable.getSelectedRow() < 0)
						return;
					ArtifactInfo a = (ArtifactInfo) tableModel.getRow(CharacterViewer.this.artifactsTable.getSelectedRow());
					if (a == null)
						return;
					TransferHandler handler = new ArtifactInfoExportTransferHandler(a);
					CharacterViewer.this.artifactsTable.setTransferHandler(handler);
					handler.exportAsDrag(CharacterViewer.this.artifactsTable, e, TransferHandler.COPY);
				} else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					if (CharacterViewer.this.artifactsTable.getSelectedRow() < 0)
						return;
					ArtifactInfo a = (ArtifactInfo) tableModel.getRow(CharacterViewer.this.artifactsTable.getSelectedRow());
					if (a == null)
						return;
					final String descr = "#" + a.getNo() + " - " + a.getName() + "\n" + a.getAlignment() + "\n" + a.getPower1() + ", " + a.getPower2();
					MessageDialog dlg = new MessageDialog("Artifact Info", descr);
					dlg.showDialog();
				}
			}

		});

		tableModel.setRowNumbers(false);
		this.artifactsTable.setModel(tableModel);
		TableUtils.setTableColumnWidths(this.artifactsTable, new int[] { 16, 100, 100 });
		this.artifactsTable.setBorder(null);

		glb.nextLine();
		glb.append(new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(20, 4);
			}
		});
		glb.nextLine();

		glb.append(this.spellsTable = new JTable(), 2, 1);
		this.spellsTable.setPreferredSize(new Dimension(150, 12));
		final ItemTableModel spellModel = new ItemTableModel(SpellProficiency.class, this.getMessageSource()) {

			@Override
			protected String[] createColumnPropertyNames() {
				return new String[] { "spellId", "name", "proficiency" };
			}

			@Override
			protected Class<?>[] createColumnClasses() {
				return new Class[] { String.class, String.class, String.class };
			}
		};
		spellModel.setRowNumbers(false);
		this.spellsTable.setModel(spellModel);
		TableUtils.setTableColumnWidths(this.spellsTable, new int[] { 30, 90, 30 });
		this.spellsTable.setBorder(null);

		this.spellsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 1) {
					if (CharacterViewer.this.spellsTable.getSelectedRow() < 0)
						return;
					SpellProficiency sp = (SpellProficiency) spellModel.getRow(CharacterViewer.this.spellsTable.getSelectedRow());
					if (sp == null)
						return;
					TransferHandler handler = new ParamTransferHandler(sp.getSpellId());
					CharacterViewer.this.spellsTable.setTransferHandler(handler);
					handler.exportAsDrag(CharacterViewer.this.spellsTable, e, TransferHandler.COPY);
				} else if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					if (CharacterViewer.this.spellsTable.getSelectedRow() < 0)
						return;
					SpellProficiency sp = (SpellProficiency) spellModel.getRow(CharacterViewer.this.spellsTable.getSelectedRow());
					if (sp == null)
						return;

					Game g = GameHolder.instance().getGame();
					SpellInfo si = g.getMetadata().getSpells().findFirstByProperty("number", sp.getSpellId());

					String descr = si.getNumber() + " - " + si.getName() + "\n" + "Description: " + si.getDescription() + "\n" + "Difficulty: " + si.getDifficulty() + "\n" + "Required Info: " + si.getRequiredInfo() + "\n" + "Requirements: " + si.getRequirements() + "\n" + "Order: " + si.getOrderNumber();

					MessageDialog dlg = new MessageDialog("Spell Info", descr);
					dlg.showDialog();
				}
			}

		});

		glb.nextLine();

		this.hostages = new JTextArea();
		this.hostages.setWrapStyleWord(true);
		this.hostages.setLineWrap(true);
		this.hostagesPane = new JScrollPane(this.hostages);
		this.hostagesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		this.hostagesPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.hostagesPane.setBorder(BorderFactory.createEmptyBorder());
		glb.append(this.hostagesPane, 6, 1);
		glb.nextLine();

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		this.order1 = new OrderViewer(FormModelHelper.createFormModel(new Order(new Character())));
		tlb.cell(this.order1comp = this.order1.createFormControl(), "rowspec=top:20px");
		tlb.row();
		this.order2 = new OrderViewer(FormModelHelper.createFormModel(new Order(new Character())));
		tlb.cell(this.order2comp = this.order2.createFormControl(), "rowspec=top:20px");
		tlb.row();
		this.order3 = new OrderViewer(FormModelHelper.createFormModel(new Order(new Character())));
		tlb.cell(this.order3comp = this.order3.createFormControl(), "rowspec=top:20px");
		tlb.row();
		this.orderPanel = new JPanel();
		this.orderPanel.add(tlb.getPanel());
		this.orderPanel.setBackground(Color.white);
		glb.append(this.orderPanel, 2, 1);
		glb.append(this.swapOrdersIconCmd = new JLabelButton(new ImageIcon(imgSource.getImage("swapOrders.icon"))));
		this.swapOrdersIconCmd.setVerticalAlignment(SwingConstants.CENTER);
		this.swapOrdersIconCmd.setToolTipText("Swap orders");
		this.swapOrdersIconCmd.setPreferredSize(new Dimension(16, 16));
		this.swapOrdersIconCmd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Character c1 = (Character) getFormObject();
				c1.setOrders(new Order[] { c1.getOrders()[1], c1.getOrders()[0] });
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshHexItems.toString(), MapPanel.instance().getSelectedHex(), this));
			}
		});

		glb.nextLine();

		this.notesViewer = new NotesViewer(FormModelHelper.createFormModel(new ArrayList<Note>()));
		tlb = new TableLayoutBuilder();
		tlb.cell(this.notesViewer.createFormControl(), "colspec=left:285px");
		JPanel notesPanel = tlb.getPanel();
		notesPanel.setBackground(Color.white);
		glb.append(notesPanel, 6, 1);

		// notes = new JTextArea();
		// //rumor.setPreferredSize(new Dimension(220, 80));
		// notes.setLineWrap(true);
		// notes.setWrapStyleWord(true);
		// notesPane = new JScrollPane(notes);
		// notesPane.setMaximumSize(new Dimension(240, 36));
		// notesPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		// notesPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// notesPane.getVerticalScrollBar().setPreferredSize(new Dimension(16,
		// 10));
		// //rumor.setBorder(null);
		// notesPane.setBorder(null);
		// Font f = GraphicUtils.getFont(notes.getFont().getName(), Font.ITALIC,
		// notes.getFont().getSize());
		// notes.setFont(f);
		// notes.getDocument().addDocumentListener(new DocumentListener() {
		// public void changedUpdate(DocumentEvent arg0) {
		// updateNotes();
		// }
		//    
		// public void insertUpdate(DocumentEvent arg0) {
		// updateNotes();
		// }
		//    
		// public void removeUpdate(DocumentEvent arg0) {
		// updateNotes();
		// }
		// });
		// glb.append(notesPane, 5, 1);
		glb.nextLine();

		JPanel panel = glb.getPanel();
		panel.setFocusTraversalPolicyProvider(true);
		panel.setFocusTraversalPolicy(new FocusTraversalPolicy() {
			@Override
			public Component getComponentAfter(java.awt.Container aContainer, Component aComponent) {
				if (aComponent == CharacterViewer.this.characterName) {
					return btnMainMenu;
				} else if (aComponent == btnMainMenu) {
					return btnCharDetails;
				} else if (aComponent == btnCharDetails) {
					return CharacterViewer.this.order1comp.getFocusTraversalPolicy().getFirstComponent(CharacterViewer.this.order1comp);
				} else if (aComponent == CharacterViewer.this.order1comp.getFocusTraversalPolicy().getLastComponent(CharacterViewer.this.order1comp)) {
					return CharacterViewer.this.order2comp.getFocusTraversalPolicy().getFirstComponent(CharacterViewer.this.order2comp);
				} else if (aComponent == CharacterViewer.this.order2comp.getFocusTraversalPolicy().getLastComponent(CharacterViewer.this.order1comp)) {
					return CharacterViewer.this.characterName;
				}
				return CharacterViewer.this.characterName;
			}

			@Override
			public Component getComponentBefore(java.awt.Container aContainer, Component aComponent) {
				if (aComponent == CharacterViewer.this.characterName) {
					return CharacterViewer.this.order2comp.getFocusTraversalPolicy().getLastComponent(CharacterViewer.this.order2comp);
				} else if (aComponent == btnMainMenu) {
					return CharacterViewer.this.characterName;
				} else if (aComponent == btnCharDetails) {
					return btnMainMenu;
				} else if (aComponent == CharacterViewer.this.order1comp.getFocusTraversalPolicy().getFirstComponent(CharacterViewer.this.order1comp)) {
					return btnCharDetails;
				} else if (aComponent == CharacterViewer.this.order2comp.getFocusTraversalPolicy().getFirstComponent(CharacterViewer.this.order2comp)) {
					return CharacterViewer.this.order1comp.getFocusTraversalPolicy().getLastComponent(CharacterViewer.this.order1comp);
				}
				return CharacterViewer.this.characterName;
			}

			@Override
			public Component getDefaultComponent(java.awt.Container aContainer) {
				return CharacterViewer.this.characterName;
			}

			@Override
			public Component getFirstComponent(java.awt.Container aContainer) {
				return CharacterViewer.this.characterName;
			}

			@Override
			public Component getLastComponent(java.awt.Container aContainer) {
				return CharacterViewer.this.order2comp.isVisible() ? CharacterViewer.this.order2comp.getFocusTraversalPolicy().getLastComponent(CharacterViewer.this.order2comp) : btnCharDetails;
			}

		});

		panel.setBackground(Color.white);
		return panel;
	}

	private void refresh() {
		setFormObject(getFormObject());
	}

	private class ShowArtifactsCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			CharacterViewer.this.showArtifacts = !CharacterViewer.this.showArtifacts;
			refresh();
		}
	}

	private class ShowSpellsCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			CharacterViewer.this.showSpells = !CharacterViewer.this.showSpells;
			refresh();
		}
	}

	private class ShowOrdersCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			CharacterViewer.this.showOrders = !CharacterViewer.this.showOrders;
			refresh();
		}
	}

	private class ShowResultsCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			HexArrowMapItem hami = null;
			Character c = (Character) getFormObject();
			Game g = GameHolder.instance().getGame();
			Turn t = g.getTurn(g.getCurrentTurn() - 1);
			if (t == null)
				return;
			Character pc = t.getCharByName(c.getName());
			if (pc != null) {
				hami = new HexArrowMapItem(pc.getHexNo(), c.getHexNo(), Color.black);
				AbstractMapItem.add(hami);
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance().getSelectedHex(), this));
			}
			DialogsUtility.showCharacterOrderResults(c);
			if (hami != null) {
				AbstractMapItem.remove(hami);
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance().getSelectedHex(), this));
			}
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

		@Override
		protected void doExecuteCommand() {
			Character c = (Character) getFormObject();
			for (int i = 0; i < c.getNumberOfOrders(); i++) {
				if (c.getOrders()[i].isBlank()) {
					c.getOrders()[i].setOrderNo(this.orderNo);
					c.getOrders()[i].setParameters(this.params);
					setFormObject(getFormObject());
					CharacterViewer.this.showOrders = true;
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), c.getOrders()[i], this));
					return;
				}
			}
		}

	}

	private class DeleteCharacterCommand extends ActionCommand {
		boolean cancel = true;

		@Override
		protected void doExecuteCommand() {
			this.cancel = true;
			Character c = (Character) getFormObject();
			ConfirmationDialog cdlg = new ConfirmationDialog("Warning", "Are you sure you want to delete character '" + c.getName() + "'?") {
				@Override
				protected void onCancel() {
					super.onCancel();
				}

				@Override
				protected void onConfirm() {
					DeleteCharacterCommand.this.cancel = false;
				}

			};
			cdlg.showDialog();
			if (this.cancel)
				return;
			Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
			Turn t = g.getTurn();
			Container<Army> armies = t.getArmies();
			Army a = armies.findFirstByProperty("commanderName", c.getName());
			if (a != null) {
				MessageDialog dlg = new MessageDialog("Error", "The character commands an army and cannot be deleted.\nDelete the army first if you want to delete the character.");
				dlg.showDialog();
				return;
			}
			Container<Character> characters = t.getCharacters();
			characters.removeItem(c);
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
		}
	}

	private class EditCharacterCommand extends ActionCommand {
		@Override
		protected void doExecuteCommand() {
			final Character c = (Character) getFormObject();
			final String charName = c.getName();
			FormModel formModel = FormModelHelper.createFormModel(c);
			final EditCharacterForm form = new EditCharacterForm(formModel);
			FormBackedDialogPage page = new FormBackedDialogPage(form);

			TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
				@Override
				protected void onAboutToShow() {
				}

				@Override
				protected boolean onFinish() {
					if (!form.getFormModel().getValueModel("name").getValue().equals(charName)) {
						ErrorDialog errDlg = new ErrorDialog("You cannot change the name of the character. Delete the character and create a new one if you want to change the name.");
						errDlg.showDialog();
						return false;
					}
					form.commit();
					c.setId(Character.getIdFromName(c.getName()));
					GameHolder.instance().getGame().getTurn().getCharacters().refreshItem(c);
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), this, this));
					return true;
				}
			};
			MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
			dialog.setTitle(ms.getMessage("editCharacter.title", new Object[] { String.valueOf(c.getName()) }, Locale.getDefault()));
			dialog.showDialog();
		}
	}

	private JPopupMenu createCharacterPopupContextMenu() {
		Character c = (Character) getFormObject();
		ActionCommand showCharacterRangeOnMapCommand = new ShowCharacterMovementRangeCommand(c.getHexNo(), 12);
		ActionCommand showCharacterLongStrideRangeCommand = new ShowCharacterLongStrideRangeCommand(c.getHexNo());
		ActionCommand showCharacterFastStrideRangeCommand = new ShowCharacterFastStrideRangeCommand(c.getHexNo());
		ActionCommand showCharacterPathMasteryRangeCommand = new ShowCharacterPathMasteryRangeCommand(c.getHexNo());
		boolean hasFastStride = false;
		boolean hasLongStride = false;
		boolean hasPathMastery = false;
		for (SpellProficiency sp : c.getSpells()) {
			if (sp.getSpellId() == 304) {
				hasFastStride = true;
			}
			if (sp.getSpellId() == 302) {
				hasLongStride = true;
			}
			if (sp.getSpellId() == 306) {
				hasPathMastery = true;
			}
		}
		showCharacterFastStrideRangeCommand.setEnabled(hasFastStride);
		showCharacterLongStrideRangeCommand.setEnabled(hasLongStride);
		showCharacterPathMasteryRangeCommand.setEnabled(hasPathMastery);

		this.showArtifactsCommand.setEnabled(c != null);
		this.showSpellsCommand.setEnabled(c != null && c.getSpells().size() > 0);
		this.showResultsCommand.setEnabled(c != null && c.getOrderResults() != null && !c.getOrderResults().equals(""));

		CommandGroup quickOrders = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("quickOrdersCommandGroup", new Object[] { new AddRefuseChallengeCommand(), "separator", new AddReconCommand(), "separator", new AddCreateCampCommand(), new AddInfYourCommand(), new AddInfOtherCommand(), new AddImprovePopCommand(), "separator", new AddPrenticeCommand() });

		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("armyCommandGroup", new Object[] { this.showArtifactsCommand, this.showSpellsCommand, this.showOrdersCommand, this.showResultsCommand, "separator", this.editCharacterCommand, "separator", showCharacterRangeOnMapCommand, showCharacterFastStrideRangeCommand, showCharacterLongStrideRangeCommand, showCharacterPathMasteryRangeCommand, "separator", this.deleteCharacterCommand, "separator", new AddEditNoteCommand(c), "separator", quickOrders, "separator", new ShowPreviousStatsCommand(), new ShowInfoSourcePopupCommand(c.getInfoSource()), "separator", this.sendOrdersByChatCommand });
		return group.createPopupMenu();
	}

	public boolean getShowColor() {
		return this.showColor;
	}

	public void setShowColor(boolean showColor) {
		this.showColor = showColor;
	}

	class SendOrdersByChatCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			Character c = (Character) getFormObject();
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SendOrdersByChat.toString(), c.getOrders()[0], this));
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SendOrdersByChat.toString(), c.getOrders()[1], this));
		}

	}

	public void updateNotes() {
		Character c = (Character) getFormObject();
		Note n = (Note) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Notes).findFirstByProperty("target", c);
		if (n != null) {
			n.setText(this.notes.getText());
		}
	}

	class ShowPreviousStatsCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			Character c = (Character) getFormObject();
			Game g = GameHolder.instance().getGame();
			int ct = g.getCurrentTurn();
			ArrayList<String> ret = new ArrayList<String>();
			ret.add("T" + ct + ": " + c.getStatString());
			for (int i = g.getCurrentTurn() - 1; i >= Math.max(0, g.getCurrentTurn() - 10); i--) {
				Turn t = g.getTurn(i);
				if (t == null)
					continue;
				Character pc = t.getCharById(c.getId());
				if (pc == null)
					continue;
				ret.add("T" + i + ": " + pc.getStatString());
			}
			String title = "Skill history for " + c.getName();
			JOptionPane.showMessageDialog(getActiveWindow().getPage().getControl(), ret.toArray(), title, JOptionPane.INFORMATION_MESSAGE);
		}

	}
}
