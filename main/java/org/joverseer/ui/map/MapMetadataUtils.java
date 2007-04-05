package org.joverseer.ui.map;

import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;


public class MapMetadataUtils {
    public void setMapSize(MapMetadata metadata, GameTypeEnum gameType) {
        if (gameType == GameTypeEnum.gameBOFA) {
            metadata.setMinMapColumn(25);
            metadata.setMinMapRow(1);
            metadata.setMaxMapColumn(40);
            metadata.setMaxMapRow(13);
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));

        } else {
            metadata.setMinMapColumn(1);
            metadata.setMinMapRow(1);
            metadata.setMaxMapColumn(44);
            metadata.setMaxMapRow(39);
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.MapMetadataChangedEvent.toString(), this, this));

        }
    }
}
