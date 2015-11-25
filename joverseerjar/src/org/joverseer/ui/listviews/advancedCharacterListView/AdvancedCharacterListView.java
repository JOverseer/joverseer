package org.joverseer.ui.listviews.advancedCharacterListView;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.tools.infoCollectors.characters.AdvancedCharacterWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterAttributeWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterInfoCollector;
import org.joverseer.ui.listviews.AbstractListViewFilter;
import org.joverseer.ui.listviews.BaseItemListView;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.HexFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.OrFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.commands.DialogsUtility;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.table.SortableTableModel;

/**
 * Advanced Character information tab Shows AdvancedCharacterWrappers in a
 * jtable
 * 
 * @author Marios Skounakis
 */
public class AdvancedCharacterListView extends BaseItemListView {

	class ActiveFilter extends AbstractListViewFilter {

		public ActiveFilter(String description) {
			super(description);
		}

		@Override
		public boolean accept(Object obj) {
			AdvancedCharacterWrapper acw = (AdvancedCharacterWrapper) obj;
			if (acw.isHostage())
				return false;
			if (acw.getDeathReason() != null && !acw.getDeathReason().equals(CharacterDeathReasonEnum.NotDead))
				return false;
			return true;
		}

	}

	class ArmyCommanderFilter extends AbstractListViewFilter {
		Boolean isArmyCommander = null;

		public ArmyCommanderFilter(String descr, Boolean isArmyCommander) {
			super(descr);
			this.isArmyCommander = isArmyCommander;
		}

		@Override
		public boolean accept(Object obj) {
			if (this.isArmyCommander == null)
				return true;
			Turn turn = GameHolder.instance().getGame().getTurn();
			if (turn.getContainer(TurnElementsEnum.Army).findAllByProperty("commanderName", ((AdvancedCharacterWrapper) obj).getName()).size() > 0) {
				return this.isArmyCommander;
			} else {
				return !this.isArmyCommander;
			}
		}
	}

	class ChampionFilter extends AbstractListViewFilter {

		public ChampionFilter(String descr) {
			super(descr);
		}

		@Override
		public boolean accept(Object obj) {
			return ((AdvancedCharacterWrapper) obj).isChampion();
		}

	}

	class CopyToClipboardCommand extends ActionCommand implements ClipboardOwner {
		boolean useStatAnnotations = true;

		String DELIM = "\t";

		String NL = "\n";

		Game game;

		public CopyToClipboardCommand() {
			this("copyToClipboardCommand", false);
		}

		public CopyToClipboardCommand(String commandId, boolean useStatAnnotations) {
			super(commandId);
			this.useStatAnnotations = useStatAnnotations;
		}

		@Override
		protected void doExecuteCommand() {
			this.game = GameHolder.instance().getGame();
			String txt = "";
			for (int j = 0; j < AdvancedCharacterListView.this.tableModel.getDataColumnCount(); j++) {
				if (j == 2) {
					txt += (txt.equals("") ? "" : this.DELIM) + AdvancedCharacterListView.this.tableModel.getDataColumnHeaders()[j] + " No";
					txt += (txt.equals("") ? "" : this.DELIM) + AdvancedCharacterListView.this.tableModel.getDataColumnHeaders()[j];
				} else {
					txt += (txt.equals("") ? "" : this.DELIM) + AdvancedCharacterListView.this.tableModel.getDataColumnHeaders()[j];
				}
			}
			txt += this.NL;
			for (int i = 0; i < AdvancedCharacterListView.this.tableModel.getRowCount(); i++) {
				int idx = ((SortableTableModel) AdvancedCharacterListView.this.table.getModel()).convertSortedIndexToDataIndex(i);
				AdvancedCharacterWrapper aw = (AdvancedCharacterWrapper) AdvancedCharacterListView.this.tableModel.getRow(idx);
				txt += getRow(aw) + this.NL;
			}
			StringSelection stringSelection = new StringSelection(txt);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, this);
		}

		private String getArtifactText(ArtifactWrapper aw) {
			if (aw == null)
				return "";
			String txt = String.valueOf(aw.getNumber());
			if (aw.getInfoSource().getTurnNo() < this.game.getCurrentTurn()) {
				txt += " (t" + aw.getInfoSource().getTurnNo() + ")";
			}
			return txt;
		}

		private String getDeathReasonStr(CharacterDeathReasonEnum cd) {
			if (cd == null)
				return "?";
			if (cd == CharacterDeathReasonEnum.NotDead)
				return "";
			return cd.toString();
		}

