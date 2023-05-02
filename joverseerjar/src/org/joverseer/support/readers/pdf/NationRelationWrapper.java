package org.joverseer.support.readers.pdf;

import org.joverseer.domain.NationRelationsEnum;

/**
 * Holds information about Nation Relations
 * 
 * @author Marios Skounakis
 */
public class NationRelationWrapper {

    String nation;
    String relation;
    int nationNo;


    public int getNationNo() {
        return this.nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }

    public String getNation() {
        return this.nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
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
