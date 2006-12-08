package org.joverseer.ui.listviews;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Οκτ 2006
 * Time: 7:33:22 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ArtifactListView extends ItemListView {
    public ArtifactListView() {
        super("artifacts", ArtifactTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{32, 96,
                        96, 96};
    }
}
