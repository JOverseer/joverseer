package org.joverseer.ui.command;

import java.io.FileWriter;

import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

public class ExportMapMetadataInfoCommand extends ActionCommand {

	public ExportMapMetadataInfoCommand() {
		super("exportMapMetadataInfoCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;
		try {
			FileWriter f = new FileWriter("c:\\map.terrain");
			Game game = GameHolder.instance().getGame();
			MapMetadata metadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
			for (int i = metadata.getMinMapColumn(); i <= metadata.getMaxMapColumn(); i++) {
				for (int j = metadata.getMinMapRow(); j <= metadata.getMaxMapRow(); j++) {
					Hex hex = game.getMetadata().getHex(i * 100 + j);
					String hexNo = String.valueOf(hex.getHexNo());
					if (hexNo.length() == 3) {
						hexNo = "0" + hexNo;
					}
					f.write("\"" + hexNo + "\"," + hex.getTerrain().getTerrain() + "\n");
				}
			}
			f.close();
			f = new FileWriter("c:\\map.traffic");
			for (int i = metadata.getMinMapColumn(); i <= metadata.getMaxMapColumn(); i++) {
				for (int j = metadata.getMinMapRow(); j <= metadata.getMaxMapRow(); j++) {
					Hex hex = game.getMetadata().getHex(i * 100 + j);
					for (HexSideEnum hse : HexSideEnum.values()) {
						for (HexSideElementEnum e : hex.getHexSideElements(hse)) {
							String hexNo = String.valueOf(hex.getHexNo());
							if (hexNo.length() == 3) {
								hexNo = "0" + hexNo;
							}
							f.write("\"" + hexNo + "\",0," + hse.getSide() + "," + e.getElement() + "\n");
						}
					}

				}
			}
			f.close();

		} catch (Exception exc) {

		}

	}
}
