package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.support.infoSources.InfoSource;

public class TurnReportItem implements IBelongsToNation, IHasMapLocation {
	Integer nationNo;
	int hexNo;
	String description;
	Object sourceItem;
	InfoSource infoSource;
	
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getHexNo() {
		return this.hexNo;
	}
	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	public InfoSource getInfoSource() {
		return this.infoSource;
	}
	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}
	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}
	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}
	public Object getSourceItem() {
		return this.sourceItem;
	}
	public void setSourceItem(Object sourceItem) {
		this.sourceItem = sourceItem;
	}
	@Override
	public int getX() {
		return getHexNo() / 100;
	}
	@Override
	public int getY() {
		return getHexNo() % 100;
	}
	
	
}
