package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class NotepadInfo implements Serializable{
	private static final long serialVersionUID = -8779143155590884307L;

	private ArrayList<String> noteTitles;
	private ArrayList<String> notes;
	
	public NotepadInfo() {
		this.notes = new ArrayList<String>();
		this.noteTitles= new ArrayList<String>();
		this.newNote("Note 1", "");
	}
	
	public boolean newNote(String title, String note) {
		if (this.noteTitles.contains(title)) return false;
		this.notes.add(note);
		this.noteTitles.add(title);
		return true;
	}
	
	public boolean removeNote(String title) {
		if(!this.noteTitles.contains(title)) return false;
		int ind = this.noteTitles.indexOf(title);
		this.notes.remove(ind);
		this.noteTitles.remove(ind);
		return true;
	}
	
	public String getNote(String title) {
		if(!this.noteTitles.contains(title)) return null;
		return this.notes.get(this.noteTitles.indexOf(title));
	}
	
	public ArrayList<String> getNoteTitles(){
		return this.noteTitles;
	}
	
	public ArrayList<String> getNotes(){
		return this.notes;
	}
	
	public boolean updateNote(String title, String newNote) {
		if(!this.noteTitles.contains(title)) return false;
		int ind = this.noteTitles.indexOf(title);
		this.notes.set(ind, newNote);
		return true;
	}
	
	public boolean updateNoteTitle(String title, String newTitle) {
		if(!this.noteTitles.contains(title)) return false;
		if(this.noteTitles.contains(newTitle)) return false;
		int ind = this.noteTitles.indexOf(title);
		this.noteTitles.set(ind, newTitle);
		return true;
	}
	
}
