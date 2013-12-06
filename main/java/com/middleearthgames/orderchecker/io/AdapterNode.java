// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AdapterNode.java

package com.middleearthgames.orderchecker.io;

import org.w3c.dom.*;

class AdapterNode
{

    AdapterNode(Node node)
    {
        this.domNode = node;
    }

    boolean isNodeAnElement()
    {
        return this.domNode.getNodeType() == AdapterNode.ELEMENT_TYPE;
    }

    boolean isNodeText()
    {
        return this.domNode.getNodeType() == AdapterNode.TEXT_TYPE;
    }

    String getNodeName()
    {
        return this.domNode.getNodeName();
    }

    String getNodeValue()
    {
        return this.domNode.getNodeValue();
    }

    int extractNodeNumber()
    {
        try {
            String value;
            value = extractNodeString();
            if(value == null)
                return -1;
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }

    String extractNodeString()
    {
        if(!isNodeAnElement())
            return null;
        AdapterNode childNode = child(0);
        if(childNode == null)
            return null;
        if(!childNode.isNodeText())
            return null;
        else
            return childNode.getNodeValue();
    }

    String extractAttributeString()
    {
        if(!this.domNode.hasAttributes())
        {
            return null;
        } else
        {
            NamedNodeMap map = this.domNode.getAttributes();
            Node mapNode = map.item(0);
            return mapNode.getNodeValue();
        }
    }

    int extractAttributeNumber()
    {
        try {
            String value;
            value = extractAttributeString();
            if(value == null)
                return -1;
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ex) {
            return -1;
        }
    }

    int index(AdapterNode child)
    {
        int count = childCount();
        for(int i = 0; i < count; i++)
        {
            AdapterNode n = child(i);
            if(child == n)
                return i;
        }

        return -1;
    }

    AdapterNode child(int searchIndex)
    {
        if(this.domNode.hasChildNodes())
        {
            Node node = this.domNode.getChildNodes().item(searchIndex);
            return new AdapterNode(node);
        } else
        {
            return null;
        }
    }

    int childCount()
    {
        return this.domNode.getChildNodes().getLength();
    }

   static private final int ELEMENT_TYPE = 1;
   static  private final int TEXT_TYPE = 3;
    private Node domNode;
}
