package org.joverseer.support.readers.orders;

import org.joverseer.game.Game;

public class AutoMagicOrderTextReader extends OrderTextReader {

	public AutoMagicOrderTextReader(Game game) {
		super(game,AUTOMAGIC_ORDER_TEXT);
	}
	@Override
	protected boolean isCharacterLine(String line) {
		return false;
	}
	@Override
	protected boolean isOrderLine(String line) {
		return false;
	}
	
	@Override
	protected String getCharacterNameFromLine(String line) {
		return null;
	}
	@Override
	protected String getCharacterLocationFromLine(String line) {
		return null;
	}

}
