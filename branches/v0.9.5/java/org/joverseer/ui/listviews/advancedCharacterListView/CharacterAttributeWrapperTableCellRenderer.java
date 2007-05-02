package org.joverseer.ui.listviews.advancedCharacterListView;

import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.tools.infoCollectors.characters.CharacterAttributeWrapper;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.richclient.table.BeanTableModel;

public class CharacterAttributeWrapperTableCellRenderer extends
		AllegianceColorCellRenderer {

	public CharacterAttributeWrapperTableCellRenderer(BeanTableModel tableModel) {
		super(tableModel);
		// TODO Auto-generated constructor stub
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		CharacterAttributeWrapper caw = (CharacterAttributeWrapper) value;
		String v = caw == null || caw.getValue() == null ? "" : caw.getValue()
				.toString();
		if (caw != null && caw.getValue() != null) {
			InfoSource is = caw.getInfoSource();
			if (DerivedFromTitleInfoSource.class.isInstance(is)) {
				v += "+";
			} else if (RumorActionInfoSource.class.isInstance(is)) {
				v += "+";
			}
		}
		if (caw != null && caw.getTotalValue() != null) {
			if (!caw.getTotalValue().toString().equals(
					caw.getValue().toString())
					&& !caw.getTotalValue().toString().equals("0")) {
				v += "(" + caw.getTotalValue().toString() + ")";
			}
		}
		JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, v,
				isSelected, hasFocus, row, column);
		String toolTip = "";
		if (caw != null && caw.getValue() != null) {
			toolTip = "t"
					+ caw.getTurnNo()
					+ " "
					+ InfoSourceTableCellRenderer.getInfoSourceDescription(caw
							.getInfoSource());

			InfoSource is = caw.getInfoSource();
			if (MetadataSource.class.isInstance(is)) {
				lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(),
						Font.ITALIC, lbl.getFont().getSize()));
			} else if (DerivedFromTitleInfoSource.class.isInstance(is)) {
				lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(),
						Font.ITALIC, lbl.getFont().getSize()));
			} else if (RumorActionInfoSource.class.isInstance(is)) {
				lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(),
						Font.ITALIC, lbl.getFont().getSize()));
			}
		}
		lbl.setToolTipText(toolTip);
		lbl.setHorizontalAlignment(JLabel.CENTER);
		return lbl;
	}

}
