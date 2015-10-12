// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ExampleFileFilter.java

package com.middleearthgames.orderchecker.gui;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;

class ExampleFileFilter extends FileFilter
{

    ExampleFileFilter()
    {
        this.filters = null;
        this.description = null;
        this.fullDescription = null;
        this.useExtensionsInDescription = true;
        this.filters = new Hashtable();
    }

    ExampleFileFilter(String extension)
    {
        this(extension, null);
    }

    ExampleFileFilter(String extension, String description)
    {
        this();
        if(extension != null)
            addExtension(extension);
        if(description != null)
            setDescription(description);
    }

    ExampleFileFilter(String filters[])
    {
        this(filters, null);
    }

    ExampleFileFilter(String filters[], String description)
    {
        this();
        for(int i = 0; i < filters.length; i++)
            addExtension(filters[i]);

        if(description != null)
            setDescription(description);
    }

    @Override
	public boolean accept(File f)
    {
        if(f != null)
        {
            if(f.isDirectory())
                return true;
            String extension = getExtension(f);
            if(extension != null && this.filters.get(getExtension(f)) != null)
                return true;
        }
        return false;
    }

    String getExtension(File f)
    {
        if(f != null)
        {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if(i > 0 && i < filename.length() - 1)
                return filename.substring(i + 1).toLowerCase();
        }
        return null;
    }

    void addExtension(String extension)
    {
        if(this.filters == null)
            this.filters = new Hashtable(5);
        this.filters.put(extension.toLowerCase(), this);
        this.fullDescription = null;
    }

    @Override
	public String getDescription()
    {
        if(this.fullDescription == null)
            if(this.description == null || isExtensionListInDescription())
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

    void setExtensionListInDescription(boolean b)
    {
        this.useExtensionsInDescription = b;
        this.fullDescription = null;
    }

    boolean isExtensionListInDescription()
    {
        return this.useExtensionsInDescription;
    }

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";
    private Hashtable filters;
    private String description;
    private String fullDescription;
    private boolean useExtensionsInDescription;

}
