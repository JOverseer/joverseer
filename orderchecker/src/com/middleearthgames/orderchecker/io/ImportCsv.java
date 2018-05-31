// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImportCsv.java

package com.middleearthgames.orderchecker.io;

import java.io.*;
import java.net.URL;

import com.middleearthgames.orderchecker.Main;

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

    protected boolean openStream()
    {
    	return this.openStream(this.filename);
    }
    protected boolean openStream(String name)
    {
    	InputStream is = this.getClass().getResourceAsStream(name);
    	if (is == null) {
    		return false;
    	}
  		this.reader = new BufferedReader(new InputStreamReader(is));
  		return true;
    }
    // for static information we can default to using the jar file.
    public boolean openFileOrStream()
    {
    	File file = new File(this.filename);
    	if (file.exists()) {
    		return this.openFile();
    	} else {
    		URL loc= Main.class.getResource("Main.class");
    		if (loc.getProtocol().equals("jar")) {
        		return openStream("/metadata/orderchecker/" + this.filename);
    		}
    	}
    	return false;
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

    protected String filename;
    private BufferedReader reader;
    private int tokenPosition;
    private String tokens[];
}
