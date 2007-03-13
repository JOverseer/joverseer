// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Data.java

package com.middleearthgames.orderchecker.io;

import java.awt.*;
import java.io.*;

class OCWindows
{

    OCWindows()
    {
        sizes = new Dimension[4];
        locations = new Point[4];
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension defaultSize = new Dimension(780, screenSize.height - 100);
        checkScreenSize(defaultSize);
        for(int i = 0; i < 4; i++)
        {
            sizes[i] = new Dimension(defaultSize.width, defaultSize.height);
            locations[i] = getScreenLocation(sizes[i]);
        }

    }

    void writeObject(ObjectOutputStream out)
        throws IOException
    {
        out.writeInt(1);
        out.writeInt(4);
        for(int i = 0; i < 4; i++)
        {
            out.writeInt(i);
            out.writeObject(sizes[i]);
            out.writeObject(locations[i]);
        }

    }

    void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        int version = in.readInt();
        int count = in.readInt();
        for(int i = 0; i < count; i++)
        {
            int index = in.readInt();
            sizes[index] = (Dimension)in.readObject();
            locations[index] = (Point)in.readObject();
        }

    }

    static Point getScreenLocation(Dimension d)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point((screenSize.width - d.width) / 2, (screenSize.height - d.height) / 2);
    }

    static boolean checkScreenSize(Dimension d)
    {
        boolean changed = false;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(screenSize.width < d.width)
        {
            d.width = screenSize.width - 20;
            changed = true;
        }
        if(screenSize.height < d.height)
        {
            d.height = screenSize.height - 100;
            changed = true;
        }
        return changed;
    }

    Dimension getWindowSize(int w)
    {
        if(w >= 0 && w < sizes.length)
            return sizes[w];
        else
            throw new RuntimeException("invalid window for getWindowSize()");
    }

    Point getWindowLocation(int w)
    {
        if(w >= 0 && w < locations.length)
            return locations[w];
        else
            throw new RuntimeException("invalid window for getWindowLocation()");
    }

    void setWindowSize(int w, Dimension d)
    {
        if(w >= 0 && w < sizes.length)
        {
            sizes[w] = d;
            return;
        } else
        {
            throw new RuntimeException("invalid window for setWindowSize()");
        }
    }

    void setWindowLocation(int w, Point p)
    {
        if(w >= 0 && w < locations.length)
        {
            locations[w] = p;
            return;
        } else
        {
            throw new RuntimeException("invalid window for setWindowLocation()");
        }
    }

    private static final int VERSION = 1;
    private static final int WINDOW_WIDTH_GAP = 20;
    private static final int WINDOW_HEIGHT_GAP = 100;
    private static final int WINDOW_DEFAULT_WIDTH = 780;
    private Dimension sizes[];
    private Point locations[];
}
