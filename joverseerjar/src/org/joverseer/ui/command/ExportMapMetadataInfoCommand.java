package org.joverseer.ui.command;

import java.io.FileWriter;

import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.richclient.command.ActionCommand;

public class ExportMapMetadataInfoCommand extends ActionCommand {

	//dependencies
	GameHolder gameHolder;
	public ExportMapMetadataInfoCommand(GameHolder gameHolder) {
		super("exportMapMetadataInfoCommand");
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;
		try {
			FileWriter f = new FileWriter("c:\\map.terrain");
			Game game = this.gameHolder.getGame();
			MapMetadata metadata = MapMetadata.instance();
			for (int i = metadata.getMinMapColumn(); i <= metadata.getMaxMapColumn(); i++) {
				for (int j = metadata.getMinMapRow(); j <= metadata.getMaxMapRow(); j++) {
					Hex hex = game.getMetadata().getHex(i * 100 + j);
					String hexNo = hex.getHexNoStr();
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
							String hexNo = hex.getHexNoStr();
							f.write("\"" + hexNo + "\",0," + hse.getSide() + "," + e.getElement() + "\n");
						}
					}

				}
			}
			f.close();

		} catch (Exception exc) {
			ErrorDialog.showErrorDialog(exc);
		}

	}
}
