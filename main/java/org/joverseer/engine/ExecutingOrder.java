package org.joverseer.engine;

import org.joverseer.domain.Character;

public abstract class ExecutingOrder {
	Character character;
	boolean executed = false;
	
	public Character getCharacter() {
		return character;
	}

	public void setCharacter(Character character) {
		this.character = character;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}
	
	public abstract void execute();
}
