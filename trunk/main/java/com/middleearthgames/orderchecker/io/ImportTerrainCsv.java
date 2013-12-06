// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImportTerrainCsv.java

package com.middleearthgames.orderchecker.io;

import com.middleearthgames.orderchecker.Hex;
import com.middleearthgames.orderchecker.Map;

// Referenced classes of package com.middleearthgames.orderchecker.io:
//            ImportCsv

public class ImportTerrainCsv extends ImportCsv
{

    public ImportTerrainCsv(String filename, Map map)
    {
        super(filename);
        this.map = map;
    }

    public boolean getMapInformation()
    {
        return openFile();
    }

    public String parseTerrain()
    {
        String line = readLine();
        if(line == null || !line.equalsIgnoreCase(":Table:TERRAIN"))
            return "Terrain data file does not appear to be valid!";
        for(line = readLine(); line != null && !line.equalsIgnoreCase(":Table:ARMY"); line = readLine())
        {
            if(line.equalsIgnoreCase(":Table:TRAFFIC"))
                continue;
            String result = parseLine(line);
            if(result != null)
                return result;
        }

        return null;
    }

    private String parseLine(String line)
    {
        String param = getToken(line, true);
        if(param == null || param.length() == 0)
            return "Couldn't extract location!\nThe terrain file appears to be invalid.";
        int location;
        try
        {
            String locationString = param.substring(1, param.length() - 1);
            location = Integer.parseInt(locationString);
        }
        catch(NumberFormatException ex)
        {
            return "Couldn't extract location from: " + param + ".\n" + "The terrain file appears to be invalid!";
        }
        Hex hex = this.map.findHex(location);
        boolean foundHex = true;
        if(hex == null)
        {
            foundHex = false;
            hex = new Hex(location);
        }
        param = getToken(line, false);
        if(param == null || param.length() == 0)
            return "Couldn't extract the terrain type @ location " + location + "!\nThe terrain file appears to be invalid!";
        int terrain;
        try
        {
            terrain = Integer.parseInt(param);
        }
        catch(NumberFormatException ex)
        {
            return "Couldn't extract the terrain type from:  " + param + ".\n" + "The terrain file appears to be invalid!";
        }
        if(terrain > 0)
        {
            hex.setTerrain(terrain);
            param = getToken(line, false);
            if(param != null)
                return "Too many parameters for location " + location + "!\n" + "The terrain file appears to be invalid!";
        } else
        {
            param = getToken(line, false);
            if(param == null || param.length() == 0)
                return "Missing direction at location " + location + "!\nThe terrain file appears to be invalid!";
            Integer direction;
            try
            {
                direction = new Integer(param);
            }
            catch(Exception ex)
            {
                return "Could not extract the direction from: " + param + ".\n" + "The terrain file appears to be invalid!";
            }
            hex.addDirection(direction);
            param = getToken(line, false);
            if(param == null || param.length() == 0)
                return "Missing feature at location " + location + "!\n" + "The terrain file appears to be invalid!";
            try
            {
                Integer feature = new Integer(param);
                hex.addFeature(feature);
            }
            catch(Exception ex)
            {
                return "Could not extract the feature from: " + param + ".\n" + "The terrain file appears to be invalid!";
            }
        }
        if(!foundHex)
            this.map.addHex(hex);
        return null;
    }

    private Map map;
}
