// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Data.java

package com.middleearthgames.orderchecker.io;

import com.middleearthgames.orderchecker.Nation;
import java.io.*;
import java.util.Vector;
import javax.swing.JCheckBox;

// Referenced classes of package com.middleearthgames.orderchecker.io:
//            Data

class GameData
{

    GameData()
    {
        this.gameType = "";
        this.alignments = new int[25];
        this.checkBoxes = null;
        this.lastTurnProcessed = 0;
        this.characters = new Vector();
        this.charNotes = new Vector();
    }

    GameData(Nation nation)
    {
        this.gameType = "";
        this.alignments = new int[25];
        this.checkBoxes = null;
        this.lastTurnProcessed = 0;
        this.characters = new Vector();
        this.charNotes = new Vector();
        this.game = nation.getGame();
        this.gameType = nation.getGameType();
        for(int i = 0; i < 25; i++)
        {
            if(i >= 0 && i < 10)
            {
                this.alignments[i] = 1;
                continue;
            }
            if(i >= 10 && i < 20)
                this.alignments[i] = 2;
            else
                this.alignments[i] = 0;
        }

    }

    void writeObject(ObjectOutputStream out)
        throws IOException
    {
        out.writeInt(4);
        out.writeInt(this.game);
        out.writeObject(this.alignments);
        out.writeObject(this.characters);
        out.writeObject(this.charNotes);
        out.writeUTF(this.gameType);
        out.writeInt(this.lastTurnProcessed);
        saveCheckBoxes(out);
    }

    void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        int version = in.readInt();
        this.game = in.readInt();
        this.alignments = (int[])(int[])in.readObject();
        if(version < 4)
            this.checkBoxes = (Vector)in.readObject();
        this.characters = (Vector)in.readObject();
        this.charNotes = (Vector)in.readObject();
        if(version >= 2)
            this.gameType = in.readUTF();
        if(version >= 3)
            this.lastTurnProcessed = in.readInt();
        if(version >= 4)
            loadCheckBoxes(in);
    }

    private void saveCheckBoxes(ObjectOutputStream out)
        throws IOException
    {
        int numCheckBoxes = this.checkBoxes.size();
        out.writeInt(numCheckBoxes);
        for(int i = 0; i < numCheckBoxes; i++)
        {
            JCheckBox box = (JCheckBox)this.checkBoxes.get(i);
            String text = box.getText();
            boolean active = box.isSelected();
            out.writeUTF(text);
            out.writeBoolean(active);
        }

    }

    private void loadCheckBoxes(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        this.checkBoxes = new Vector();
        int numCheckBoxes = in.readInt();
        for(int i = 0; i < numCheckBoxes; i++)
        {
            String content = in.readUTF();
            boolean state = in.readBoolean();
            JCheckBox box = new JCheckBox(content, state);
            this.checkBoxes.add(box);
        }

    }

    private int findCharacter(String name)
    {
        if(this.characters != null)
        {
            for(int i = 0; i < this.characters.size(); i++)
            {
                String character = (String)this.characters.get(i);
                if(character.equals(name))
                    return i;
            }

        }
        return -1;
    }

    int getGame()
    {
        return this.game;
    }

    String getGameType()
    {
        return this.gameType;
    }

    int getNationAlignment(int index)
    {
        if(index >= 0 && index < this.alignments.length)
            return this.alignments[index];
        else
            return 0;
    }

    Vector getCheckBoxes()
    {
        return this.checkBoxes;
    }

    String getCharacterNotes(String name)
    {
        int index = findCharacter(name);
        if(index != -1)
        {
            String notes = (String)this.charNotes.get(index);
            return notes;
        } else
        {
            return "";
        }
    }

    void setGameType(String type)
    {
        this.gameType = type;
    }

    void setNationAlignment(int index, int alignment)
    {
        if(index >= 0 && index < this.alignments.length)
            this.alignments[index] = alignment;
    }

    void setCheckBoxes(Vector newList, int turn)
    {
        if(this.checkBoxes == null || this.lastTurnProcessed != turn)
        {
            this.lastTurnProcessed = turn;
            this.checkBoxes = (Vector)newList.clone();
            return;
        }
        for(int i = 0; i < newList.size(); i++)
        {
            JCheckBox box = (JCheckBox)newList.get(i);
            JCheckBox oldBox = Data.isDuplicateRequest(this.checkBoxes, box);
            if(oldBox != null)
                oldBox.setSelected(box.isSelected());
            else
                this.checkBoxes.add(box);
        }

    }

    void setCharacterNotes(String name, String notes)
    {
        int index = findCharacter(name);
        if(index == -1)
        {
            this.characters.add(name);
            this.charNotes.add(notes);
        } else
        {
            this.charNotes.set(index, notes);
        }
    }

    @Override
	public String toString()
    {
        StringBuffer desc = new StringBuffer();
        desc.append("Game #" + this.game);
        if(this.gameType.length() > 0)
            desc.append(", " + this.gameType);
        return desc.toString();
    }

    @SuppressWarnings("unused")
	private static final int VERSION = 4;
    private int game;
    private String gameType;
    private int alignments[];
    private Vector checkBoxes;
    private int lastTurnProcessed;
    private Vector characters;
    private Vector charNotes;
}
