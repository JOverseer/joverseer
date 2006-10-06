package org.joverseer.support.readers.xml;

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
}
