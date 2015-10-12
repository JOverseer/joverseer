// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Spell.java

package com.middleearthgames.orderchecker;


class Spell
{

    static final int EASY = 0;
    static final int AVERAGE = 1;
    static final int HARD = 2;
    private final int spell;
    private final String name;
    private final int difficulty;
    private final int order;
    private final int list;

    Spell(int spell, String name, int difficulty, int order, int list)
    {
        this.spell = spell;
        this.name = name;
        this.difficulty = difficulty;
        this.order = order;
        this.list = list;
    }

    int getSpellNumber()
    {
        return this.spell;
    }

    String getName()
    {
        return this.name;
    }

    int getDifficulty()
    {
        return this.difficulty;
    }

    int getOrder()
    {
        return this.order;
    }

    int getList()
    {
        return this.list;
    }

    @Override
	public String toString()
    {
        return this.name + " (" + this.spell + ")";
    }
}
