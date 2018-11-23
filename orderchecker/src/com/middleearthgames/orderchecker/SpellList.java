// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   SpellList.java

package com.middleearthgames.orderchecker;

import java.util.Vector;

// Referenced classes of package com.middleearthgames.orderchecker:
//            Spell, Character

class SpellList
{

    private static final int SPELL_LISTS = 18;
    private static final int HEALING_MASTERY = 0;
    private static final int HEALING_WAYS = 1;
    private static final int BARRIER_MASTERY = 2;
    private static final int RESISTANCE_MASTERY = 3;
    private static final int FIRE_MASTERY = 4;
    private static final int WORD_MASTERY = 5;
    private static final int WIND_MASTERY = 6;
    private static final int DARK_SUMMONS = 7;
    private static final int SPIRIT_MASTERY = 8;
    private static final int CONJURING_WAYS = 9;
    private static final int MOVEMENT_MASTERY = 10;
    private static final int RETURN_MASTERY = 11;
    private static final int TELEPORT_MASTERY = 12;
    private static final int PERCEPTIONS = 13;
    private static final int DIVINATIONS = 14;
    private static final int ARTIFACT_LORE = 15;
    private static final int SCRYING = 16;
    private static final int HIDDEN_VISIONS = 17;
    private Vector spells[];

    SpellList()
    {
        this.spells = new Vector[SPELL_LISTS];
        for(int i = 0; i < SPELL_LISTS; i++)
        {
            this.spells[i] = new Vector();
        }

        configureLists();
    }

    boolean hasSpellRequisite(Character character, Spell spell)
    {
        if(spell.getDifficulty() == lowestDifficulty(spell.getList()))
        {
            return true;
        }
        int size = character.getSpellCount();
        int spellList[] = character.getSpells();
        for(int i = 0; i < size; i++)
        {
            Spell charSpell = findSpell(spellList[i]);
            if(charSpell != null && charSpell.getList() == spell.getList() && charSpell.getDifficulty() <= spell.getDifficulty())
            {
                return true;
            }
        }

        return false;
    }

    boolean isSpellInLostList(int spellNum)
    {
        Spell spell = findSpell(spellNum);
        switch(spell.getList())
        {
        case DARK_SUMMONS:
        case SPIRIT_MASTERY:
        case CONJURING_WAYS:
        case TELEPORT_MASTERY:
            return true;

        case MOVEMENT_MASTERY:
        case RETURN_MASTERY:
        default:
            return false;
        }
    }

    Spell findSpell(int spellNum)
    {
        for(int i = 0; i < SPELL_LISTS; i++)
        {
            int size = this.spells[i].size();
            for(int j = 0; j < size; j++)
            {
                Spell spell = (Spell)this.spells[i].get(j);
                if(spell.getSpellNumber() == spellNum)
                {
                    return spell;
                }
            }

        }

        return null;
    }

    private int lowestDifficulty(int list)
    {
        int easiest = Spell.HARD;
        int size = this.spells[list].size();
        for(int i = 0; i < size && easiest != Spell.EASY; i++)
        {
            Spell spell = (Spell)this.spells[list].get(i);
            if(spell.getDifficulty() < easiest)
            {
                easiest = spell.getDifficulty();
            }
        }

        return easiest;
    }

