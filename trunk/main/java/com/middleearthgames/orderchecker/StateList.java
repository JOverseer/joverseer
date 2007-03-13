// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   StateList.java

package com.middleearthgames.orderchecker;

import java.util.Vector;

class StateList extends Vector
{
    private class StateInfo
    {

        private int orderNumber;
        Object value;

        int getOrderNumber()
        {
            return orderNumber;
        }

        Object getValue()
        {
            return value;
        }

        void setValue(Object value)
        {
            this.value = value;
        }

        StateInfo(int orderNumber, Object value)
        {
            this.orderNumber = orderNumber;
            this.value = value;
        }
    }


    private Object defaultValue;

    StateList()
    {
        defaultValue = null;
    }

    public void clear()
    {
        super.clear();
        defaultValue = null;
    }

    Object findValueByOrderNumber(int order)
    {
        int highest = 0;
        int index = 0;
        for(int i = 0; i < size(); i++)
        {
            StateInfo info = (StateInfo)get(i);
            int stateOrder = info.getOrderNumber();
            if(highest > 0 && stateOrder == highest)
            {
                throw new RuntimeException("findValueByOrderNumber: duplicate key");
            }
            if(order > stateOrder && stateOrder > highest)
            {
                highest = stateOrder;
                index = i;
            }
        }

        if(highest > 0)
        {
            StateInfo info = (StateInfo)get(index);
            return info.getValue();
        } else
        {
            return defaultValue;
        }
    }

    int[] getKeys()
    {
        int size = size();
        if(defaultValue != null)
        {
            size++;
        }
        int index = 0;
        int keys[] = new int[size];
        if(defaultValue != null)
        {
            keys[index] = 0;
            index++;
        }
        for(int i = 0; i < size(); i++)
        {
            StateInfo info = (StateInfo)get(i);
            keys[index] = info.getOrderNumber();
            index++;
        }

        return keys;
    }

    Object[] getValues()
    {
        int size = size();
        if(defaultValue != null)
        {
            size++;
        }
        int index = 0;
        Object values[] = new Object[size];
        if(defaultValue != null)
        {
            values[index] = defaultValue;
            index++;
        }
        for(int i = 0; i < size(); i++)
        {
            StateInfo info = (StateInfo)get(i);
            values[index] = info.getValue();
            index++;
        }

        return values;
    }

    void put(int order, Object value)
    {
        if(order == 0)
        {
            putDefaultValue(value);
            return;
        } else
        {
            StateInfo newInfo = new StateInfo(order, value);
            add(((Object) (newInfo)));
            return;
        }
    }

    void putDefaultValue(Object value)
    {
        defaultValue = value;
    }
}
