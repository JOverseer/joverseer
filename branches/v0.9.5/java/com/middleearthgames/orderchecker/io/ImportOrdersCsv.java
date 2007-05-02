// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImportOrdersCsv.java

package com.middleearthgames.orderchecker.io;

import com.middleearthgames.orderchecker.*;
import com.middleearthgames.orderchecker.Character;

// Referenced classes of package com.middleearthgames.orderchecker.io:
//            ImportCsv

public class ImportOrdersCsv extends ImportCsv
{

    public ImportOrdersCsv(String filename, Nation nation)
    {
        super(filename);
        this.nation = nation;
    }

    public boolean getOrders()
    {
        return openFile();
    }

    public String parseOrders()
    {
        String gameStr;
        String numberStr;
        String secretStr;
        String line = readLine();
        if(line == null || !line.equalsIgnoreCase("BEGINMEAUTOINPUT"))
            return "Orders file does not appear to be valid!";
        try {
            line = readLine();
            gameStr = getToken(line, true);
            numberStr = getToken(line, false);
            String accountStr = getToken(line, false);
            secretStr = getToken(line, false);
            int game;
            int number;
            int secret;
            game = Integer.parseInt(gameStr);
            number = Integer.parseInt(numberStr);
            secret = Integer.parseInt(secretStr);
            if(nation.getGame() != game || nation.getNation() != number)
                return "The turn results file is for game " + nation.getGame() + ", nation " + nation.getNation() + ".\n" + "The orders file is for game " + game + ", nation " + number + ".";
            if(nation.getSecret() != secret)
                return "The game and nation are correct, but the orders file appears to be for a different turn.";
        }        
        catch (Exception ex) {
            return "The game number, turn number, or secret code could not be determined from the order file.";
            //return "Game number could not be read from orders file.\nInvalid file?";
        }
        for(line = readLine(); line != null && !line.equalsIgnoreCase("ENDMEAUTOINPUT"); line = readLine())
        {
            if(line == null)
                continue;
            String result = parseLine(line);
            if(result != null)
                return result;
        }

        return null;
    }

    private String parseLine(String line)
    {
        String param;
        Character character;
        param = getToken(line, true);
        if(param == null)
            return null;
        character = nation.findCharacterById(param);
        if(character == null)
            return "Could not find character: " + param;
        try {
            param = getToken(line, false);
            if(param == null || param.length() == 0)
            {
                Order order = new Order(character, 9999);
                character.addOrder(order);
                return null;
            }
            Order order = new Order(character, Integer.parseInt(param));
            parseOrderParameters(order, line);
            character.addOrder(order);
            return null;
        }
        catch (Exception ex) {
            return "Could not convert " + param + " to an order number for " + character + "!";
        }
    }

    private void parseOrderParameters(Order order, String line)
    {
        int endCount = 0;
        for(String param = getToken(line, false); param != null && endCount < 3; param = getToken(line, false))
        {
            if(param.equals("--"))
            {
                endCount++;
                continue;
            }
            if(endCount > 0)
            {
                for(int i = 0; i < endCount; i++)
                    order.addParameter("--");

                endCount = 0;
                order.addParameter(param);
            } else
            {
                order.addParameter(param);
            }
        }

    }

    private Nation nation;
}
