package org.joverseer.support.info;

import java.util.ArrayList;

import org.springframework.richclient.application.Application;


public class InfoRegistry {
    ArrayList<Info> infos = new ArrayList<Info>();

    
    public ArrayList<Info> getInfos() {
        return infos;
    }

    
    public void setInfos(ArrayList<Info> infos) {
        this.infos = infos;
    }
    
    public Info getInfo(String key) {
        for (Info i : infos) {
            if (i.getKey().equals(key)) return i;
        }
        return null;
    }
    
    public static InfoRegistry instance() {
        return (InfoRegistry)Application.instance().getApplicationContext().getBean("infoRegistry");
    }
    
}
