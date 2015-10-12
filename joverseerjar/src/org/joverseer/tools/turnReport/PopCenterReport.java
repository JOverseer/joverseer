package org.joverseer.tools.turnReport;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.PopulationCenter;

public class PopCenterReport extends BaseReportObject implements IBelongsToNation {
	PopulationCenter pc;	
	boolean created = false;
	PopulationCenter prevPc;
	
	public PopCenterReport() {
		super();
	}
	
	public PopCenterReport(PopulationCenter pc) {
		super();
		setName(pc.getName());
		setNationNo(pc.getNationNo());
		setHexNo(pc.getHexNo());
		setPc(pc);
	}
	
	
	
	public PopulationCenter getPrevPc() {
		return this.prevPc;
	}

	public void setPrevPc(PopulationCenter prevPc) {
		this.prevPc = prevPc;
	}

	public boolean isCreated() {
		return this.created;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public PopulationCenter getPc() {
		return this.pc;
	}

	public void setPc(PopulationCenter pc) {
		this.pc = pc;
	}

	@Override
	public String getExtraInfo() {
		if (this.pc != null && !getModification().equals(ObjectModificationType.Lost)) {
			String ret = this.pc.getSize().getRenderString();
			if (!this.pc.getFortification().equals(FortificationSizeEnum.none)) {
				ret += "/" + this.pc.getFortification().getRenderString();
			}
			if (this.pc.getLoyalty() > 0) {
				ret += "/" + this.pc.getLoyalty();
			}
			return ret;
		}
		if (this.prevPc != null) {
			String ret = this.prevPc.getSize().getRenderString();
			if (!this.prevPc.getFortification().equals(FortificationSizeEnum.none)) {
				ret += "/" + this.prevPc.getFortification().getRenderString();
			}
			return "was " + ret;
		}
		return null;
	}
	
	

}

