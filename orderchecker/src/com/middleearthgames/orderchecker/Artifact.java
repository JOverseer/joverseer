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
        return this.artifact;
    }

    String getName()
    {
        return this.name;
    }

    int getGame()
    {
        return this.game;
    }

    int getAlignment()
    {
        return this.alignment;
    }

    String getProperties()
    {
        return this.properties;
    }

    private String alignmentString()
    {
        if(this.alignment >= 0 && this.alignment < 3)
        {
            return alignmentAbbrev[this.alignment];
        } else
        {
            return "";
        }
    }

    @Override
	public String toString()
    {
        return this.name + " (#" + this.artifact + "," + alignmentString() + "," + this.properties + ")";
    }

}
