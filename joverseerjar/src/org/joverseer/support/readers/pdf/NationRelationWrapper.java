package org.joverseer.support.readers.pdf;

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


}
