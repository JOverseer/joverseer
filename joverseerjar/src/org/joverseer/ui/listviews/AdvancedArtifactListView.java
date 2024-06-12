package org.joverseer.ui.listviews;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.CellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.joverseer.JOApplication;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactTrueInfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactInfoCollector;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.HexFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.joverseer.ui.listviews.renderers.HexNumberCellRenderer;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.controls.AutocompletionComboBox;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.controls.TableUtils;
import org.joverseer.ui.views.Messages;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.list.SortedListModel;
import org.springframework.binding.value.support.ListListModel;

/**
 * The advanced artifact information tab Shows ArtifactWrappers from the
 * ArtifactInfoCollector
 *
 * @author Marios Skounakis
 */
public class AdvancedArtifactListView extends BaseItemListView {

	/**
	 * Filter based on the artifact power
	 *
	 * @author Marios Skounakis
	 */
	class ArtifactPowerFilter extends AbstractListViewFilter {

		String powerStr;

		public ArtifactPowerFilter(String descr, String power) {
			super(descr);
			this.powerStr = power;
		}

		@Override
		public boolean accept(Object obj) {
			if (this.powerStr == null)
				return true;
			ArtifactWrapper aw = (ArtifactWrapper) obj;
			return (aw.getPower1().indexOf(this.powerStr) > -1 || aw.getPower2().indexOf(this.powerStr) > -1);
		}
	}
	
	class ActivateEditMode extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			AdvancedArtifactTableModel tableModelArt = (AdvancedArtifactTableModel) AdvancedArtifactListView.this.tableModel;
			tableModelArt.setEditable(!tableModelArt.getEditable());
		}
		
	}
	
	class ClearUserInfo extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			// TODO Auto-generated method stub
			 Game g = AdvancedArtifactListView.this.gameHolder.getGame();
			 for (int i = 0; i <= g.getMaxTurn(); i++) {
				 g.getTurn(i).getArtifactsUser().clear();
			 }
			 ArtifactInfoCollector.instance().refreshWrappers();
			 
			 JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, AdvancedArtifactListView.this, AdvancedArtifactListView.this);
		}
		
	}

	class CopyToClipboardCommand extends ActionCommand implements ClipboardOwner {

		String DELIM = "\t";
		String NL = "\n";

		@Override
		protected void doExecuteCommand() {
			String txt = "";
			for (int j = 0; j < AdvancedArtifactListView.this.tableModel.getDataColumnCount(); j++) {
				txt += (txt.equals("") ? "" : this.DELIM) + AdvancedArtifactListView.this.tableModel.getDataColumnHeaders()[j];
				if (j == 2) {
					// duplicate column "nation"
					txt += (txt.equals("") ? "" : this.DELIM) + AdvancedArtifactListView.this.tableModel.getDataColumnHeaders()[j];
				}
			}
			txt += this.NL;
			for (int i = 0; i < AdvancedArtifactListView.this.tableModel.getRowCount(); i++) {
				int idx = ((SortableTableModel) AdvancedArtifactListView.this.table.getModel()).convertSortedIndexToDataIndex(i);
				ArtifactWrapper aw = (ArtifactWrapper) AdvancedArtifactListView.this.tableModel.getRow(idx);
				txt += getRow(aw) + this.NL;
			}
			StringSelection stringSelection = new StringSelection(txt);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, this);
		}

		private String getRow(ArtifactWrapper aw) {
			Nation n = aw.getNationNo() == null ? null : AdvancedArtifactListView.this.getGame().getMetadata().getNationByNum(aw.getNationNo());
			String nationName = n == null || n.getNumber() == 0 ? "" : n.getShortName();
			return aw.getNumber() + this.DELIM + aw.getName() + this.DELIM + aw.getNationNo() + this.DELIM + nationName + this.DELIM + aw.getOwner() + this.DELIM + aw.getHexNo() + this.DELIM + aw.getAlignment() + this.DELIM + aw.getPower1() + this.DELIM + aw.getPower2() + this.DELIM + aw.getTurnNo() + this.DELIM + InfoSourceTableCellRenderer.getInfoSourceDescription(aw.getInfoSource()) + this.NL;
		}

		@Override
		public void lostOwnership(Clipboard arg0, Transferable arg1) {
		}
	}

	/**
	 * Filter based on the class of the info source
	 *
	 * @author Marios Skounakis
	 */
	class InfoSourceClassFilter extends AbstractListViewFilter {

		Class<InfoSource>[] classes;

		public InfoSourceClassFilter(String descr, Class<InfoSource>[] classes) {
			super(descr);
			this.classes = classes;
		}

		@Override
		public boolean accept(Object obj) {
			if (this.classes == null)
				return true;
			InfoSource is = ((ArtifactWrapper) obj).getInfoSource();
			for (Class<InfoSource> c : this.classes) {
				if (c.isInstance(is))
					return true;
			}
			return false;
		}
	}

	/**
	 * Filter for owned/not owned artifacts
	 *
	 * @author Marios Skounakis
	 */
	class OwnedArtifactFilter extends AbstractListViewFilter {

		Boolean owned;

		public OwnedArtifactFilter(String descr, Boolean owned) {
			super(descr);
			this.owned = owned;
		}

		@Override
		public boolean accept(Object obj) {
			ArtifactWrapper aw = (ArtifactWrapper) obj;
			if (this.owned == null)
				return true;
			if (this.owned) {
				return aw.isOwned();
			} else {
				return !aw.isOwned();
			}
		}
	}

	public AdvancedArtifactListView() {
		super(AdvancedArtifactTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 24, 96, 32, 132, 32, 32, 120, 120, 24, 180 };
	}

	@Override
	protected JComponent[] getButtons() {
		ArrayList<JComponent> comps = new ArrayList<JComponent>();
		comps.addAll(Arrays.asList(super.getButtons()));
		JLabelButton popupMenu = new JLabelButton();
		ImageSource imgSource = JOApplication.getImageSource();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
		popupMenu.setIcon(ico);
		popupMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("advancedArtifactListViewCommandGroup", new Object[] { new CopyToClipboardCommand(), new ClearUserInfo(), });
				return group.createPopupMenu();
			}
		});
		comps.add(popupMenu);
		JLabelButton editMode = new JLabelButton();
		ico = new ImageIcon(imgSource.getImage("edit.image"));
		editMode.setIcon(ico);
		editMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AdvancedArtifactTableModel tableModelArt = (AdvancedArtifactTableModel) AdvancedArtifactListView.this.tableModel;
				tableModelArt.setEditable(!tableModelArt.getEditable());
				AdvancedArtifactListView.this.getControl().repaint();
				
			}

		});
		editMode.setToolTipText(Messages.getString("activateEditMode.label.tooltip"));
		comps.add(editMode);
