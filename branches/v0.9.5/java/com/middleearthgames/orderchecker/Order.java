// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Order.java

package com.middleearthgames.orderchecker;

import com.middleearthgames.orderchecker.gui.OCTreeNode;
import java.util.Vector;
import javax.swing.JTree;

// Referenced classes of package com.middleearthgames.orderchecker:
//            Ruleset, Main, Nation, Character, 
//            Map, Hex

public class Order
    implements Comparable
{

    public static final int NO_ORDER = 9999;
    public static final int START_TURN = 0;
    public static final String infoOrder = "[G] ";
    public static final String warnOrder = "[Y] ";
    public static final String errorOrder = "[R] ";
    public static final String helpOrder = "[H] ";
    private final int orderNumber;
    private Ruleset rules;
    private final Vector parameters = new Vector();
    private final Character parent;
    private boolean done;
    private Vector infoResults;
    private Vector warnResults;
    private Vector errorResults;
    private Vector helpResults;

    public Order(Character parent, int number)
    {
        infoResults = new Vector();
        warnResults = new Vector();
        errorResults = new Vector();
        helpResults = new Vector();
        this.parent = parent;
        orderNumber = number;
    }

    void printStateInformation()
    {
        rules.printStateInformation();
    }

    String implementPhase(int phase)
    {
        if(orderNumber == 9999)
        {
            if(phase == 1)
            {
                errorResults.add("Missing order.");
            }
            done = true;
            return null;
        }
        if(phase == 1)
        {
            rules = Main.main.getRuleSet().getRulesForOrder(orderNumber, false);
        }
        if(rules.size() == 0)
        {
            if(phase == 2)
            {
                warnResults.add("No rules exist for this order.");
            }
            done = true;
            return null;
        }
        if(phase == 1)
        {
            infoResults.clear();
            warnResults.clear();
            errorResults.clear();
            helpResults.clear();
            done = false;
        }
        String result = rules.processRules(this, phase);
        if(result == null && rules.getDone())
        {
            done = true;
        }
        return result;
    }

    boolean getDone()
    {
        return done;
    }

    boolean getStateDone(int state)
    {
        if(rules != null)
        {
            return rules.getStateDone(state);
        } else
        {
            return true;
        }
    }

    Ruleset getRules()
    {
        return rules;
    }

    void addWarning(String msg)
    {
        warnResults.add(((Object) (msg)));
    }

    void addError(String msg)
    {
        errorResults.add(((Object) (msg)));
    }

    void addInfo(String msg)
    {
        infoResults.add(((Object) (msg)));
    }

    void addHelp(String msg)
    {
        helpResults.add(((Object) (msg)));
    }

    public void addTreeNodes(JTree tree, OCTreeNode parent)
    {
        OCTreeNode orderNode = new OCTreeNode(tree, ((Object) (this)), true);
        parent.add(((javax.swing.tree.MutableTreeNode) (orderNode)));
        int errors = errorResults.size();
        for(int i = 0; i < errors; i++)
        {
            String resultNode = "[R] " + (String)errorResults.get(i);
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }

        int warnings = warnResults.size();
        for(int i = 0; i < warnings; i++)
        {
            String resultNode = "[Y] " + (String)warnResults.get(i);
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }

        int messages = infoResults.size();
        for(int i = 0; i < messages; i++)
        {
            String resultNode = "[G] " + (String)infoResults.get(i);
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }

        int helpmsgs = helpResults.size();
        for(int i = 0; i < helpmsgs; i++)
        {
            String resultNode = "[H] " + (String)helpResults.get(i);
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }

        if(errors + warnings + messages + helpmsgs == 0)
        {
            String resultNode = "[G] ";
            orderNode.add(((javax.swing.tree.MutableTreeNode) (new OCTreeNode(tree, ((Object) (resultNode)), false))));
        }
    }

    public String getParameterList()
    {
        String paramList = "";
        int size = parameters.size();
        for(int i = 0; i < size; i++)
        {
            String param = (String)parameters.get(i);
            paramList = paramList + param + " ";
        }

        return paramList;
    }

    public void addParameter(String param)
    {
        parameters.add(((Object) (param)));
    }

    int getNumberOfParameters()
    {
        return parameters.size();
    }

    public int getOrder()
    {
        return orderNumber;
    }

    Character getParent()
    {
        return parent;
    }

    void getInfoRequests(Vector list)
    {
        if(rules != null)
        {
            rules.getInfoRequests(list);
        }
    }

    public int compareTo(Object object)
    {
        Order order = (Order)object;
        if(orderNumber < order.orderNumber)
        {
            return -1;
        }
        return orderNumber != order.orderNumber ? 1 : 0;
    }

    public String toString()
    {
        return String.valueOf(orderNumber);
    }

    String getParameterString(int paramNumber)
    {
        int index = paramNumber - 1;
        if(index >= 0 && index < parameters.size())
        {
            return (String)parameters.get(index);
        } else
        {
            return null;
        }
    }

    int getParameterNumber(int paramNumber)
    {
        try {
            String strValue;
            strValue = getParameterString(paramNumber);
            if(strValue == null)
            {
                return -1;
            }
            return Integer.parseInt(strValue);
        }
        catch (Exception ex) {
            return -1;
        }
    }

    Character extractCharacter(int param, boolean reportError)
    {
        Character character = null;
        if(param == 0)
        {
            character = parent;
        } else
        {
            String id = getParameterString(param);
            if(id != null)
            {
                character = Main.main.getNation().findCharacterById(id);
                if(character == null)
                {
                    character = Main.main.getNation().findCharacterIdInArmy(id);
                }
            }
            if(character == null && reportError)
            {
                if(id != null)
                {
                    warnResults.add(((Object) ("No information could be obtained for " + id + ".")));
                } else
                {
                    errorResults.add("No character was specified.");
                }
            }
        }
        return character;
    }

    int extractLocation(int param, boolean reportError)
    {
        int location = parent.getLocation(orderNumber);
        if(param > 0)
        {
            location = getParameterNumber(param);
        }
        if(location == -1)
        {
            if(reportError)
            {
                errorResults.add("Could not determine the location.");
            }
        } else
        if(location < 101 || location > 4439)
        {
            if(reportError)
            {
                errorResults.add(((Object) ("Invalid location of " + location + ".")));
            }
            location = -1;
        }
        return location;
    }

    boolean onLand(int param)
    {
        int location = extractLocation(param, false);
        if(location >= 0)
        {
            Hex hex = Main.main.getMap().findHex(location);
            if(hex != null)
            {
                int terrain = hex.getTerrain();
                if(terrain == 9 || terrain == 8)
                {
                    return false;
                }
            }
        }
        return true;
    }

    
    public Vector getErrorResults() {
        return errorResults;
    }

    
    public Vector getHelpResults() {
        return helpResults;
    }

    
    public Vector getInfoResults() {
        return infoResults;
    }

    
    public Vector getWarnResults() {
        return warnResults;
    }
    
    
}
