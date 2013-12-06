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
        this.tokenPosition = 0;
        this.tokens = null;
        this.filename = filename;
    }

    protected boolean openFile()
    {
        try {
            File inputFile = new File(this.filename);
            this.reader = new BufferedReader(new FileReader(inputFile));
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
            if(this.reader != null)
                this.reader.close();
        }
        catch(Exception ex) { }
    }

    protected String readLine()
    {
        try {
            String line = this.reader.readLine();
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
            this.tokenPosition = 0;
            this.tokens = line.split(",");
        }
        if(this.tokenPosition >= this.tokens.length)
            return null;
        String token = this.tokens[this.tokenPosition];
        this.tokenPosition++;
        if(token.length() == 0)
            return null;
        else
            return token;
    }

    protected void forceTokenAdvance()
    {
        this.tokenPosition++;
    }

    private String filename;
    private BufferedReader reader;
    private int tokenPosition;
    private String tokens[];
}
