package org.joverseer.tools.turnReport;

import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;

public class ArtifactReport extends BaseReportObject {
	int artifactNo;

	public int getArtifactNo() {
		return artifactNo;
	}

	public void setArtifactNo(int artifactNo) {
		this.artifactNo = artifactNo;
	}
	
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
