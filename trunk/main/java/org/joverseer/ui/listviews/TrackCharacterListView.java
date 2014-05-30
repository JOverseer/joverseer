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
import org.joverseer.ui.support.Messages;
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
		tlb.cell(new JLabel(Messages.getString("TrackCharacterListView.CharacterColon")), "colspec=left:80px"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		tlb.cell(this.character = new JTextField(), "colspec=left:150px"); //$NON-NLS-1$
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
					String txt = ""; //$NON-NLS-1$
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
		JButton btn = new JButton(Messages.getString("TrackCharacterListView.Track")); //$NON-NLS-1$
		btn.setPreferredSize(new Dimension(70, 20));
		tlb.gapCol();
		tlb.cell(btn, "colspec=left:70px"); //$NON-NLS-1$
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setItems();
			}
		});

		tlb.gapCol();
		btn = new JButton(Messages.getString("TrackCharacterListView.Draw")); //$NON-NLS-1$
		btn.setPreferredSize(new Dimension(70, 20));
		tlb.gapCol();
		tlb.cell(btn, "align=left"); //$NON-NLS-1$
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TrackCharacterMapItem tcmi = new TrackCharacterMapItem();
				int lastTurn = -1;

				final JTextField st = new JTextField();
				InputDialog id = new InputDialog();
				id.setTitle(Messages.getString("TrackCharacterListView.title")); //$NON-NLS-1$
				id.addComponent(Messages.getString("TrackCharacterListView.startTurnTitle"), st); //$NON-NLS-1$
				id.init(Messages.getString("TrackCharacterListView.trackingInit")); //$NON-NLS-1$
				st.setText("0"); //$NON-NLS-1$
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
		String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames"); //$NON-NLS-1$
		if (pv == null || pv.equals("accented")) { //$NON-NLS-1$
			Character c = (Character) t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", name); //$NON-NLS-1$
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
		String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames"); //$NON-NLS-1$
		if (pv == null || pv.equals("accented")) { //$NON-NLS-1$
			return (Army) t.getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", name); //$NON-NLS-1$
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
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
		if (g == null || !Game.isInitialized(g))
			return;
		String charName = this.character.getText();
		if (!charName.equals("")) { //$NON-NLS-1$
			for (Turn t : g.getTurns()) {
				// find in characters
				Character c = findChar(t, charName);
				if (c != null) {
					// TODO move TrackCharacterInfo outside this class
					TrackCharacterInfo tci = new TrackCharacterInfo();
					tci.setTurnNo(t.getTurnNo());
					tci.setInfo(Messages.getString("TrackCharacterListView.LocatedAt", new Object[] { c.getHexNo() } )); //$NON-NLS-1$
					tci.setHexNo(c.getHexNo());
					items.add(tci);
					if (c.getOrderResults() != null && !c.getOrderResults().equals("")) { //$NON-NLS-1$
						tci = new TrackCharacterInfo();
						tci.setTurnNo(t.getTurnNo());
						tci.setInfo(c.getOrderResults());
						tci.setHexNo(c.getHexNo());
						items.add(tci);
					}
					if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) {
						tci = new TrackCharacterInfo();
						tci.setTurnNo(t.getTurnNo());
						tci.setInfo(Messages.getString("TrackCharacterListView.Died", new Object[] { c.getDeathReason().toString() })); //$NON-NLS-1$ //$NON-NLS-2$
						tci.setHexNo(c.getHexNo());
						items.add(tci);
					}
				}
				// find in armies
				Army a = findInArmies(t, charName);
				if (a != null) {
					TrackCharacterInfo tci = new TrackCharacterInfo();
					tci.setTurnNo(t.getTurnNo());
					tci.setInfo(Messages.getString("TrackCharacterListView.leading", new Object[] { a.getHexNo() })); //$NON-NLS-1$
					tci.setHexNo(Integer.parseInt(a.getHexNo()));
					items.add(tci);
				}
				// find in rumors
				for (NationMessage nm : t.getNationMessages()) {
					boolean found = false;
					String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames"); //$NON-NLS-1$
					if (pv == null || pv.equals("accented")) { //$NON-NLS-1$
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
					String pv = PreferenceRegistry.instance().getPreferenceValue("listviews.trackCharacterNames"); //$NON-NLS-1$
					if (pv == null || pv.equals("accented")) { //$NON-NLS-1$
						found = arti.getOwner().indexOf(charName) >= 0;
					} else {
						found = AsciiUtils.convertNonAscii(arti.getOwner()).toLowerCase().indexOf(charName.toLowerCase()) >= 0;
					}
					if (found) {
						TrackCharacterInfo tci = new TrackCharacterInfo();
						tci.setTurnNo(t.getTurnNo());
						tci.setInfo(Messages.getString("TrackCharacterListView.Possesses", new Object[] { arti.getOwner(), arti.getNumber(), arti.getName()})); //$NON-NLS-1$ //$NON-NLS-2$
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
					Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
					g.setCurrentTurn(tci.getTurnNo());
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));

				} catch (Exception exc) {
					// do nothing
				}
			}
		}
	}

}
