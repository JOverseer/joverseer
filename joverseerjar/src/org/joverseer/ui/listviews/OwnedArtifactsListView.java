package org.joverseer.ui.listviews;

import java.util.ArrayList;

import javax.swing.JComponent;

import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.Container;
import org.joverseer.ui.domain.OwnedArtifact;
import org.joverseer.ui.listviews.renderers.HexNumberCellRenderer;
import org.joverseer.ui.support.controls.TableUtils;
import org.springframework.richclient.table.ColumnToSort;

/**
 * List view for Owned Artifacts
 *
 * @author Marios Skounakis
 */
public class OwnedArtifactsListView extends ItemListView {
	public OwnedArtifactsListView() {
		super(TurnElementsEnum.Character, OwnedArtifactsTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 32, 96, 48, 132, 48, 120, 120 };
	}

	@Override
	protected ColumnToSort[] getDefaultSort() {
		return new ColumnToSort[] { new ColumnToSort(0, 2), new ColumnToSort(1, 3), new ColumnToSort(2, 0) };
	}

	@Override
	protected void setItems() {
		Turn t = this.getTurn();
		if (t == null)
			return;
		Container<Character> items = t.getCharacters();
		ArrayList<OwnedArtifact> artis = new ArrayList<OwnedArtifact>();
		for (Character c : items.getItems()) {
			for (Integer id : c.getArtifacts()) {
				ArtifactInfo ai = this.game.getMetadata().findFirstArtifactByNumber(id);
				if (ai == null)
					continue;
				// TODO move OwnedArtifact creation outside this class
				OwnedArtifact a = new OwnedArtifact();
				a.setNationNo(c.getNationNo());
				a.setName(ai.getName());
				a.setNumber(ai.getNo());
				a.setOwner(c.getName());
				a.setHexNo(c.getHexNo());
				a.setPower1(ai.getPower1());
				a.setPower2(ai.getPower2());
				artis.add(a);
			}
		}
		for (Artifact ar : t.getArtifacts().getItems()) {
			if (ar.getOwner() != null && !ar.getOwner().equals("")) {
				ArtifactInfo ai = this.game.getMetadata().findFirstArtifactByNumber(ar.getNumber());
				if (ai == null)
					continue;
				Nation n = this.game.getMetadata().getNationByName(ar.getOwner());
				if (n == null)
					continue;
				// TODO move OwnedArtifact creation outside this class
				OwnedArtifact a = new OwnedArtifact();
				a.setNationNo(n.getNumber());
				a.setName(ai.getName());
				a.setNumber(ai.getNo());
				a.setOwner(ar.getOwner());
				a.setHexNo(ar.getHexNo());
				a.setPower1(ai.getPower1());
				a.setPower2(ai.getPower2());
				artis.add(a);
			}
		}
		this.tableModel.setRows(artis);
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent c=super.createControlImpl();
		TableUtils.setTableColumnRenderer(this.table, OwnedArtifactsTableModel.iHexNo, new HexNumberCellRenderer(this.tableModel));
		return c;
	}

}
