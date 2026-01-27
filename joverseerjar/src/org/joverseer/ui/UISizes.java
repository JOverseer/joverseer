package org.joverseer.ui;

import java.awt.Dimension;

import org.joverseer.preferences.PreferenceRegistry;

public class UISizes {
	private int height3; // default 12
	private int height4; // default 16
	private int height5; // default 20
	private int height6; // default 24
	private int height7;
	public int diff;
	
	public Dimension newDimension(float scale,int height) {
		return new Dimension((int)((scale * height)+0.5),height);
	}
	public Dimension newIconDimension(int height) {
		return new Dimension(height,height);
	}
	public Dimension newTextPreferredDimension()
	{
		return new Dimension(100, 20);
	}
	public int calculateTableColumnWidth(int base) {
		float num = (float)base * (1 + (float)this.diff/10);
		return (int)num;
	}
	public UISizes() {
		String pval = PreferenceRegistry.instance().getPreferenceValue("UIscaling.defaultTextSize");
		this.diff = 0;
		if(!pval.equals("12")) {
			this.diff = Integer.parseInt(pval) - 12;
		}
		
		this.height3 = 12 + this.diff;
		this.height4 = 16 + this.diff;
		this.height5 = 20 + this.diff;
		this.height6 = 24 + this.diff;
		this.height7 = 48 + this.diff;

	}
	public int getHeight3() {
		return this.height3;
	}
	public void setHeight3(int height3) {
		this.height3 = height3;
	}
	public int getHeight4() {
		return this.height4;
	}
	public void setHeight4(int height4) {
		this.height4 = height4;
	}
	public int getHeight5() {
		return this.height5;
	}
	public void setHeight5(int height5) {
		this.height5 = height5;
	}
	public int getHeight6() {
		return this.height6;
	}
	public void setHeight6(int height6) {
		this.height6 = height6;
	}
	public Dimension getComboBoxDimension() {
		return new Dimension(150, this.getComboxBoxHeight());
	}
	public int getComboxBoxHeight()
	{
		return this.height5;
	}
	public Dimension getSquareBtHeight() {
		return new Dimension(42, this.height6);
	}
}
