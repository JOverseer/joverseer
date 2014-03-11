// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Data.java

package com.middleearthgames.orderchecker.io;

import com.middleearthgames.orderchecker.Nation;
import java.awt.Dimension;
import java.awt.Point;
import java.io.*;
import java.util.Vector;
import javax.swing.JCheckBox;

// Referenced classes of package com.middleearthgames.orderchecker.io:
//            OCWindows, GameData

public class Data
{

    public Data()
    {
        this.turnPath = "";
        this.ordersPath = "";
        this.gameType = 1;
        this.exportFormat = 0;
        this.sortFormat = 0;
        this.endOfTurnInfo = true;
        this.showCharInfo = true;
        this.showAllResults = false;
        this.gameData = new Vector();
        this.windowSizes = new OCWindows();
    }

    public static JCheckBox isDuplicateRequest(Vector list, JCheckBox request)
    {
        for(int i = 0; i < list.size(); i++)
        {
            JCheckBox box = (JCheckBox)list.get(i);
            if(request.getText().equals(box.getText()))
                return box;
        }

        return null;
    }

    public void writeObject(ObjectOutputStream out)
        throws IOException
    {
        out.writeInt(7);
        out.writeUTF(this.turnPath);
        out.writeUTF(this.ordersPath);
        out.writeInt(this.gameType);
        out.writeInt(this.gameData.size());
        for(int i = 0; i < this.gameData.size(); i++)
        {
            GameData data = (GameData)this.gameData.get(i);
            data.writeObject(out);
        }

        this.windowSizes.writeObject(out);
        out.writeInt(this.exportFormat);
        out.writeInt(this.sortFormat);
        out.writeBoolean(this.endOfTurnInfo);
        out.writeBoolean(this.showCharInfo);
        out.writeBoolean(this.showAllResults);
    }

    public void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        int version = in.readInt();
        this.turnPath = in.readUTF();
        this.ordersPath = in.readUTF();
        if(version < 6)
        {
            @SuppressWarnings("unused")
			String rulesPath = in.readUTF();
            String terrainPath = in.readUTF();
            for(int i = 0; i < terrainFiles.length; i++)
                if(terrainPath.equalsIgnoreCase(terrainFiles[i]))
                    this.gameType = i;

        } else
        {
            this.gameType = in.readInt();
        }
        int count = in.readInt();
        for(int i = 0; i < count; i++)
        {
            GameData data = new GameData();
            data.readObject(in);
            this.gameData.add(data);
        }

