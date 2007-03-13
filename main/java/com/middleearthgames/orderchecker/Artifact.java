// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Artifact.java

package com.middleearthgames.orderchecker;


class Artifact
{

    static final int ALIGNMENT_NEUTRAL = 0;
    static final int ALIGNMENT_EVIL = 1;
    static final int ALIGNMENT_GOOD = 2;
    static final String alignmentAbbrev[] = {
        "N", "E", "G"
    };
    private final int artifact;
    private final int game;
    private final String name;
    private final int alignment;
    private final String properties;

    Artifact(int artifact, String name, int game, int alignment, String properties)
    {
        this.artifact = artifact;
        this.name = name;
        this.game = game;
        this.alignment = alignment;
        this.properties = properties;
    }

    int getArtifactNumber()
    {
        return artifact;
    }

    String getName()
    {
        return name;
    }

    int getGame()
    {
        return game;
    }

    int getAlignment()
    {
        return alignment;
    }

    String getProperties()
    {
        return properties;
    }

    private String alignmentString()
    {
        if(alignment >= 0 && alignment < 3)
        {
            return alignmentAbbrev[alignment];
        } else
        {
            return "";
        }
    }

    public String toString()
    {
        return name + " (#" + artifact + "," + alignmentString() + "," + properties + ")";
    }

}
