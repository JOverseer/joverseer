package com.middleearthgames.orderchecker;

import java.io.Serializable;

public class Relations {
	public enum NationRelationsEnum implements Serializable {
	    Hated,
	    Disliked,
	    Neutral,
	    Tolerated,
	    Friendly
	}
	NationRelationsEnum[] relationTable = new NationRelationsEnum[26];
	
	public Relations() {

	}
	
    public void setRelationsFor(int nationNo, int relation) {
    	if(relation == 0) this.relationTable[nationNo] = NationRelationsEnum.Hated;
    	if(relation == 1) this.relationTable[nationNo] = NationRelationsEnum.Disliked;
    	if(relation == 2) this.relationTable[nationNo] = NationRelationsEnum.Neutral;
    	if(relation == 3) this.relationTable[nationNo] = NationRelationsEnum.Tolerated;
    	if(relation == 4) this.relationTable[nationNo] = NationRelationsEnum.Friendly;
    }
	
	public NationRelationsEnum getRelationsFor(int nationNum) {
		return this.relationTable[nationNum];
	}
	
	public boolean isNationEnemy(int nationNum) {
		if(this.getRelationsFor(nationNum) == NationRelationsEnum.Disliked || this.getRelationsFor(nationNum) == NationRelationsEnum.Hated) {
			return true;
		}
		return false;
	}
	
	public boolean isNation(int nationNum) {
		if(this.getRelationsFor(nationNum) == NationRelationsEnum.Disliked || this.getRelationsFor(nationNum) == NationRelationsEnum.Hated) {
			return true;
		}
		return false;
	}
}