		private String getRow(AdvancedCharacterWrapper aw) {
			Nation n = aw.getNationNo() == null ? null : this.game.getMetadata().getNationByNum(aw.getNationNo());
			String nationName = n == null || n.getNumber() == 0 ? "" : n.getShortName();
			String orderResults = aw.getOrderResults();
			if (orderResults == null)
				orderResults = "";
			return aw.getName() + this.DELIM + aw.getHexNo() + this.DELIM + aw.getNationNo() + this.DELIM + nationName + this.DELIM + getStatText(aw.getCommand()) + this.DELIM + getStatText(aw.getAgent()) + this.DELIM + getStatText(aw.getEmmisary()) + this.DELIM + getStatText(aw.getMage()) + this.DELIM + getStatText(aw.getStealth()) + this.DELIM + getStatText(aw.getHealth()) + this.DELIM + getStatText(aw.getChallenge()) + this.DELIM + getArtifactText(aw.getA0()) + this.DELIM + getArtifactText(aw.getA1()) + this.DELIM + getArtifactText(aw.getA2()) + this.DELIM + getArtifactText(aw.getA3()) + this.DELIM + getArtifactText(aw.getA4()) + this.DELIM + getArtifactText(aw.getA5()) + this.DELIM + aw.getTravellingWith() + this.DELIM + getDeathReasonStr(aw.getDeathReason()) + this.DELIM + InfoSourceTableCellRenderer.getInfoSourceDescription(aw.getInfoSource()) + this.DELIM + aw.getTurnNo() + this.DELIM + (aw.getDragonPotential() == null ? "" : aw.getDragonPotential()) + this.DELIM + orderResults.replace("\n", " ").replace("\r", " ");
		}

		private String getStatText(CharacterAttributeWrapper caw) {
			String v = caw == null || caw.getValue() == null ? "" : caw.getValue().toString();
			if (caw != null && caw.getValue() != null && this.useStatAnnotations) {
				InfoSource is = caw.getInfoSource();
				if (DerivedFromTitleInfoSource.class.isInstance(is)) {
					v += "+";
				} else if (RumorActionInfoSource.class.isInstance(is)) {
					v += "+";
				}
			}
			if (caw != null && caw.getTotalValue() != null) {
				if (!caw.getTotalValue().toString().equals(caw.getValue().toString()) && !caw.getTotalValue().toString().equals("0")) {
					v += "(" + caw.getTotalValue().toString() + ")";
				}
			}
			if (v == null || v.equals("0"))
				return v;

			if (caw != null && caw.getInfoSource() != null && this.useStatAnnotations) {
				InfoSource is = caw.getInfoSource();
				if (MetadataSource.class.isInstance(is)) {
					v += "*";
				} else if (DerivedFromTitleInfoSource.class.isInstance(is)) {
					v += "*";
				} else if (RumorActionInfoSource.class.isInstance(is)) {
					v += "*";
				}
			}
			return v;
		}