        this.windowSizes.readObject(in);
        if(version >= 2)
            this.exportFormat = in.readInt();
        if(version >= 7)
            this.sortFormat = in.readInt();
        if(version >= 3)
            this.endOfTurnInfo = in.readBoolean();
        if(version >= 4)
            this.showCharInfo = in.readBoolean();
        if(version >= 5)
            this.showAllResults = in.readBoolean();
    }

    public GameData findGame(Nation nation)
    {
        for(int i = 0; i < this.gameData.size(); i++)
        {
            GameData data = (GameData)this.gameData.get(i);
            if(data.getGame() == nation.getGame())
            {
                if(data.getGameType().length() == 0)
                    data.setGameType(nation.getGameType());
                return data;
            }
        }

        GameData newGame = new GameData(nation);
        this.gameData.add(newGame);
        return newGame;
    }

    public String getTurnResultsPath()
    {
        return this.turnPath;
    }

    public String getOrdersPath()
    {
        return this.ordersPath;
    }

    public int getGameType()
    {
        return this.gameType;
    }

    public String[] getGameDescriptions()
    {
        return gameDesc;
    }

    public String getGameDescription()
    {
        return gameDesc[this.gameType];
    }

    public String getRulesPath()
    {
        return dataDirectory + rulesFile;
    }

    public String getTerrainPath()
    {
        return dataDirectory + terrainFiles[this.gameType];
    }

    
    public static String getDataDirectory() {
        return dataDirectory;
    }

    
    public static void setDataDirectory(String dataDirectory) {
        Data.dataDirectory = dataDirectory;
    }

    public Vector getGames()
    {
        return this.gameData;
    }

    public int getNationAlignment(int number, Nation nation)
    {
        GameData data = findGame(nation);
        return data.getNationAlignment(number);
    }

    public Vector getCheckBoxes(Nation nation)
    {
        GameData data = findGame(nation);
        return data.getCheckBoxes();
    }

    public String getCharacterNotes(Nation nation, String name)
    {
        GameData data = findGame(nation);
        return data.getCharacterNotes(name);
    }

    public int getExportFormat()
    {
        return this.exportFormat;
    }

    public int getSortFormat()
    {
        return this.sortFormat;
    }

    public boolean getEndOfTurnInfo()
    {
        return this.endOfTurnInfo;
    }

    public boolean getShowCharNotes()
    {
        return this.showCharInfo;
    }

    public boolean getShowAllInfo()
    {
        return this.showAllResults;
    }

    public void setTurnResultsPath(String path)
    {
        this.turnPath = path;
    }

    public void setOrdersPath(String path)
    {
        this.ordersPath = path;
    }

    public void setGameType(String type)
    {
        for(int i = 0; i < gameDesc.length; i++)
            if(type.equals(gameDesc[i]))
                this.gameType = i;

    }

    public void setNationAlignment(int number, int alignment, Nation nation)
    {
        GameData data = findGame(nation);
        data.setNationAlignment(number, alignment);
    }

    public void setCheckBoxes(Vector checkBoxes, Nation nation)
    {
        GameData data = findGame(nation);
        data.setCheckBoxes(checkBoxes, nation.getTurn());
    }

    public void setCharacterNotes(Nation nation, String name, String notes)
    {
        GameData data = findGame(nation);
        data.setCharacterNotes(name, notes);
    }

    public void setExportFormat(int format)
    {
        this.exportFormat = format;
    }

    public void setSortFormat(int format)
    {
        this.sortFormat = format;
    }

    public void setEndOfTurnInfo(boolean value)
    {
        this.endOfTurnInfo = value;
    }

    public void setShowCharNotes(boolean value)
    {
        this.showCharInfo = value;
    }

    public void setShowAllInfo(boolean value)
    {
        this.showAllResults = value;
    }

    public static Point getScreenLocation(Dimension d)
    {
        return OCWindows.getScreenLocation(d);
    }

    public static void checkScreenSize(Dimension d)
    {
        OCWindows.checkScreenSize(d);
    }

    public Dimension getWindowSize(int w)
    {
        return this.windowSizes.getWindowSize(w);
    }

    public Point getWindowLocation(int w)
    {
        return this.windowSizes.getWindowLocation(w);
    }

    public void setWindowSize(int w, Dimension d)
    {
        this.windowSizes.setWindowSize(w, d);
    }

    public void setWindowLocation(int w, Point p)
    {
        this.windowSizes.setWindowLocation(w, p);
    }

    @SuppressWarnings("unused")
	private static final int VERSION = 7;
    public static final int NEUTRAL = 0;
    public static final int FREE_PEOPLE = 1;
    public static final int DARK_SERVANT = 2;
    public static final int TOTAL_NATIONS = 25;
    //private static final int GAME_TYPES = 4;
    public static final int GAME_1650 = 0;
    public static final int GAME_2950 = 1;
    public static final int GAME_1000 = 2;
    public static final int GAME_BOFA = 3;
    public static final int GAME_UW = 4;
    public static final int GAME_KS = 5;
    private static String gameDesc[] = {
        "1650", "2950", "Fourth Age", "BOFA", "Untold War", "Kin Strife"
    };
    public static final int NORMAL_FORMAT = 0;
    public static final int HTML_FORMAT = 1;
    public static final int BBCODE_FORMAT = 2;
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_LOCATION = 1;
    static final int OC_WINDOWS = 4;
    public static final int MAIN_WINDOW = 0;
    public static final int FILE_EXPORT_WINDOW = 1;
    public static final int HELP_INFO_WINDOW = 2;
    public static final int CHAR_NOTES_WINDOW = 3;
    private static String dataDirectory = "data/";
    private static String rulesFile = "ruleset.csv";
    private static String terrainFiles[] = {
        "1650.game", "2950.game", "fa.game", "bofa.game", "uw.game", "ks.game"
    };
    private String turnPath;
    private String ordersPath;
    private int gameType;
    private int exportFormat;
    private int sortFormat;
    private boolean endOfTurnInfo;
    private boolean showCharInfo;
    private boolean showAllResults;
    private Vector gameData;
    private OCWindows windowSizes;

}
