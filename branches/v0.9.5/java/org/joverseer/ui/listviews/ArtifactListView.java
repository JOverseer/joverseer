package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;

@Deprecated
public class ArtifactListView extends ItemListView {
    public ArtifactListView() {
        super(TurnElementsEnum.Artifact, ArtifactTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{32, 32,
                        120, 96, 160};
    }
}

