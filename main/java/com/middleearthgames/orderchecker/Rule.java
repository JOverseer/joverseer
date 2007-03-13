// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Rule.java

package com.middleearthgames.orderchecker;

import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JCheckBox;

// Referenced classes of package com.middleearthgames.orderchecker:
//            PopCenter, Army, Ruleset, Main, 
//            Nation, Order, Character, Hex, 
//            Map, SpellList, Spell

public class Rule
    implements Cloneable
{

    private static final int STATE_NUM = 7;
    public static final int STATE_ARMY = 0;
    public static final int STATE_PC = 1;
    public static final int STATE_ARMY_LOCATION = 2;
    public static final int STATE_CHARACTER_ARMY = 3;
    public static final int STATE_CHARACTER_LOCATION = 4;
    public static final int STATE_ARTIFACT = 5;
    public static final int STATE_COMPANY = 6;
    public static final String STATE_REQ = "state";
    private static String stateDesc[] = {
        "Army", "PC", "Army Loc", "Char Army", "Char Loc", "Artifact", "Company"
    };
    private static final String productMapping[][] = {
        {
            "le", "Leather"
        }, {
            "br", "Bronze"
        }, {
            "st", "Steel"
        }, {
            "mi", "Mithril"
        }, {
            "fo", "Food"
        }, {
            "ti", "Timber"
        }, {
            "mo", "Mounts"
        }, {
            "go", "Gold"
        }
    };
    private int order;
    private String name;
    private Vector parameters;
    private boolean spell;
    private Ruleset spellRules;
    private Vector additionalInfo;
    private boolean stateProcessed[];
    private boolean done;
    private Vector debugList;
    private Order parentOrder;
    private Character parentChar;
    private int phase;

    public Rule(int order)
    {
        this.order = -1;
        name = null;
        parameters = new Vector();
        spell = false;
        spellRules = null;
        additionalInfo = null;
        stateProcessed = null;
        done = false;
        debugList = new Vector();
        parentOrder = null;
        parentChar = null;
        phase = -1;
        this.order = order;
    }

    protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    void printStateInformation()
    {
        for(int i = 0; i < debugList.size(); i++)
        {
            String entry = (String)debugList.get(i);
            System.out.println((i + 1) + ": " + entry);
        }

        debugList.removeAllElements();
        if(spellRules != null)
        {
            spellRules.printStateInformation();
        }
    }

    void getInfoRequests(Vector list)
    {
        list.addAll(((java.util.Collection) (additionalInfo)));
        if(spellRules != null)
        {
            spellRules.getInfoRequests(list);
        }
    }

    private void addAdditionalInfo(String msg, boolean state)
    {
        JCheckBox box = new JCheckBox(msg, state);
        additionalInfo.add(((Object) (box)));
    }

    private boolean getAdditionalInfo()
    {
        if(additionalInfo.size() > 0)
        {
            JCheckBox box = (JCheckBox)additionalInfo.remove(0);
            return box.isSelected();
        } else
        {
            return false;
        }
    }

    boolean getDone()
    {
        if(spellRules != null && !spellRules.getDone())
        {
            return false;
        } else
        {
            return done;
        }
    }

    boolean getStateDone(int state)
    {
        if(spellRules != null && !spellRules.getStateDone(state))
        {
            return false;
        } else
        {
            return stateProcessed[state];
        }
    }

    private void clearProcessState(int state)
    {
        if(stateProcessed[state])
        {
            stateProcessed[state] = false;
        }
    }

    private void setProcessState(int state)
    {
        if(!stateProcessed[state])
        {
            stateProcessed[state] = true;
        }
    }

    private boolean waitForProcessState(int state)
    {
        return !Main.main.getNation().isStateDone(state);
    }

    private boolean waitForPartialState(int state)
    {
        return !Main.main.getNation().isPartialStateDone(state, parentOrder.getOrder());
    }

    String processRule(Order order, int phase)
    {
        parentOrder = order;
        parentChar = order.getParent();
        this.phase = phase;
        if(phase == 1)
        {
            additionalInfo = new Vector();
            stateProcessed = new boolean[7];
            for(int i = 0; i < 7; i++)
            {
                stateProcessed[i] = true;
            }

        }
        String result;
        if(spellRules != null && !spellRules.getDone())
        {
            result = spellRules.processRules(parentOrder, phase);
            if(result != null && !result.equals("state"))
            {
                return result;
            }
        }
        if(done)
        {
            return null;
        }
        if(name.equalsIgnoreCase("ARMYMOVE"))
        {
            result = processArmyMove();
        } else
        if(name.equalsIgnoreCase("ARMYORPC"))
        {
            result = processArmyOrPc();
        } else
        if(name.equalsIgnoreCase("ARMYSCOUT"))
        {
            result = processScoutArmy();
        } else
        if(name.equalsIgnoreCase("ARTIFACT"))
        {
            result = processArtifact(-1, true, true);
        } else
        if(name.equalsIgnoreCase("ARTYFIND"))
        {
            result = processFindArtifact();
        } else
        if(name.equalsIgnoreCase("ARTYLIST"))
        {
            result = processArtifactList();
        } else
        if(name.equalsIgnoreCase("ARTYNAME"))
        {
            result = processArtifactName();
        } else
        if(name.equalsIgnoreCase("CHARACTER"))
        {
            result = processCharacter(-1, -1, -1);
        } else
        if(name.equalsIgnoreCase("CHARMOVE"))
        {
            result = processCharacterMove();
        } else
        if(name.equalsIgnoreCase("COMMANDER"))
        {
            result = processCommander();
        } else
        if(name.equalsIgnoreCase("COMMANDERNOT"))
        {
            result = processNotCommander();
        } else
        if(name.equalsIgnoreCase("COMMANDXFER"))
        {
            result = processCommandTransfer();
        } else
        if(name.equalsIgnoreCase("DOCK"))
        {
            result = processDock();
        } else
        if(name.equalsIgnoreCase("ENEMYARMY"))
        {
            result = processEnemyArmy();
        } else
        if(name.equalsIgnoreCase("FEATURE"))
        {
            result = processFeature();
        } else
        if(name.equalsIgnoreCase("FORT"))
        {
            result = processFortification();
        } else
        if(name.equalsIgnoreCase("HOSTAGE"))
        {
            result = processHostage();
        } else
        if(name.equalsIgnoreCase("IMPROVEPC"))
        {
            result = processImprovePopCenter();
        } else
        if(name.equalsIgnoreCase("LAND"))
        {
            result = processLand();
        } else
        if(name.equalsIgnoreCase("MAGEAMT"))
        {
            result = processMageAmount();
        } else
        if(name.equalsIgnoreCase("NATION"))
        {
            result = processNation();
        } else
        if(name.equalsIgnoreCase("NAVYMOVE"))
        {
            result = processNavyMove();
        } else
        if(name.equalsIgnoreCase("NEWCHAR"))
        {
            result = processNewCharacter();
        } else
        if(name.equalsIgnoreCase("ONERING"))
        {
            result = processOneRing();
        } else
        if(name.equalsIgnoreCase("PC"))
        {
            result = processPC();
        } else
        if(name.equalsIgnoreCase("PCSIZE"))
        {
            result = processPCSize();
        } else
        if(name.equalsIgnoreCase("PCSTATE"))
        {
            result = processPCState();
        } else
        if(name.equalsIgnoreCase("PRODUCTINFO"))
        {
            result = processProductInfo();
        } else
        if(name.equalsIgnoreCase("RANK"))
        {
            result = processRank(-1, -1, -1, -1);
        } else
        if(name.equalsIgnoreCase("SETCOMMAND"))
        {
            result = processSetCommander();
        } else
        if(name.equalsIgnoreCase("SHIPS"))
        {
            result = processShips();
        } else
        if(name.equalsIgnoreCase("SIEGENOT"))
        {
            result = processNotSieged();
        } else
        if(name.equalsIgnoreCase("SPELLREQ"))
        {
            result = processSpellReq();
        } else
        if(name.equalsIgnoreCase("SPELL"))
        {
            result = processSpell();
        } else
        if(name.equalsIgnoreCase("SPELLLIST"))
        {
            result = processSpellList();
        } else
        if(name.equalsIgnoreCase("STATUS"))
        {
            result = processStatus();
        } else
        if(name.equalsIgnoreCase("THREATENPC"))
        {
            result = processThreatenPopCenter();
        } else
        if(name.equalsIgnoreCase("TROOPAMT"))
        {
            result = processTroopAmount(-1, -1, ((int []) (null)));
        } else
        if(name.equalsIgnoreCase("TROOPRECRUIT"))
        {
            result = processTroopRecruit();
        } else
        if(name.equalsIgnoreCase("TROOPTYPE"))
        {
            result = processTroopType();
        } else
        if(name.equalsIgnoreCase("TROOPXFER"))
        {
            result = processTroopTransfer();
        } else
        if(name.equalsIgnoreCase("NONE"))
        {
            done = true;
            return null;
        } else
        {
            throw new RuntimeException("Unknown rule: " + name);
        }
        if(result == null && phase > 1)
        {
            done = true;
        }
        return result;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setSpellType()
    {
        spell = true;
    }

    public void addParameter(Integer param)
    {
        parameters.add(((Object) (param)));
    }

    public int getOrder()
    {
        return order;
    }

    String getName()
    {
        return name;
    }

    boolean isSpellType()
    {
        return spell;
    }

    boolean isRuleComplete()
    {
        return order != -1 && name != null;
    }

    public String toString()
    {
        return name;
    }

    private String parameterCount(int expected, String desc)
    {
        if(parameters.size() != expected)
        {
            return "Invalid parameter count for " + desc + ": " + order;
        } else
        {
            return null;
        }
    }

    private int convertParameter(int index)
    {
        try {
            Integer intValue = (Integer)parameters.get(index);
            return intValue.intValue();
        }
        catch (Exception ex) {
            return -1;
        }
    }

    private boolean willPCBlockMovement(int location)
    {
        PopCenter pc = Main.main.getNation().findPopulationCenter(location);
        if(pc == null)
        {
            return false;
        }
        if(pc.getFortification() == 0)
        {
            return false;
        }
        boolean owned = pc.getNation() == parentChar.getNation();
        boolean enemy = Nation.isEnemy(parentChar.getNation(), pc.getNation());
        boolean neutral = Nation.isNeutral(pc.getNation());
        if(owned || !enemy && !neutral)
        {
            if(pc.getEnemyArmyPresent())
            {
                parentOrder.addWarning("Army/navy movement could be blocked if " + pc + " is captured.");
            }
            return false;
        }
        if(pc.getPossibleCapture() < parentOrder.getOrder() && pc.getCapturingNation() == parentChar.getNation())
        {
            parentOrder.addWarning("Army/navy movement could be blocked if " + pc + " is not captured.");
            return false;
        }
        if(pc.getPossibleInfluence() < parentOrder.getOrder())
        {
            parentOrder.addWarning("Army/navy movement could be blocked if " + pc + " is not captured.");
            return false;
        } else
        {
            parentOrder.addWarning(pc + " may block army/navy movement.");
            return true;
        }
    }

    private String processArmyMove()
    {
        String result = parameterCount(1, "ARMYMOVE");
        if(result != null)
        {
            return result;
        }
        boolean forced = convertParameter(0) == 1;
        if(phase == 1)
        {
            clearProcessState(2);
            clearProcessState(4);
        } else
        if(phase == 2)
        {
            if(waitForProcessState(0))
            {
                return "state";
            }
            Army army = parentChar.getArmy(parentOrder.getOrder());
            if(army == null)
            {
                setProcessState(2);
                setProcessState(4);
                return null;
            }
            if(army.getFoodRequirement() == 0)
            {
                if(additionalInfo.size() == 0)
                {
                    String msg = "FOOD:Will " + parentChar + "'s army have 1 food?";
                    addAdditionalInfo(msg, false);
                    return "state";
                }
                army.setFoodRequired(1);
                army.setHasEnoughFood(getAdditionalInfo());
            }
            boolean food = army.getHasEnoughFood();
            if(waitForPartialState(2) || waitForPartialState(4))
            {
                return "state";
            }
            setProcessState(2);
            setProcessState(4);
            String evasiveString = parentOrder.getParameterString(1);
            boolean evasive = !evasiveString.equalsIgnoreCase("no");
            boolean cavalry = army.isAllCavalry(parentOrder.getOrder());
            int maxMovement = ((int) (forced ? 14 : 12));
            int totalMovement = maxMovement;
            int lastHex = parentChar.getLocation(parentOrder.getOrder());
            int currentHex = lastHex;
            for(int param = 1; param < parentOrder.getNumberOfParameters() && currentHex > 0 && totalMovement >= 0; param++)
            {
                String dirString = parentOrder.getParameterString(param + 1);
                int direction = Hex.convertDirection(dirString);
                int points = 1;
                if(dirString.equalsIgnoreCase("h"))
                {
                    totalMovement--;
                    continue;
                }
                points = Main.main.getMap().calcArmyMovement(currentHex, direction, cavalry);
                if(points == -1)
                {
                    currentHex = -1;
                    continue;
                }
                if(!food)
                {
                    if((points * 4) % 3 == 0)
                    {
                        points = (points * 4) / 3;
                    } else
                    {
                        points = (points * 4) / 3 + 1;
                    }
                }
                if(evasive)
                {
                    points *= 2;
                }
                totalMovement -= points;
                if(totalMovement < 0)
                {
                    continue;
                }
                currentHex = Main.main.getMap().getAdjacentHex(currentHex, direction);
                lastHex = currentHex;
                if(param + 1 >= parentOrder.getNumberOfParameters())
                {
                    continue;
                }
                willPCBlockMovement(currentHex);
                if(Main.main.getNation().isEnemyArmyPresent(currentHex))
                {
                    parentOrder.addWarning("An enemy army is present at " + currentHex + " and may block movement.");
                }
            }

            String msg = cavalry ? "Cavalry, " : "Infantry, ";
            msg = msg + (food ? "Food, " : "No Food, ");
            msg = msg + (evasive ? "Evasive, " : "Normal, ");
            if(currentHex == -1)
            {
                parentOrder.addError("The army tried to move to an invalid hex!");
                if(totalMovement >= 0)
                {
                    int movementSpent = maxMovement - totalMovement;
                    msg = msg + maxMovement + " points, " + movementSpent + " spent, ";
                }
                msg = msg + "stopped at " + Main.main.locationStr(lastHex) + ".";
                parentOrder.addInfo(msg);
                if(parentChar.isArmyCO(parentOrder.getOrder()))
                {
                    army.setNewLocation(lastHex, parentOrder.getOrder());
                }
            } else
            {
                if(totalMovement < 0)
                {
                    parentOrder.addError("Maximum movement was exceeded!");
                }
                int movementSpent = maxMovement - totalMovement;
                msg = msg + maxMovement + " points, " + movementSpent + " spent, stopped at " + Main.main.locationStr(currentHex) + ".";
                parentOrder.addInfo(msg);
                if(parentChar.isArmyCO(parentOrder.getOrder()))
                {
                    army.setNewLocation(currentHex, parentOrder.getOrder());
                }
            }
        }
        return null;
    }

    private String processArmyOrPc()
    {
        String result = parameterCount(1, "ARMYORPC");
        if(result != null)
        {
            return result;
        }
        int type = convertParameter(0);
        if(phase == 2)
        {
            if(waitForProcessState(3))
            {
                return "state";
            }
            PopCenter pc = Main.main.getNation().findOwnedPopulationCenter(parentChar.getLocation(parentOrder.getOrder()));
            boolean armyCheck = false;
            String qualifier = "";
            switch(type)
            {
            case 0: // '\0'
                armyCheck = parentChar.isArmyCO(parentOrder.getOrder());
                qualifier = "commanding";
                break;

            case 1: // '\001'
                Army army = parentChar.getArmy(parentOrder.getOrder());
                if(army != null)
                {
                    armyCheck = true;
                }
                qualifier = "with";
                break;

            default:
                return "Invalid type (" + type + ") for ARMYORPC!";
            }
            if(!armyCheck && pc == null)
            {
                parentOrder.addError(parentChar + " needs to be " + qualifier + " an army or at an owned PC.");
            }
        }
        return null;
    }

    private String processScoutArmy()
    {
        String result = parameterCount(2, "ARMYSCOUT");
        if(result != null)
        {
            return result;
        }
        int charParam = convertParameter(0);
        int maxDistance = convertParameter(1);
        if(phase == 2)
        {
            Character character = parentOrder.extractCharacter(charParam, true);
            if(character == null)
            {
                return null;
            }
            if(waitForProcessState(3) || waitForProcessState(4))
            {
                return "state";
            }
            if(!character.isArmyCO(parentOrder.getOrder()))
            {
                parentOrder.addError(character + " is not commander of an army.");
            } else
            {
                int distance = Hex.calcHexDistance(parentChar.getLocation(parentOrder.getOrder()), character.getLocation(parentOrder.getOrder()));
                if(distance > maxDistance)
                {
                    parentOrder.addError(character + "'s army is too far away (" + distance + " hexes, maximum is " + maxDistance + ").");
                }
            }
        }
        return null;
    }

    private String processArtifact(int artifactParam, boolean using, boolean wait)
    {
        if(artifactParam == -1)
        {
            String result = parameterCount(1, "ARTIFACT");
            if(result != null)
            {
                return result;
            }
            artifactParam = convertParameter(0);
        }
        if(phase == 2)
        {
            if(wait && waitForProcessState(5))
            {
                return "state";
            }
            int artifactNum = parentOrder.getParameterNumber(artifactParam);
            if(artifactNum == -1)
            {
                return "Couldn't get artifact (" + artifactParam + ") for artifact check!";
            }
            String artifact = parentChar.getArtifactName(artifactNum);
            boolean hasArtifact = parentChar.hasArtifact(artifactNum, parentOrder.getOrder());
            if(!hasArtifact)
            {
                parentOrder.addError(parentChar + " doesn't have " + (artifact != null ? artifact : "artifact #" + artifactNum) + ".");
            } else
            if(using && artifact != null)
            {
                parentOrder.addHelp("Attempting to use " + artifact + ".");
            }
        }
        return null;
    }

    private String processFindArtifact()
    {
        String result = parameterCount(1, "ARTYFIND");
        if(result != null)
        {
            return result;
        }
        int artifactParam = convertParameter(0);
        int artifact = parentOrder.getParameterNumber(artifactParam);
        if(artifact == -1)
        {
            return "Couldn't get artifact (" + artifactParam + ") for find artifact check!";
        }
        if(phase == 1)
        {
            clearProcessState(5);
        } else
        if(phase == 2)
        {
            if(waitForPartialState(5))
            {
                return "state";
            }
            setProcessState(5);
            if(parentChar.getTotalMageRank() == 0)
            {
                parentOrder.addWarning(parentChar + " doesn't have Mage rank.");
            }
            if(artifact == 0)
            {
                parentOrder.addWarning("Chance of success is much higher if artifact id # is known and provided.");
            } else
            {
                String artifactName = parentChar.getArtifactName(artifact);
                parentOrder.addHelp("Trying to find " + (artifactName != null ? artifactName : "artifact #" + artifact) + ".");
            }
            int artiList[] = new int[1];
            artiList[0] = artifact;
            parentChar.setNewArtifacts(artiList, parentOrder.getOrder());
        }
        return null;
    }

    private String processArtifactList()
    {
        String result = parameterCount(2, "ARTYLIST");
        if(result != null)
        {
            return result;
        }
        int type = convertParameter(0);
        int artifactParam = convertParameter(1);
        if(phase == 1)
        {
            clearProcessState(5);
        } else
        if(phase == 2)
        {
            if(waitForPartialState(5))
            {
                return "state";
            }
            setProcessState(5);
            int numArtifacts = parentOrder.getNumberOfParameters() + 1;
            int total = numArtifacts - artifactParam;
            if(total < 1)
            {
                parentOrder.addError("No artifacts have been specified.");
                return null;
            }
            if(total > 6)
            {
                parentOrder.addError("Only 6 artifacts can be specified.");
                return null;
            }
            int addList[] = new int[total];
            int delList[] = new int[total];
            for(int i = artifactParam; i < numArtifacts; i++)
            {
                result = processArtifact(i, false, false);
                if(result != null)
                {
                    return result;
                }
                int artifact = parentOrder.getParameterNumber(i);
                addList[i - artifactParam] = artifact;
                delList[i - artifactParam] = -1 * artifact;
            }

            StringBuffer info = new StringBuffer();
            info.append("Affects ");
            for(int i = 0; i < addList.length; i++)
            {
                String artifactName = parentChar.getArtifactName(addList[i]);
                if(artifactName != null)
                {
                    info.append(artifactName + ", ");
                }
            }

            info.delete(info.length() - 2, info.length());
            parentOrder.addHelp(info.toString());
            switch(type)
            {
            case 1: // '\001'
                Character character = parentOrder.extractCharacter(type, false);
                if(character == null)
                {
                    return null;
                }
                character.setNewArtifacts(addList, parentOrder.getOrder());
                parentChar.setNewArtifacts(delList, parentOrder.getOrder());
                break;

            case 2: // '\002'
                parentChar.setNewArtifacts(delList, parentOrder.getOrder());
                break;

            case 3: // '\003'
                parentChar.setNewArtifacts(addList, parentOrder.getOrder());
                break;

            default:
                return "Invalid type (" + type + ") for ARTYLIST!";
            }
        }
        return null;
    }

    private String processArtifactName()
    {
        String result = parameterCount(1, "ARTYNAME");
        if(result != null)
        {
            return result;
        }
        int artifactParam = convertParameter(0);
        if(phase == 2)
        {
            int artifactNum = parentOrder.getParameterNumber(artifactParam);
            if(artifactNum == -1)
            {
                return "Couldn't get artifact (" + artifactParam + ") for ARTYNAME!";
            }
            String artifact = parentChar.getArtifactName(artifactNum);
            if(artifact != null)
            {
                parentOrder.addHelp("Affects " + artifact + ".");
            }
        }
        return null;
    }

    private String processCharacter(int charParam, int charLoc, int state)
    {
        if(charParam == -1)
        {
            String result = parameterCount(3, "CHARACTER");
            if(result != null)
            {
                return result;
            }
            charParam = convertParameter(0);
            charLoc = convertParameter(1);
            state = convertParameter(2);
        }
        if(phase == 2)
        {
            Character character = parentOrder.extractCharacter(charParam, true);
            int location = parentOrder.extractLocation(charLoc, true);
            if(character == null || location == -1)
            {
                return null;
            }
            if(waitForProcessState(4))
            {
                return "state";
            }
            if(character.getLocation(parentOrder.getOrder()) != location && state != 4)
            {
                if(character.getLocation(parentOrder.getOrder()) != 0)
                {
                    parentOrder.addError(character + " is not at " + Main.main.locationStr(location) + ".");
                }
            } else
            if(state == 4)
            {
                int distance = Hex.calcHexDistance(character.getLocation(parentOrder.getOrder()), location);
                if(distance > 1)
                {
                    parentOrder.addError(character + " is not at or adjacent to " + Main.main.locationStr(location) + ".");
                }
            }
            String sourceNation = Main.main.getNation().getNationName(Main.main.getNation().getNation());
            String targetNation = Main.main.getNation().getNationName(character.getNation());
            switch(state)
            {
            case 0: // '\0'
                if(character.getNation() != Main.main.getNation().getNation())
                {
                    parentOrder.addError(character + " belongs to " + targetNation + ".");
                }
                break;

            case 1: // '\001'
                break;

            case 2: // '\002'
            case 4: // '\004'
                if(character.getNation() == Main.main.getNation().getNation())
                {
                    parentOrder.addError(character + " belongs to your nation.");
                }
                break;

            case 3: // '\003'
                Main.main.getNation();
                if(!Nation.isFriend(parentChar.getNation(), character.getNation()))
                {
                    parentOrder.addError(targetNation + " is not a friendly nation.");
                }
                break;

            case 5: // '\005'
                if(character.getNation() != Main.main.getNation().getNation())
                {
                    parentOrder.addWarning(sourceNation + " and " + targetNation + " must have friendly relations with each other.");
                }
                break;

            case 6: // '\006'
                if(character.getNation() != Main.main.getNation().getNation())
                {
                    parentOrder.addWarning("You must have friendly relations with " + targetNation + ".");
                }
                break;

            default:
                return "Invalid state (" + state + ") for CHARACTER!";
            }
        }
        return null;
    }

    private String processCharacterMove()
    {
        String result = parameterCount(3, "CHARMOVE");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        int max = convertParameter(1);
        int type = convertParameter(2);
        if(phase == 1)
        {
            if(type < 0 || type > 5)
            {
                return "Invalid type (" + type + ") for CHARMOVE!";
            }
            if(type != 1)
            {
                clearProcessState(4);
                clearProcessState(3);
            }
        } else
        if(phase == 2)
        {
            if(type != 1)
            {
                if(type == 2 && waitForProcessState(2))
                {
                    return "state";
                }
                if(waitForPartialState(4) || waitForPartialState(3))
                {
                    return "state";
                }
                setProcessState(4);
                setProcessState(3);
            } else
            if(waitForProcessState(4))
            {
                return "state";
            }
            if(type != 1)
            {
                if(type != 2 && type != 5)
                {
                    parentChar.setArmy(((Army) (null)), parentOrder.getOrder());
                } else
                {
                    int charParam = 2;
                    if(type == 5)
                    {
                        charParam = 1;
                    }
                    Character character = parentOrder.extractCharacter(charParam, false);
                    if(character != null)
                    {
                        int location = parentOrder.extractLocation(locParam, false);
                        if(character.getLocation(parentOrder.getOrder()) == location)
                        {
                            Army army = character.getArmy(parentOrder.getOrder());
                            if(army != null)
                            {
                                parentChar.setArmy(army, parentOrder.getOrder());
                            }
                        }
                    }
                }
            }
            if(type != 3 && type != 5)
            {
                int location;
                if(type == 4)
                {
                    location = Main.main.getNation().getCapital();
                } else
                {
                    location = parentOrder.getParameterNumber(locParam);
                    if(location == -1)
                    {
                        parentOrder.addError("Could not determine destination location.");
                        return null;
                    }
                }
                int distance = Hex.calcHexDistance(parentChar.getLocation(parentOrder.getOrder()), location);
                if(distance == 0 && type != 1)
                {
                    parentOrder.addWarning(parentChar + " is already at the destination location.");
                } else
                if(distance > max && max > 0)
                {
                    if(type != 1)
                    {
                        parentOrder.addError("Can not move " + distance + " hexes, maximum is " + max + ".");
                    } else
                    if(type == 1)
                    {
                        parentOrder.addError(distance + " hexes " + "is too far, maximum is " + max + ".");
                    }
                } else
                if(type != 1)
                {
                    parentChar.setNewLocation(location, parentOrder.getOrder());
                    String plural = distance != 1 ? " hexes " : " hex ";
                    parentOrder.addInfo("Moved " + distance + plural + "to " + Main.main.locationStr(location) + ".");
                }
            }
        }
        return null;
    }

    private String processCommander()
    {
        String result = parameterCount(4, "COMMANDER");
        if(result != null)
        {
            return result;
        }
        int charParam = convertParameter(0);
        int charLoc = convertParameter(1);
        int status = convertParameter(2);
        int CO = convertParameter(3);
        if(phase == 2)
        {
            Character character = parentOrder.extractCharacter(charParam, true);
            if(character == null)
            {
                return null;
            }
            if(waitForProcessState(4) || waitForProcessState(3))
            {
                return "state";
            }
            if(character.getTotalCommandRank() > 0 && (CO == 4 || CO == 5))
            {
                if(additionalInfo.size() == 0)
                {
                    String msg = "COMPANYCO:Is " + character + " a company commander?";
                    addAdditionalInfo(msg, false);
                    return "state";
                }
                character.setCompanyCO(getAdditionalInfo(), 0);
            }
            boolean companyCO = character.isCompanyCO(parentOrder.getOrder());
            processCharacter(charParam, charLoc, status);
            boolean armyCO = character.isArmyCO(parentOrder.getOrder());
            boolean navyCO = false;
            boolean withArmy = false;
            Army army = character.getArmy(parentOrder.getOrder());
            if(army != null && armyCO && army.isNavy())
            {
                armyCO = false;
                navyCO = true;
            }
            if(!armyCO && !navyCO)
            {
                withArmy = character.isCommanderInArmy(parentOrder.getOrder());
            }
            switch(CO)
            {
            case 0: // '\0'
                if(!armyCO)
                {
                    parentOrder.addError(character + " is not an army CO.");
                }
                break;

            case 1: // '\001'
                if(navyCO)
                {
                    break;
                }
                Hex hex = null;
                int location = parentOrder.extractLocation(charLoc, false);
                if(location != -1)
                {
                    hex = Main.main.getMap().findHex(location);
                }
                if(hex != null && Main.main.getMap().adjacentToWater(hex))
                {
                    parentOrder.addWarning(character + " is an army CO doing navy movement.");
                } else
                {
                    parentOrder.addError(character + " is not a navy CO.");
                }
                break;

            case 2: // '\002'
                if(!armyCO && !navyCO)
                {
                    parentOrder.addError(character + " is not an army or navy CO.");
                }
                break;

            case 3: // '\003'
                if(!armyCO && !navyCO && !withArmy)
                {
                    parentOrder.addError(character + " is not with an army or navy.");
                }
                break;

            case 4: // '\004'
                if(!companyCO)
                {
                    parentOrder.addError(character + " is not a company CO.");
                }
                break;

            case 5: // '\005'
                if(!armyCO && !navyCO && !companyCO)
                {
                    parentOrder.addError(character + " is not an army, " + "navy, or company commander.");
                }
                break;

            case 6: // '\006'
                if(character.getArmy(parentOrder.getOrder()) == null)
                {
                    parentOrder.addError(character + " is not with an army.");
                }
                break;

            case 7: // '\007'
                if(armyCO)
                {
                    break;
                }
                boolean land = parentOrder.onLand(charLoc);
                if(navyCO && land)
                {
                    parentOrder.addWarning(character + " is a navy CO doing army movement.");
                } else
                {
                    parentOrder.addError(character + " is not an army CO.");
                }
                break;

            default:
                return "Invalid CO parameter for COMMANDER order!";
            }
        }
        return null;
    }

    private String processNotCommander()
    {
        String result = parameterCount(4, "COMMANDERNOT");
        if(result != null)
        {
            return result;
        }
        int charParam = convertParameter(0);
        int charLoc = convertParameter(1);
        int status = convertParameter(2);
        int CO = convertParameter(3);
        if(phase == 2)
        {
            Character character = parentOrder.extractCharacter(charParam, false);
            if(character == null)
            {
                return null;
            }
            if(waitForProcessState(4) || waitForProcessState(3))
            {
                return "state";
            }
            if(character.getTotalCommandRank() > 0 && (CO == 4 || CO == 5))
            {
                if(additionalInfo.size() == 0)
                {
                    String msg = "COMPANYCO:Is " + character + " a company commander?";
                    addAdditionalInfo(msg, false);
                    return "state";
                }
                character.setCompanyCO(getAdditionalInfo(), 0);
            }
            boolean companyCO = character.isCompanyCO(parentOrder.getOrder());
            processCharacter(charParam, charLoc, status);
            boolean armyCO = character.isArmyCO(parentOrder.getOrder());
            boolean navyCO = false;
            boolean withArmy = false;
            Army army = character.getArmy(parentOrder.getOrder());
            if(army != null && armyCO && army.isNavy())
            {
                armyCO = false;
                navyCO = true;
            }
            if(!armyCO && !navyCO)
            {
                withArmy = character.isCommanderInArmy(parentOrder.getOrder());
            }
            switch(CO)
            {
            case 0: // '\0'
                if(armyCO)
                {
                    parentOrder.addError(character + " is an army CO.");
                }
                break;

            case 1: // '\001'
                if(navyCO)
                {
                    parentOrder.addError(character + " is a navy CO.");
                }
                break;

            case 2: // '\002'
                if(armyCO || navyCO)
                {
                    parentOrder.addError(character + " is an army or navy CO.");
                }
                break;

            case 4: // '\004'
                if(companyCO)
                {
                    parentOrder.addError(character + " is a company CO.");
                }
                break;

            case 5: // '\005'
                if(armyCO || navyCO || companyCO)
                {
                    parentOrder.addError(character + " is an army, " + "navy, or company commander.");
                }
                break;

            case 6: // '\006'
                if(character.getArmy(parentOrder.getOrder()) == null || armyCO || navyCO)
                {
                    parentOrder.addError(character + " is not with an army or is the commander of it.");
                }
                break;

            case 3: // '\003'
            default:
                return "Invalid CO parameter (" + CO + ") for COMMANDERNOT order!";
            }
        }
        return null;
    }

    private String processCommandTransfer()
    {
        String result = parameterCount(1, "COMMANDXFER");
        if(result != null)
        {
            return result;
        }
        int charParam = convertParameter(0);
        Character character = parentOrder.extractCharacter(charParam, false);
        if(character == null)
        {
            return null;
        }
        if(phase == 1)
        {
            clearProcessState(0);
            clearProcessState(6);
            clearProcessState(3);
        } else
        if(phase == 2)
        {
            if(parentChar.getTotalCommandRank() > 0)
            {
                if(additionalInfo.size() == 0)
                {
                    String msg = "COMPANYCO:Is " + parentChar + " a company commander?";
                    addAdditionalInfo(msg, false);
                    return "state";
                }
                parentChar.setCompanyCO(getAdditionalInfo(), 0);
            }
            boolean companyCO = parentChar.isCompanyCO(parentOrder.getOrder());
            if(waitForPartialState(0) || waitForPartialState(6) || waitForPartialState(3))
            {
                return "state";
            }
            setProcessState(0);
            setProcessState(6);
            setProcessState(3);
            if(companyCO)
            {
                parentChar.setCompanyCO(false, parentOrder.getOrder());
                character.setCompanyCO(true, parentOrder.getOrder());
                return null;
            }
            Army sourceArmy = parentChar.getArmy(parentOrder.getOrder());
            Army destinationArmy = null;
            if(character.isArmyCO(parentOrder.getOrder() + 1))
            {
                destinationArmy = character.getArmy(parentOrder.getOrder() + 1);
            }
            if(sourceArmy == null)
            {
                parentOrder.addError(parentChar + " is not an army/navy or company CO.");
                return null;
            }
            if(destinationArmy == null)
            {
                character.setArmy(sourceArmy, parentOrder.getOrder());
                sourceArmy.setArmyCommander(character, parentOrder.getOrder());
            } else
            {
                int currentSourceTroops[] = sourceArmy.getTroopContent(parentOrder.getOrder());
                destinationArmy.setTroopContent(currentSourceTroops, parentOrder.getOrder());
                sourceArmy.disbandArmy(destinationArmy, parentOrder.getOrder(), false);
            }
            String stays = parentOrder.getParameterString(charParam + 1);
            if(stays == null)
            {
                parentOrder.addError("Join army was not specified.");
            } else
            {
                char stayFlag = stays.charAt(0);
                switch(stayFlag)
                {
                case 78: // 'N'
                case 110: // 'n'
                    parentChar.setArmy(((Army) (null)), parentOrder.getOrder());
                    break;

                case 89: // 'Y'
                case 121: // 'y'
                    if(destinationArmy != null)
                    {
                        parentChar.setArmy(destinationArmy, parentOrder.getOrder());
                    }
                    break;

                default:
                    parentOrder.addError("Invalid join army parameter specified.");
                    break;
                }
            }
        }
        return null;
    }

    private String processDock()
    {
        String result = parameterCount(1, "DOCK");
        if(result != null)
        {
            return result;
        }
        int type = convertParameter(0);
        if(phase == 2)
        {
            if(waitForProcessState(4))
            {
                return "state";
            }
            PopCenter pc = Main.main.getNation().findPopulationCenter(parentChar.getLocation(parentOrder.getOrder()));
            String location = Main.main.locationStr(parentChar.getLocation(parentOrder.getOrder()));
            switch(type)
            {
            case 0: // '\0'
                if(pc == null || pc.getDock() != 1)
                {
                    parentOrder.addError(location + " does not have a harbor.");
                }
                break;

            case 1: // '\001'
                if(pc == null || pc.getDock() != 2)
                {
                    parentOrder.addError(location + " does not have a port.");
                }
                break;

            case 2: // '\002'
                Hex locHex = Main.main.getMap().findHex(parentChar.getLocation(parentOrder.getOrder()));
                if(locHex == null)
                {
                    return "Could not find location " + location + " for DOCK!";
                }
                if(locHex.getTerrain() != 2 && (pc == null || pc.getDock() == 0))
                {
                    parentOrder.addError(location + " does not have a shore, harbor, or dock.");
                }
                break;

            case 3: // '\003'
                if(pc != null && pc.getDock() != 0)
                {
                    parentOrder.addError(pc + " already has a harbor or port.");
                }
                break;

            case 4: // '\004'
                if(pc == null || pc.getDock() == 0)
                {
                    parentOrder.addError(location + " doesn't have a harbor or port.");
                }
                break;

            default:
                return "Invalid type parameter for DOCK order!";
            }
        }
        return null;
    }

    private String processEnemyArmy()
    {
        String result = parameterCount(2, "ENEMYARMY");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        int type = convertParameter(1);
        if(phase == 2)
        {
            int location = parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            if(type == 1)
            {
                if(!Main.main.getNation().isEnemyArmyPresent(location))
                {
                    if(Main.main.getNation().isNeutralArmyPresent(location))
                    {
                        parentOrder.addWarning("Army belonging to a neutral nation is present at " + Main.main.locationStr(location) + ".");
                    } else
                    {
                        parentOrder.addError("An enemy army is not present at " + Main.main.locationStr(location) + ".");
                    }
                }
            } else
            if(type == 0)
            {
                if(Main.main.getNation().isEnemyArmyPresent(location))
                {
                    parentOrder.addError("An enemy army is present at " + Main.main.locationStr(location) + ".");
                } else
                if(Main.main.getNation().isNeutralArmyPresent(location))
                {
                    parentOrder.addWarning("Possible enemy force is present at " + Main.main.locationStr(location) + ".");
                }
            } else
            {
                return "Invalid type (" + type + ") for ENEMYARMY!";
            }
        }
        return null;
    }

    private String processFeature()
    {
        String result = parameterCount(4, "FEATURE");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        int dirParam = convertParameter(1);
        int type = convertParameter(2);
        int logic = convertParameter(3);
        if(phase == 2)
        {
            int location = parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            Hex hex = Main.main.getMap().findHex(location);
            if(hex == null)
            {
                parentOrder.addError("Invalid location of " + Main.main.locationStr(location) + " specified.");
                return null;
            }
            int direction = -1;
            if(dirParam == 0)
            {
                direction = 0;
            } else
            {
                String dirString = parentOrder.getParameterString(dirParam);
                if(dirString != null)
                {
                    direction = Hex.convertDirection(dirString);
                }
                if(direction == -1)
                {
                    parentOrder.addError("Could not determine direction.");
                    return null;
                }
            }
            switch(type)
            {
            case 9: // '\t'
                if(hex.hasFeature(3, direction) && !hex.hasFeature(5, direction))
                {
                    parentOrder.addError("A deep river is present while a road is not.");
                }
                break;

            case 10: // '\n'
                boolean adjacent = Main.main.getMap().adjacentToWater(hex) | hex.hasFeature(3);
                if(adjacent && logic == 1)
                {
                    parentOrder.addError("Location " + Main.main.locationStr(location) + " is adjacent to water.");
                    break;
                }
                if(!adjacent && logic == 0)
                {
                    parentOrder.addError("Location " + Main.main.locationStr(location) + " is not adjacent to water.");
                }
                break;

            case 1: // '\001'
            case 2: // '\002'
            case 3: // '\003'
            case 4: // '\004'
            case 5: // '\005'
                if(!hex.hasFeature(type, direction) && logic == 0)
                {
                    parentOrder.addError("Location " + Main.main.locationStr(location) + " does not have a " + Hex.featureDescription(type) + ".");
                    break;
                }
                if(hex.hasFeature(type, direction) && logic == 1)
                {
                    parentOrder.addError("Location " + Main.main.locationStr(location) + " has a " + Hex.featureDescription(type) + ".");
                }
                break;

            case 6: // '\006'
            case 7: // '\007'
            case 8: // '\b'
            default:
                return "Invalid type (" + type + ") specified for FEATURE!";
            }
        }
        return null;
    }

    private String processFortification()
    {
        String result = parameterCount(1, "FORT");
        if(result != null)
        {
            return result;
        }
        int type = convertParameter(0);
        if(phase == 2)
        {
            PopCenter pc = Main.main.getNation().findPopulationCenter(parentChar.getLocation(parentOrder.getOrder()));
            if(pc != null)
            {
                switch(type)
                {
                case 0: // '\0'
                    if(pc.getFortification() == 0)
                    {
                        parentOrder.addError(pc + " has no fortifications.");
                    }
                    break;

                case 1: // '\001'
                    if(pc.getFortification() == 5)
                    {
                        parentOrder.addError(pc + " already has a citadel.");
                    }
                    break;

                default:
                    return "Types 1 to 5 of FORT are not implemented!";
                }
            }
        }
        return null;
    }

    private String processHostage()
    {
        String result = parameterCount(2, "HOSTAGE");
        if(result != null)
        {
            return result;
        }
        int charParam = convertParameter(0);
        int type = convertParameter(1);
        if(phase == 2)
        {
            String charName = "";
            Character character = parentOrder.extractCharacter(charParam, false);
            if(character != null)
            {
                charName = character.getName();
            } else
            {
                charName = parentOrder.getParameterString(charParam);
                if(charName == null)
                {
                    if(phase == 1)
                    {
                        parentOrder.addError("Couldn't get character for parameter " + charParam + ".");
                    }
                    return null;
                }
            }
            boolean hostage = false;
            if(character == null)
            {
                if(additionalInfo.size() == 0)
                {
                    String msg = "HOSTAGE:Is \"" + charName + "\" a hostage?";
                    addAdditionalInfo(msg, false);
                    return "state";
                }
                hostage = getAdditionalInfo();
            } else
            if(character.getLocation(parentOrder.getOrder()) == 0)
            {
                hostage = true;
            }
            switch(type)
            {
            case 0: // '\0'
                if(hostage)
                {
                    parentOrder.addError(charName + " is a hostage.");
                }
                break;

            case 1: // '\001'
                if(!hostage)
                {
                    parentOrder.addError(charName + " is not a hostage.");
                }
                break;

            default:
                return "Invalid type parameter (" + type + ") for HOSTAGE!";
            }
        }
        return null;
    }

    private String processImprovePopCenter()
    {
        if(phase == 2)
        {
            PopCenter pc = Main.main.getNation().findOwnedPopulationCenter(parentChar.getLocation(parentOrder.getOrder()));
            if(pc != null)
            {
                int requirement = pc.improvementRequirement();
                int total = pc.getLoyalty() + parentChar.getTotalEmissaryRank();
                if(total < requirement - 5)
                {
                    parentOrder.addWarning("Loyalty plus emissary rank may be too low to improve " + pc + ".");
                }
                String msg = parentChar + " [E" + parentChar.getTotalEmissaryRank() + "], ";
                msg = msg + pc + " loyalty " + pc.getLoyalty() + ", ";
                msg = msg + "Total is " + total + ", ";
                msg = msg + "Recommended is " + requirement + ".";
                parentOrder.addInfo(msg);
            }
        }
        return null;
    }

    private String processLand()
    {
        String result = parameterCount(1, "LAND");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        if(phase == 2)
        {
            if(locParam == 0 && waitForProcessState(4))
            {
                return "state";
            }
            int location = parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            if(!parentOrder.onLand(locParam))
            {
                parentOrder.addError("Location " + location + " is shallow or deep water.");
            }
        }
        return null;
    }

    private String processMageAmount()
    {
        String result = parameterCount(3, "MAGEAMT");
        if(result != null)
        {
            return result;
        }
        int amtParam = convertParameter(0);
        int max = convertParameter(1);
        int type = convertParameter(2);
        if(phase == 1)
        {
            if(type == 2)
            {
                clearProcessState(0);
            }
        } else
        if(phase == 2)
        {
            if(type == 2)
            {
                if(waitForPartialState(0))
                {
                    return "state";
                }
                setProcessState(0);
            }
            int quantity = parentOrder.getParameterNumber(amtParam);
            if(quantity == -1)
            {
                parentOrder.addError("Could not determine the quantity.");
                return null;
            }
            int maxQuantity = parentChar.getNaturalMageRank() * max;
            if(quantity > maxQuantity)
            {
                String commodity = "";
                switch(type)
                {
                case 0: // '\0'
                    commodity = "Mounts";
                    break;

                case 1: // '\001'
                    commodity = "Food";
                    break;

                case 2: // '\002'
                    commodity = "Men-at-Arms";
                    break;

                default:
                    return "Invalid commodity type (" + type + ") for MAGEAMT!";
                }
                parentOrder.addError("Maximum of " + maxQuantity + " " + commodity + " was exceeded.");
                quantity = maxQuantity;
            }
            if(type == 2)
            {
                Army army = parentChar.getArmy(parentOrder.getOrder());
                if(army != null)
                {
                    int amounts[] = new int[6];
                    amounts[5] = quantity;
                    army.setTroopContent(amounts, parentOrder.getOrder());
                }
            }
        }
        return null;
    }

    private String processNation()
    {
        String result = parameterCount(2, "NATION");
        if(result != null)
        {
            return result;
        }
        int type = convertParameter(0);
        int param = convertParameter(1);
        if(phase == 2)
        {
            int nationNumber = parentOrder.getParameterNumber(param);
            if(nationNumber == -1)
            {
                parentOrder.addError("Could not determine nation for order.");
                return null;
            }
            String nationName = Main.main.getNation().getNationName(nationNumber);
            Main.main.getNation();
            if(Nation.isNeutral(nationNumber) && type != 2)
            {
                parentOrder.addWarning(nationName + " is a neutral nation.");
                return null;
            }
            String nationMsg = nationName + " (";
            int alignment = Nation.getNationAlignment(nationNumber);
            switch(alignment)
            {
            case 1: // '\001'
                nationMsg = nationMsg + "Free People)";
                break;

            case 2: // '\002'
                nationMsg = nationMsg + "Dark Servant)";
                break;

            case 0: // '\0'
                nationMsg = nationMsg + "Neutral)";
                break;

            default:
                return "Invalid nation alignment (" + alignment + ") for NATION!";
            }
            parentOrder.addHelp(nationMsg);
            switch(type)
            {
            case 0: // '\0'
                Main.main.getNation();
                if(Nation.isEnemy(parentChar.getNation(), nationNumber))
                {
                    parentOrder.addError(nationName + " is an enemy nation.");
                }
                break;

            case 2: // '\002'
                break;

            case 1: // '\001'
                Main.main.getNation();
                if(Nation.isFriend(parentChar.getNation(), nationNumber))
                {
                    parentOrder.addError(nationName + " is an allied nation.");
                }
                break;

            default:
                return "Invalid type of " + type + " indicated for NATION!";
            }
        }
        return null;
    }

    private String processNavyMove()
    {
        if(phase == 1)
        {
            clearProcessState(2);
            clearProcessState(4);
        } else
        if(phase == 2)
        {
            if(waitForProcessState(0))
            {
                return "state";
            }
            Army army = parentChar.getArmy(parentOrder.getOrder());
            if(army == null)
            {
                setProcessState(2);
                setProcessState(4);
                return null;
            }
            boolean requiredTransports = false;
            if(additionalInfo.size() == 0)
            {
                int amounts[] = army.getTroopContent(parentOrder.getOrder());
                int cavalry = amounts[0] + amounts[1];
                int grunts = amounts[2] + amounts[3] + amounts[4] + amounts[5];
                int cavships = cavalry != 0 ? cavalry / 150 : 0;
                if(cavalry % 150 > 0)
                {
                    cavships++;
                }
                int gruntships = grunts != 0 ? grunts / 250 : 0;
                if(grunts % 250 > 0)
                {
                    gruntships++;
                }
                int ships = cavships + gruntships;
                String msg = "NAVY:Does " + parentChar + "'s navy have enough " + "transports? (needs " + ships + ")";
                addAdditionalInfo(msg, false);
                if(army.getFoodRequired() == 0)
                {
                    msg = "FOOD:Will " + parentChar + "'s navy have 1 food?";
                    addAdditionalInfo(msg, false);
                }
                return "state";
            }
            requiredTransports = getAdditionalInfo();
            if(army.getFoodRequired() == 0)
            {
                army.setFoodRequired(1);
                army.setHasEnoughFood(getAdditionalInfo());
            }
            boolean food = army.getHasEnoughFood();
            if(waitForPartialState(2) || waitForPartialState(4))
            {
                return "state";
            }
            setProcessState(2);
            setProcessState(4);
            if(!requiredTransports)
            {
                parentOrder.addError("The navy does not have enough transports to move.");
                return null;
            }
            String evasiveString = parentOrder.getParameterString(1);
            boolean evasive = !evasiveString.equalsIgnoreCase("no");
            int maxMovement = 14;
            int totalMovement = maxMovement;
            int currentHex = parentChar.getLocation(parentOrder.getOrder());
            int lastHex = currentHex;
            for(int param = 1; param < parentOrder.getNumberOfParameters() && currentHex > 0 && totalMovement >= 0; param++)
            {
                String dirString = parentOrder.getParameterString(param + 1);
                int direction = Hex.convertDirection(dirString);
                int points = 1;
                if(dirString.equalsIgnoreCase("h"))
                {
                    totalMovement--;
                    continue;
                }
                points = Main.main.getMap().calcNavyMovement(currentHex, direction);
                if(points < 0)
                {
                    currentHex = points;
                    continue;
                }
                if(points == 999)
                {
                    totalMovement = 0;
                } else
                {
                    if(!food)
                    {
                        if((points * 4) % 3 == 0)
                        {
                            points = (points * 4) / 3;
                        } else
                        {
                            points = (points * 4) / 3 + 1;
                        }
                    }
                    if(evasive)
                    {
                        points *= 2;
                    }
                    totalMovement -= points;
                }
                if(totalMovement < 0)
                {
                    continue;
                }
                currentHex = Main.main.getMap().getAdjacentHex(currentHex, direction);
                lastHex = currentHex;
                if(param + 1 >= parentOrder.getNumberOfParameters())
                {
                    continue;
                }
                willPCBlockMovement(currentHex);
                if(Main.main.getNation().isEnemyArmyPresent(currentHex))
                {
                    parentOrder.addWarning("An enemy army is present at " + currentHex + " and may block movement.");
                }
            }

            String msg = food ? "Food, " : "No Food, ";
            msg = msg + (evasive ? "Evasive, " : "Normal, ");
            if(currentHex < 0)
            {
                if(currentHex == -1)
                {
                    parentOrder.addError("The navy tried to move into an invalid hex!");
                } else
                {
                    parentOrder.addWarning("The navy can only move onto a shore hex or hex with a harbor/port or major river.");
                }
                if(totalMovement >= 0)
                {
                    int movementSpent = maxMovement - totalMovement;
                    msg = msg + maxMovement + " points, " + movementSpent + " spent, ";
                }
                msg = msg + "stopped at " + Main.main.locationStr(lastHex) + ".";
                parentOrder.addInfo(msg);
                if(parentChar.isArmyCO(parentOrder.getOrder()))
                {
                    army.setNewLocation(lastHex, parentOrder.getOrder());
                }
            } else
            {
                if(totalMovement < 0)
                {
                    parentOrder.addError("Maximum movement was exceeded!");
                }
                int movementSpent = maxMovement - totalMovement;
                msg = msg + maxMovement + " points, " + movementSpent + " spent, stopped at " + Main.main.locationStr(currentHex) + ".";
                parentOrder.addInfo(msg);
                if(parentChar.isArmyCO(parentOrder.getOrder()))
                {
                    army.setNewLocation(currentHex, parentOrder.getOrder());
                }
            }
        }
        return null;
    }

    private String processNewCharacter()
    {
        String result = parameterCount(2, "NEWCHAR");
        if(result != null)
        {
            return result;
        }
        int param = convertParameter(0);
        int type = convertParameter(1);
        if(phase == 2)
        {
            if(type >= 0 && type < 4)
            {
                processRank(0, type, 0, 0);
                int rankValue = parentChar.getNaturalRank(type);
                if(rankValue > 0)
                {
                    int maxRank = Main.main.getNation().getMaxRank(type);
                    if(rankValue > maxRank)
                    {
                        rankValue = maxRank;
                    }
                    parentOrder.addHelp("Creating new " + Character.getRankName(type) + " character with rank " + rankValue + ".");
                }
            } else
            if(type == 4)
            {
                StringBuffer desc = new StringBuffer();
                int sum = 0;
                int ranks[] = new int[4];
                for(int i = 0; i < 4; i++)
                {
                    ranks[i] = parentOrder.getParameterNumber(param + i);
                    sum += ranks[i];
                    if(ranks[i] == -1)
                    {
                        parentOrder.addError("Could not determine one of the ranks.");
                        return null;
                    }
                    if(ranks[i] > 0 && ranks[i] < 10)
                    {
                        parentOrder.addError(Character.getRankName(i) + " rank less " + "than 10 specified.");
                        return null;
                    }
                    if(ranks[i] > 30)
                    {
                        parentOrder.addError(Character.getRankName(i) + " rank of more " + "than 30 specified.");
                        return null;
                    }
                    if(ranks[i] > 0)
                    {
                        desc.append(ranks[i] + " " + Character.getRankName(i) + ", ");
                    }
                }

                if(sum != 30)
                {
                    parentOrder.addError("Spend 30 points on ranks, current total is " + sum + ".");
                } else
                {
                    desc.delete(desc.length() - 2, desc.length());
                    parentOrder.addHelp("Creating new character: " + desc + ".");
                }
            }
        }
        return null;
    }

    private String processOneRing()
    {
        if(phase == 2)
        {
            if(waitForProcessState(4) || waitForProcessState(5))
            {
                return "state";
            }
            if(parentChar.getLocation(parentOrder.getOrder()) != 3423)
            {
                parentOrder.addError(parentChar + " is not at hex 3423.");
                return null;
            }
            String artifact = parentChar.getArtifactName(14);
            boolean hasArtifact = parentChar.hasArtifact(14, parentOrder.getOrder());
            if(!hasArtifact)
            {
                parentOrder.addError(parentChar + " does not have " + (artifact != null ? artifact : "the ring") + ".");
            } else
            if(Nation.getNationAlignment(parentChar.getNation()) == 1)
            {
                parentOrder.addHelp(parentChar + " is attempting to destroy " + (artifact != null ? artifact : "the ring") + ".");
            } else
            {
                parentOrder.addHelp(parentChar + " is attempting to transfer " + (artifact != null ? artifact : "the ring") + " to Sauron.");
            }
            for(int i = 0; i < parentChar.getOrderCount(); i++)
            {
                if(parentChar.getOrder(i) != 990)
                {
                    parentOrder.addError("Both orders must be order 990.");
                    return null;
                }
            }

        }
        return null;
    }

    private String processPC()
    {
        String result = parameterCount(2, "PC");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        int state = convertParameter(1);
        if(phase != 1 && phase == 2)
        {
            if(waitForProcessState(1))
            {
                return "state";
            }
            if(locParam == 0 && waitForProcessState(4))
            {
                return "state";
            }
            int location = parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            PopCenter pc = Main.main.getNation().findPopulationCenter(location);
            if(pc == null)
            {
                if(state != 4)
                {
                    parentOrder.addError("No population center is present at location " + Main.main.locationStr(location) + ".");
                } else
                {
                    pc = new PopCenter(location);
                    pc.setNewPC();
                    pc.setName("NEW PC (Created by " + parentChar.getName() + ")");
                    pc.setNation(parentChar.getNation());
                    pc.setFortification(0);
                    pc.setSize(1);
                    pc.setDock(0);
                    pc.setLoyalty(15);
                    pc.initStateInformation();
                    Main.main.getNation().addPopulationCenter(pc);
                }
                return null;
            }
            boolean owned = pc.getNation() == parentChar.getNation();
            boolean enemy = Nation.isEnemy(parentChar.getNation(), pc.getNation());
            boolean neutral = Nation.isNeutral(pc.getNation());
            String possibleLoss = "Enemy troops are present at " + pc + " which could result in it being captured or destroyed.";
            String possibleCombatGain = pc + " is not owned by your nation " + "but may be captured due to combat.";
            String possibleInfluenceGain = pc + " is not owned by your nation " + "but could be influenced away by emissaries.";
            String possibleDestruction = pc + " may be destroyed due to combat.";
            switch(state)
            {
            case 0: // '\0'
                if(owned)
                {
                    if(pc.getEnemyArmyPresent())
                    {
                        parentOrder.addWarning(possibleLoss);
                    }
                    break;
                }
                if(pc.getPossibleCapture() < parentOrder.getOrder() && pc.getCapturingNation() == parentChar.getNation())
                {
                    parentOrder.addWarning(possibleCombatGain);
                    break;
                }
                if(pc.getPossibleInfluence() < parentOrder.getOrder())
                {
                    parentOrder.addWarning(possibleInfluenceGain);
                } else
                {
                    parentOrder.addError(pc + " is not owned by your nation.");
                }
                break;

            case 9: // '\t'
                break;

            case 1: // '\001'
                if(!owned && pc.getHidden())
                {
                    parentOrder.addError(pc + " is hidden and not owned by you.");
                }
                break;

            case 2: // '\002'
                if(pc.getHidden())
                {
                    parentOrder.addError(pc + " is hidden.");
                    break;
                }
                if(owned)
                {
                    if(pc.getEnemyArmyPresent())
                    {
                        parentOrder.addWarning(possibleLoss);
                    } else
                    {
                        parentOrder.addError(pc + " is owned by your nation.");
                    }
                    break;
                }
                if(pc.getPossibleCapture() < parentOrder.getOrder() && pc.getCapturingNation() == parentChar.getNation())
                {
                    parentOrder.addWarning(possibleCombatGain);
                    break;
                }
                if(pc.getPossibleInfluence() < parentOrder.getOrder())
                {
                    parentOrder.addWarning(possibleInfluenceGain);
                }
                break;

            case 3: // '\003'
                if(enemy && !pc.getHidden())
                {
                    break;
                }
                if(pc.getHidden())
                {
                    parentOrder.addError(pc + " is hidden.");
                    break;
                }
                if(neutral)
                {
                    parentOrder.addWarning(pc + " belongs to a neutral.");
                } else
                {
                    parentOrder.addError(pc + " does not belong to an enemy.");
                }
                break;

            case 4: // '\004'
                if(pc.getPossibleDestruction() < parentOrder.getOrder())
                {
                    parentOrder.addWarning(possibleDestruction);
                    break;
                }
                if(pc.getEnemyArmyPresent())
                {
                    parentOrder.addWarning(possibleDestruction);
                } else
                {
                    parentOrder.addError("A population center, " + pc + ", is already here.");
                }
                break;

            case 7: // '\007'
                if(pc.isNationsCapital(parentChar.getNation()))
                {
                    parentOrder.addError(pc + " is your capital.");
                    break;
                }
                if(pc.getHidden())
                {
                    parentOrder.addError(pc + " is hidden.");
                    break;
                }
                if(owned)
                {
                    if(pc.getEnemyArmyPresent())
                    {
                        parentOrder.addWarning(possibleLoss);
                    }
                    break;
                }
                if(pc.getPossibleCapture() < parentOrder.getOrder() && pc.getCapturingNation() == parentChar.getNation())
                {
                    parentOrder.addWarning(possibleCombatGain);
                    break;
                }
                if(pc.getPossibleInfluence() < parentOrder.getOrder())
                {
                    parentOrder.addWarning(possibleInfluenceGain);
                } else
                {
                    parentOrder.addError(pc + " is not owned by your nation.");
                }
                break;

            case 8: // '\b'
                if(!pc.isNationsCapital(parentChar.getNation()))
                {
                    parentOrder.addError(pc + " is not your capital.");
                }
                break;

            case 10: // '\n'
                String sourceNation = Main.main.getNation().getNationName(Main.main.getNation().getNation());
                String targetNation = Main.main.getNation().getNationName(pc.getNation());
                if(owned)
                {
                    break;
                }
                if(pc.getHidden())
                {
                    parentOrder.addError(pc + " is hidden and not owned by you.");
                } else
                {
                    parentOrder.addWarning(targetNation + " must have " + "friendly relations with your nation.");
                }
                break;

            case 5: // '\005'
            case 6: // '\006'
            default:
                return "Invalid PC state of " + state + " for PC rule!";
            }
        }
        return null;
    }

    private String processPCSize()
    {
        String result = parameterCount(2, "PCSIZE");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        int type = convertParameter(1);
        if(phase == 2)
        {
            int location = parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            PopCenter pc = Main.main.getNation().findPopulationCenter(location);
            if(pc == null)
            {
                return null;
            }
            switch(type)
            {
            case 8: // '\b'
                if(pc.getSize() != 3 && pc.getSize() != 4 && pc.getSize() != 5)
                {
                    parentOrder.addError(pc + " needs to be a town, major town, " + "or city.");
                }
                break;

            case 9: // '\t'
                if(pc.getSize() != 4 && pc.getSize() != 5)
                {
                    parentOrder.addError(pc + " needs to be a major town or city.");
                }
                break;

            case 10: // '\n'
                if(pc.getSize() == 1)
                {
                    parentOrder.addError(pc + " needs to be larger than a camp.");
                }
                break;

            case 1: // '\001'
            case 2: // '\002'
            case 3: // '\003'
            case 4: // '\004'
            case 5: // '\005'
                if(pc.getSize() != type)
                {
                    parentOrder.addError(pc + " is not a " + PopCenter.getSizeName(type) + ".");
                }
                break;

            case 6: // '\006'
            case 7: // '\007'
            default:
                return "Invalid PC type of " + type + " for PCSIZE rule!";
            }
        }
        return null;
    }

    private String processPCState()
    {
        String result = parameterCount(2, "PC");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        int state = convertParameter(1);
        if(phase == 1)
        {
            clearProcessState(1);
        } else
        if(phase == 2)
        {
            if(waitForPartialState(1))
            {
                return "state";
            }
            setProcessState(1);
            int location = parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            PopCenter pc = Main.main.getNation().findPopulationCenter(location);
            if(pc == null)
            {
                return null;
            }
            switch(state)
            {
            case 0: // '\0'
                pc.setPossibleDestruction(parentOrder.getOrder());
                break;

            case 1: // '\001'
                pc.setPossibleCapture(parentOrder.getOrder());
                break;

            case 2: // '\002'
                pc.setPossibleInfluence(parentOrder.getOrder());
                break;

            default:
                return "Invalid state change of " + state + " for PCSTATE rule!";
            }
        }
        return null;
    }

    private String processProductInfo()
    {
        String result = parameterCount(5, "PRODUCTINFO");
        if(result != null)
        {
            return result;
        }
        int type = convertParameter(0);
        int destParam = convertParameter(1);
        int productParam = convertParameter(2);
        int numParam = convertParameter(3);
        int units = convertParameter(4);
        if(phase == 2)
        {
            int destination = 0;
            if(destParam > 0)
            {
                destination = parentOrder.getParameterNumber(destParam);
            }
            String product = parentOrder.getParameterString(productParam);
            boolean bFound = false;
            if(product != null)
            {
                for(int i = 0; i < productMapping.length && !bFound; i++)
                {
                    if(product.equalsIgnoreCase(productMapping[i][0]))
                    {
                        product = productMapping[i][1];
                        bFound = true;
                    }
                }

            }
            if(product == null || !bFound)
            {
                product = null;
                parentOrder.addError("Could not determine the product being transfered.");
            }
            int quantity = parentOrder.getParameterNumber(numParam);
            if(quantity == -1)
            {
                parentOrder.addError("Could not determine quantity for the transfer.");
            }
            if(quantity <= 100 && units == 0)
            {
                parentOrder.addWarning("# of units is expected, make sure you are not specifying a percentage.");
            }
            String msg;
            switch(type)
            {
            case 0: // '\0'
                msg = "Transporting";
                break;

            case 1: // '\001'
                msg = "Selling";
                break;

            case 2: // '\002'
                msg = "Buying";
                break;

            default:
                return "Invalid type (" + type + ") for PRODUCTINFO!";
            }
            if(quantity >= 0)
            {
                msg = msg + " " + quantity;
            }
            switch(units)
            {
            case 1: // '\001'
                msg = msg + "%";
                break;

            default:
                return "Invalid units (" + units + ") for PRODUCTINFO!";

            case 0: // '\0'
                break;
            }
            if(product != null)
            {
                msg = msg + " " + product;
            }
            if(destination > 0)
            {
                msg = msg + " to " + Main.main.locationStr(destination);
            }
            msg = msg + ".";
            parentOrder.addHelp(msg);
        }
        return null;
    }

    private String processRank(int charParam, int rank, int min, int type)
    {
        if(charParam == -1)
        {
            String result = parameterCount(4, "RANK");
            if(result != null)
            {
                return result;
            }
            charParam = convertParameter(0);
            rank = convertParameter(1);
            min = convertParameter(2);
            type = convertParameter(3);
            if(type != 0 && type != 1)
            {
                return "Invalid type (" + type + ") for RANK!";
            }
        }
        if(phase == 2)
        {
            Character character = parentOrder.extractCharacter(charParam, false);
            if(character == null)
            {
                return null;
            }
            String errorMsg = null;
            switch(rank)
            {
            case 0: // '\0'
                if(character.getTotalCommandRank() == 0)
                {
                    parentOrder.addError(character + " has no command rank.");
                }
                if(min > 0 && character.getTotalCommandRank() < min)
                {
                    errorMsg = "command";
                }
                break;

            case 1: // '\001'
                if(character.getTotalAgentRank() == 0)
                {
                    parentOrder.addError(character + " has no agent rank.");
                }
                if(min > 0 && character.getTotalAgentRank() < min)
                {
                    errorMsg = "agent";
                }
                break;

            case 2: // '\002'
                if(character.getTotalEmissaryRank() == 0)
                {
                    parentOrder.addError(character + " has no emissary rank.");
                }
                if(min > 0 && character.getTotalEmissaryRank() < min)
                {
                    errorMsg = "emissary";
                }
                break;

            case 3: // '\003'
                if(character.getTotalMageRank() == 0)
                {
                    parentOrder.addError(character + " has no mage rank.");
                }
                if(min > 0 && character.getTotalMageRank() < min)
                {
                    errorMsg = "mage";
                }
                break;

            default:
                return "Invalid rank type (" + rank + ") for RANK check!";
            }
            if(errorMsg != null)
            {
                if(type == 0)
                {
                    parentOrder.addWarning(character + " should have at least a " + errorMsg + " rank of " + min + ".");
                } else
                {
                    parentOrder.addWarning("New PC will have low loyalty if " + errorMsg + " rank is less than " + min + ".");
                }
            }
        }
        return null;
    }

    private String processSetCommander()
    {
        String result = parameterCount(3, "SETCOMMAND");
        if(result != null)
        {
            return result;
        }
        int charParam = convertParameter(0);
        int type = convertParameter(1);
        int logic = convertParameter(2);
        if(phase == 1)
        {
            if(logic != 0 && logic != 1)
            {
                return "Invalid logic (" + logic + ") for SETCOMMAND!";
            }
            clearProcessState(0);
            clearProcessState(6);
        } else
        if(phase == 2)
        {
            if(waitForPartialState(0) || waitForPartialState(6))
            {
                return "state";
            }
            setProcessState(0);
            setProcessState(6);
            Character character = parentOrder.extractCharacter(charParam, false);
            if(character == null)
            {
                return null;
            }
            int warn21;
            switch(type)
            {
            case 0: // '\0'
                if(logic == 1)
                {
                    return "Invalid logic (" + logic + ") for army SETCOMMAND!";
                }
                Army army = character.getArmy(parentOrder.getOrder());
                if(army != null)
                {
                    army.disbandArmy(((Army) (null)), parentOrder.getOrder(), true);
                }
                break;

            case 1: // '\001'
                if(character.isCompanyCO(parentOrder.getOrder()) && logic == 0 || !character.isCompanyCO(parentOrder.getOrder()) && logic == 1)
                {
                    character.setCompanyCO(!character.isCompanyCO(parentOrder.getOrder()), parentOrder.getOrder());
                } else
                {
                    warn21 = 1;
                }
                break;

            default:
                return "Invalid type (" + type + ") for SETCOMMAND!";
            }
            result = processRank(charParam, 0, 0, 0);
            if(result != null)
            {
                return result;
            }
        }
        return null;
    }

    private String processShips()
    {
        String result = parameterCount(4, "SHIPS");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        int warParam = convertParameter(1);
        int tranParam = convertParameter(2);
        int status = convertParameter(3);
        if(phase == 2)
        {
            int location = parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            int warships = parentOrder.getParameterNumber(warParam);
            int transports = parentOrder.getParameterNumber(tranParam);
            if(warships == -1 || transports == -1)
            {
                parentOrder.addError("Could not determine the number of required ships.");
                return null;
            }
            if(additionalInfo.size() == 0)
            {
                String msg;
                if(status == 1)
                {
                    msg = "NAVY:Does " + parentChar + "'s army/navy have ";
                } else
                {
                    msg = "NAVY:Does hex " + location + " have ";
                }
                msg = msg + warships + " warships and " + transports + " transports?";
                addAdditionalInfo(msg, false);
                return "state";
            }
            if(!getAdditionalInfo())
            {
                parentOrder.addError("The required number of ships are not present.");
            }
        }
        return null;
    }

    private String processNotSieged()
    {
        String result = parameterCount(2, "SIEGENOT");
        if(result != null)
        {
            return result;
        }
        int locParam = convertParameter(0);
        int type = convertParameter(1);
        if(phase == 2)
        {
            int location = Main.main.getNation().getCapital();
            if(type == 0)
            {
                location = parentOrder.extractLocation(locParam, true);
                if(location == -1)
                {
                    return null;
                }
            }
            PopCenter pc = Main.main.getNation().findPopulationCenter(location);
            if(pc == null)
            {
                return null;
            }
            if(Main.main.getNation().isEnemyArmyPresent(pc.getNation(), location))
            {
                parentOrder.addWarning(pc + " risks possibility of siege from " + "enemy armies that are present.");
            }
        }
        return null;
    }

    private String processSpellReq()
    {
        String result = parameterCount(1, "SPELLREQ");
        if(result != null)
        {
            return result;
        }
        int spellParam = convertParameter(0);
        if(phase == 2)
        {
            int spellNumber = parentOrder.getParameterNumber(spellParam);
            if(spellNumber == -1)
            {
                parentOrder.addError("Could not determine spell number.");
                return null;
            }
            Spell spell = Main.main.getSpellList().findSpell(spellNumber);
            if(spell == null)
            {
                parentOrder.addError("Spell " + spellNumber + " does not exist.");
                return null;
            }
            boolean hasPrereq = Main.main.getNation().hasSpellPrereq(spellNumber);
            if(!hasPrereq)
            {
                hasPrereq = Main.main.getSpellList().hasSpellRequisite(parentChar, spell);
            }
            if(!hasPrereq)
            {
                parentOrder.addError(parentChar + " does not have the " + "prerequisite for " + spell + ".");
            } else
            {
                parentOrder.addHelp(parentChar + " has the prerequisite for " + spell + ".");
                if(Main.main.getSpellList().isSpellInLostList(spell.getSpellNumber()))
                {
                    parentOrder.addWarning(spell + " is part of a lost list.");
                }
                parentChar.addSpell(new Integer(spell.getSpellNumber()), spell.getName());
            }
        }
        return null;
    }

    private String processSpell()
    {
        String result = parameterCount(1, "SPELL");
        if(result != null)
        {
            return result;
        }
        int spellParam = convertParameter(0);
        int spellNumber = parentOrder.getParameterNumber(spellParam);
        if(phase == 1)
        {
            if(spellNumber == -1)
            {
                parentOrder.addError("Could not determine spell number.");
                return null;
            }
            Ruleset spellSet = Main.main.getRuleSet().getRulesForOrder(spellNumber, true);
            if(spellSet.size() > 0)
            {
                spellRules = spellSet;
                spellRules.processRules(parentOrder, phase);
            }
        } else
        if(phase == 2)
        {
            Spell spell = Main.main.getSpellList().findSpell(spellNumber);
            if(spell == null)
            {
                parentOrder.addError("Spell " + spellNumber + " does not exist.");
                return null;
            }
            String spellInfo = parentChar.isSpellKnown(spellNumber);
            if(spellInfo == null)
            {
                parentOrder.addError(parentChar + " does not know " + spell + ".");
            } else
            {
                parentOrder.addHelp("Attempting to cast " + spellInfo + ".");
            }
            if(spell.getOrder() != parentOrder.getOrder())
            {
                parentOrder.addError(spell + " requires order " + spell.getOrder() + " for casting.");
            }
            if(!Main.main.getRuleSet().doesSpellHaveRules(spell.getSpellNumber()))
            {
                parentOrder.addWarning("No rules exist for " + spell + ".");
            }
        }
        return null;
    }

    private String processSpellList()
    {
        String result = parameterCount(1, "SPELLLIST");
        if(result != null)
        {
            return result;
        }
        int spellParam = convertParameter(0);
        if(phase == 2)
        {
            int numSpells = parentOrder.getNumberOfParameters() + 1;
            for(int i = spellParam; i < numSpells; i++)
            {
                int spellNumber = parentOrder.getParameterNumber(i);
                if(spellNumber == -1)
                {
                    parentOrder.addError("Could not determine spell number (param " + i + ").");
                    return null;
                }
                Spell spell = Main.main.getSpellList().findSpell(spellNumber);
                if(spell == null)
                {
                    parentOrder.addError("Spell " + spellNumber + " does not exist.");
                    continue;
                }
                if(parentChar.isSpellKnown(spellNumber) == null)
                {
                    parentOrder.addError(parentChar + " does not know " + spell + ".");
                }
            }

            int total = numSpells - spellParam;
            if(total > 6)
            {
                parentOrder.addError("Only 6 spells can be specified.");
            }
        }
        return null;
    }

    private String processStatus()
    {
        String result = parameterCount(1, "STATUS");
        if(result != null)
        {
            return result;
        }
        int charParam = convertParameter(0);
        if(phase == 2)
        {
            Character character = parentOrder.extractCharacter(charParam, false);
            if(character == null)
            {
                return null;
            }
            if(waitForProcessState(3))
            {
                return "state";
            }
            if(character.isArmyCO(parentOrder.getOrder()) && !parentChar.isCommanderInArmy(parentOrder.getOrder()))
            {
                parentOrder.addError(parentChar + " needs to be in an army " + "since " + character + " is an army/navy commander.");
            }
        }
        return null;
    }

    private String processThreatenPopCenter()
    {
        boolean bDisabled = true;
        if(phase == 2 && !bDisabled)
        {
            if(waitForProcessState(4) || waitForProcessState(0) || waitForProcessState(2))
            {
                return "state";
            }
            int location = parentChar.getLocation(parentOrder.getOrder());
            int troops = Main.main.getNation().totalTroopsAtLocation(location, parentOrder.getOrder());
            PopCenter pc = Main.main.getNation().findPopulationCenter(location);
            if(pc != null && troops >= 100 && pc.getSize() > 1)
            {
                int chance = parentChar.getTotalCommandRank();
                int troopsRequired = pc.threatenRequirement();
                if(troopsRequired > 0)
                {
                    int percentTroops = (int)(((double)troops / (double)troopsRequired) * 100D);
                    chance += percentTroops;
                }
                boolean haveLoyalty = false;
                if(pc.getLoyalty() > 0)
                {
                    haveLoyalty = true;
                    chance -= pc.getLoyalty();
                }
                int fortLevel = pc.getFortification();
                if(fortLevel > 0)
                {
                    chance -= 20 * fortLevel;
                }
                String msg = "Rough estimate chance to threaten is " + chance + "% - 25 if capital";
                if(!haveLoyalty)
                {
                    msg = msg + " - PC loyalty";
                }
                msg = msg + ".";
                parentOrder.addInfo(msg);
            }
        }
        return null;
    }

    private String processTroopAmount(int type, int retire, int amountParam[])
    {
        if(amountParam == null)
        {
            String result = parameterCount(8, "TROOPAMT");
            if(result != null)
            {
                return result;
            }
            type = convertParameter(0);
            retire = convertParameter(1);
            amountParam = new int[6];
            for(int i = 0; i < 6; i++)
            {
                amountParam[i] = convertParameter(i + 2);
            }

        }
        if(phase == 1)
        {
            if(retire == 1)
            {
                clearProcessState(0);
            }
        } else
        if(phase == 2)
        {
            if(retire == 1)
            {
                if(waitForPartialState(0))
                {
                    return "state";
                }
                setProcessState(0);
            } else
            if(waitForProcessState(0))
            {
                return "state";
            }
            Army army = null;
            if(type == 0)
            {
                army = parentChar.getArmy(parentOrder.getOrder());
            } else
            if(type == 1)
            {
                if(parentChar.isCommanderInArmy(parentOrder.getOrder()))
                {
                    army = parentChar.getArmy(parentOrder.getOrder());
                }
            } else
            {
                return "Invalid type (" + type + ") for TROOPAMT!";
            }
            if(army == null)
            {
                return null;
            }
            int amount[] = new int[6];
            for(int i = 0; i < 6; i++)
            {
                amount[i] = parentOrder.getParameterNumber(amountParam[i]);
                if(amount[i] == -1 && phase == 1)
                {
                    parentOrder.addError("Could not get " + Army.getTroopName(i) + " troop amount.");
                    return null;
                }
            }

            String desc = "";
            int troops[] = army.getTroopContent(parentOrder.getOrder());
            int totalTroops = 0;
            for(int i = 0; i < 6; i++)
            {
                if(amount[i] <= 0)
                {
                    continue;
                }
                if(troops[i] < amount[i])
                {
                    parentOrder.addError("There aren't " + amount[i] + " " + Army.getTroopName(i) + " in the army.");
                    amount[i] = troops[i];
                }
                desc = desc + amount[i] + " " + Army.getTroopName(i) + ", ";
                totalTroops += amount[i];
            }

            if(totalTroops > 0)
            {
                desc = desc.substring(0, desc.length() - 2);
                desc = desc + " affected.";
                parentOrder.addHelp(desc);
            }
            if(retire == 1)
            {
                for(int i = 0; i < 6; i++)
                {
                    amount[i] *= -1;
                }

                army.setTroopContent(amount, parentOrder.getOrder());
            } else
            if(retire != 0)
            {
                return "Invalid retire (" + retire + ") value for TROOPAMT!";
            }
        }
        return null;
    }

    private String processTroopRecruit()
    {
        String result = parameterCount(4, "TROOPRECRUIT");
        if(result != null)
        {
            return result;
        }
        int amountParam = convertParameter(0);
        int typeParam = convertParameter(1);
        int type = convertParameter(2);
        int foodParam = convertParameter(3);
        if(typeParam > 0)
        {
            String typeString = parentOrder.getParameterString(typeParam);
            type = Army.findTroopType(typeString);
        }
        if((type < 0 || type > 5) && typeParam > 0)
        {
            return "Invalid type " + type + " in TROOPAMT, order " + order + "!";
        }
        if(phase == 1)
        {
            clearProcessState(0);
            clearProcessState(3);
        } else
        if(phase == 2)
        {
            int amount = parentOrder.getParameterNumber(amountParam);
            if(amount == -1)
            {
                parentOrder.addError("Could not determine amount of troops.");
            }
            if(type < 0 || type > 5)
            {
                parentOrder.addError("Invalid troop type was specified.");
            }
            PopCenter pc = Main.main.getNation().findOwnedPopulationCenter(parentChar.getLocation(parentOrder.getOrder()));
            if(amount == -1 || type < 0 || type > 5 || pc == null)
            {
                setProcessState(0);
                setProcessState(3);
                return null;
            }
            if(type == 0 || type == 1)
            {
                if(additionalInfo.size() == 0)
                {
                    String msg = "TROOP:Does " + pc + " have " + amount + " mounts and " + amount * 2 + " leather?";
                    addAdditionalInfo(msg, false);
                    return "state";
                }
                if(!getAdditionalInfo())
                {
                    parentOrder.addError(pc + " doesn't have enough mounts " + "and/or leather for cavalry.");
                }
            }
            if(waitForPartialState(0) || waitForPartialState(3))
            {
                return "state";
            }
            setProcessState(0);
            setProcessState(3);
            int amounts[] = new int[6];
            amounts[type] = amount;
            Army army = null;
            if(parentChar.isCommanderInArmy(parentOrder.getOrder()))
            {
                army = parentChar.getArmy(parentOrder.getOrder());
                army.setTroopContent(amounts, parentOrder.getOrder());
            } else
            {
                army = new Army(parentChar.getLocation(parentOrder.getOrder()));
                army.setNewArmy(parentChar, amounts, parentOrder.getOrder());
                if(foodParam > 0)
                {
                    int food = parentOrder.getParameterNumber(foodParam);
                    if(food != -1)
                    {
                        army.setFoodRequired(1);
                        army.setHasEnoughFood(food > 0);
                    }
                }
                Main.main.getNation().addArmy(army);
            }
            int left = pc.reduceTroopLimit(amount);
            if(left > 0)
            {
                parentOrder.addWarning(pc + " still has " + left + " troops available.");
            } else
            if(left < 0)
            {
                parentOrder.addError(pc + " is out of troops!  Maximum " + pc.getMaximumTroops() + " available.");
            }
        }
        return null;
    }

    private String processTroopType()
    {
        String result = parameterCount(2, "TROOPTYPE");
        if(result != null)
        {
            return result;
        }
        int charParam = convertParameter(0);
        int typeParam = convertParameter(1);
        if(phase == 2)
        {
            if(waitForProcessState(3) || waitForProcessState(0))
            {
                return "state";
            }
            Character character = parentOrder.extractCharacter(charParam, true);
            if(character == null)
            {
                return null;
            }
            int type = -1;
            String typeString = parentOrder.getParameterString(typeParam);
            if(typeString != null)
            {
                type = Army.findTroopType(typeString);
            }
            if(type == -1)
            {
                parentOrder.addError("Could not determine troop type (" + typeString + ").");
                return null;
            }
            Army army = null;
            if(character.isCommanderInArmy(parentOrder.getOrder()))
            {
                army = character.getArmy(parentOrder.getOrder());
            }
            if(army == null)
            {
                parentOrder.addError(character + " is not a commander in an army.");
                return null;
            }
            int troops[] = army.getTroopContent(parentOrder.getOrder());
            if(troops[type] == 0)
            {
                parentOrder.addError("There are no " + Army.getTroopName(type) + " in the army.");
            }
        }
        return null;
    }

    private String processTroopTransfer()
    {
        String result = parameterCount(8, "TROOPXFER");
        if(result != null)
        {
            return result;
        }
        int type = convertParameter(0);
        int charParam = convertParameter(1);
        int amountParam[] = new int[6];
        for(int i = 0; i < 6; i++)
        {
            amountParam[i] = convertParameter(i + 2);
        }

        if(phase == 1)
        {
            clearProcessState(0);
            clearProcessState(3);
        } else
        if(phase == 2)
        {
            processTroopAmount(type, 1, amountParam);
            if(waitForPartialState(3))
            {
                return "state";
            }
            setProcessState(3);
            Army srcArmy = null;
            if(type == 0)
            {
                srcArmy = parentChar.getArmy(parentOrder.getOrder());
            } else
            if(parentChar.isCommanderInArmy(parentOrder.getOrder()))
            {
                srcArmy = parentChar.getArmy(parentOrder.getOrder());
            }
            if(srcArmy == null)
            {
                return null;
            }
            Character character = parentOrder.extractCharacter(charParam, false);
            if(character == null)
            {
                return null;
            }
            Army destArmy = null;
            if(character.isArmyCO(parentOrder.getOrder() + 1))
            {
                destArmy = character.getArmy(parentOrder.getOrder() + 1);
            }
            int currentTroops[] = srcArmy.getTroopContent(parentOrder.getOrder());
            int amount[] = new int[6];
            for(int i = 0; i < 6; i++)
            {
                amount[i] = parentOrder.getParameterNumber(amountParam[i]);
                if(amount[i] == -1)
                {
                    return null;
                }
                if(amount[i] > currentTroops[i])
                {
                    amount[i] = currentTroops[i];
                }
            }

            if(destArmy == null)
            {
                destArmy = new Army(character.getLocation(parentOrder.getOrder()));
                destArmy.setNewArmy(character, amount, parentOrder.getOrder());
                Main.main.getNation().addArmy(destArmy);
            } else
            {
                destArmy.setTroopContent(amount, parentOrder.getOrder());
            }
            int totalSplit = 0;
            for(int i = 0; i < 6; i++)
            {
                if(amount[i] > 0)
                {
                    totalSplit += amount[i];
                }
            }

            int srcTroops = srcArmy.getTotalTroops(parentOrder.getOrder()) - totalSplit;
            if(srcTroops < 100)
            {
                if(srcTroops > 0)
                {
                    parentOrder.addError(parentChar + " will have less than 100 " + "troops left after the split.");
                } else
                {
                    parentOrder.addWarning(parentChar + " is disbanding the army." + " All baggage will be lost.");
                }
                srcArmy.disbandArmy(((Army) (null)), parentOrder.getOrder() + 1, true);
            }
            if(totalSplit < 100)
            {
                parentOrder.addError("The new army won't have 100 troops after the split.");
            }
        }
        return null;
    }

}
