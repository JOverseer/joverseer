package org.joverseer.ui;

import java.awt.Dimension;

public class UISizes {
	private int height3; // default 12
	private int height4; // default 16
	private int height5; // default 20
	private int height6; // default 24
	
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
	public UISizes() {
		this.height3 = 12;
		this.height4 = 16;
		this.height5 = 20;
		this.height6 = 24;
	}
	public int getHeight3() {
		return height3;
	}
	public void setHeight3(int height3) {
		this.height3 = height3;
	}
	public int getHeight4() {
		return height4;
	}
	public void setHeight4(int height4) {
		this.height4 = height4;
	}
	public int getHeight5() {
		return height5;
	}
	public void setHeight5(int height5) {
		this.height5 = height5;
	}
	public int getHeight6() {
		return height6;
	}
	public void setHeight6(int height6) {
		this.height6 = height6;
	}
	public int getComboxBoxHeight()
	{
		return this.height5;
	}
}
