package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.ArtifactInfo;

public class TransferArtifactsOrder extends ExecutingOrder {

	public TransferArtifactsOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String id = getParameter(0);
		int[] a = new int[6];
		for (int i=0; i<6; i++) {
			a[i] = getParameterInt(i+1);
		}
		
		addMessage("{char} was ordered to transfer some artifacts.");
		if (!loadCharacter2(turn, id)) {
			addMessage("{gp} was unable to transfer the artifacts because no character with id " + id + " was found");
			return;
		}
		if (!areCharsAtSameHex()) {
			addMessage("{gp} was unable to transfer the artifacts because he was not in the same hex with {char2}.");
			return;
		}

		int artifactsTransfered = 0;
		String artifacts = "";
		for (int i=0; i<6; i++) {
			if (a[i] > 0) {
				// check artifact held
				if (!getCharacter().getArtifacts().contains(a[i])) continue;
				if (getCharacter2().getArtifacts().size() == 6) continue;
				getCharacter().getArtifacts().remove(new Integer(a[i]));
				getCharacter2().getArtifacts().add(a[i]);
				artifactsTransfered++;
				ArtifactInfo ai = (ArtifactInfo)game.getMetadata().getArtifacts().findFirstByProperty("no", a[i]);
				artifacts += (artifacts.equals("") ? "" : ", ") + ai.getName();
				if (getCharacter().getArtifactInUse() == a[i]) {
					getCharacter().setArtifactInUse(0);
					//TODO Fix artifact in use
				}
			}
		}
		if (artifactsTransfered == 0) {
			addMessage("No artifacts were transfered.");
		} else if (artifactsTransfered == 1) {
			addMessage (artifacts + " was transfered to {char2}.");
		} else {
			addMessage (artifacts + " were transfered to {char2}.");
		}
	}
	
	

}
