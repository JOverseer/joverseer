package org.joverseer.ui.listviews.advancedCharacterListView;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.richclient.table.BeanTableModel;


public class ArtifactWrapperTableCellRenderer extends AllegianceColorCellRenderer {
        

        public ArtifactWrapperTableCellRenderer(BeanTableModel tableModel) {
                super(tableModel);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                ArtifactWrapper aw = (ArtifactWrapper)value;
                String aid = "";
                if (aw != null) {
                    aid = String.valueOf(aw.getNumber());
                }
                JLabel lbl = (JLabel)super.getTableCellRendererComponent(table, aid, isSelected, hasFocus, row, column);
                String toolTip = "";
                if (aw != null) {
                        toolTip = "<html><body>" + "#" + aw.getNumber() + " " + aw.getName() + " - " + aw.getPower1() + ", " + aw.getPower2() + 
                                "<br>" +
                                "t" + aw.getTurnNo() + " " + InfoSourceTableCellRenderer.getInfoSourceDescription(aw.getInfoSource()) +
                                "</body></html>";
                        InfoSource is = aw.getInfoSource();
                        if (is.getTurnNo() < GameHolder.instance().getGame().getCurrentTurn()) {
                            lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.ITALIC, lbl.getFont().getSize()));
                        } 
                }
                lbl.setToolTipText(toolTip);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
        }

}
