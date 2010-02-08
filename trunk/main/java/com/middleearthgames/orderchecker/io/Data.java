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
        turnPath = "";
        ordersPath = "";
        gameType = 1;
        exportFormat = 0;
        sortFormat = 0;
        endOfTurnInfo = true;
        showCharInfo = true;
        showAllResults = false;
        gameData = new Vector();
        windowSizes = new OCWindows();
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
        out.writeUTF(turnPath);
        out.writeUTF(ordersPath);
        out.writeInt(gameType);
        out.writeInt(gameData.size());
        for(int i = 0; i < gameData.size(); i++)
        {
            GameData data = (GameData)gameData.get(i);
            data.writeObject(out);
        }

        windowSizes.writeObject(out);
        out.writeInt(exportFormat);
        out.writeInt(sortFormat);
        out.writeBoolean(endOfTurnInfo);
        out.writeBoolean(showCharInfo);
        out.writeBoolean(showAllResults);
    }

    public void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        int version = in.readInt();
        turnPath = in.readUTF();
        ordersPath = in.readUTF();
        if(version < 6)
        {
            String rulesPath = in.readUTF();
            String terrainPath = in.readUTF();
            for(int i = 0; i < terrainFiles.length; i++)
                if(terrainPath.equalsIgnoreCase(terrainFiles[i]))
                    gameType = i;

        } else
        {
            gameType = in.readInt();
        }
        int count = in.readInt();
        for(int i = 0; i < count; i++)
        {
            GameData data = new GameData();
            data.readObject(in);
            gameData.add(data);
        }

        windowSizes.readObject(in);
        if(version >= 2)
            exportFormat = in.readInt();
        if(version >= 7)
            sortFormat = in.readInt();
        if(version >= 3)
            endOfTurnInfo = in.readBoolean();
        if(version >= 4)
            showCharInfo = in.readBoolean();
        if(version >= 5)
            showAllResults = in.readBoolean();
    }

    private GameData findGame(Nation nation)
    {
        for(int i = 0; i < gameData.size(); i++)
        {
            GameData data = (GameData)gameData.get(i);
            if(data.getGame() == nation.getGame())
            {
                if(data.getGameType().length() == 0)
                    data.setGameType(nation.getGameType());
                return data;
            }
        }

        GameData newGame = new GameData(nation);
        gameData.add(newGame);
        return newGame;
    }

    public String getTurnResultsPath()
    {
        return turnPath;
    }

    public String getOrdersPath()
    {
        return ordersPath;
    }

    public int getGameType()
    {
        return gameType;
    }

    public String[] getGameDescriptions()
    {
        return gameDesc;
    }

    public String getGameDescription()
    {
        return gameDesc[gameType];
    }

    public String getRulesPath()
    {
        return dataDirectory + rulesFile;
    }

    public String getTerrainPath()
    {
        return dataDirectory + terrainFiles[gameType];
    }

    
    public static String getDataDirectory() {
        return dataDirectory;
    }

    
    public static void setDataDirectory(String dataDirectory) {
        Data.dataDirectory = dataDirectory;
    }

    public Vector getGames()
    {
        return gameData;
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
        return exportFormat;
    }

    public int getSortFormat()
    {
        return sortFormat;
    }

    public boolean getEndOfTurnInfo()
    {
        return endOfTurnInfo;
    }

    public boolean getShowCharNotes()
    {
        return showCharInfo;
    }

    public boolean getShowAllInfo()
    {
        return showAllResults;
    }

    public void setTurnResultsPath(String path)
    {
        turnPath = path;
    }

    public void setOrdersPath(String path)
    {
        ordersPath = path;
    }

    public void setGameType(String type)
    {
        for(int i = 0; i < gameDesc.length; i++)
            if(type.equals(gameDesc[i]))
                gameType = i;

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
        exportFormat = format;
    }

    public void setSortFormat(int format)
    {
        sortFormat = format;
    }

    public void setEndOfTurnInfo(boolean value)
    {
        endOfTurnInfo = value;
    }

    public void setShowCharNotes(boolean value)
    {
        showCharInfo = value;
    }

    public void setShowAllInfo(boolean value)
    {
        showAllResults = value;
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
        return windowSizes.getWindowSize(w);
    }

    public Point getWindowLocation(int w)
    {
        return windowSizes.getWindowLocation(w);
    }

    public void setWindowSize(int w, Dimension d)
    {
        windowSizes.setWindowSize(w, d);
    }

    public void setWindowLocation(int w, Point p)
    {
        windowSizes.setWindowLocation(w, p);
    }

    private static final int VERSION = 7;
    public static final int NEUTRAL = 0;
    public static final int FREE_PEOPLE = 1;
    public static final int DARK_SERVANT = 2;
    public static final int TOTAL_NATIONS = 25;
    private static final int GAME_TYPES = 4;
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
