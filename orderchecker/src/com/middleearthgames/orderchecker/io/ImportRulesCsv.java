// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ImportRulesCsv.java

package com.middleearthgames.orderchecker.io;

import com.middleearthgames.orderchecker.*;

// Referenced classes of package com.middleearthgames.orderchecker.io:
//            ImportCsv

public class ImportRulesCsv extends ImportCsv
{
    public static final String VERSIONCHECK = "\"# " + Main.getVersionString() + "\"";
    public static final String BEGINTAG = "BEGINRULES";
    public static final String ENDTAG = "ENDRULES";

    public ImportRulesCsv(String filename, Ruleset ruleset)
    {
        super(filename);
        this.ruleset = ruleset;
    }

    public boolean getRules()
    {
    	return super.openFileOrStream();
    }

    public String parseRules()
    {
        String line = readLine();
        if(line == null || !line.equalsIgnoreCase(VERSIONCHECK))
            return "Rules file does not appear to be valid!";
        for(line = readLine(); line != null && !matchesTag(line, ENDTAG); line = readLine())
        {
            if(line == null || line.length() == 0 || line.charAt(0) == '#' || matchesTag(line, BEGINTAG))
                continue;
            String result = parseLine(line);
            if(result != null)
                return result;
        }

        return null;
    }
	public String makeRuleString(String rule) {
		return VERSIONCHECK + "\n" +
				BEGINTAG + "\n" +
				rule + "\n" +
				ENDTAG + "\n";
	}

    private boolean matchesTag(String line, String tag)
    {
        if(line.length() >= tag.length())
        {
            String subLine = line.substring(0, tag.length());
            if(subLine.equals(tag))
                return true;
        }
        return false;
    }

    private String parseLine(String line)
    {
        String param;
        param = getToken(line, true);
        if(param == null || param.length() == 0)
            return null;
        try {
            Integer order;
            Rule rule;
            order = Integer.valueOf(param);
            rule = new Rule(order.intValue(),this.ruleset);
            param = getToken(line, false);
            if(param != null)
                if(param.trim().equals("Spell"))
                    rule.setSpellType();
                else
                    this.ruleset.addOrderName(order, param);
            param = getToken(line, false);
            if(param == null)
            {
                forceTokenAdvance();
                param = getToken(line, false);
            }
            if(param == null || param.length() == 0)
                return "Could not find the rule id for order: " + order;
            String result;
            rule.setName(param);
            result = parseRuleParameters(rule, line);
            if(result != null)
                return result;
            this.ruleset.add(rule);
            return null;
        }
        catch (Exception ex) {
            return "Could not convert " + param + " to an order number!";
        }
    }

    private String parseRuleParameters(Rule rule, String line)
    {
        try {
            for(String param = getToken(line, false); param != null && param.length() > 0; param = getToken(line, false))
            {
                Integer newParam = Integer.valueOf(param);
                rule.addParameter(newParam);
            }

            return null;
        }
        catch (Exception ex) {
            return "Could not parse rule parameters for " + rule + ", order " + rule.getOrder() + "!";
        }
    }

    private Ruleset ruleset;
}
