// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Ruleset.java

package com.middleearthgames.orderchecker;

import java.util.Vector;

// Referenced classes of package com.middleearthgames.orderchecker:
//            Rule, Order

public class Ruleset extends Vector
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector orderNumbers;
    private Vector orderNames;

    public Ruleset()
    {
        this.orderNumbers = new Vector();
        this.orderNames = new Vector();
    }

    void printStateInformation()
    {
        for(int i = 0; i < size(); i++)
        {
            Rule rule = (Rule)get(i);
            rule.printStateInformation();
        }

    }

    void getInfoRequests(Vector list)
    {
        for(int i = 0; i < size(); i++)
        {
            Rule rule = (Rule)get(i);
            rule.getInfoRequests(list);
        }

    }

    boolean getDone()
    {
        for(int i = 0; i < size(); i++)
        {
            Rule rule = (Rule)get(i);
            if(!rule.getDone())
            {
                return false;
            }
        }

        return true;
    }

    boolean getStateDone(int state)
    {
        for(int i = 0; i < size(); i++)
        {
            Rule rule = (Rule)get(i);
            if(!rule.getStateDone(state))
            {
                return false;
            }
        }

        return true;
    }

    public Ruleset getRulesForOrder(int number, boolean spell)
    {
        Ruleset subsetRules = new Ruleset();
        for(int i = 0; i < size(); i++)
        {
            Rule rule = (Rule)get(i);
            if(rule.getOrder() != number || rule.isSpellType() != spell)
            {
                continue;
            }
            try
            {
                Rule copyRule = (Rule)rule.clone();
                subsetRules.add(((Object) (copyRule)));
            }
            catch(CloneNotSupportedException ex)
            {
                throw new RuntimeException(((Throwable) (ex)));
            }
        }

        return subsetRules;
    }

    public String findOrderName(int number)
    {
        for(int i = 0; i < this.orderNumbers.size(); i++)
        {
            Integer value = (Integer)this.orderNumbers.get(i);
            if(value.intValue() == number)
            {
                return (String)this.orderNames.get(i);
            }
        }

        return null;
    }

    String processRules(Order order, int phase)
    {
        for(int i = 0; i < size(); i++)
        {
            Rule rule = (Rule)get(i);
            if(rule.getDone())
            {
                continue;
            }
            String result = rule.processRule(order, phase);
            if(result != null && !result.equals("state"))
            {
                return result;
            }
        }

        return null;
    }

    boolean doesSpellHaveRules(int spell)
    {
        for(int i = 0; i < size(); i++)
        {
            Rule rule = (Rule)get(i);
            if(rule.getOrder() == spell && rule.isSpellType())
            {
                return true;
            }
        }

        return false;
    }

    public void addOrderName(Integer number, String name)
    {
        this.orderNumbers.add(((Object) (number)));
        this.orderNames.add(((Object) (name)));
    }

    public boolean isRuleSetComplete()
    {
        for(int i = 0; i < size(); i++)
        {
            Rule rule = (Rule)get(i);
            if(!rule.isRuleComplete())
            {
                return false;
            }
        }

        return true;
    }
}