    private void configureLists()
    {
        this.spells[HEALING_MASTERY].add(((Object) (new Spell(2, "Minor Heal", Spell.EASY, 120, HEALING_MASTERY))));
        this.spells[HEALING_MASTERY].add(((Object) (new Spell(8, "Heal True", Spell.HARD, 120, HEALING_MASTERY))));
        this.spells[HEALING_WAYS].add(((Object) (new Spell(4, "Major Heal", Spell.EASY, 120, HEALING_WAYS))));
        this.spells[HEALING_WAYS].add(((Object) (new Spell(6, "Greater Heal", Spell.AVERAGE, 120, HEALING_WAYS))));
        this.spells[BARRIER_MASTERY].add(((Object) (new Spell(102, "Barriers", Spell.EASY, 225, BARRIER_MASTERY))));
        this.spells[BARRIER_MASTERY].add(((Object) (new Spell(106, "Deflections", Spell.AVERAGE, 225, BARRIER_MASTERY))));
        this.spells[BARRIER_MASTERY].add(((Object) (new Spell(112, "Shields", Spell.HARD, 225, BARRIER_MASTERY))));
        this.spells[BARRIER_MASTERY].add(((Object) (new Spell(114, "Barrier Walls", Spell.HARD, 225, BARRIER_MASTERY))));
        this.spells[RESISTANCE_MASTERY].add(((Object) (new Spell(104, "Resistances", Spell.EASY, 225, RESISTANCE_MASTERY))));
        this.spells[RESISTANCE_MASTERY].add(((Object) (new Spell(108, "Blessings", Spell.AVERAGE, 225, RESISTANCE_MASTERY))));
        this.spells[RESISTANCE_MASTERY].add(((Object) (new Spell(110, "Protections", Spell.HARD, 225, RESISTANCE_MASTERY))));
        this.spells[RESISTANCE_MASTERY].add(((Object) (new Spell(116, "Force Walls", Spell.HARD, 225, RESISTANCE_MASTERY))));
        this.spells[FIRE_MASTERY].add(((Object) (new Spell(202, "Call Fire", Spell.EASY, 225, FIRE_MASTERY))));
        this.spells[FIRE_MASTERY].add(((Object) (new Spell(204, "Wild Flames", Spell.EASY, 225, FIRE_MASTERY))));
        this.spells[FIRE_MASTERY].add(((Object) (new Spell(206, "Wall of Fire", Spell.EASY, 225, FIRE_MASTERY))));
        this.spells[FIRE_MASTERY].add(((Object) (new Spell(232, "Fire Bolts", Spell.AVERAGE, 225, FIRE_MASTERY))));
        this.spells[FIRE_MASTERY].add(((Object) (new Spell(234, "Fire Balls", Spell.AVERAGE, 225, FIRE_MASTERY))));
        this.spells[FIRE_MASTERY].add(((Object) (new Spell(236, "Fire Storms", Spell.AVERAGE, 225, FIRE_MASTERY))));
        this.spells[FIRE_MASTERY].add(((Object) (new Spell(240, "Summon Fire Spirits", Spell.EASY, 225, FIRE_MASTERY))));
        this.spells[WORD_MASTERY].add(((Object) (new Spell(208, "Words of Pain", Spell.EASY, 225, WORD_MASTERY))));
        this.spells[WORD_MASTERY].add(((Object) (new Spell(210, "Words of Calm", Spell.EASY, 225, WORD_MASTERY))));
        this.spells[WORD_MASTERY].add(((Object) (new Spell(212, "Words of Paralysis", Spell.EASY, 225, WORD_MASTERY))));
        this.spells[WORD_MASTERY].add(((Object) (new Spell(220, "Words of Agony", Spell.AVERAGE, 225, WORD_MASTERY))));
        this.spells[WORD_MASTERY].add(((Object) (new Spell(222, "Words of Stun", Spell.AVERAGE, 225, WORD_MASTERY))));
        this.spells[WORD_MASTERY].add(((Object) (new Spell(224, "Words of Command", Spell.AVERAGE, 225, WORD_MASTERY))));
        this.spells[WORD_MASTERY].add(((Object) (new Spell(242, "Words of Death", Spell.EASY, 225, WORD_MASTERY))));
        this.spells[WIND_MASTERY].add(((Object) (new Spell(214, "Call Winds", Spell.EASY, 225, WIND_MASTERY))));
        this.spells[WIND_MASTERY].add(((Object) (new Spell(216, "Wild Winds", Spell.EASY, 225, WIND_MASTERY))));
        this.spells[WIND_MASTERY].add(((Object) (new Spell(218, "Wall of Wind", Spell.EASY, 225, WIND_MASTERY))));
        this.spells[WIND_MASTERY].add(((Object) (new Spell(226, "Chill Bolts", Spell.AVERAGE, 225, WIND_MASTERY))));
        this.spells[WIND_MASTERY].add(((Object) (new Spell(228, "Frost Balls", Spell.AVERAGE, 225, WIND_MASTERY))));
        this.spells[WIND_MASTERY].add(((Object) (new Spell(230, "Wind Storms", Spell.AVERAGE, 225, WIND_MASTERY))));
        this.spells[WIND_MASTERY].add(((Object) (new Spell(238, "Summon Wind Spirits", Spell.EASY, 225, WIND_MASTERY))));
        this.spells[DARK_SUMMONS].add(((Object) (new Spell(244, "Fearful Hearts", Spell.AVERAGE, 225, DARK_SUMMONS))));
        this.spells[DARK_SUMMONS].add(((Object) (new Spell(246, "Summon Storms", Spell.AVERAGE, 225, DARK_SUMMONS))));
        this.spells[DARK_SUMMONS].add(((Object) (new Spell(248, "Fanaticism", Spell.AVERAGE, 225, DARK_SUMMONS))));
        this.spells[SPIRIT_MASTERY].add(((Object) (new Spell(502, "Weakness", Spell.EASY, 330, SPIRIT_MASTERY))));
        this.spells[SPIRIT_MASTERY].add(((Object) (new Spell(504, "Sickness", Spell.AVERAGE, 330, SPIRIT_MASTERY))));
        this.spells[SPIRIT_MASTERY].add(((Object) (new Spell(506, "Curses", Spell.HARD, 330, SPIRIT_MASTERY))));
        this.spells[CONJURING_WAYS].add(((Object) (new Spell(508, "Conjure Mounts", Spell.EASY, 330, CONJURING_WAYS))));
        this.spells[CONJURING_WAYS].add(((Object) (new Spell(510, "Conjure Food", Spell.AVERAGE, 330, CONJURING_WAYS))));
        this.spells[CONJURING_WAYS].add(((Object) (new Spell(512, "Conjure Hordes", Spell.AVERAGE, 330, CONJURING_WAYS))));
        this.spells[MOVEMENT_MASTERY].add(((Object) (new Spell(302, "Long Stride", Spell.EASY, 825, MOVEMENT_MASTERY))));
        this.spells[MOVEMENT_MASTERY].add(((Object) (new Spell(304, "Fast Stride", Spell.AVERAGE, 825, MOVEMENT_MASTERY))));
        this.spells[MOVEMENT_MASTERY].add(((Object) (new Spell(306, "Path Mastery", Spell.HARD, 825, MOVEMENT_MASTERY))));
        this.spells[RETURN_MASTERY].add(((Object) (new Spell(308, "Capital Return", Spell.EASY, 825, RETURN_MASTERY))));
        this.spells[RETURN_MASTERY].add(((Object) (new Spell(310, "Major Return", Spell.AVERAGE, 825, RETURN_MASTERY))));
        this.spells[RETURN_MASTERY].add(((Object) (new Spell(312, "Return True", Spell.HARD, 825, RETURN_MASTERY))));
        this.spells[TELEPORT_MASTERY].add(((Object) (new Spell(314, "Teleport", Spell.AVERAGE, 825, TELEPORT_MASTERY))));
        this.spells[PERCEPTIONS].add(((Object) (new Spell(402, "Perceive Allegiance", Spell.EASY, 940, PERCEPTIONS))));
        this.spells[PERCEPTIONS].add(((Object) (new Spell(404, "Perceive Relations", Spell.EASY, 940, PERCEPTIONS))));
        this.spells[PERCEPTIONS].add(((Object) (new Spell(408, "Perceive Nationality", Spell.EASY, 940, PERCEPTIONS))));
        this.spells[PERCEPTIONS].add(((Object) (new Spell(422, "Perceive Power", Spell.AVERAGE, 940, PERCEPTIONS))));
        this.spells[PERCEPTIONS].add(((Object) (new Spell(424, "Perceive Mission", Spell.AVERAGE, 940, PERCEPTIONS))));
        this.spells[PERCEPTIONS].add(((Object) (new Spell(432, "Perceive Secrets", Spell.HARD, 940, PERCEPTIONS))));
        this.spells[DIVINATIONS].add(((Object) (new Spell(406, "Divine Army", Spell.EASY, 940, DIVINATIONS))));
        this.spells[DIVINATIONS].add(((Object) (new Spell(410, "Divine Allegiance Forces", Spell.EASY, 940, DIVINATIONS))));
        this.spells[DIVINATIONS].add(((Object) (new Spell(417, "Divine Characters w/Forces", Spell.AVERAGE, 940, DIVINATIONS))));
        this.spells[DIVINATIONS].add(((Object) (new Spell(419, "Divine Nation Forces", Spell.AVERAGE, 940, DIVINATIONS))));
        this.spells[DIVINATIONS].add(((Object) (new Spell(426, "Divine Army True", Spell.HARD, 940, DIVINATIONS))));
        this.spells[ARTIFACT_LORE].add(((Object) (new Spell(412, "Research Artifact", Spell.EASY, 940, ARTIFACT_LORE))));
        this.spells[ARTIFACT_LORE].add(((Object) (new Spell(418, "Locate Artifact", Spell.AVERAGE, 940, ARTIFACT_LORE))));
        this.spells[ARTIFACT_LORE].add(((Object) (new Spell(428, "Locate Artifact True", Spell.HARD, 940, ARTIFACT_LORE))));
        this.spells[SCRYING].add(((Object) (new Spell(413, "Scry Population Center", Spell.EASY, 940, SCRYING))));
        this.spells[SCRYING].add(((Object) (new Spell(414, "Scry Hex", Spell.EASY, 940, SCRYING))));
        this.spells[SCRYING].add(((Object) (new Spell(415, "Scry Area", Spell.AVERAGE, 940, SCRYING))));
        this.spells[SCRYING].add(((Object) (new Spell(436, "Scry Character", Spell.HARD, 940, SCRYING))));
        this.spells[HIDDEN_VISIONS].add(((Object) (new Spell(416, "Reveal Production", Spell.EASY, 940, HIDDEN_VISIONS))));
        this.spells[HIDDEN_VISIONS].add(((Object) (new Spell(420, "Reveal Character", Spell.AVERAGE, 940, HIDDEN_VISIONS))));
        this.spells[HIDDEN_VISIONS].add(((Object) (new Spell(430, "Reveal Character True", Spell.HARD, 940, HIDDEN_VISIONS))));
        this.spells[HIDDEN_VISIONS].add(((Object) (new Spell(434, "Reveal Population Center", Spell.HARD, 940, HIDDEN_VISIONS))));
    }
}
