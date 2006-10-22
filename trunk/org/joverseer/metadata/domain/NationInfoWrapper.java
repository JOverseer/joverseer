package org.joverseer.metadata.domain;

import org.joverseer.domain.HexInfo;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 30 Σεπ 2006
 * Time: 11:43:41 μμ
 * To change this template use File | Settings | File Templates.
 */
public class NationInfoWrapper {
    String emptyPopHexes;
    String popHexes;

    ArrayList rumors = new ArrayList();

    public ArrayList getRumors() {
        return rumors;
    }

    public void setRumors(ArrayList rumors) {
        this.rumors = rumors;
    }

    public String getEmptyPopHexes() {
        return emptyPopHexes;
    }

    public void setEmptyPopHexes(String emptyPopHexes) {
        this.emptyPopHexes = emptyPopHexes;
    }

    public String getPopHexes() {
        return popHexes;
    }

    public void setPopHexes(String popHexes) {
        this.popHexes = popHexes;
    }

    public ArrayList getHexInfos(int nationNo) {
        ArrayList ret = new ArrayList();
        String[] emptyHexes = getEmptyPopHexes().split(",");
        String[] popHexes = getPopHexes().split(",");

        for (String eh : emptyHexes) {
            int ehi = Integer.parseInt(eh);
            HexInfo hi = new HexInfo();
            hi.getNationSources().add(nationNo);
            hi.setVisible(true);
            hi.setHasPopulationCenter(false);
            hi.setHexNo(ehi);
            ret.add(hi);
        }

        for (String ph : popHexes) {
            int phi = Integer.parseInt(ph);
            HexInfo hi = new HexInfo();
            hi.getNationSources().add(nationNo);
            hi.setVisible(true);
            hi.setHasPopulationCenter(true);
            hi.setHexNo(phi);
            ret.add(hi);
        }
        return ret;
    }
}
