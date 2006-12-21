package org.joverseer.ui.domain.mapItems;

import org.joverseer.support.Container;
import org.springframework.richclient.application.*;

public abstract class AbstractMapItem {
    public abstract String getDescription();
    
    public static void add(AbstractMapItem mapItem) {
        Container mapItems = (Container)Application.instance().getApplicationContext().getBean("mapItemContainer");
        mapItems.addItem(mapItem);
    }
}
