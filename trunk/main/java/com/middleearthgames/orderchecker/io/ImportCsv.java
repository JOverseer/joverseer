// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImportCsv.java

package com.middleearthgames.orderchecker.io;

import java.io.*;

abstract class ImportCsv
{

    protected ImportCsv(String filename)
    {
        tokenPosition = 0;
        tokens = null;
        this.filename = filename;
    }

    protected boolean openFile()
    {
        try {
            File inputFile = new File(filename);
            reader = new BufferedReader(new FileReader(inputFile));
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public void closeFile()
    {
        try
        {
            if(reader != null)
                reader.close();
        }
        catch(Exception ex) { }
    }

    protected String readLine()
    {
        try {
            String line = reader.readLine();
            return line;
        }
        catch (Exception ex) {
            return null;
        }
    }

    protected String getToken(String line, boolean init)
    {
        if(init)
        {
            tokenPosition = 0;
            tokens = line.split(",");
        }
        if(tokenPosition >= tokens.length)
            return null;
        String token = tokens[tokenPosition];
        tokenPosition++;
        if(token.length() == 0)
            return null;
        else
            return token;
    }

    protected void forceTokenAdvance()
    {
        tokenPosition++;
    }

    private String filename;
    private BufferedReader reader;
    private int tokenPosition;
    private String tokens[];
}
