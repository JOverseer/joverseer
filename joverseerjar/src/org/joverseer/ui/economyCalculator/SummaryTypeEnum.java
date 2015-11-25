package org.joverseer.ui.economyCalculator;

import org.joverseer.ui.support.UIUtils;

	// what to show for each product
public enum SummaryTypeEnum {
		Total (0),Gain(1),Production(2),Stores(3);
		private final int index;
		SummaryTypeEnum(int index) { this.index = index; }
		public int getIndex() { return this.index; }
		public String getRenderString() {
			return UIUtils.enumToString(this);
		}
}
