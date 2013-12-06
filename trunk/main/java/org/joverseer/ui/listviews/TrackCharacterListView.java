package org.joverseer.ui.listviews;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.joverseer.domain.Army;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.NationMessage;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.AsciiUtils;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.TrackCharacterInfo;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.TrackCharacterMapItem;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.dataFlavors.CharacterDataFlavor;
import org.joverseer.ui.support.dialogs.InputDialog;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortableTableModel;

/**
 * List view for Track Character results
 * 
 * @author Marios Skounakis
 */
public class TrackCharacterListView extends BaseItemListView {

	JTextField character;
	JComboBox from;
	JComboBox to;
	protected SelectRowCommandExecutor selectRowCommandExecutor = new SelectRowCommandExecutor();

	public TrackCharacterListView() {
		super(TrackCharacterTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 64, 64, 400 };
	}

	@Override
	protected ColumnToSort[] getDefaultSort() {
		return new ColumnToSort[] { new ColumnToSort(0, 0), new ColumnToSort(0, 1) };
	}

	/**
	 * Override and replace the base implementation to create the text box for
	 * the character id
	 */
	@Override
	protected JComponent createControlImpl() {
		JComponent tableComp = super.createControlImpl();
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel("Character : "), "colspec=left:80px");
		tlb.gapCol();
		tlb.cell(this.character = new JTextField(), "colspec=left:150px");
		this.character.setPreferredSize(new Dimension(200, 20));
		this.character.setDragEnabled(true);
		this.character.setOpaque(true);
		// accept char names and free text from drag & drop operations
		this.character.setDropTarget(new DropTarget(this.character, new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent dtde) {
				try {
					Transferable t = dtde.getTransferable();
					CharacterDataFlavor characterDataFlavor = new CharacterDataFlavor();
					String txt = "";
					if (t.isDataFlavorSupported(characterDataFlavor)) {
						txt = ((Character) t.getTransferData(characterDataFlavor)).getName();
						// txt = Character.getSpacePaddedIdFromId(txt);
					} else {
						txt = (t.getTransferData(DataFlavor.stringFlavor)).toString();
					}
					TrackCharacterListView.this.character.setText(txt);
					TrackCharacterListView.this.character.requestFocus();
				} catch (Exception exc) {
				}
			}
		}));
		this.character.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setItems();
			}
		});

		tlb.gapCol();
		JButton btn = new JButton("Track");
		btn.setPreferredSize(new Dimension(70, 20));
		tlb.gapCol();
		tlb.cell(btn, "colspec=left:70px");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setItems();
			}
		});

		tlb.gapCol();
		btn = new JButton("Draw");
		btn.setPreferredSize(new Dimension(70, 20));
		tlb.gapCol();
		tlb.cell(btn, "align=left");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TrackCharacterMapItem tcmi = new TrackCharacterMapItem();
				int lastTurn = -1;

				final JTextField st = new JTextField();
				InputDialog id = new InputDialog();
				id.setTitle("Track Character");
				id.addComponent("Start turn: ", st);
				id.init("Draw tracking info starting at turn...");
				st.setText("0");
				id.setPreferredSize(new Dimension(400, 100));
				id.showDialog();

				int startTurn = 0;
				try {
					startTurn = Integer.parseInt(st.getText());
				} catch (Exception exc) {
				}
				;

				for (int i = 0; i < TrackCharacterListView.this.tableModel.getRowCount(); i++) {
					TrackCharacterInfo tci = (TrackCharacterInfo) TrackCharacterListView.this.tableModel.getRow(i);
					if (tci.getTurnNo() != lastTurn && tci.getTurnNo() >= startTurn && tci.getHexNo() > 0) {
						lastTurn = tci.getTurnNo();
						tcmi.addPoint(tci.getHexNo(), tci.getTurnNo());
					}
				}
				AbstractMapItem.add(tcmi);
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), tcmi, this));
			}
		});

		tlb.row();
		tlb.relatedGapRow();
		tlb.cell(tableComp);
		tlb.row();
		return tlb.getPanel();
	}

	/**
	 * Find character with given name in the given turn
	 */
	private Character findChar(Turn t, String name) {
		String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames");
		if (pv == null || pv.equals("accented")) {
			Character c = (Character) t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", name);
			return c;
		} else {
			for (Character c : t.getCharacters()) {
				if (AsciiUtils.convertNonAscii(c.getName()).toLowerCase().equals(name.toLowerCase())) {
					return c;
				}
			}
		}
		return null;
	}

	/**
	 * Find army with given commander name in given turn
	 */
	private Army findInArmies(Turn t, String name) {
		String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames");
		if (pv == null || pv.equals("accented")) {
			return (Army) t.getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", name);
		} else {
			for (Army a : t.getArmies()) {
				if (AsciiUtils.convertNonAscii(a.getCommanderName()).toLowerCase().equals(name.toLowerCase())) {
					return a;
				}
			}
		}
		return null;
	}

	@Override
	protected void setItems() {
		ArrayList<TrackCharacterInfo> items = new ArrayList<TrackCharacterInfo>();
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (g == null || !Game.isInitialized(g))
			return;
		String charName = this.character.getText();
		if (!charName.equals("")) {
			for (Turn t : g.getTurns()) {
				// find in characters
				Character c = findChar(t, charName);
				if (c != null) {
					// TODO move TrackCharacterInfo outside this class
					TrackCharacterInfo tci = new TrackCharacterInfo();
					tci.setTurnNo(t.getTurnNo());
					tci.setInfo(String.format("Character was located at %s.", c.getHexNo()));
					tci.setHexNo(c.getHexNo());
					items.add(tci);
					if (c.getOrderResults() != null && !c.getOrderResults().equals("")) {
						tci = new TrackCharacterInfo();
						tci.setTurnNo(t.getTurnNo());
						tci.setInfo(c.getOrderResults());
						tci.setHexNo(c.getHexNo());
						items.add(tci);
					}
					if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
						tci = new TrackCharacterInfo();
						tci.setTurnNo(t.getTurnNo());
						tci.setInfo("Character died (" + c.getDeathReason().toString() + ").");
						tci.setHexNo(c.getHexNo());
						items.add(tci);
					}
				}
				// find in armies
				Army a = findInArmies(t, charName);
				if (a != null) {
					TrackCharacterInfo tci = new TrackCharacterInfo();
					tci.setTurnNo(t.getTurnNo());
					tci.setInfo(String.format("Character was leading an army at %s.", a.getHexNo()));
					tci.setHexNo(Integer.parseInt(a.getHexNo()));
					items.add(tci);
				}
				// find in rumors
				for (NationMessage nm : t.getNationMessages()) {
					boolean found = false;
					String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames");
					if (pv == null || pv.equals("accented")) {
						found = nm.getMessage().indexOf(charName) >= 0;
					} else {
						found = AsciiUtils.convertNonAscii(nm.getMessage()).toLowerCase().indexOf(charName.toLowerCase()) >= 0;
					}
					if (found) {
						TrackCharacterInfo tci = new TrackCharacterInfo();
						tci.setTurnNo(t.getTurnNo());
						tci.setInfo(nm.getMessage());
						if (nm.getX() > 0) {
							tci.setHexNo(nm.getX() * 100 + nm.getY());
						} else {
							tci.setHexNo(0);
						}
						items.add(tci);
					}
				}
				// find in LA/LAT results
				for (Artifact arti : t.getArtifacts()) {
					boolean found = false;
					String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames");
					if (pv == null || pv.equals("accented")) {
						found = arti.getOwner().indexOf(charName) >= 0;
					} else {
						found = AsciiUtils.convertNonAscii(arti.getOwner()).toLowerCase().indexOf(charName.toLowerCase()) >= 0;
					}
					if (found) {
						TrackCharacterInfo tci = new TrackCharacterInfo();
						tci.setTurnNo(t.getTurnNo());
						tci.setInfo(arti.getOwner() + " possesses #" + arti.getNumber() + " " + arti.getName());
						tci.setHexNo(arti.getHexNo());
						items.add(tci);
					}
				}
			}
			this.tableModel.setRows(items);
			this.tableModel.fireTableDataChanged();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == 1) {
			this.selectRowCommandExecutor.execute();
		}
		if (e.getClickCount() == 1 && e.getButton() == 3) {
			showContextMenu(e);
		}
	}

	/**
	 * Specialized executor that will move back to the appropriate turn and
	 * select the appropriate hex
	 * 
	 * @author Marios Skounakis
	 */
	private class SelectRowCommandExecutor extends AbstractActionCommandExecutor {

		@Override
		public void execute() {
			int row = TrackCharacterListView.this.table.getSelectedRow();
			if (row >= 0) {
				int idx = ((SortableTableModel) TrackCharacterListView.this.table.getModel()).convertSortedIndexToDataIndex(row);
				if (idx >= TrackCharacterListView.this.tableModel.getRowCount())
					return;
				try {
					Object obj = TrackCharacterListView.this.tableModel.getRow(idx);
					TrackCharacterInfo tci = (TrackCharacterInfo) obj;
					if (tci.getHexNo() > 0) {
						Point selectedHex = new Point(tci.getX(), tci.getY());
						Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selectedHex, this));
					}
					Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
					g.setCurrentTurn(tci.getTurnNo());
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));

				} catch (Exception exc) {
					// do nothing
				}
			}
		}
	}

}
