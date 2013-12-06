// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OCTreeNode.java

package com.middleearthgames.orderchecker.gui;

import com.middleearthgames.orderchecker.*;
import com.middleearthgames.orderchecker.Character;

import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;

public class OCTreeNode extends DefaultMutableTreeNode
{

    public OCTreeNode(JTree tree, Object userObject, boolean allowsChildren)
    {
        super(userObject, allowsChildren);
        this.normalFont = tree.getFont();
        this.boldFont = new Font(this.normalFont.getName(), 1, this.normalFont.getSize());
        if(userObject instanceof Order)
            this.nodeType = ORDER_NODE;
        else
        if(userObject instanceof Character)
            this.nodeType = CHARACTER_NODE;
        else
            this.nodeType = RESULT_NODE;
    }

    public int getNodeType()
    {
        return this.nodeType;
    }

    public Font getActiveFont()
    {
        if(this.nodeType == CHARACTER_NODE)
            return this.boldFont;
        else
            return this.normalFont;
    }

    @Override
	public String toString()
    {
        String currentText = getUserObject().toString();
        try
        {
            switch(this.nodeType)
            {
            case RESULT_NODE: // '\002'
            default:
                break;

            case CHARACTER_NODE: // '\001'
                Character character = (Character)getUserObject();
                currentText = currentText + " (" + character.getId();
                currentText = currentText + ", " + character.getAttributes() + ") ";
                currentText = currentText + "@ " + Main.main.locationStr(character.getLocation(0)) + " ";
                Army army = character.getArmy(0);
                if(army != null)
                {
                    currentText = currentText + " \n";
                    currentText = currentText + army.getArmyDescription(character, 0);
                }
                break;

            case ORDER_NODE: // '\0'
                Order order = (Order)getUserObject();
                if(order.getOrder() == 9999)
                    currentText = "";
                String orderName = Main.main.getRuleSet().findOrderName(order.getOrder());
                if(orderName != null)
                    currentText = currentText + " (" + orderName + ")";
                currentText = currentText + " " + order.getParameterList();
                break;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return currentText;
    }

    private int getResultType()
    {
        int result = NO_RESULT;
        String currentText = getUserObject().toString();
        if(currentText.length() > 3 && this.nodeType == RESULT_NODE)
        {
            String typeTag = currentText.substring(0, 4);
            if(typeTag.equals("[R] "))
                return ERROR_RESULT;
            if(typeTag.equals("[Y] "))
                return WARNING_RESULT;
            if(typeTag.equals("[G] "))
                return INFO_RESULT;
            if(typeTag.equals("[H] "))
                return HELP_RESULT;
        }
        return result;
    }

    public String getResultString(boolean allResults)
    {
        String result = toString();
        int type = getResultType();
        if(allResults)
        {
            if(result.length() > 4)
                return result;
        } else
        if(type == HELP_RESULT)
            return result;
        return "";
    }

    public ImageIcon getIcon()
    {
        switch(this.nodeType)
        {
        case CHARACTER_NODE: // '\001'
            return getCharIcon();

        case ORDER_NODE: // '\0'
            return getOrderIcon();

        case RESULT_NODE: // '\002'
            int type = getResultType();
            switch(type)
            {
            case ERROR_RESULT: // '\004'
                return getRedIcon();

            case WARNING_RESULT: // '\003'
                return getYellowIcon();

            case INFO_RESULT: // '\001'
            case HELP_RESULT: // '\002'
                return getGreenIcon();

            case NO_RESULT: // '\0'
            default:
                return null;
            }
        }
        return null;
    }
    
    public ImageIcon getRedIcon() {
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        return new ImageIcon(imgSource.getImage("orderchecker.red.image"));
    }

    public ImageIcon getYellowIcon() {
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        return new ImageIcon(imgSource.getImage("orderchecker.yellow.image"));
    }

    public ImageIcon getGreenIcon() {
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        return new ImageIcon(imgSource.getImage("orderchecker.green.image"));
    }

    public ImageIcon getOrderIcon() {
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        return new ImageIcon(imgSource.getImage("orderchecker.order.image"));
    }

    public ImageIcon getCharIcon() {
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        return new ImageIcon(imgSource.getImage("orderchecker.character.image"));
    }

    @SuppressWarnings("unused")
	private static final ImageIcon redIcon = new ImageIcon("images/red.gif");
    @SuppressWarnings("unused")
	private static final ImageIcon yellowIcon = new ImageIcon("images/yellow.gif");
    @SuppressWarnings("unused")
	private static final ImageIcon greenIcon = new ImageIcon("images/green.gif");
    @SuppressWarnings("unused")
	private static final ImageIcon orderIcon = new ImageIcon("images/order.gif");
    @SuppressWarnings("unused")
	private static final ImageIcon charIcon = new ImageIcon("images/character.gif");
    private static final int NO_RESULT = 0;
    private static final int INFO_RESULT = 1;
    private static final int HELP_RESULT = 2;
    private static final int WARNING_RESULT = 3;
    private static final int ERROR_RESULT = 4;
    static final int ORDER_NODE = 0;
    static final int CHARACTER_NODE = 1;
    static final int RESULT_NODE = 2;
    private final Font normalFont;
    private final Font boldFont;
    private final int nodeType;

}
