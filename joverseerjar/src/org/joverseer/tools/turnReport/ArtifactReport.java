package org.joverseer.tools.turnReport;

import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;

public class ArtifactReport extends BaseReportObject {
	int artifactNo;

	public ArtifactReport(ObjectModificationType modification,ArtifactWrapper aw) {
		super();
		this.setModification(modification);
		this.setArtifactNo(aw.getNumber());
		this.setName(aw.getName() + " (" + aw.getNumber() + ")");
		this.setHexNo(aw.getHexNo());

//		this.setNationNo(aw.getNationNo()); // can't as may be spell source and so null.

	}
	public int getArtifactNo() {
		return this.artifactNo;
	}

	public void setArtifactNo(int artifactNo) {
		this.artifactNo = artifactNo;
	}
	
	@Override
	public String getExtraInfo() {
		ArtifactInfo ai = (ArtifactInfo)GameHolder.instance().getGame().getMetadata().getArtifacts().findFirstByProperty("no", getArtifactNo());
		String ret = "";
		if (ai != null) {
			if (ai.getPower1() != null && !ai.getPower1().equals("Unknown")) ret += ai.getPower1();
			if (ai.getPower2() != null && !ai.getPower2().equals("Unknown") && !ai.getPower2().equals("")) ret += ", " + ai.getPower2();
		}
		return ret;
	}
}
