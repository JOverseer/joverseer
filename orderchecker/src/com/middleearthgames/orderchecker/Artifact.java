// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe
// Source File Name:   Artifact.java

package com.middleearthgames.orderchecker;


public class Artifact
{

    public static final int ALIGNMENT_NEUTRAL = 0;
    public static final int ALIGNMENT_EVIL = 1;
    public static final int ALIGNMENT_GOOD = 2;
    static final String alignmentAbbrev[] = {
        "N", "E", "G"
    };
    private final int artifact;
    private final int game;
    private final String name;
    private final int alignment;
    private final String properties;

    public Artifact(int artifact, String name, int game, int alignment, String properties)
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
	/**
	 *
	 * @param word
	 * @return the code appropriate for the supplied alignment
	 */
	public static int encodeAlignment(String word) {
		switch (word.charAt(0)) {
		case 'E':
		case 'e':
		case 'U':
		case 'u': // evil or usurpers
			return ALIGNMENT_EVIL;
		case 'G':
		case 'g':
		case 'L':
		case 'l': // good or loyalist
			return ALIGNMENT_GOOD;
		default:
			return ALIGNMENT_NEUTRAL;
		}
	}
}
