package org.joverseer.support.info;

import java.util.ArrayList;

/**
 * Registry for infos. Typically implemented as a Singleton.
 * 
 * @author Marios Skounakis
 *
 */
public class InfoRegistry {
    ArrayList<Info> infos = new ArrayList<Info>();

    
    public ArrayList<Info> getInfos() {
        return this.infos;
    }

    
    public void setInfos(ArrayList<Info> infos) {
        this.infos = infos;
    }
    
    public Info getInfo(String key) {
        for (Info i : this.infos) {
            if (i.getKey().equals(key)) return i;
        }
        return null;
    }
   
}
