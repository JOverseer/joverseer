package org.joverseer.engine.orders;

import org.joverseer.domain.Army;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.ui.command.AddEditNoteCommand;

public class MoveArmyOrder extends ExecutingOrder {
	String[] moves = new String[14];
	int[] hexes = new int[14];
	boolean stopped = false;
	int currentDay;
	
	public MoveArmyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to {mt} the {t}.");
		if (!loadArmyByCommander(turn)) {
			addMessage("{char} was unable to move the army because he does not command an army.");
			setValid(false);
			return;
		}
		setStartHex(getHex());
		populateMoves(game, turn);
		currentDay = -1;
	}
	
	
	
	public int getCurrentDay() {
		return currentDay;
	}

	public void setCurrentDay(int currentDay) {
		this.currentDay = currentDay;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}
	
	public void stopBecauseOfIntercept() {
		for (int i=Math.max(currentDay, 0); i<13; i++) {
			hexes[i] = getHex();
			moves[i] = "-1";
		}
		if (isStopped()) return;
		setStopped(true);
		if (getHexAtDay(13) != getHexAtDay(currentDay)) {
			addMessage("Movement was stopped by non-friendly forces.");
		}
	}
	
	public int getCurrentHex() {
		return getHexAtDay(currentDay);
	}
	
	public void doMove(Game game, Turn turn, int day) {
		setCurrentDay(day);
		if (isStopped()) return;
		int curHex = getHex();
		
		String dirStr = moves[day];
		if (dirStr.equals("h")) return;
		if (dirStr.equals("-1")) {
			setStopped(true);
			return;
		}
		MovementDirection dir = MovementDirection.getDirectionFromString(dirStr);
		curHex = MovementUtils.getHexNoAtDir(curHex, dir);
		ExecutingOrderUtils.setArmyLocation(turn, getArmy(), curHex);
	}

	public void doAllMoves(Game game, Turn turn) throws ErrorException {
		int curHex = getHex();
		for (int i=0; i<14; i++) {
			doMove(game, turn, i);
		}
		finished(game, turn);
	}
	
	public void finished(Game game, Turn turn) {
		ExecutingOrderUtils.setArmyLocation(turn, getArmy(), getHex());
		setEndHex(getHex());
		addMessage("{char} moved the {t} to {endhex}.");
		appendOrderResults();
	}
	
	public int getHexAtDay(int day) {
		if (day < 0) return getStartHex();
		return hexes[day];
	}
	
	public String getDirAtDay(int day) {
		return moves[day];
	}
	
	public void populateMoves(Game game, Turn turn) throws ErrorException {
		String moveMode = getParameter(getOrder().getLastParamIndex());
		if (moveMode.equals("ev")) {
			moveMode = "no";
		}
		Army a = getArmy(); 
		
		Boolean isCavalry = a.computeCavalry();
		if (isCavalry == null) isCavalry = false;
		Boolean isFed = a.computeFed();
		if (isFed == null) isFed = false;
		int curHex = Integer.parseInt(a.getHexNo());
		int initialHex = curHex;
		int daysUsed = 0;
		int maxDays = getOrderNo() == 850 ? 12 : 14;
		int c = 0;
		int destHex = curHex;
		for (int i=0; i<getOrder().getLastParamIndex(); i++) {
			if (c > 13) break;
			String dirStr = getParameter(i);
			MovementDirection dir = MovementDirection.getDirectionFromString(dirStr);
			if (dir == null) throw new ErrorException("Invalid direction " + dirStr);
			destHex = MovementUtils.getHexNoAtDir(curHex, dir);
			int dist;
			if (getOrderNo() == 830) {
				dist = MovementUtils.calculateMovementCostForNavy(curHex, dirStr, isFed, initialHex);
			} else {
				dist = MovementUtils.calculateMovementCostForArmy(curHex, dirStr, isCavalry, isFed, false, a.getNationAllegiance(), initialHex);
			}
			boolean move = true;
			if (dist == -1) {
				destHex = curHex;
				moves[c] = "-1";
				hexes[c] = destHex;
				c++;
				addMessage("Movement was stopped.");
				break;
			}
			if (daysUsed + dist > maxDays) {
				destHex = curHex;
				moves[c] = "-1";
				hexes[c] = destHex;
				c++;
				addMessage("Movement was exhausted.");
				break;
			}
			moves[c] = dirStr;
			hexes[c] = destHex;
			c++;
			for (int j=1; j<dist; j++) {
				moves[c] = "h";
				hexes[c] = destHex;
				c++;
			}
			daysUsed += dist;
			curHex = destHex;
		}
		for (int j=c; j<14; j++) {
			moves[j] = "h";
			hexes[j] = destHex;
		}
		
	}

	@Override
	public String renderMessage(String msg) {
		String ret = super.renderMessage(msg);
		ret = ret.replace("{t}", getOrderNo() == 830 ? "navy" : "army");
		ret = ret.replace("{mt}", getOrderNo() == 860 ? "force march" : "move");
		return ret;
	}
	
	

}
