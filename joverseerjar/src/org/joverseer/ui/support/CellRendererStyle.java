package org.joverseer.ui.support;

import java.awt.Color;

/**
 * A style to encasulate the various colours available on a table cell renderer
 * @author Dave
 *
 */
public interface CellRendererStyle {
	Color getBackground();
	Color getForeground();
	Color getWarningForeground();
	Color getWarningBackground();
	Color getSelectedForeground();
	Color getSelectedBackground();
	Color getFocusBorder();
	Color getForeground(boolean selected);
	Color getBackground(boolean selected);
	Color getLastRowBackground();
	Color getTaxWarningForeground();
}
