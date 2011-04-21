package org.joverseer.ui.listviews;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.commands.GenericCopyToClipboardCommand;
import org.joverseer.ui.listviews.commands.PopupMenuCommand;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.HexFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.support.transferHandlers.GenericExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.GenericTransferable;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.table.SortableTableModel;

/**
 * List view for Army objects
 * 
 * @author Marios Skounakis
 */
public class ArmyListView extends ItemListView {

	public ArmyListView() {
		super(TurnElementsEnum.Army, ArmyTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 48, 64, 120, 64, 120, 48, 48, 48, 150, 48 };
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		return new AbstractListViewFilter[][] { NationFilter.createNationFilters(), AllegianceFilter.createAllegianceFilters() };
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
		return new TextFilter("CommanderName", "commanderName", txt);
	}

	@Override
	protected boolean hasTextFilter() {
		return true;
	}

	@Override
	protected JComponent[] getButtons() {
		return new JComponent[] { new PopupMenuCommand().getButton(new Object[] { new GenericCopyToClipboardCommand(table), new ExportArmyDataAction() }) };
	}

	/**
	 * Drag and drop generates a transferable with the following three flavors:
	 * - Army[] (all the selected armies) - Army (the first selected army) -
	 * String (string representation of all selected armies)
	 */
	@Override
	protected void startDragAndDropAction(MouseEvent e) {
		final Army[] selectedArmies = new Army[table.getSelectedRowCount()];
		String copyString = "";
		for (int i = 0; i < table.getSelectedRowCount(); i++) {
			int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(table.getSelectedRows()[i]);
			Army a = (Army) tableModel.getRow(idx);
			selectedArmies[i] = a;
			String ln = "";
			for (int j = 0; j < table.getColumnCount(); j++) {
				Object v = table.getValueAt(i, j);
				if (v == null)
					v = "";
				ln += (ln.equals("") ? "" : "\t") + v;
			}
			copyString += (copyString.equals("") ? "" : "\n") + ln;
		}
		final String str = copyString;

		TransferHandler handler = new GenericExportTransferHandler() {

			@Override
			protected Transferable createTransferable(JComponent arg0) {
				try {
					Transferable t = new GenericTransferable(new DataFlavor[] { new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + Army[].class.getName() + "\""), new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Army.class.getName()), DataFlavor.stringFlavor }, new Object[] { selectedArmies, selectedArmies[0], str });
					return t;
				} catch (Exception exc) {
					exc.printStackTrace();
					return null;
				}

			}
		};
		table.setTransferHandler(handler);
		handler.exportAsDrag(table, e, TransferHandler.COPY);
	}

	public class ExportArmyDataAction extends ActionCommand implements ClipboardOwner {
		String DELIM = ";";
		String NL = "\n";
		Game game;

		public ExportArmyDataAction() {
			super("exportArmyDataAction");
		}

		@Override
		protected void doExecuteCommand() {
			String str = "";
			for (int j = 0; j < tableModel.getRowCount(); j++) {
				Army a = (Army) tableModel.getRow(j);
				str += a.getHexNo() + DELIM + a.getCommanderName() + DELIM + a.getNationAllegiance().getAllegiance() + DELIM + a.getNationNo() + DELIM + (a.isNavy() ? 1 : 0) + DELIM + a.getSize().getSize() + DELIM + a.getTroopCount() + DELIM + a.getMorale() + DELIM + getElementString(a, ArmyElementType.HeavyCavalry) + DELIM + getElementString(a, ArmyElementType.LightCavalry) + DELIM + getElementString(a, ArmyElementType.HeavyInfantry) + DELIM + getElementString(a, ArmyElementType.LightInfantry) + DELIM + getElementString(a, ArmyElementType.Archers) + DELIM + getElementString(a, ArmyElementType.MenAtArms) + DELIM + getElementString(a, ArmyElementType.WarMachimes) + DELIM + getElementString(a, ArmyElementType.Warships) + DELIM + getElementString(a, ArmyElementType.Transports) + DELIM + NL;
			}
			StringSelection stringSelection = new StringSelection(str);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, this);
		}

		protected String getElementString(Army a, ArmyElementType aet) {
			String str = "";
			ArmyElement ae = a.getElement(aet);
			if (ae == null) {
				str = aet.getType() + DELIM + "" + DELIM + "" + DELIM + "" + DELIM + "";
			} else {
				str = aet.getType() + DELIM + ae.getNumber() + DELIM + ae.getTraining() + DELIM + ae.getWeapons() + DELIM + ae.getArmor();
			}
			return str;
		}

		public void lostOwnership(Clipboard arg0, Transferable arg1) {
			// TODO Auto-generated method stub

		}

	}
}