		@Override
		public void lostOwnership(Clipboard arg0, Transferable arg1) {
		}
	}

	class CopyToClipboardCommandWithoutAnnotations extends CopyToClipboardCommand {

		public CopyToClipboardCommandWithoutAnnotations() {
			super("copyToClipboardCommandWithoutAnnotations", false);
		}

	}

	class DeathFilter extends AbstractListViewFilter {
		ArrayList<CharacterDeathReasonEnum> deathReasons = new ArrayList<CharacterDeathReasonEnum>();

		public DeathFilter(String descr, CharacterDeathReasonEnum[] deathReasons) {
			super(descr);
			if (deathReasons == null) {
				this.deathReasons = null;
			} else {
				this.deathReasons.addAll(Arrays.asList(deathReasons));
			}
		}

		@Override
		public boolean accept(Object obj) {
			return this.deathReasons == null || this.deathReasons.contains(((AdvancedCharacterWrapper) obj).getDeathReason());
		}
	}

	class HostageFilter extends AbstractListViewFilter {
		boolean hostage;

		public HostageFilter(String descr, boolean hostage) {
			super(descr);
			this.hostage = hostage;
		}

		@Override
		public boolean accept(Object obj) {
			return ((AdvancedCharacterWrapper) obj).isHostage() == this.hostage;
		}

	}

	class ShowResultsCommand extends ActionCommand {

		public ShowResultsCommand() {
		}

		@Override
		protected void doExecuteCommand() {
			AdvancedCharacterWrapper acw = (AdvancedCharacterWrapper) getSelectedObject();
			if (acw.getOrderResults() == null)
				return;
			Character c = GameHolder.instance().getGame().getTurn().getCharByName(acw.getName());
			if (c != null)
				DialogsUtility.showCharacterOrderResults(c);
		}
	}

	class ToggleAttributeSortModeCommand extends ActionCommand {
		AdvancedCharacterTableModel model;

		public ToggleAttributeSortModeCommand(AdvancedCharacterTableModel model) {
			super("toggleAttributeSortModeCommand");
			this.model = model;
		}

		@Override
		protected void doExecuteCommand() {
			if (CharacterAttributeWrapper.COMPARIZON_MODE == CharacterAttributeWrapper.COMPARE_BY_TOTAL_VALUE) {
				CharacterAttributeWrapper.COMPARIZON_MODE = CharacterAttributeWrapper.COMPARE_BY_NET_VALUE;
			} else {
				CharacterAttributeWrapper.COMPARIZON_MODE = CharacterAttributeWrapper.COMPARE_BY_TOTAL_VALUE;
			}
			this.model.fireTableDataChanged();
		}
	}

	public AdvancedCharacterListView() {
		super(AdvancedCharacterTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 96, 48, 48, 48, 48, 48, 48, 48, 48, 48, 32, 32, 32, 32, 32, 32, 120, 48, 96, 48, 32, 48 };
	}

	@SuppressWarnings("serial")
	@Override
	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		this.table.setDefaultRenderer(CharacterAttributeWrapper.class, new CharacterAttributeWrapperTableCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(ArtifactWrapper.class, new ArtifactWrapperTableCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(this.tableModel) {

			@Override
			public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
				Component c1 = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
				JLabel lbl = (JLabel) c1;
				Integer v = (Integer) arg1;
				if (v == null || v.equals(0)) {
					lbl.setText("");
				}
				if (arg5 == 1) {
					// render capital with bold
					int idx = ((SortableTableModel) AdvancedCharacterListView.this.table.getModel()).convertSortedIndexToDataIndex(arg4);
					Object obj = AdvancedCharacterListView.this.tableModel.getRow(idx);
					AdvancedCharacterWrapper ch = (AdvancedCharacterWrapper) obj;
					PopulationCenter capital = (PopulationCenter) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[] { "nationNo", "capital" }, new Object[] { ch.getNationNo(), Boolean.TRUE });
					if (capital != null && ch.getHexNo() == capital.getHexNo()) {
						lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
					}

				}

				return c1;
			}

		});

		this.table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(this.tableModel) {
			@Override
			public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
				Component c1 = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
				JLabel lbl = (JLabel) c1;
				if (arg5 == 0) {
					// check for champion
					int idx = ((SortableTableModel) AdvancedCharacterListView.this.table.getModel()).convertSortedIndexToDataIndex(arg4);
					Object obj = AdvancedCharacterListView.this.tableModel.getRow(idx);
					AdvancedCharacterWrapper ch = (AdvancedCharacterWrapper) obj;
					if (ch.isChampion()) {
						lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
					}
				}
				if (arg5 == 21) {
					String results = (String) arg1;
					if (results != null) {
						results = "<html><body>" + results.replace("\n", "<br>") + "</body></html>";
						lbl.setToolTipText(results);
					}
				} else {
					lbl.setToolTipText("");
				}
				return c1;
			}
		});
		return c;
	}

	@Override
	protected JComponent[] getButtons() {
		ArrayList<JComponent> comps = new ArrayList<JComponent>();
		comps.addAll(Arrays.asList(super.getButtons()));
		JLabelButton popupMenu = new JLabelButton();
		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
		popupMenu.setIcon(ico);
		popupMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("advancedCharacterListViewCommandGroup", new Object[] { new CopyToClipboardCommand(), new CopyToClipboardCommandWithoutAnnotations(), new ToggleAttributeSortModeCommand((AdvancedCharacterTableModel) AdvancedCharacterListView.this.tableModel), new ShowResultsCommand(), });
				return group.createPopupMenu();
			}
		});
		comps.add(popupMenu);
		return comps.toArray(new JComponent[] {});
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
		filters1.addAll(Arrays.asList(NationFilter.createNationFilters()));
		filters1.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
		return new AbstractListViewFilter[][] { filters1.toArray(new AbstractListViewFilter[] {}), TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(), new AbstractListViewFilter[] { new DeathFilter("All", null), new ActiveFilter("Active Only"), new DeathFilter("Not Dead", new CharacterDeathReasonEnum[] { CharacterDeathReasonEnum.NotDead, null }), new DeathFilter("Dead", new CharacterDeathReasonEnum[] { CharacterDeathReasonEnum.Assassinated, CharacterDeathReasonEnum.Executed, CharacterDeathReasonEnum.Dead, CharacterDeathReasonEnum.Cursed, CharacterDeathReasonEnum.Missing, CharacterDeathReasonEnum.Challenged }), new HostageFilter("Hostage", true), new ChampionFilter("Champion")

		}, new AbstractListViewFilter[] { new ArmyCommanderFilter("", null), new ArmyCommanderFilter("Army commander", true), new ArmyCommanderFilter("Not army commander", false) }, };
	}

	@Override
	protected AbstractListViewFilter getTextFilter(String txt) {
		if (txt == null || txt.equals(""))
			return super.getTextFilter(txt);
		try {
			int hexNo = Integer.parseInt(txt.trim());
			return new HexFilter("", hexNo);
		} catch (Exception exc) {
			// do nothing
		}
		OrFilter f = new OrFilter();
		f.addFilter(new TextFilter("Name", "name", txt));
		f.addFilter(new TextFilter("OrderResults", "orderResults", txt));
		return f;
	}

	@Override
	protected boolean hasTextFilter() {
		return true;
	}

	@Override
	protected void setItems() {
		ArrayList<AdvancedCharacterWrapper> items = CharacterInfoCollector.instance().getWrappers();
		ArrayList<AdvancedCharacterWrapper> filteredItems = new ArrayList<AdvancedCharacterWrapper>();
		AbstractListViewFilter activeFilter = getActiveFilter();
		for (AdvancedCharacterWrapper o : items) {
			if (activeFilter == null || activeFilter.accept(o)) {
				filteredItems.add(o);
			}
		}
		this.tableModel.setRows(filteredItems);
	}

}
