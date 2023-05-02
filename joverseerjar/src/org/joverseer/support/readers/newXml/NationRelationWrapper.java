package org.joverseer.support.readers.newXml;

import org.joverseer.domain.NationRelationsEnum;

public class NationRelationWrapper {
	int nationNo;
	String relation;
	
	public int getNationNo() {
		return this.nationNo;
	}
	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}
	public String getRelation() {
		return this.relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	
    public static NationRelationsEnum fromString(String candidate)
    {
    	NationRelationsEnum relation = NationRelationsEnum.Tolerated;
    	if (candidate.equals("Friendly")) {
            relation = NationRelationsEnum.Friendly;
        } else if (candidate.equals("Tolerated")) {
            relation = NationRelationsEnum.Tolerated;
        } else if (candidate.equals("Neutral")) {
            relation = NationRelationsEnum.Neutral;
        } else if (candidate.equals("Disliked")) {
            relation = NationRelationsEnum.Disliked;
        } else if (candidate.equals("Hated")) {
            relation = NationRelationsEnum.Hated;
        }
    	return relation;
    }

	
}
