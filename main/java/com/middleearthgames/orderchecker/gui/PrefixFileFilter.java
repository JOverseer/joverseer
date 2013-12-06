// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PrefixFileFilter.java

package com.middleearthgames.orderchecker.gui;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;

class PrefixFileFilter extends FileFilter
{

    PrefixFileFilter()
    {
        this.filters = null;
        this.description = null;
        this.fullDescription = null;
        this.filters = new Hashtable();
    }

    PrefixFileFilter(String prefix)
    {
        this(prefix, null);
    }

    PrefixFileFilter(String prefix, String description)
    {
        this();
        if(prefix != null)
            addPrefix(prefix);
        if(description != null)
            setDescription(description);
    }

    PrefixFileFilter(String filters[])
    {
        this(filters, null);
    }

    PrefixFileFilter(String filters[], String description)
    {
        this();
        for(int i = 0; i < filters.length; i++)
            addPrefix(filters[i]);

        if(description != null)
            setDescription(description);
    }

    @Override
	public boolean accept(File f)
    {
label0:
        {
            if(f == null)
                break label0;
            if(f.isDirectory())
                return true;
            String filename = f.getName();
            Enumeration itr = this.filters.keys();
            String prefix;
            do
            {
                if(!itr.hasMoreElements())
                    break label0;
                prefix = (String)itr.nextElement();
            } while(filename.indexOf(prefix) != 0);
            return true;
        }
        return false;
    }

    void addPrefix(String prefix)
    {
        if(this.filters == null)
            this.filters = new Hashtable(5);
        this.filters.put(prefix.toLowerCase(), this);
        this.fullDescription = null;
    }

    @Override
	public String getDescription()
    {
        if(this.fullDescription == null)
            if(this.description == null)
            {
                this.fullDescription = this.description != null ? this.description + " (" : "(";
                Enumeration extensions = this.filters.keys();
                if(extensions != null)
                    for(this.fullDescription += "." + (String)extensions.nextElement(); extensions.hasMoreElements(); this.fullDescription += ", ." + (String)extensions.nextElement());
                this.fullDescription += ")";
            } else
            {
                this.fullDescription = this.description;
            }
        return this.fullDescription;
    }

    void setDescription(String description)
    {
        this.description = description;
        this.fullDescription = null;
    }

    private Hashtable filters;
    private String description;
    private String fullDescription;
}
