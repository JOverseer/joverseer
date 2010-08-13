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
		return prevPc;
	}

	public void setPrevPc(PopulationCenter prevPc) {
		this.prevPc = prevPc;
	}

	public boolean isCreated() {
		return created;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public PopulationCenter getPc() {
		return pc;
	}

	public void setPc(PopulationCenter pc) {
		this.pc = pc;
	}

	@Override
	public String getExtraInfo() {
		if (pc != null && !getModification().equals(ObjectModificationType.Lost)) {
			String ret = pc.getSize().getRenderString();
			if (!pc.getFortification().equals(FortificationSizeEnum.none)) {
				ret += "/" + pc.getFortification().getRenderString();
			}
			if (pc.getLoyalty() > 0) {
				ret += "/" + pc.getLoyalty();
			}
			return ret;
		}
		if (prevPc != null) {
			String ret = prevPc.getSize().getRenderString();
			if (!prevPc.getFortification().equals(FortificationSizeEnum.none)) {
				ret += "/" + prevPc.getFortification().getRenderString();
			}
			return "was " + ret;
		}
		return null;
	}
	
	

}

