package org.joverseer.ui.listviews;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.ui.support.transferHandlers.ParamTransferHandler;

/**
 * List view for ArtifactInfo objects
 * 
 * @author Marios Skounakis
 */
public class ArtifactInfoListView extends ItemListView {

    public ArtifactInfoListView() {
        super("artifacts", ArtifactInfoTableModel.class);
    }

    @Override
	protected int[] columnWidths() {
        return new int[] {32, 96, 96, 96, 96};
    }

    @Override
	protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();

        //TODO does this drag and drop work? Needs refinement maybe?
        this.table.addMouseListener(new MouseAdapter() {

            @Override
			public void mousePressed(MouseEvent arg0) {
                ArtifactInfo a = (ArtifactInfo) ArtifactInfoListView.this.tableModel.getRow(ArtifactInfoListView.this.table.getSelectedRow());
                if (a == null)
                    return;
                TransferHandler handler = new ParamTransferHandler(a.getNo());
                ArtifactInfoListView.this.table.setTransferHandler(handler);
                handler.exportAsDrag(ArtifactInfoListView.this.table, arg0, TransferHandler.COPY);
            }
        });
        return c;
    }


}
