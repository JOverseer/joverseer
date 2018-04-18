// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Rule.java

package com.middleearthgames.orderchecker;

import java.util.Vector;

import javax.swing.JCheckBox;

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
    @SuppressWarnings("unused")
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
        this.name = null;
        this.parameters = new Vector();
        this.spell = false;
        this.spellRules = null;
        this.additionalInfo = null;
        this.stateProcessed = null;
        this.done = false;
        this.debugList = new Vector();
        this.parentOrder = null;
        this.parentChar = null;
        this.phase = -1;
        this.order = order;
    }
    private void resetState()
    {
        if (this.stateProcessed == null) {
        	this.stateProcessed = new boolean[7];
        }
        for(int i = 0; i < STATE_NUM; i++)
        {
            this.stateProcessed[i] = true;
        }
        if (this.additionalInfo == null) {
        	this.additionalInfo = new Vector();
        }
        this.additionalInfo.clear();
    	
    }

    @Override
	protected Object clone()
        throws CloneNotSupportedException
    {
        return super.clone();
    }

    void printStateInformation()
    {
        for(int i = 0; i < this.debugList.size(); i++)
        {
            String entry = (String)this.debugList.get(i);
            System.out.println((i + 1) + ": " + entry);
        }

        this.debugList.removeAllElements();
        if(this.spellRules != null)
        {
            this.spellRules.printStateInformation();
        }
    }

    void getInfoRequests(Vector list)
    {
        list.addAll(((java.util.Collection) (this.additionalInfo)));
        if(this.spellRules != null)
        {
            this.spellRules.getInfoRequests(list);
        }
    }

    private void addAdditionalInfo(String msg, boolean state)
    {
        JCheckBox box = new JCheckBox(msg, state);
        this.additionalInfo.add(((Object) (box)));
    }

    private boolean getAdditionalInfo()
    {
        if(this.additionalInfo.size() > 0)
        {
            JCheckBox box = (JCheckBox)this.additionalInfo.remove(0);
            return box.isSelected();
        } else
        {
            return false;
        }
    }

    boolean getDone()
    {
        if(this.spellRules != null && !this.spellRules.getDone())
        {
            return false;
        } else
        {
            return this.done;
        }
    }

    boolean getStateDone(int state)
    {
        if(this.spellRules != null && !this.spellRules.getStateDone(state))
        {
            return false;
        } else
        {
            return this.stateProcessed[state];
        }
    }

    private void clearProcessState(int state)
    {
        if(this.stateProcessed[state])
        {
            this.stateProcessed[state] = false;
        }
    }

    private void setProcessState(int state)
    {
        if(!this.stateProcessed[state])
        {
            this.stateProcessed[state] = true;
        }
    }

    private boolean waitForProcessState(int state)
    {
        return !Main.main.getNation().isStateDone(state);
    }

    private boolean waitForPartialState(int state)
    {
        return !Main.main.getNation().isPartialStateDone(state, this.parentOrder.getOrder());
    }

    String processRule(Order order1, int phase1)
    {
        this.parentOrder = order1;
        this.parentChar = order1.getParent();
        this.phase = phase1;
        if(phase1 == 1)
        {
            resetState();
        }
        String result;
        if(this.spellRules != null && !this.spellRules.getDone())
        {
            result = this.spellRules.processRules(this.parentOrder, phase1);
            if(result != null && !result.equals(STATE_REQ))
            {
                return result;
            }
        }
        if(this.done)
        {
            return null;
        }
        if(this.name.equalsIgnoreCase("ARMYMOVE"))
        {
            result = processArmyMove();
        } else
        if(this.name.equalsIgnoreCase("ARMYORPC"))
        {
            result = processArmyOrPc();
        } else
        if(this.name.equalsIgnoreCase("ARMYSCOUT"))
        {
            result = processScoutArmy();
        } else
        if(this.name.equalsIgnoreCase("ARTIFACT"))
        {
            result = processArtifact(-1, true, true);
        } else
        if(this.name.equalsIgnoreCase("ARTYFIND"))
        {
            result = processFindArtifact();
        } else
        if(this.name.equalsIgnoreCase("ARTYLIST"))
        {
            result = processArtifactList();
        } else
        if(this.name.equalsIgnoreCase("ARTYNAME"))
        {
            result = processArtifactName();
        } else
        if(this.name.equalsIgnoreCase("CHARACTER"))
        {
            result = processCharacter(-1, -1, -1);
        } else
        if(this.name.equalsIgnoreCase("CHARMOVE"))
        {
            result = processCharacterMove();
        } else
        if(this.name.equalsIgnoreCase("COMMANDER"))
        {
            result = processCommander();
        } else
        if(this.name.equalsIgnoreCase("COMMANDERNOT"))
        {
            result = processNotCommander();
        } else
        if(this.name.equalsIgnoreCase("COMMANDXFER"))
        {
            result = processCommandTransfer();
        } else
        if(this.name.equalsIgnoreCase("DOCK"))
        {
            result = processDock();
        } else
        if(this.name.equalsIgnoreCase("ENEMYARMY"))
        {
            result = processEnemyArmy();
        } else
        if(this.name.equalsIgnoreCase("FEATURE"))
        {
            result = processFeature();
        } else
        if(this.name.equalsIgnoreCase("FORT"))
        {
            result = processFortification();
        } else
        if(this.name.equalsIgnoreCase("HOSTAGE"))
        {
            result = processHostage();
        } else
        if(this.name.equalsIgnoreCase("IMPROVEPC"))
        {
            result = processImprovePopCenter();
        } else
        if(this.name.equalsIgnoreCase("LAND"))
        {
            result = processLand();
        } else
        if(this.name.equalsIgnoreCase("MAGEAMT"))
        {
            result = processMageAmount();
        } else
        if(this.name.equalsIgnoreCase("NATION"))
        {
            result = processNation();
        } else
        if(this.name.equalsIgnoreCase("NAVYMOVE"))
        {
            result = processNavyMove();
        } else
        if(this.name.equalsIgnoreCase("NEWCHAR"))
        {
            result = processNewCharacter();
        } else
        if(this.name.equalsIgnoreCase("ONERING"))
        {
            result = processOneRing();
        } else
        if(this.name.equalsIgnoreCase("PC"))
        {
            result = processPC();
        } else
        if(this.name.equalsIgnoreCase("PCSIZE"))
        {
            result = processPCSize();
        } else
        if(this.name.equalsIgnoreCase("PCSTATE"))
        {
            result = processPCState();
        } else
        if(this.name.equalsIgnoreCase("PRODUCTINFO"))
        {
            result = processProductInfo();
        } else
        if(this.name.equalsIgnoreCase("RANK"))
        {
            result = processRank(-1, -1, -1, -1);
        } else
        if(this.name.equalsIgnoreCase("SETCOMMAND"))
        {
            result = processSetCommander();
        } else
        if(this.name.equalsIgnoreCase("SHIPS"))
        {
            result = processShips();
        } else
        if(this.name.equalsIgnoreCase("SIEGENOT"))
        {
            result = processNotSieged();
        } else
        if(this.name.equalsIgnoreCase("SPELLREQ"))
        {
            result = processSpellReq();
        } else
        if(this.name.equalsIgnoreCase("SPELL"))
        {
            result = processSpell();
        } else
        if(this.name.equalsIgnoreCase("SPELLLIST"))
        {
            result = processSpellList();
        } else
        if(this.name.equalsIgnoreCase("STATUS"))
        {
            result = processStatus();
        } else
        if(this.name.equalsIgnoreCase("THREATENPC"))
        {
            result = processThreatenPopCenter();
        } else
        if(this.name.equalsIgnoreCase("TROOPAMT"))
        {
            result = processTroopAmount(-1, -1, ((int []) (null)));
        } else
        if(this.name.equalsIgnoreCase("TROOPRECRUIT"))
        {
            result = processTroopRecruit();
        } else
        if(this.name.equalsIgnoreCase("TROOPTYPE"))
        {
            result = processTroopType();
        } else
        if(this.name.equalsIgnoreCase("TROOPXFER"))
        {
            result = processTroopTransfer();
        } else
        if(this.name.equalsIgnoreCase("NONE"))
        {
            this.done = true;
            return null;
        } else
        {
            throw new RuntimeException("Unknown rule: " + this.name);
        }
        if(result == null && phase1 > 1)
        {
            this.done = true;
        }
        return result;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setSpellType()
    {
        this.spell = true;
    }

    public void addParameter(Integer param)
    {
        this.parameters.add(((Object) (param)));
    }

    public int getOrder()
    {
        return this.order;
    }

    String getName()
    {
        return this.name;
    }

    boolean isSpellType()
    {
        return this.spell;
    }

    boolean isRuleComplete()
    {
        return this.order != -1 && this.name != null;
    }

    @Override
	public String toString()
    {
        return this.name;
    }

    private String parameterCount(int expected, String desc)
    {
        if(this.parameters.size() != expected)
        {
            return "Invalid parameter count for " + desc + ": " + this.order;
        } else
        {
            return null;
        }
    }

    private int convertParameter(int index)
    {
        try {
            Integer intValue = (Integer)this.parameters.get(index);
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
        boolean owned = pc.getNation() == this.parentChar.getNation();
        boolean enemy = Nation.isEnemy(this.parentChar.getNation(), pc.getNation());
        boolean neutral = Nation.isNeutral(pc.getNation());
        if(owned || !enemy && !neutral)
        {
            if(pc.getEnemyArmyPresent())
            {
                this.parentOrder.addWarning("Army/navy movement could be blocked if " + pc + " is captured.");
            }
            return false;
        }
        if(pc.getPossibleCapture() < this.parentOrder.getOrder() && pc.getCapturingNation() == this.parentChar.getNation())
        {
            this.parentOrder.addWarning("Army/navy movement could be blocked if " + pc + " is not captured.");
            return false;
        }
        if(pc.getPossibleInfluence() < this.parentOrder.getOrder())
        {
            this.parentOrder.addWarning("Army/navy movement could be blocked if " + pc + " is not captured.");
            return false;
        } else
        {
            this.parentOrder.addWarning(pc + " may block army/navy movement.");
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
        if(this.phase == 1)
        {
            clearProcessState(STATE_ARMY_LOCATION);
            clearProcessState(STATE_CHARACTER_LOCATION);
        } else
        if(this.phase == 2)
        {
            if(waitForProcessState(STATE_ARMY))
            {
                return STATE_REQ;
            }
            Army army = this.parentChar.getArmy(this.parentOrder.getOrder());
            if(army == null)
            {
                setProcessState(STATE_ARMY_LOCATION);
                setProcessState(STATE_CHARACTER_LOCATION);
                return null;
            }
            if(army.getFoodRequirement() == 0)
            {
                if(this.additionalInfo.size() == 0)
                {
                    String msg = "FOOD:Will " + this.parentChar + "'s army have 1 food?";
                    addAdditionalInfo(msg, false);
                    return STATE_REQ;
                }
                army.setFoodRequired(1);
                army.setHasEnoughFood(getAdditionalInfo());
            } else {
            	if (army.getFoodGainedBeforeMove() > 0) {
            		army.setHasEnoughFood(true);
            	}
            }
            boolean food = army.getHasEnoughFood();
            if(waitForPartialState(STATE_ARMY_LOCATION) || waitForPartialState(STATE_CHARACTER_LOCATION))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_ARMY_LOCATION);
            setProcessState(STATE_CHARACTER_LOCATION);
            String evasiveString = this.parentOrder.getParameterString(1);
            boolean evasive = !evasiveString.equalsIgnoreCase("no");
            boolean cavalry = army.isAllCavalry(this.parentOrder.getOrder());
            int maxMovement = ((int) (forced ? 14 : 12));
            int totalMovement = maxMovement;
            int lastHex = this.parentChar.getLocation(this.parentOrder.getOrder());
            int currentHex = lastHex;
            for(int param = 1; param < this.parentOrder.getNumberOfParameters() && currentHex > 0 && totalMovement >= 0; param++)
            {
                String dirString = this.parentOrder.getParameterString(param + 1);
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
                if(param + 1 >= this.parentOrder.getNumberOfParameters())
                {
                    continue;
                }
                willPCBlockMovement(currentHex);
                if(Main.main.getNation().isEnemyArmyPresent(currentHex))
                {
                    this.parentOrder.addWarning("An enemy army is present at " + currentHex + " and may block movement.");
                }
            }

            String msg = cavalry ? "Cavalry, " : "Infantry, ";
            msg = msg + (food ? "Food, " : "No Food, ");
            msg = msg + (evasive ? "Evasive, " : "Normal, ");
            if(currentHex == -1)
            {
                this.parentOrder.addError("The army tried to move to an invalid hex!");
                if(totalMovement >= 0)
                {
                    int movementSpent = maxMovement - totalMovement;
                    msg = msg + maxMovement + " points, " + movementSpent + " spent, ";
                }
                msg = msg + "stopped at " + Main.main.locationStr(lastHex) + ".";
                this.parentOrder.addInfo(msg);
                if(this.parentChar.isArmyCO(this.parentOrder.getOrder()))
                {
                    army.setNewLocation(lastHex, this.parentOrder.getOrder());
                }
            } else
            {
                if(totalMovement < 0)
                {
                    this.parentOrder.addError("Maximum movement was exceeded!");
                }
                int movementSpent = maxMovement - totalMovement;
                msg = msg + maxMovement + " points, " + movementSpent + " spent, stopped at " + Main.main.locationStr(currentHex) + ".";
                this.parentOrder.addInfo(msg);
                if(this.parentChar.isArmyCO(this.parentOrder.getOrder()))
                {
                    army.setNewLocation(currentHex, this.parentOrder.getOrder());
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
        if(this.phase == 2)
        {
            if(waitForProcessState(STATE_CHARACTER_ARMY))
            {
                return STATE_REQ;
            }
            PopCenter pc = Main.main.getNation().findOwnedPopulationCenter(this.parentChar.getLocation(this.parentOrder.getOrder()));
            boolean armyCheck = false;
            String qualifier = "";
            switch(type)
            {
            case 0: // '\0'
                armyCheck = this.parentChar.isArmyCO(this.parentOrder.getOrder());
                qualifier = "commanding";
                break;

            case 1: // '\001'
                Army army = this.parentChar.getArmy(this.parentOrder.getOrder());
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
                this.parentOrder.addError(this.parentChar + " needs to be " + qualifier + " an army or at an owned PC.");
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
        if(this.phase == 2)
        {
            Character character = this.parentOrder.extractCharacter(charParam, true);
            if(character == null)
            {
                return null;
            }
            if(waitForProcessState(STATE_CHARACTER_ARMY) || waitForProcessState(STATE_CHARACTER_LOCATION))
            {
                return STATE_REQ;
            }
            if(!character.isArmyCO(this.parentOrder.getOrder()))
            {
                this.parentOrder.addError(character + " is not commander of an army.");
            } else
            {
                int distance = Hex.calcHexDistance(this.parentChar.getLocation(this.parentOrder.getOrder()), character.getLocation(this.parentOrder.getOrder()));
                if(distance > maxDistance)
                {
                    this.parentOrder.addError(character + "'s army is too far away (" + distance + " hexes, maximum is " + maxDistance + ").");
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
        if(this.phase == 2)
        {
            if(wait && waitForProcessState(STATE_ARTIFACT))
            {
                return STATE_REQ;
            }
            int artifactNum = this.parentOrder.getParameterNumber(artifactParam);
            if(artifactNum == -1)
            {
                return "Couldn't get artifact (" + artifactParam + ") for artifact check!";
            }
            String artifact = this.parentChar.getArtifactName(artifactNum);
            boolean hasArtifact = this.parentChar.hasArtifact(artifactNum, this.parentOrder.getOrder());
            if(!hasArtifact && convertParameter(0) != 3)
            {
                this.parentOrder.addError(this.parentChar + " doesn't have " + (artifact != null ? artifact : "artifact #" + artifactNum) + ".");
            } else
            if(using && artifact != null)
            {
                this.parentOrder.addHelp("Attempting to use " + artifact + ".");
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
        int artifact = this.parentOrder.getParameterNumber(artifactParam);
        if(artifact == -1)
        {
            return "Couldn't get artifact (" + artifactParam + ") for find artifact check!";
        }
        if(this.phase == 1)
        {
            clearProcessState(STATE_ARTIFACT);
        } else
        if(this.phase == 2)
        {
            if(waitForPartialState(STATE_ARTIFACT))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_ARTIFACT);
            if(this.parentChar.getTotalMageRank() == 0)
            {
                this.parentOrder.addWarning(this.parentChar + " doesn't have Mage rank.");
            }
            if(artifact == 0)
            {
                this.parentOrder.addWarning("Chance of success is much higher if artifact id # is known and provided.");
            } else
            {
                String artifactName = this.parentChar.getArtifactName(artifact);
                this.parentOrder.addHelp("Trying to find " + (artifactName != null ? artifactName : "artifact #" + artifact) + ".");
            }
            int artiList[] = new int[1];
            artiList[0] = artifact;
            this.parentChar.setNewArtifacts(artiList, this.parentOrder.getOrder());
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
        if(this.phase == 1)
        {
            clearProcessState(STATE_ARTIFACT);
        } else
        if(this.phase == 2)
        {
            if(waitForPartialState(STATE_ARTIFACT))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_ARTIFACT);
            int numArtifacts = this.parentOrder.getNumberOfParameters() + 1;
            int total = numArtifacts - artifactParam;
            if(total < 1)
            {
                this.parentOrder.addError("No artifacts have been specified.");
                return null;
            }
            if(total > 6)
            {
                this.parentOrder.addError("Only 6 artifacts can be specified.");
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
                int artifact = this.parentOrder.getParameterNumber(i);
                addList[i - artifactParam] = artifact;
                delList[i - artifactParam] = -1 * artifact;
            }

            StringBuffer info = new StringBuffer();
            info.append("Affects ");
            for(int i = 0; i < addList.length; i++)
            {
                String artifactName = this.parentChar.getArtifactName(addList[i]);
                if(artifactName != null)
                {
                    info.append(artifactName + ", ");
                }
            }

            info.delete(info.length() - 2, info.length());
            this.parentOrder.addHelp(info.toString());
            switch(type)
            {
            case 1: // '\001'
                Character character = this.parentOrder.extractCharacter(type, false);
                if(character == null)
                {
                    return null;
                }
                character.setNewArtifacts(addList, this.parentOrder.getOrder());
                this.parentChar.setNewArtifacts(delList, this.parentOrder.getOrder());
                break;

            case 2: // '\002'
                this.parentChar.setNewArtifacts(delList, this.parentOrder.getOrder());
                break;

            case 3: // '\003'
                this.parentChar.setNewArtifacts(addList, this.parentOrder.getOrder());
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
        if(this.phase == 2)
        {
            int artifactNum = this.parentOrder.getParameterNumber(artifactParam);
            if(artifactNum == -1)
            {
                return "Couldn't get artifact (" + artifactParam + ") for ARTYNAME!";
            }
            String artifact = this.parentChar.getArtifactName(artifactNum);
            if(artifact != null)
            {
                this.parentOrder.addHelp("Affects " + artifact + ".");
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
        if(this.phase == 2)
        {
            Character character = this.parentOrder.extractCharacter(charParam, true);
            int location = this.parentOrder.extractLocation(charLoc, true);
            if(character == null || location == -1)
            {
                return null;
            }
            if(waitForProcessState(STATE_CHARACTER_LOCATION))
            {
                return STATE_REQ;
            }
            if(character.getLocation(this.parentOrder.getOrder()) != location && state != 4)
            {
                if(character.getLocation(this.parentOrder.getOrder()) != 0)
                {
                    this.parentOrder.addError(character + " is not at " + Main.main.locationStr(location) + ".");
                }
            } else
            if(state == 4)
            {
                int distance = Hex.calcHexDistance(character.getLocation(this.parentOrder.getOrder()), location);
                if(distance > 1)
                {
                    this.parentOrder.addError(character + " is not at or adjacent to " + Main.main.locationStr(location) + ".");
                }
            }
            String sourceNation = Main.main.getNation().getNationName(Main.main.getNation().getNation());
            String targetNation = Main.main.getNation().getNationName(character.getNation());
            switch(state)
            {
            case 0: // '\0'
                if(character.getNation() != Main.main.getNation().getNation())
                {
                    this.parentOrder.addError(character + " belongs to " + targetNation + ".");
                }
                break;

            case 1: // '\001'
                break;

            case 2: // '\002'
            case 4: // '\004'
                if(character.getNation() == Main.main.getNation().getNation())
                {
                    this.parentOrder.addError(character + " belongs to your nation.");
                }
                break;

            case 3: // '\003'
                Main.main.getNation();
                if(!Nation.isFriend(this.parentChar.getNation(), character.getNation()))
                {
                    this.parentOrder.addError(targetNation + " is not a friendly nation.");
                }
                break;

            case 5: // '\005'
                if(character.getNation() != Main.main.getNation().getNation())
                {
                    this.parentOrder.addWarning(sourceNation + " and " + targetNation + " must have friendly relations with each other.");
                }
                break;

            case 6: // '\006'
                if(character.getNation() != Main.main.getNation().getNation())
                {
                    this.parentOrder.addWarning("You must have friendly relations with " + targetNation + ".");
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
        if(this.phase == 1)
        {
            if(type < 0 || type > 5)
            {
                return "Invalid type (" + type + ") for CHARMOVE!";
            }
            if(type != 1)
            {
                clearProcessState(STATE_CHARACTER_LOCATION);
                clearProcessState(STATE_CHARACTER_ARMY);
            }
        } else
        if(this.phase == 2)
        {
            if(type != 1)
            {
                if(type == 2 && waitForProcessState(STATE_ARMY_LOCATION))
                {
                    return STATE_REQ;
                }
                if(waitForPartialState(STATE_CHARACTER_LOCATION) || waitForPartialState(STATE_CHARACTER_ARMY))
                {
                    return STATE_REQ;
                }
                setProcessState(STATE_CHARACTER_LOCATION);
                setProcessState(STATE_CHARACTER_ARMY);
            } else
            if(waitForProcessState(STATE_CHARACTER_LOCATION))
            {
                return STATE_REQ;
            }
            if(type != 1)
            {
                if(type != 2 && type != 5)
                {
                    this.parentChar.setArmy(((Army) (null)), this.parentOrder.getOrder());
                } else
                {
                    int charParam = 2;
                    if(type == 5)
                    {
                        charParam = 1;
                    }
                    Character character = this.parentOrder.extractCharacter(charParam, false);
                    if(character != null)
                    {
                        int location = this.parentOrder.extractLocation(locParam, false);
                        if(character.getLocation(this.parentOrder.getOrder()) == location)
                        {
                            Army army = character.getArmy(this.parentOrder.getOrder());
                            if(army != null)
                            {
                                this.parentChar.setArmy(army, this.parentOrder.getOrder());
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
                    location = this.parentOrder.getParameterNumber(locParam);
                    if(location == -1)
                    {
                        this.parentOrder.addError("Could not determine destination location.");
                        return null;
                    }
                }
                int distance = Hex.calcHexDistance(this.parentChar.getLocation(this.parentOrder.getOrder()), location);
                if(distance == 0 && type != 1)
                {
                    this.parentOrder.addWarning(this.parentChar + " is already at the destination location.");
                } else
                if(distance > max && max > 0)
                {
                    if(type != 1)
                    {
                        this.parentOrder.addError("Can not move " + distance + " hexes, maximum is " + max + ".");
                    } else
                    if(type == 1)
                    {
                        this.parentOrder.addError(distance + " hexes " + "is too far, maximum is " + max + ".");
                    }
                } else
                if(type != 1)
                {
                    this.parentChar.setNewLocation(location, this.parentOrder.getOrder());
                    String plural = distance != 1 ? " hexes " : " hex ";
                    this.parentOrder.addInfo("Moved " + distance + plural + "to " + Main.main.locationStr(location) + ".");
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
        if(this.phase == 2)
        {
            Character character = this.parentOrder.extractCharacter(charParam, true);
            if(character == null)
            {
                return null;
            }
            if(waitForProcessState(STATE_CHARACTER_LOCATION) || waitForProcessState(STATE_CHARACTER_ARMY))
            {
                return STATE_REQ;
            }
            if(character.getTotalCommandRank() > 0 && (CO == 4 || CO == 5))
            {
                if(this.additionalInfo.size() == 0)
                {
                    String msg = "COMPANYCO:Is " + character + " a company commander?";
                    addAdditionalInfo(msg, false);
                    return STATE_REQ;
                }
                character.setCompanyCO(getAdditionalInfo(), 0);
            }
            boolean companyCO = character.isCompanyCO(this.parentOrder.getOrder());
            processCharacter(charParam, charLoc, status);
            boolean armyCO = character.isArmyCO(this.parentOrder.getOrder());
            boolean navyCO = false;
            boolean withArmy = false;
            Army army = character.getArmy(this.parentOrder.getOrder());
            if(army != null && armyCO && army.isNavy())
            {
                armyCO = false;
                navyCO = true;
            }
            if(!armyCO && !navyCO)
            {
                withArmy = character.isCommanderInArmy(this.parentOrder.getOrder());
            }
            switch(CO)
            {
            case 0: // '\0'
                if(!armyCO)
                {
                    this.parentOrder.addError(character + " is not an army CO.");
                }
                break;

            case 1: // '\001'
                if(navyCO)
                {
                    break;
                }
                Hex hex = null;
                int location = this.parentOrder.extractLocation(charLoc, false);
                if(location != -1)
                {
                    hex = Main.main.getMap().findHex(location);
                }
                if(hex != null && Main.main.getMap().adjacentToWater(hex))
                {
                    this.parentOrder.addWarning(character + " is an army CO doing navy movement.");
                } else
                {
                    this.parentOrder.addError(character + " is not a navy CO.");
                }
                break;

            case 2: // '\002'
                if(!armyCO && !navyCO)
                {
                    this.parentOrder.addError(character + " is not an army or navy CO.");
                }
                break;

            case 3: // '\003'
                if(!armyCO && !navyCO && !withArmy)
                {
                    this.parentOrder.addError(character + " is not with an army or navy.");
                }
                break;

            case 4: // '\004'
                if(!companyCO)
                {
                    this.parentOrder.addError(character + " is not a company CO.");
                }
                break;

            case 5: // '\005'
                if(!armyCO && !navyCO && !companyCO)
                {
                    this.parentOrder.addError(character + " is not an army, " + "navy, or company commander.");
                }
                break;

            case 6: // '\006'
                if(character.getArmy(this.parentOrder.getOrder()) == null)
                {
                    this.parentOrder.addError(character + " is not with an army.");
                }
                break;

            case 7: // '\007'
                if(armyCO)
                {
                    break;
                }
                boolean land = this.parentOrder.onLand(charLoc);
                if(navyCO && land)
                {
                    this.parentOrder.addWarning(character + " is a navy CO doing army movement.");
                } else
                {
                    this.parentOrder.addError(character + " is not an army CO.");
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
        if(this.phase == 2)
        {
            Character character = this.parentOrder.extractCharacter(charParam, false);
            if(character == null)
            {
                return null;
            }
            if(waitForProcessState(STATE_CHARACTER_LOCATION) || waitForProcessState(STATE_CHARACTER_ARMY))
            {
                return STATE_REQ;
            }
            if(character.getTotalCommandRank() > 0 && (CO == 4 || CO == 5))
            {
                if(this.additionalInfo.size() == 0)
                {
                    String msg = "COMPANYCO:Is " + character + " a company commander?";
                    addAdditionalInfo(msg, false);
                    return STATE_REQ;
                }
                character.setCompanyCO(getAdditionalInfo(), 0);
            }
            boolean companyCO = character.isCompanyCO(this.parentOrder.getOrder());
            processCharacter(charParam, charLoc, status);
            boolean armyCO = character.isArmyCO(this.parentOrder.getOrder());
            boolean navyCO = false;
            boolean withArmy = false;
            Army army = character.getArmy(this.parentOrder.getOrder());
            if(army != null && armyCO && army.isNavy())
            {
                armyCO = false;
                navyCO = true;
            }
            if(!armyCO && !navyCO)
            {
                withArmy = character.isCommanderInArmy(this.parentOrder.getOrder());
            }
            switch(CO)
            {
            case 0: // '\0'
                if(armyCO)
                {
                    this.parentOrder.addError(character + " is an army CO.");
                }
                break;

            case 1: // '\001'
                if(navyCO)
                {
                    this.parentOrder.addError(character + " is a navy CO.");
                }
                break;

            case 2: // '\002'
                if(armyCO || navyCO)
                {
                    this.parentOrder.addError(character + " is an army or navy CO.");
                }
                break;

            case 4: // '\004'
                if(companyCO)
                {
                    this.parentOrder.addError(character + " is a company CO.");
                }
                break;

            case 5: // '\005'
                if(armyCO || navyCO || companyCO)
                {
                    this.parentOrder.addError(character + " is an army, " + "navy, or company commander.");
                }
                break;

            case 6: // '\006'
                if(character.getArmy(this.parentOrder.getOrder()) == null || armyCO || navyCO)
                {
                    this.parentOrder.addError(character + " is not with an army or is the commander of it.");
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
        Character character = this.parentOrder.extractCharacter(charParam, false);
        if(character == null)
        {
            return null;
        }
        if(this.phase == 1)
        {
            clearProcessState(STATE_ARMY);
            clearProcessState(STATE_COMPANY);
            clearProcessState(STATE_CHARACTER_ARMY);
        } else
        if(this.phase == 2)
        {
            if(this.parentChar.getTotalCommandRank() > 0)
            {
                if(this.additionalInfo.size() == 0)
                {
                    String msg = "COMPANYCO:Is " + this.parentChar + " a company commander?";
                    addAdditionalInfo(msg, false);
                    return STATE_REQ;
                }
                this.parentChar.setCompanyCO(getAdditionalInfo(), 0);
            }
            boolean companyCO = this.parentChar.isCompanyCO(this.parentOrder.getOrder());
            if(waitForPartialState(STATE_ARMY) || waitForPartialState(STATE_COMPANY) || waitForPartialState(STATE_CHARACTER_ARMY))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_ARMY);
            setProcessState(STATE_COMPANY);
            setProcessState(STATE_CHARACTER_ARMY);
            if(companyCO)
            {
                this.parentChar.setCompanyCO(false, this.parentOrder.getOrder());
                character.setCompanyCO(true, this.parentOrder.getOrder());
                return null;
            }
            Army sourceArmy = this.parentChar.getArmy(this.parentOrder.getOrder());
            Army destinationArmy = null;
            if(character.isArmyCO(this.parentOrder.getOrder() + 1))
            {
                destinationArmy = character.getArmy(this.parentOrder.getOrder() + 1);
            }
            if(sourceArmy == null)
            {
                this.parentOrder.addError(this.parentChar + " is not an army/navy or company CO.");
                return null;
            }
            if(destinationArmy == null)
            {
                character.setArmy(sourceArmy, this.parentOrder.getOrder());
                sourceArmy.setArmyCommander(character, this.parentOrder.getOrder());
            } else
            {
                int currentSourceTroops[] = sourceArmy.getTroopContent(this.parentOrder.getOrder());
                destinationArmy.setTroopContent(currentSourceTroops, this.parentOrder.getOrder());
                sourceArmy.disbandArmy(destinationArmy, this.parentOrder.getOrder(), false);
            }
            String stays = this.parentOrder.getParameterString(charParam + 1);
            if(stays == null)
            {
                this.parentOrder.addError("Join army was not specified.");
            } else
            {
                char stayFlag = stays.charAt(0);
                switch(stayFlag)
                {
                case 78: // 'N'
                case 110: // 'n'
                    this.parentChar.setArmy(((Army) (null)), this.parentOrder.getOrder());
                    break;

                case 89: // 'Y'
                case 121: // 'y'
                    if(destinationArmy != null)
                    {
                        this.parentChar.setArmy(destinationArmy, this.parentOrder.getOrder());
                    }
                    break;

                default:
                    this.parentOrder.addError("Invalid join army parameter specified.");
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
        if(this.phase == 2)
        {
            if(waitForProcessState(STATE_CHARACTER_LOCATION))
            {
                return STATE_REQ;
            }
            PopCenter pc = Main.main.getNation().findPopulationCenter(this.parentChar.getLocation(this.parentOrder.getOrder()));
            String location = Main.main.locationStr(this.parentChar.getLocation(this.parentOrder.getOrder()));
            switch(type)
            {
            case 0: // '\0'
                if(pc == null || pc.getDock() != 1)
                {
                    this.parentOrder.addError(location + " does not have a harbor.");
                }
                break;

            case 1: // '\001'
                if(pc == null || pc.getDock() != 2)
                {
                    this.parentOrder.addError(location + " does not have a port.");
                }
                break;

            case 2: // '\002'
                Hex locHex = Main.main.getMap().findHex(this.parentChar.getLocation(this.parentOrder.getOrder()));
                if(locHex == null)
                {
                    return "Could not find location " + location + " for DOCK!";
                }
                if(locHex.getTerrain() != 2 && (pc == null || pc.getDock() == 0))
                {
                    this.parentOrder.addError(location + " does not have a shore, harbor, or dock.");
                }
                break;

            case 3: // '\003'
                if(pc != null && pc.getDock() != 0)
                {
                    this.parentOrder.addError(pc + " already has a harbor or port.");
                }
                break;

            case 4: // '\004'
                if(pc == null || pc.getDock() == 0)
                {
                    this.parentOrder.addError(location + " doesn't have a harbor or port.");
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
        if(this.phase == 2)
        {
            int location = this.parentOrder.extractLocation(locParam, true);
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
                        this.parentOrder.addWarning("Army belonging to a neutral nation is present at " + Main.main.locationStr(location) + ".");
                    } else
                    {
                        this.parentOrder.addError("An enemy army is not present at " + Main.main.locationStr(location) + ".");
                    }
                }
            } else
            if(type == 0)
            {
                if(Main.main.getNation().isEnemyArmyPresent(location))
                {
                    this.parentOrder.addError("An enemy army is present at " + Main.main.locationStr(location) + ".");
                } else
                if(Main.main.getNation().isNeutralArmyPresent(location))
                {
                    this.parentOrder.addWarning("Possible enemy force is present at " + Main.main.locationStr(location) + ".");
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
        if(this.phase == 2)
        {
            int location = this.parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            Hex hex = Main.main.getMap().findHex(location);
            if(hex == null)
            {
                this.parentOrder.addError("Invalid location of " + Main.main.locationStr(location) + " specified.");
                return null;
            }
            int direction = -1;
            if(dirParam == 0)
            {
                direction = 0;
            } else
            {
                String dirString = this.parentOrder.getParameterString(dirParam);
                if(dirString != null)
                {
                    direction = Hex.convertDirection(dirString);
                }
                if(direction == -1)
                {
                    this.parentOrder.addError("Could not determine direction.");
                    return null;
                }
            }
            switch(type)
            {
            case 9: // '\t'
                if(hex.hasFeature(3, direction) && !hex.hasFeature(5, direction))
                {
                    this.parentOrder.addError("A deep river is present while a road is not.");
                }
                break;

            case 10: // '\n'
                boolean adjacent = Main.main.getMap().adjacentToWater(hex) | hex.hasFeature(3);
                if(adjacent && logic == 1)
                {
                    this.parentOrder.addError("Location " + Main.main.locationStr(location) + " is adjacent to water.");
                    break;
                }
                if(!adjacent && logic == 0)
                {
                    this.parentOrder.addError("Location " + Main.main.locationStr(location) + " is not adjacent to water.");
                }
                break;

            case 1: // '\001'
            case 2: // '\002'
            case 3: // '\003'
            case 4: // '\004'
            case 5: // '\005'
                if(!hex.hasFeature(type, direction) && logic == 0)
                {
                    this.parentOrder.addError("Location " + Main.main.locationStr(location) + " does not have a " + Hex.featureDescription(type) + ".");
                    break;
                }
                if(hex.hasFeature(type, direction) && logic == 1)
                {
                    this.parentOrder.addError("Location " + Main.main.locationStr(location) + " has a " + Hex.featureDescription(type) + ".");
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
        if(this.phase == 2)
        {
            PopCenter pc = Main.main.getNation().findPopulationCenter(this.parentChar.getLocation(this.parentOrder.getOrder()));
            if(pc != null)
            {
                switch(type)
                {
                case 0: // '\0'
                    if(pc.getFortification() == 0)
                    {
                        this.parentOrder.addError(pc + " has no fortifications.");
                    }
                    break;

                case 1: // '\001'
                    if(pc.getFortification() == 5)
                    {
                        this.parentOrder.addError(pc + " already has a citadel.");
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
        if(this.phase == 2)
        {
            String charName = "";
            Character character = this.parentOrder.extractCharacter(charParam, false);
            if(character != null)
            {
                charName = character.getName();
            } else
            {
                charName = this.parentOrder.getParameterString(charParam);
                if(charName == null)
                {
                    if(this.phase == 1)
                    {
                        this.parentOrder.addError("Couldn't get character for parameter " + charParam + ".");
                    }
                    return null;
                }
            }
            boolean hostage = false;
            if(character == null)
            {
                if(this.additionalInfo.size() == 0)
                {
                    String msg = "HOSTAGE:Is \"" + charName + "\" a hostage?";
                    addAdditionalInfo(msg, false);
                    return STATE_REQ;
                }
                hostage = getAdditionalInfo();
            } else
            if(character.getLocation(this.parentOrder.getOrder()) == 0)
            {
                hostage = true;
            }
            switch(type)
            {
            case 0: // '\0'
                if(hostage)
                {
                    this.parentOrder.addError(charName + " is a hostage.");
                }
                break;

            case 1: // '\001'
                if(!hostage)
                {
                    this.parentOrder.addError(charName + " is not a hostage.");
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
        if(this.phase == 2)
        {
            PopCenter pc = Main.main.getNation().findOwnedPopulationCenter(this.parentChar.getLocation(this.parentOrder.getOrder()));
            if(pc != null)
            {
                int requirement = pc.improvementRequirement();
                int total = pc.getLoyalty() + this.parentChar.getTotalEmissaryRank();
                if(total < requirement - 5)
                {
                    this.parentOrder.addWarning("Loyalty plus emissary rank may be too low to improve " + pc + ".");
                }
                String msg = this.parentChar + " [E" + this.parentChar.getTotalEmissaryRank() + "], ";
                msg = msg + pc + " loyalty " + pc.getLoyalty() + ", ";
                msg = msg + "Total is " + total + ", ";
                msg = msg + "Recommended is " + requirement + ".";
                this.parentOrder.addInfo(msg);
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
        if(this.phase == 2)
        {
            if(locParam == 0 && waitForProcessState(STATE_CHARACTER_LOCATION))
            {
                return STATE_REQ;
            }
            int location = this.parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            if(!this.parentOrder.onLand(locParam))
            {
                this.parentOrder.addError("Location " + location + " is shallow or deep water.");
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
        if(this.phase == 1)
        {
            if(type == 2)
            {
                clearProcessState(STATE_ARMY);
            }
        } else
        if(this.phase == 2)
        {
            if(type == 2)
            {
                if(waitForPartialState(STATE_ARMY))
                {
                    return STATE_REQ;
                }
                setProcessState(STATE_ARMY);
            }
            int quantity = this.parentOrder.getParameterNumber(amtParam);
            if(quantity == -1)
            {
                this.parentOrder.addError("Could not determine the quantity.");
                return null;
            }
            int maxQuantity = this.parentChar.getNaturalMageRank() * max;
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
                this.parentOrder.addError("Maximum of " + maxQuantity + " " + commodity + " was exceeded.");
                quantity = maxQuantity;
            }
            if(type == 2)
            {
                Army army = this.parentChar.getArmy(this.parentOrder.getOrder());
                if(army != null)
                {
                    int amounts[] = new int[6];
                    amounts[5] = quantity;
                    army.setTroopContent(amounts, this.parentOrder.getOrder());
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
        if(this.phase == 2)
        {
            int nationNumber = this.parentOrder.getParameterNumber(param);
            if(nationNumber == -1)
            {
                this.parentOrder.addError("Could not determine nation for order.");
                return null;
            }
            String nationName = Main.main.getNation().getNationName(nationNumber);
            Main.main.getNation();
            if(Nation.isNeutral(nationNumber) && type != 2)
            {
                this.parentOrder.addWarning(nationName + " is a neutral nation.");
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
            this.parentOrder.addHelp(nationMsg);
            switch(type)
            {
            case 0: // '\0'
                Main.main.getNation();
                if(Nation.isEnemy(this.parentChar.getNation(), nationNumber))
                {
                    this.parentOrder.addError(nationName + " is an enemy nation.");
                }
                break;

            case 2: // '\002'
                break;

            case 1: // '\001'
                Main.main.getNation();
                if(Nation.isFriend(this.parentChar.getNation(), nationNumber))
                {
                    this.parentOrder.addError(nationName + " is an allied nation.");
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
        if(this.phase == 1)
        {
            clearProcessState(STATE_ARMY_LOCATION);
            clearProcessState(STATE_CHARACTER_LOCATION);
        } else
        if(this.phase == 2)
        {
            if(waitForProcessState(STATE_ARMY))
            {
                return STATE_REQ;
            }
            Army army = this.parentChar.getArmy(this.parentOrder.getOrder());
            if(army == null)
            {
                setProcessState(STATE_ARMY_LOCATION);
                setProcessState(STATE_CHARACTER_LOCATION);
                return null;
            }
            boolean requiredTransports = false;
            if(this.additionalInfo.size() == 0)
            {
                int amounts[] = army.getTroopContent(this.parentOrder.getOrder());
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
                String msg = "NAVY:Does " + this.parentChar + "'s navy have enough " + "transports? (needs " + ships + ")";
                addAdditionalInfo(msg, false);
                if(army.getFoodRequired() == 0)
                {
                    msg = "FOOD:Will " + this.parentChar + "'s navy have 1 food?";
                    addAdditionalInfo(msg, false);
                }
                return STATE_REQ;
            }
            requiredTransports = getAdditionalInfo();
            if(army.getFoodRequired() == 0)
            {
                army.setFoodRequired(1);
                army.setHasEnoughFood(getAdditionalInfo());
            }
            boolean food = army.getHasEnoughFood();
            if(waitForPartialState(STATE_ARMY_LOCATION) || waitForPartialState(STATE_CHARACTER_LOCATION))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_ARMY_LOCATION);
            setProcessState(STATE_CHARACTER_LOCATION);
            if(!requiredTransports)
            {
                this.parentOrder.addError("The navy does not have enough transports to move.");
                return null;
            }
            String evasiveString = this.parentOrder.getParameterString(1);
            boolean evasive = !evasiveString.equalsIgnoreCase("no");
            int maxMovement = 14;
            int totalMovement = maxMovement;
            int currentHex = this.parentChar.getLocation(this.parentOrder.getOrder());
            int lastHex = currentHex;
            for(int param = 1; param < this.parentOrder.getNumberOfParameters() && currentHex > 0 && totalMovement >= 0; param++)
            {
                String dirString = this.parentOrder.getParameterString(param + 1);
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
                if(param + 1 >= this.parentOrder.getNumberOfParameters())
                {
                    continue;
                }
                willPCBlockMovement(currentHex);
                if(Main.main.getNation().isEnemyArmyPresent(currentHex))
                {
                    this.parentOrder.addWarning("An enemy army is present at " + currentHex + " and may block movement.");
                }
            }

            String msg = food ? "Food, " : "No Food, ";
            msg = msg + (evasive ? "Evasive, " : "Normal, ");
            if(currentHex < 0)
            {
                if(currentHex == -1)
                {
                    this.parentOrder.addError("The navy tried to move into an invalid hex!");
                } else
                {
                    this.parentOrder.addWarning("The navy can only move onto a shore hex or hex with a harbor/port or major river.");
                }
                if(totalMovement >= 0)
                {
                    int movementSpent = maxMovement - totalMovement;
                    msg = msg + maxMovement + " points, " + movementSpent + " spent, ";
                }
                msg = msg + "stopped at " + Main.main.locationStr(lastHex) + ".";
                this.parentOrder.addInfo(msg);
                if(this.parentChar.isArmyCO(this.parentOrder.getOrder()))
                {
                    army.setNewLocation(lastHex, this.parentOrder.getOrder());
                }
            } else
            {
                if(totalMovement < 0)
                {
                    this.parentOrder.addError("Maximum movement was exceeded!");
                }
                int movementSpent = maxMovement - totalMovement;
                msg = msg + maxMovement + " points, " + movementSpent + " spent, stopped at " + Main.main.locationStr(currentHex) + ".";
                this.parentOrder.addInfo(msg);
                if(this.parentChar.isArmyCO(this.parentOrder.getOrder()))
                {
                    army.setNewLocation(currentHex, this.parentOrder.getOrder());
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
        if(this.phase == 2)
        {
            if(type >= 0 && type < 4)
            {
                processRank(0, type, 0, 0);
                int rankValue = this.parentChar.getNaturalRank(type);
                if(rankValue > 0)
                {
                    int maxRank = Main.main.getNation().getMaxRank(type);
                    if(rankValue > maxRank)
                    {
                        rankValue = maxRank;
                    }
                    this.parentOrder.addHelp("Creating new " + Character.getRankName(type) + " character with rank " + rankValue + ".");
                }
            } else
            if(type == 4)
            {
                StringBuffer desc = new StringBuffer();
                int sum = 0;
                int ranks[] = new int[4];
                for(int i = 0; i < 4; i++)
                {
                    ranks[i] = this.parentOrder.getParameterNumber(param + i);
                    sum += ranks[i];
                    if(ranks[i] == -1)
                    {
                        this.parentOrder.addError("Could not determine one of the ranks.");
                        return null;
                    }
                    if(ranks[i] > 0 && ranks[i] < 10)
                    {
                        this.parentOrder.addError(Character.getRankName(i) + " rank less " + "than 10 specified.");
                        return null;
                    }
                    if(ranks[i] > 30)
                    {
                        this.parentOrder.addError(Character.getRankName(i) + " rank of more " + "than 30 specified.");
                        return null;
                    }
                    if(ranks[i] > 0)
                    {
                        desc.append(ranks[i] + " " + Character.getRankName(i) + ", ");
                    }
                }

                if(sum != 30)
                {
                    this.parentOrder.addError("Spend 30 points on ranks, current total is " + sum + ".");
                } else
                {
                    desc.delete(desc.length() - 2, desc.length());
                    this.parentOrder.addHelp("Creating new character: " + desc + ".");
                }
            }
        }
        return null;
    }

    private String processOneRing()
    {
        if(this.phase == 2)
        {
            if(waitForProcessState(STATE_CHARACTER_LOCATION) || waitForProcessState(STATE_ARTIFACT))
            {
                return STATE_REQ;
            }
            if(this.parentChar.getLocation(this.parentOrder.getOrder()) != 3423)
            {
                this.parentOrder.addError(this.parentChar + " is not at hex 3423.");
                return null;
            }
            String artifact = this.parentChar.getArtifactName(14);
            boolean hasArtifact = this.parentChar.hasArtifact(14, this.parentOrder.getOrder());
            if(!hasArtifact)
            {
                this.parentOrder.addError(this.parentChar + " does not have " + (artifact != null ? artifact : "the ring") + ".");
            } else
            if(Nation.getNationAlignment(this.parentChar.getNation()) == 1)
            {
                this.parentOrder.addHelp(this.parentChar + " is attempting to destroy " + (artifact != null ? artifact : "the ring") + ".");
            } else
            {
                this.parentOrder.addHelp(this.parentChar + " is attempting to transfer " + (artifact != null ? artifact : "the ring") + " to Sauron.");
            }
            for(int i = 0; i < this.parentChar.getOrderCount(); i++)
            {
                if(this.parentChar.getOrder(i) != 990)
                {
                    this.parentOrder.addError("Both orders must be order 990.");
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
        if(this.phase != 1 && this.phase == 2)
        {
            if(waitForProcessState(STATE_PC))
            {
                return STATE_REQ;
            }
            if(locParam == 0 && waitForProcessState(STATE_CHARACTER_LOCATION))
            {
                return STATE_REQ;
            }
            int location = this.parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            PopCenter pc = Main.main.getNation().findPopulationCenter(location);
            if(pc == null)
            {
                if(state != 4)
                {
                    this.parentOrder.addError("No population center is present at location " + Main.main.locationStr(location) + ".");
                } else
                {
                    pc = new PopCenter(location);
                    pc.setNewPC();
                    pc.setName("NEW PC (Created by " + this.parentChar.getName() + ")");
                    pc.setNation(this.parentChar.getNation());
                    pc.setFortification(0);
                    pc.setSize(1);
                    pc.setDock(0);
                    pc.setLoyalty(15);
                    pc.initStateInformation();
                    Main.main.getNation().addPopulationCenter(pc);
                }
                return null;
            }
            boolean owned = pc.getNation() == this.parentChar.getNation();
            boolean enemy = Nation.isEnemy(this.parentChar.getNation(), pc.getNation());
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
                        this.parentOrder.addWarning(possibleLoss);
                    }
                    break;
                }
                if(pc.getPossibleCapture() < this.parentOrder.getOrder() && pc.getCapturingNation() == this.parentChar.getNation())
                {
                    this.parentOrder.addWarning(possibleCombatGain);
                    break;
                }
                if(pc.getPossibleInfluence() < this.parentOrder.getOrder())
                {
                    this.parentOrder.addWarning(possibleInfluenceGain);
                } else
                {
                    this.parentOrder.addError(pc + " is not owned by your nation.");
                }
                break;

            case 9: // '\t'
                break;

            case 1: // '\001'
                if(!owned && pc.getHidden())
                {
                    this.parentOrder.addError(pc + " is hidden and not owned by you.");
                }
                break;

            case 2: // '\002'
                if(pc.getHidden())
                {
                    this.parentOrder.addError(pc + " is hidden.");
                    break;
                }
                if(owned)
                {
                    if(pc.getEnemyArmyPresent())
                    {
                        this.parentOrder.addWarning(possibleLoss);
                    } else
                    {
                        this.parentOrder.addError(pc + " is owned by your nation.");
                    }
                    break;
                }
                if(pc.getPossibleCapture() < this.parentOrder.getOrder() && pc.getCapturingNation() == this.parentChar.getNation())
                {
                    this.parentOrder.addWarning(possibleCombatGain);
                    break;
                }
                if(pc.getPossibleInfluence() < this.parentOrder.getOrder())
                {
                    this.parentOrder.addWarning(possibleInfluenceGain);
                }
                break;

            case 3: // '\003'
                if(enemy && !pc.getHidden())
                {
                    break;
                }
                if(pc.getHidden())
                {
                    this.parentOrder.addError(pc + " is hidden.");
                    break;
                }
                if(neutral)
                {
                    this.parentOrder.addWarning(pc + " belongs to a neutral.");
                } else
                {
                    this.parentOrder.addError(pc + " does not belong to an enemy.");
                }
                break;

            case 4: // '\004'
                if(pc.getPossibleDestruction() < this.parentOrder.getOrder())
                {
                    this.parentOrder.addWarning(possibleDestruction);
                    break;
                }
                if(pc.getEnemyArmyPresent())
                {
                    this.parentOrder.addWarning(possibleDestruction);
                } else
                {
                    this.parentOrder.addError("A population center, " + pc + ", is already here.");
                }
                break;

            case 7: // '\007'
                if(pc.isNationsCapital(this.parentChar.getNation()))
                {
                    this.parentOrder.addError(pc + " is your capital.");
                    break;
                }
                if(pc.getHidden())
                {
                    this.parentOrder.addError(pc + " is hidden.");
                    break;
                }
                if(owned)
                {
                    if(pc.getEnemyArmyPresent())
                    {
                        this.parentOrder.addWarning(possibleLoss);
                    }
                    break;
                }
                if(pc.getPossibleCapture() < this.parentOrder.getOrder() && pc.getCapturingNation() == this.parentChar.getNation())
                {
                    this.parentOrder.addWarning(possibleCombatGain);
                    break;
                }
                if(pc.getPossibleInfluence() < this.parentOrder.getOrder())
                {
                    this.parentOrder.addWarning(possibleInfluenceGain);
                } else
                {
                    this.parentOrder.addError(pc + " is not owned by your nation.");
                }
                break;

            case 8: // '\b'
                if(!pc.isNationsCapital(this.parentChar.getNation()))
                {
                    this.parentOrder.addError(pc + " is not your capital.");
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
                    this.parentOrder.addError(pc + " is hidden and not owned by you.");
                } else
                {
                    this.parentOrder.addWarning(targetNation + " must have " + "friendly relations with your nation.");
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
        if(this.phase == 2)
        {
            int location = this.parentOrder.extractLocation(locParam, true);
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
                    this.parentOrder.addError(pc + " needs to be a town, major town, " + "or city.");
                }
                break;

            case 9: // '\t'
                if(pc.getSize() != 4 && pc.getSize() != 5)
                {
                    this.parentOrder.addError(pc + " needs to be a major town or city.");
                }
                break;

            case 10: // '\n'
                if(pc.getSize() == 1)
                {
                    this.parentOrder.addError(pc + " needs to be larger than a camp.");
                }
                break;

            case 1: // '\001'
            case 2: // '\002'
            case 3: // '\003'
            case 4: // '\004'
            case 5: // '\005'
                if(pc.getSize() != type)
                {
                    this.parentOrder.addError(pc + " is not a " + PopCenter.getSizeName(type) + ".");
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
        if(this.phase == 1)
        {
            clearProcessState(STATE_PC);
        } else
        if(this.phase == 2)
        {
            if(waitForPartialState(STATE_PC))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_PC);
            int location = this.parentOrder.extractLocation(locParam, true);
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
                pc.setPossibleDestruction(this.parentOrder.getOrder());
                break;

            case 1: // '\001'
                pc.setPossibleCapture(this.parentOrder.getOrder());
                break;

            case 2: // '\002'
                pc.setPossibleInfluence(this.parentOrder.getOrder());
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
        if(this.phase == 2)
        {
            int destination = 0;
            if(destParam > 0)
            {
                destination = this.parentOrder.getParameterNumber(destParam);
            }
            String product = this.parentOrder.getParameterString(productParam);
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
                this.parentOrder.addError("Could not determine the product being transfered.");
            }
            int quantity = this.parentOrder.getParameterNumber(numParam);
            if(quantity == -1)
            {
                this.parentOrder.addError("Could not determine quantity for the transfer.");
            }
            if(quantity <= 100 && units == 0)
            {
                this.parentOrder.addWarning("# of units is expected, make sure you are not specifying a percentage.");
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
            //is this food coming into an army?
            if ((type == 2) && (product.equals("Food"))) {
                Army army = this.parentChar.getArmy(this.parentOrder.getOrder());
                if (army != null) {
                	army.setFoodGainedBeforeMove(quantity);
                }
            }
            msg = msg + ".";
            this.parentOrder.addHelp(msg);
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
        if(this.phase == 2)
        {
            Character character = this.parentOrder.extractCharacter(charParam, false);
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
                    this.parentOrder.addError(character + " has no command rank.");
                }
                if(min > 0 && character.getTotalCommandRank() < min)
                {
                    errorMsg = "command";
                }
                break;

            case 1: // '\001'
                if(character.getTotalAgentRank() == 0)
                {
                    this.parentOrder.addError(character + " has no agent rank.");
                }
                if(min > 0 && character.getTotalAgentRank() < min)
                {
                    errorMsg = "agent";
                }
                break;

            case 2: // '\002'
                if(character.getTotalEmissaryRank() == 0)
                {
                    this.parentOrder.addError(character + " has no emissary rank.");
                }
                if(min > 0 && character.getTotalEmissaryRank() < min)
                {
                    errorMsg = "emissary";
                }
                break;

            case 3: // '\003'
                if(character.getTotalMageRank() == 0)
                {
                    this.parentOrder.addError(character + " has no mage rank.");
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
                    this.parentOrder.addWarning(character + " should have at least a " + errorMsg + " rank of " + min + ".");
                } else
                {
                    this.parentOrder.addWarning("New PC will have low loyalty if " + errorMsg + " rank is less than " + min + ".");
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
        if(this.phase == 1)
        {
            if(logic != 0 && logic != 1)
            {
                return "Invalid logic (" + logic + ") for SETCOMMAND!";
            }
            clearProcessState(STATE_ARMY);
            clearProcessState(STATE_COMPANY);
        } else
        if(this.phase == 2)
        {
            if(waitForPartialState(STATE_ARMY) || waitForPartialState(STATE_COMPANY))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_ARMY);
            setProcessState(STATE_COMPANY);
            Character character = this.parentOrder.extractCharacter(charParam, false);
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
                Army army = character.getArmy(this.parentOrder.getOrder());
                if(army != null)
                {
                    army.disbandArmy(((Army) (null)), this.parentOrder.getOrder(), true);
                }
                break;

            case 1: // '\001'
                if(character.isCompanyCO(this.parentOrder.getOrder()) && logic == 0 || !character.isCompanyCO(this.parentOrder.getOrder()) && logic == 1)
                {
                    character.setCompanyCO(!character.isCompanyCO(this.parentOrder.getOrder()), this.parentOrder.getOrder());
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
        if(this.phase == 2)
        {
            int location = this.parentOrder.extractLocation(locParam, true);
            if(location == -1)
            {
                return null;
            }
            int warships = this.parentOrder.getParameterNumber(warParam);
            int transports = this.parentOrder.getParameterNumber(tranParam);
            if(warships == -1 || transports == -1)
            {
                this.parentOrder.addError("Could not determine the number of required ships.");
                return null;
            }
            if(this.additionalInfo.size() == 0)
            {
                String msg;
                if(status == 1)
                {
                    msg = "NAVY:Does " + this.parentChar + "'s army/navy have ";
                } else
                {
                    msg = "NAVY:Does hex " + location + " have ";
                }
                msg = msg + warships + " warships and " + transports + " transports?";
                addAdditionalInfo(msg, false);
                return STATE_REQ;
            }
            if(!getAdditionalInfo())
            {
                this.parentOrder.addError("The required number of ships are not present.");
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
        if(this.phase == 2)
        {
            int location = Main.main.getNation().getCapital();
            if(type == 0)
            {
                location = this.parentOrder.extractLocation(locParam, true);
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
                this.parentOrder.addWarning(pc + " risks possibility of siege from " + "enemy armies that are present.");
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
        if(this.phase == 2)
        {
            int spellNumber = this.parentOrder.getParameterNumber(spellParam);
            if(spellNumber == -1)
            {
                this.parentOrder.addError("Could not determine spell number.");
                return null;
            }
            Spell spell1 = Main.main.getSpellList().findSpell(spellNumber);
            if(spell1 == null)
            {
                this.parentOrder.addError("Spell " + spellNumber + " does not exist.");
                return null;
            }
            boolean hasPrereq = Main.main.getNation().hasSpellPrereq(spellNumber);
            if(!hasPrereq)
            {
                hasPrereq = Main.main.getSpellList().hasSpellRequisite(this.parentChar, spell1);
            }
            if(!hasPrereq)
            {
                this.parentOrder.addError(this.parentChar + " does not have the " + "prerequisite for " + spell1 + ".");
            } else
            {
                this.parentOrder.addHelp(this.parentChar + " has the prerequisite for " + spell1 + ".");
                if(Main.main.getSpellList().isSpellInLostList(spell1.getSpellNumber()))
                {
                    this.parentOrder.addWarning(spell1 + " is part of a lost list.");
                }
                this.parentChar.addSpell(new Integer(spell1.getSpellNumber()), spell1.getName());
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
        int spellNumber = this.parentOrder.getParameterNumber(spellParam);
        if(this.phase == 1)
        {
            if(spellNumber == -1)
            {
                this.parentOrder.addError("Could not determine spell number.");
                return null;
            }
            Ruleset spellSet = Main.main.getRuleSet().getRulesForOrder(spellNumber, true);
            if(spellSet.size() > 0)
            {
                this.spellRules = spellSet;
                this.spellRules.processRules(this.parentOrder, this.phase);
            }
        } else
        if(this.phase == 2)
        {
            Spell spell1 = Main.main.getSpellList().findSpell(spellNumber);
            if(spell1 == null)
            {
                this.parentOrder.addError("Spell " + spellNumber + " does not exist.");
                return null;
            }
            String spellInfo = this.parentChar.isSpellKnown(spellNumber);
            if(spellInfo == null)
            {
                this.parentOrder.addError(this.parentChar + " does not know " + spell1 + ".");
            } else
            {
                this.parentOrder.addHelp("Attempting to cast " + spellInfo + ".");
            }
            if(spell1.getOrder() != this.parentOrder.getOrder())
            {
                this.parentOrder.addError(spell1 + " requires order " + spell1.getOrder() + " for casting.");
            }
            if(!Main.main.getRuleSet().doesSpellHaveRules(spell1.getSpellNumber()))
            {
                this.parentOrder.addWarning("No rules exist for " + spell1 + ".");
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
        if(this.phase == 2)
        {
            int numSpells = this.parentOrder.getNumberOfParameters() + 1;
            for(int i = spellParam; i < numSpells; i++)
            {
                int spellNumber = this.parentOrder.getParameterNumber(i);
                if(spellNumber == -1)
                {
                    this.parentOrder.addError("Could not determine spell number (param " + i + ").");
                    return null;
                }
                Spell spell1 = Main.main.getSpellList().findSpell(spellNumber);
                if(spell1 == null)
                {
                    this.parentOrder.addError("Spell " + spellNumber + " does not exist.");
                    continue;
                }
                if(this.parentChar.isSpellKnown(spellNumber) == null)
                {
                    this.parentOrder.addError(this.parentChar + " does not know " + spell1 + ".");
                }
            }

            int total = numSpells - spellParam;
            if(total > 6)
            {
                this.parentOrder.addError("Only 6 spells can be specified.");
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
        if(this.phase == 2)
        {
            Character character = this.parentOrder.extractCharacter(charParam, false);
            if(character == null)
            {
                return null;
            }
            if(waitForProcessState(STATE_CHARACTER_ARMY))
            {
                return STATE_REQ;
            }
            if(character.isArmyCO(this.parentOrder.getOrder()) && (this.parentChar.army == null))
            {
                this.parentOrder.addError(this.parentChar + " needs to be in an army " + "since " + character + " is an army/navy commander.");
            }
        }
        return null;
    }

    private String processThreatenPopCenter()
    {
        boolean bDisabled = true;
        if(this.phase == 2 && !bDisabled)
        {
            if(waitForProcessState(STATE_CHARACTER_LOCATION) || waitForProcessState(STATE_ARMY) || waitForProcessState(STATE_ARMY_LOCATION))
            {
                return STATE_REQ;
            }
            int location = this.parentChar.getLocation(this.parentOrder.getOrder());
            int troops = Main.main.getNation().totalTroopsAtLocation(location, this.parentOrder.getOrder());
            PopCenter pc = Main.main.getNation().findPopulationCenter(location);
            if(pc != null && troops >= 100 && pc.getSize() > 1)
            {
                int chance = this.parentChar.getTotalCommandRank();
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
                this.parentOrder.addInfo(msg);
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
        if(this.phase == 1)
        {
            if(retire == 1)
            {
                clearProcessState(STATE_ARMY);
            }
        } else
        if(this.phase == 2)
        {
            if(retire == 1)
            {
                if(waitForPartialState(STATE_ARMY))
                {
                    return STATE_REQ;
                }
                setProcessState(STATE_ARMY);
            } else
            if(waitForProcessState(STATE_ARMY))
            {
                return STATE_REQ;
            }
            Army army = null;
            if(type == 0)
            {
                army = this.parentChar.getArmy(this.parentOrder.getOrder());
            } else
            if(type == 1)
            {
                if(this.parentChar.isCommanderInArmy(this.parentOrder.getOrder()))
                {
                    army = this.parentChar.getArmy(this.parentOrder.getOrder());
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
                amount[i] = this.parentOrder.getParameterNumber(amountParam[i]);
                if(amount[i] == -1 && this.phase == 1)
                {
                    this.parentOrder.addError("Could not get " + Army.getTroopName(i) + " troop amount.");
                    return null;
                }
            }

            String desc = "";
            int troops[] = army.getTroopContent(this.parentOrder.getOrder());
            int totalTroops = 0;
            for(int i = 0; i < 6; i++)
            {
                if(amount[i] <= 0)
                {
                    continue;
                }
                if(troops[i] < amount[i])
                {
                    this.parentOrder.addError("There aren't " + amount[i] + " " + Army.getTroopName(i) + " in the army.");
                    amount[i] = troops[i];
                }
                desc = desc + amount[i] + " " + Army.getTroopName(i) + ", ";
                totalTroops += amount[i];
            }

            if(totalTroops > 0)
            {
                desc = desc.substring(0, desc.length() - 2);
                desc = desc + " affected.";
                this.parentOrder.addHelp(desc);
            }
            if(retire == 1)
            {
                for(int i = 0; i < 6; i++)
                {
                    amount[i] *= -1;
                }

                army.setTroopContent(amount, this.parentOrder.getOrder());
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
            String typeString = this.parentOrder.getParameterString(typeParam);
            type = Army.findTroopType(typeString);
        }
        if((type < 0 || type > 5) && typeParam > 0)
        {
            return "Invalid type " + type + " in TROOPAMT, order " + this.order + "!";
        }
        if(this.phase == 1)
        {
            clearProcessState(STATE_ARMY);
            clearProcessState(STATE_CHARACTER_ARMY);
        } else
        if(this.phase == 2)
        {
            int amount = this.parentOrder.getParameterNumber(amountParam);
            if(amount == -1)
            {
                this.parentOrder.addError("Could not determine amount of troops.");
            }
            if(type < 0 || type > 5)
            {
                this.parentOrder.addError("Invalid troop type was specified.");
            }
            PopCenter pc = Main.main.getNation().findOwnedPopulationCenter(this.parentChar.getLocation(this.parentOrder.getOrder()));
            if(amount == -1 || type < 0 || type > 5 || pc == null)
            {
                setProcessState(STATE_ARMY);
                setProcessState(STATE_CHARACTER_ARMY);
                return null;
            }
            if(type == 0 || type == 1)
            {
//            	Hex hex = Main.main.getMap().findHex(pc.getLocation());
            	
                if(this.additionalInfo.size() == 0)
                {
                    String msg = "TROOP:Does " + pc + " have " + amount + " mounts and " + amount * 2 + " leather?";
                    addAdditionalInfo(msg, false);
                    return STATE_REQ;
                }
                if(!getAdditionalInfo())
                {
                    this.parentOrder.addError(pc + " doesn't have enough mounts " + "and/or leather for cavalry.");
                }
            }
            if(waitForPartialState(STATE_ARMY) || waitForPartialState(STATE_CHARACTER_ARMY))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_ARMY);
            setProcessState(STATE_CHARACTER_ARMY);
            int amounts[] = new int[6];
            amounts[type] = amount;
            Army army = null;
            if(this.parentChar.isCommanderInArmy(this.parentOrder.getOrder()))
            {
                army = this.parentChar.getArmy(this.parentOrder.getOrder());
                army.setTroopContent(amounts, this.parentOrder.getOrder());
            } else
            {
                army = new Army(this.parentChar.getLocation(this.parentOrder.getOrder()));
                army.setNewArmy(this.parentChar, amounts, this.parentOrder.getOrder());
                if(foodParam > 0)
                {
                    int food = this.parentOrder.getParameterNumber(foodParam);
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
                this.parentOrder.addWarning(pc + " still has " + left + " troops available.");
            } else
            if(left < 0)
            {
                this.parentOrder.addError(pc + " is out of troops!  Maximum " + pc.getMaximumTroops() + " available.");
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
        if(this.phase == 2)
        {
            if(waitForProcessState(STATE_CHARACTER_ARMY) || waitForProcessState(STATE_ARMY))
            {
                return STATE_REQ;
            }
            Character character = this.parentOrder.extractCharacter(charParam, true);
            if(character == null)
            {
                return null;
            }
            int type = -1;
            String typeString = this.parentOrder.getParameterString(typeParam);
            if(typeString != null)
            {
                type = Army.findTroopType(typeString);
            }
            if(type == -1)
            {
                this.parentOrder.addError("Could not determine troop type (" + typeString + ").");
                return null;
            }
            Army army = null;
            if(character.isCommanderInArmy(this.parentOrder.getOrder()))
            {
                army = character.getArmy(this.parentOrder.getOrder());
            }
            if(army == null)
            {
                this.parentOrder.addError(character + " is not a commander in an army.");
                return null;
            }
            int troops[] = army.getTroopContent(this.parentOrder.getOrder());
            if(troops[type] == 0)
            {
                this.parentOrder.addError("There are no " + Army.getTroopName(type) + " in the army.");
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

        if(this.phase == 1)
        {
            clearProcessState(STATE_ARMY);
            clearProcessState(STATE_CHARACTER_ARMY);
        } else
        if(this.phase == 2)
        {
            processTroopAmount(type, 1, amountParam);
            if(waitForPartialState(STATE_CHARACTER_ARMY))
            {
                return STATE_REQ;
            }
            setProcessState(STATE_CHARACTER_ARMY);
            Army srcArmy = null;
            if(type == 0)
            {
                srcArmy = this.parentChar.getArmy(this.parentOrder.getOrder());
            } else
            if(this.parentChar.isCommanderInArmy(this.parentOrder.getOrder()))
            {
                srcArmy = this.parentChar.getArmy(this.parentOrder.getOrder());
            }
            if(srcArmy == null)
            {
                return null;
            }
            Character character = this.parentOrder.extractCharacter(charParam, false);
            if(character == null)
            {
                return null;
            }
            Army destArmy = null;
            if(character.isArmyCO(this.parentOrder.getOrder() + 1))
            {
                destArmy = character.getArmy(this.parentOrder.getOrder() + 1);
            }
            int currentTroops[] = srcArmy.getTroopContent(this.parentOrder.getOrder());
            int amount[] = new int[6];
            for(int i = 0; i < 6; i++)
            {
                amount[i] = this.parentOrder.getParameterNumber(amountParam[i]);
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
                destArmy = new Army(character.getLocation(this.parentOrder.getOrder()));
                destArmy.setNewArmy(character, amount, this.parentOrder.getOrder());
                Main.main.getNation().addArmy(destArmy);
            } else
            {
                destArmy.setTroopContent(amount, this.parentOrder.getOrder());
            }
            int totalSplit = 0;
            for(int i = 0; i < 6; i++)
            {
                if(amount[i] > 0)
                {
                    totalSplit += amount[i];
                }
            }

            int srcTroops = srcArmy.getTotalTroops(this.parentOrder.getOrder()) - totalSplit;
            if(srcTroops < 100)
            {
                if(srcTroops > 0)
                {
                    this.parentOrder.addError(this.parentChar + " will have less than 100 " + "troops left after the split.");
                } else
                {
                    this.parentOrder.addWarning(this.parentChar + " is disbanding the army." + " All baggage will be lost.");
                }
                srcArmy.disbandArmy(((Army) (null)), this.parentOrder.getOrder() + 1, true);
            }
            if(totalSplit < 100)
            {
                this.parentOrder.addError("The new army won't have 100 troops after the split.");
            }
        }
        return null;
    }

}