;
		return comps.toArray(new JComponent[] {});
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
		filters1.addAll(Arrays.asList(NationFilter.createNationFilters()));
		filters1.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters(true)));
		filters1.add(new OwnedArtifactFilter("Owned", true));
		filters1.add(new OwnedArtifactFilter("Not Owned", false));
		return new AbstractListViewFilter[][] { filters1.toArray(new AbstractListViewFilter[] {}), TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(), new AbstractListViewFilter[] { new InfoSourceClassFilter("All sources", null), new InfoSourceClassFilter("LA/LAT", new Class[] { DerivedFromLocateArtifactInfoSource.class, DerivedFromLocateArtifactTrueInfoSource.class }), new InfoSourceClassFilter("Xml/Pdf", new Class[] { XmlTurnInfoSource.class }), new InfoSourceClassFilter("Starting", new Class[] { MetadataSource.class }), },
				new AbstractListViewFilter[] { new ArtifactPowerFilter("All Powers", null), new ArtifactPowerFilter("Combat", "Combat "), new ArtifactPowerFilter("Agent", "Agent "), new ArtifactPowerFilter("Command", "Command "), new ArtifactPowerFilter("Stealth", "Stealth "), new ArtifactPowerFilter("Mage", "Mage "), new ArtifactPowerFilter("Emissary", "Emissary "), new ArtifactPowerFilter("Scrying", "Scry"), new ArtifactPowerFilter("Curse", "Spirit Mastery"), new ArtifactPowerFilter("Conjuring", "Conjuring Ways"), new ArtifactPowerFilter("Teleport", " Teleport") } };
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
		return new TextFilter("Name", "name", txt);
	}

	@Override
	protected boolean hasTextFilter() {
		return true;
	}

	@Override
	protected void setItems() {
		Game g = this.gameHolder.getGame();
		if (!Game.isInitialized(g))
			return;
		this.createCharAuto();
		ArrayList<ArtifactWrapper> aws = ArtifactInfoCollector.instance().getWrappersForTurn(g.getCurrentTurn());
		ArrayList<ArtifactWrapper> filteredItems = new ArrayList<ArtifactWrapper>();
		AbstractListViewFilter filter = getActiveFilter();
		for (ArtifactWrapper obj : aws) {
			if (filter == null || filter.accept(obj)) {
				filteredItems.add(obj);
			}
		}
		this.tableModel.setRows(filteredItems);
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		TableUtils.setTableColumnRenderer(this.table, AdvancedArtifactTableModel.iHexNo, new HexNumberCellRenderer(this.tableModel));

		return c;
	}
	
	/*
	 * Creates an auto-completing drop down box in the cell for characters
	 * Only includes characters that have been reported in turns
	 */
	private void createCharAuto() {
		TableColumn charCol = this.table.getColumnModel().getColumn(AdvancedArtifactTableModel.iOwner);

		ListListModel llm = new ListListModel();
		for (Turn t : this.gameHolder.getGame().getTurns()) {
			for (Character c : t.getAllCharacters()){
				if(!llm.contains(c.getName())) llm.add(c.getName());
			}
		}
		SortedListModel slm = new SortedListModel(llm);
		ComboBoxListModelAdapter ls = new ComboBoxListModelAdapter(slm);
		final JComboBox comboBox = new AutocompletionComboBox(ls);
		
		comboBox.setEditable(true);
		comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		final ComboBoxCellEditor editor = new ComboBoxCellEditor(comboBox);
		charCol.setCellEditor(editor);
		comboBox.getEditor().getEditorComponent().addKeyListener(new OrderEditingKeyAdapter(editor));
		editor.addCellEditorListener(new CellEditorListener() {

			@Override
			public void editingCanceled(ChangeEvent e1) {
				AdvancedArtifactListView.this.table.requestFocus();
			}

			@Override
			public void editingStopped(ChangeEvent e1) {
				AdvancedArtifactListView.this.table.requestFocus();
			}

		});
	}
	
	class OrderEditingKeyAdapter extends KeyAdapter {
		CellEditor editor;
		public OrderEditingKeyAdapter(CellEditor editor) {
			this.editor = editor;
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.editor.cancelCellEditing();
				arg0.consume();
			}

		}

	}
}
