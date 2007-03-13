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
        domNode = node;
    }

    boolean isNodeAnElement()
    {
        return domNode.getNodeType() == 1;
    }

    boolean isNodeText()
    {
        return domNode.getNodeType() == 3;
    }

    String getNodeName()
    {
        return domNode.getNodeName();
    }

    String getNodeValue()
    {
        return domNode.getNodeValue();
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
        catch (Exception ex) {
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
        if(!domNode.hasAttributes())
        {
            return null;
        } else
        {
            NamedNodeMap map = domNode.getAttributes();
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
        catch (Exception ex) {
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
        if(domNode.hasChildNodes())
        {
            Node node = domNode.getChildNodes().item(searchIndex);
            return new AdapterNode(node);
        } else
        {
            return null;
        }
    }

    int childCount()
    {
        return domNode.getChildNodes().getLength();
    }

    private final int ELEMENT_TYPE = 1;
    private final int TEXT_TYPE = 3;
    private Node domNode;
}
