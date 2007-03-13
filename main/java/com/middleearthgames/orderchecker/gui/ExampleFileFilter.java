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
        filters = null;
        description = null;
        fullDescription = null;
        useExtensionsInDescription = true;
        filters = new Hashtable();
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

    public boolean accept(File f)
    {
        if(f != null)
        {
            if(f.isDirectory())
                return true;
            String extension = getExtension(f);
            if(extension != null && filters.get(getExtension(f)) != null)
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
        if(filters == null)
            filters = new Hashtable(5);
        filters.put(extension.toLowerCase(), this);
        fullDescription = null;
    }

    public String getDescription()
    {
        if(fullDescription == null)
            if(description == null || isExtensionListInDescription())
            {
                fullDescription = description != null ? description + " (" : "(";
                Enumeration extensions = filters.keys();
                if(extensions != null)
                    for(fullDescription += "." + (String)extensions.nextElement(); extensions.hasMoreElements(); fullDescription += ", ." + (String)extensions.nextElement());
                fullDescription += ")";
            } else
            {
                fullDescription = description;
            }
        return fullDescription;
    }

    void setDescription(String description)
    {
        this.description = description;
        fullDescription = null;
    }

    void setExtensionListInDescription(boolean b)
    {
        useExtensionsInDescription = b;
        fullDescription = null;
    }

    boolean isExtensionListInDescription()
    {
        return useExtensionsInDescription;
    }

    private static String TYPE_UNKNOWN = "Type Unknown";
    private static String HIDDEN_FILE = "Hidden File";
    private Hashtable filters;
    private String description;
    private String fullDescription;
    private boolean useExtensionsInDescription;

}
