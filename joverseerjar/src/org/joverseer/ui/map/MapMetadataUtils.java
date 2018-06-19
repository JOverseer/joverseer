package org.joverseer.ui.map;

import org.joverseer.joApplication;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;

/**
 * Map Metadata utilities
 * 
 * @author Marios Skounakis
 */
public class MapMetadataUtils {
    public void setMapSize(MapMetadata metadata, GameTypeEnum gameType) {
        if (gameType == GameTypeEnum.gameBOFA) {
            metadata.setMinMapColumn(25);
            metadata.setMinMapRow(1);
            metadata.setMaxMapColumn(40);
            metadata.setMaxMapRow(15);
            joApplication.publishEvent(LifecycleEventsEnum.MapMetadataChangedEvent, this, this);

        } else if (gameType == GameTypeEnum.gameUW) {
            metadata.setMinMapColumn(19);
            metadata.setMinMapRow(2);
            metadata.setMaxMapColumn(32);
            metadata.setMaxMapRow(21);

            joApplication.publishEvent(LifecycleEventsEnum.MapMetadataChangedEvent, this, this);

        } else if (gameType == GameTypeEnum.gameCME) {
            metadata.setMinMapColumn(19);
            metadata.setMinMapRow(6);
            metadata.setMaxMapColumn(34);
            metadata.setMaxMapRow(25);

            joApplication.publishEvent(LifecycleEventsEnum.MapMetadataChangedEvent, this, this);

    	} else {
            metadata.setMinMapColumn(1);
            metadata.setMinMapRow(1);
            metadata.setMaxMapColumn(44);
            metadata.setMaxMapRow(39);

            joApplication.publishEvent(LifecycleEventsEnum.MapMetadataChangedEvent, this, this);

        }
    }
}
