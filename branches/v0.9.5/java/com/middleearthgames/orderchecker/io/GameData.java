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
        gameType = "";
        alignments = new int[25];
        checkBoxes = null;
        lastTurnProcessed = 0;
        characters = new Vector();
        charNotes = new Vector();
    }

    GameData(Nation nation)
    {
        gameType = "";
        alignments = new int[25];
        checkBoxes = null;
        lastTurnProcessed = 0;
        characters = new Vector();
        charNotes = new Vector();
        game = nation.getGame();
        gameType = nation.getGameType();
        for(int i = 0; i < 25; i++)
        {
            if(i >= 0 && i < 10)
            {
                alignments[i] = 1;
                continue;
            }
            if(i >= 10 && i < 20)
                alignments[i] = 2;
            else
                alignments[i] = 0;
        }

    }

    void writeObject(ObjectOutputStream out)
        throws IOException
    {
        out.writeInt(4);
        out.writeInt(game);
        out.writeObject(alignments);
        out.writeObject(characters);
        out.writeObject(charNotes);
        out.writeUTF(gameType);
        out.writeInt(lastTurnProcessed);
        saveCheckBoxes(out);
    }

    void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        int version = in.readInt();
        game = in.readInt();
        alignments = (int[])(int[])in.readObject();
        if(version < 4)
            checkBoxes = (Vector)in.readObject();
        characters = (Vector)in.readObject();
        charNotes = (Vector)in.readObject();
        if(version >= 2)
            gameType = in.readUTF();
        if(version >= 3)
            lastTurnProcessed = in.readInt();
        if(version >= 4)
            loadCheckBoxes(in);
    }

    private void saveCheckBoxes(ObjectOutputStream out)
        throws IOException
    {
        int numCheckBoxes = checkBoxes.size();
        out.writeInt(numCheckBoxes);
        for(int i = 0; i < numCheckBoxes; i++)
        {
            JCheckBox box = (JCheckBox)checkBoxes.get(i);
            String text = box.getText();
            boolean active = box.isSelected();
            out.writeUTF(text);
            out.writeBoolean(active);
        }

    }

    private void loadCheckBoxes(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        checkBoxes = new Vector();
        int numCheckBoxes = in.readInt();
        for(int i = 0; i < numCheckBoxes; i++)
        {
            String content = in.readUTF();
            boolean state = in.readBoolean();
            JCheckBox box = new JCheckBox(content, state);
            checkBoxes.add(box);
        }

    }

    private int findCharacter(String name)
    {
        if(characters != null)
        {
            for(int i = 0; i < characters.size(); i++)
            {
                String character = (String)characters.get(i);
                if(character.equals(name))
                    return i;
            }

        }
        return -1;
    }

    int getGame()
    {
        return game;
    }

    String getGameType()
    {
        return gameType;
    }

    int getNationAlignment(int index)
    {
        if(index >= 0 && index < alignments.length)
            return alignments[index];
        else
            return 0;
    }

    Vector getCheckBoxes()
    {
        return checkBoxes;
    }

    String getCharacterNotes(String name)
    {
        int index = findCharacter(name);
        if(index != -1)
        {
            String notes = (String)charNotes.get(index);
            return notes;
        } else
        {
            return "";
        }
    }

    void setGameType(String type)
    {
        gameType = type;
    }

    void setNationAlignment(int index, int alignment)
    {
        if(index >= 0 && index < alignments.length)
            alignments[index] = alignment;
    }

    void setCheckBoxes(Vector newList, int turn)
    {
        if(checkBoxes == null || lastTurnProcessed != turn)
        {
            lastTurnProcessed = turn;
            checkBoxes = (Vector)newList.clone();
            return;
        }
        for(int i = 0; i < newList.size(); i++)
        {
            JCheckBox box = (JCheckBox)newList.get(i);
            JCheckBox oldBox = Data.isDuplicateRequest(checkBoxes, box);
            if(oldBox != null)
                oldBox.setSelected(box.isSelected());
            else
                checkBoxes.add(box);
        }

    }

    void setCharacterNotes(String name, String notes)
    {
        int index = findCharacter(name);
        if(index == -1)
        {
            characters.add(name);
            charNotes.add(notes);
        } else
        {
            charNotes.set(index, notes);
        }
    }

    public String toString()
    {
        StringBuffer desc = new StringBuffer();
        desc.append("Game #" + game);
        if(gameType.length() > 0)
            desc.append(", " + gameType);
        return desc.toString();
    }

    private static final int VERSION = 4;
    private int game;
    private String gameType;
    private int alignments[];
    private Vector checkBoxes;
    private int lastTurnProcessed;
    private Vector characters;
    private Vector charNotes;
}
