package org.joverseer.ui.listviews;


public class ArtifactInfoListView extends ItemListView {
    public ArtifactInfoListView() {
        super("artifacts", ArtifactInfoTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{32, 96,
                        96, 96};
    }
}
