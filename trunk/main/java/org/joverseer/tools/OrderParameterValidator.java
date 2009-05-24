package org.joverseer.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.StringUtils;
import org.joverseer.ui.orderEditor.OrderEditorData;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

/**
 * Utility class that validates order parameters based on the resource file
 * orderEditorData.csv
 * 
 * It returns an error message for every order parameter or null if the param is correct
 * 
 * Validation is more syntactical than logical
 * 
 * @author Marios Skounakis
 */
public class OrderParameterValidator {

    Container orderEditorData = null;

    private Container getOrderEditorData() {
        if (orderEditorData == null) {
            orderEditorData = new Container(new String[] {"orderNo"});
            try {
                GameMetadata gm = (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata");
                Resource resource = gm.getResource("orderEditorData.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

                String ln;
                while ((ln = reader.readLine()) != null) {
                    try {
                        String[] partsL = ln.split(";");
                        String[] parts = new String[] {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
                        for (int i = 0; i < partsL.length; i++) {
                            parts[i] = partsL[i];
                        }
                        OrderEditorData oed = new OrderEditorData();
                        oed.setOrderNo(Integer.parseInt(parts[0]));
                        oed.setOrderDescr(parts[1]);
                        oed.setParameterDescription(parts[2]);
                        oed.setOrderType(parts[3]);
                        oed.getParamTypes().add(parts[4]);
                        oed.getParamTypes().add(parts[5]);
                        oed.getParamTypes().add(parts[6]);
                        oed.getParamTypes().add(parts[7]);
                        oed.getParamTypes().add(parts[8]);
                        oed.getParamTypes().add(parts[9]);
                        oed.getParamTypes().add(parts[10]);
                        oed.setMajorSkill(parts[11]);
                        oed.setSkill(parts[12]);
                        orderEditorData.addItem(oed);
                        ln = reader.readLine();
                        if (ln == null) {
                            ln = "";
                        }
                        partsL = ln.split(";");
                        parts = new String[] {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
                        for (int i = 0; i < partsL.length; i++) {
                            parts[i] = partsL[i];
                        }
                        oed.getParamDescriptions().add(parts[4]);
                        oed.getParamDescriptions().add(parts[5]);
                        oed.getParamDescriptions().add(parts[6]);
                        oed.getParamDescriptions().add(parts[7]);
                        oed.getParamDescriptions().add(parts[8]);
                        oed.getParamDescriptions().add(parts[9]);
                        oed.getParamDescriptions().add(parts[10]);
                    } catch (Exception exc) {
                        System.out.println(ln);
                    }
                }
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
                orderEditorData = null;
            }
        }
        return orderEditorData;
    }
    
    public OrderValidationResult checkForEncounter(Order o) {
    	if (o.isBlank()) return null;
    	Character c = o.getCharacter();
    	String orderResults = c.getOrderResults();
    	if (orderResults == null || orderResults.equals("")) return null;
    	orderResults = StringUtils.removeAllNewline(orderResults);
    	orderResults = StringUtils.removeExtraspaces(orderResults);
    	if (orderResults.contains("had a special encounter. See Encounter Messages.")) {
    		if (o.getOrderNo() == 285) return null;
    		int i = o.getCharacter().getOrders()[0] == o ? 1 : 0;
    		Order o1 = c.getOrders()[i];
    		if (o1.isBlank()) return null;
    		if (o1.getOrderNo() != 285) {
    			return new OrderValidationResult(OrderValidationResult.WARNING, "Character has an encounter but no react encounter order.");
    		}
    	}
    	return null;
    }
    
    public OrderValidationResult checkForDuplicateSkillOrder(Order o) {
        if (o.isBlank())
            return null;
        OrderMetadata om = (OrderMetadata) GameHolder.instance().getGame().getMetadata().getOrders()
                .findFirstByProperty("number", o.getOrderNo());
        if (om == null)
            return null;
        // check for duplicate skill order
        Character c = o.getCharacter();
        int i = o.getCharacter().getOrders()[0] == o ? 1 : 0;
        Order o1 = c.getOrders()[i];
        if (!o1.isBlank()) {
            OrderMetadata om1 = (OrderMetadata) GameHolder.instance().getGame().getMetadata().getOrders()
                                    .findFirstByProperty("number", o1.getOrderNo());
            if (om1 == null) return null;
            if (om1.getSkillRequirement().equals(om.getSkillRequirement()) && om1.getSkillRequirement().endsWith("S")) {
                String type = "";
                if (om.getSkillRequirement().equals("CS")) {
                    type = "Command skill";
                } else if (om.getSkillRequirement().equals("AS")) {
                    type = "Agent skill";
                } else if (om.getSkillRequirement().equals("MS")) {
                    type = "Mage skill";
                } else if (om.getSkillRequirement().equals("ES")) {
                    type = "Emmisary skill";
                } else if (om.getSkillRequirement().equals("ECS")) {
                    type = "Command/Emmisary skill";
                } 
                return new OrderValidationResult(OrderValidationResult.ERROR, "Character is trying to issue two " + type + " orders.");
            }
            // check for duplicate movement orders
            boolean o1IsMove = om1.getSkillRequirement().equals("Move") || o1.getOrderNo() == 825;
            boolean o2IsMove = om.getSkillRequirement().equals("Move") || o.getOrderNo() == 825;
            if (o1IsMove && o2IsMove) {
            	return new OrderValidationResult(OrderValidationResult.ERROR, "Character is trying to issue two Move orders.");
            }
            // check for miscellaneous mage orders
            boolean o1IsCastSpell = o1.getOrderNo() == 120 || 
            						o1.getOrderNo() == 225 || 
            						o1.getOrderNo() == 330 ||
            						o1.getOrderNo() == 940;
            if (o1IsCastSpell && o1.getOrderNo() == o.getOrderNo()) {
            	return new OrderValidationResult(OrderValidationResult.ERROR, "Character is trying to issue two Casting orders of the same type of spell.");
            }
        }
        return null;
    }
    
    public OrderValidationResult checkOrder(Order o) {
        if (o.isBlank())
            return null;
        OrderMetadata om = (OrderMetadata) GameHolder.instance().getGame().getMetadata().getOrders()
                .findFirstByProperty("number", o.getOrderNo());
        if (om == null)
            return null;

        OrderValidationResult ovr = checkForDuplicateSkillOrder(o);
        if (ovr != null) return ovr;
        
        ovr = checkForEncounter(o);
        if (ovr != null) return ovr;

        if (om.getRequirement().indexOf("At Capital") >= 0) {
            if (o.getOrderNo() < 800) {
                PopulationCenter capital = (PopulationCenter) GameHolder.instance().getGame().getTurn().getContainer(
                        TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[] {"nationNo", "capital"},
                        new Object[] {o.getCharacter().getNationNo(), true});
                if (capital == null)
                    return null;
                if (capital.getHexNo() != o.getCharacter().getHexNo()) {
                    return new OrderValidationResult(OrderValidationResult.WARNING, "Character must be in capital");
                }
            }
        }
        return null;
    }

    public OrderValidationResult checkParam(Order o, int iParam) {
        if (o.isBlank())
            return null;

        String paramValue = o.getParameter(iParam);
        if (paramValue == null || paramValue.equals("--") || paramValue.equals("")) {
            paramValue = "-";
        }

        // moveArmy order and high number param
        if (o.getOrderNo() == 850 || o.getOrderNo() == 860 || o.getOrderNo() == 830) {
            int iLastParam = o.getLastParamIndex();
            if (iParam == 0) {
                if (inList(paramValue, "e,ne,nw,se,sw,w,h")) {
                    return null;
                } else {
                    return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + "e,ne,nw,se,sw,w,h");
                }
            } else if (iParam < iLastParam) {
                if (inList(paramValue, "e,ne,nw,se,sw,w,h")) {
                    return null;
                } else {
                    return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + "e,ne,nw,se,sw,w,h,no,ev");
                }
            } else if (iParam == iLastParam) {
                if (inList(paramValue, "e,ne,nw,se,sw,w,h,no,ev")) {
                    return null;
                } else {
                    return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + "e,ne,nw,se,sw,w,h,no,ev");
                }
            } else if (iParam == iLastParam + 1) {
                String lastParam = o.getParameter(iLastParam);
                if (inList(lastParam, "e,ne,nw,se,sw,w")) {
                    return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + "e,ne,nw,se,sw,w,h,no,ev");
                } else {
                    return null;
                }
            } else {
                if (isEmpty(paramValue)) {
                    return null;
                } else {
                    return new OrderValidationResult(OrderValidationResult.ERROR, "must be empty");
                }
            }
        }

        OrderEditorData oed = (OrderEditorData) getOrderEditorData().findFirstByProperty("orderNo", o.getOrderNo());
        if (oed == null) {
            return new OrderValidationResult(OrderValidationResult.ERROR, "OED not found");
        }
        String paramType = "";
        
        if (iParam < oed.getParamTypes().size()) {
            paramType = oed.getParamTypes().get(iParam);
        }
        if (paramType == null) {
            paramType = "";
        }
        // 940 order
        if (o.getOrderNo() == 940 && iParam == 1) {
            // get spellId
            String spellId = o.getParameter(0);
            if (",406,408,417,420,422,424,426,430,436,".indexOf("," + spellId + ",") > -1) {
                paramType = "cid";
            } else if (",416,".indexOf("," + spellId + ",") > -1) {
                paramType = "pro";
            } else if (",404,419,432,".indexOf("," + spellId + ",") > -1) {
                paramType = "nat";
            } else if (",402,410,".indexOf("," + spellId + ",") > -1) {
                paramType = "alg";
            } else if (",412,418,428,".indexOf("," + spellId + ",") > -1) {
                paramType = "b";
            } else if (",413,414,415,434,".indexOf("," + spellId + ",") > -1) {
                paramType = "hex";
            }
        }
        
        if (paramType.equals("a")) {
            if (isNumberOK(paramValue, 1, 99)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 1 and 99");
            }
        } else if (paramType.equals("ae")) {
            if (isNumberOK(paramValue, 0, 99)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 0 and 99");
            }
        } else if (paramType.equals("i")) {
            if (isNumberOK(paramValue, 0, 999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 0 and 999");
            }
        }
        if (paramType.equals("%")) {
            if (isNumberOK(paramValue, 1, 100)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 1 and 100");
            }
        } else if (paramType.equals("b")) {
            if (isNumberOK(paramValue, 1, 999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 1 and 999");
            }
        } else if (paramType.equals("d")) {
            if (isNumberOK(paramValue, 0, 99999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 0 and 99999");
            }
        } else if (paramType.equals("de")) {
            if (isNumberOK(paramValue, 0, 99999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 0 and 99999");
            }
        } else if (paramType.equals("c")) {
            if (isNumberOK(paramValue, 1, 999999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 1 and 999999");
            }
        } else if (paramType.equals("e")) {
            if (isEmpty(paramValue) || isNumberOK(paramValue, 1, 999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be empty or between 1 and 999");
            }
        } else if (paramType.equals("f")) {
            if (isEmpty(paramValue) || isNumberOK(paramValue, 1, 99999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be empty or between 1 and 99999");
            }
        } else if (paramType.equals("g")) {
            if (isNumberOK(paramValue, 0, 999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 0 and 999");
            }
        } else if (paramType.equals("nam")) {
            if (isEmpty(paramValue)) {
            	return new OrderValidationResult(OrderValidationResult.INFO, "may be between 5 and 17 chars (name).");
            } else if (lengthOK(paramValue, 5, 17)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 5 and 17 chars");
            }
        } else if (paramType.equals("rsp")) {
            if (isResponse(paramValue) || lengthOK(paramValue, 1, 20)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be between 5 and 17 chars");
            }
        } else if (paramType.equals("dcid")) {
            if (isEmpty(paramValue) || isCharId(paramValue) || isNumberOK(paramValue, 1, 99999)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be 5 lowercase chars (including spaces) or a number between 1 and 99999");
            }
        } else if (paramType.equals("cid")) {
            if (isCharId(paramValue)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be 5 lowercase chars (including spaces)");
            }
        } else if (paramType.equals("xid")) {
            if (isEmpty(paramValue) || isCharId(paramValue)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be 5 lowercase chars (including spaces) or empty");
            }
        } else if (paramType.equals("hex")) {
            if (isHexNum(paramValue)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be a 4-digit (hex) number");
            }
        } else if (paramType.equals("alg")) {
            if (inList(paramValue, "g,e,n")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of g,e,n");
            }
        } else if (paramType.equals("arm")) {
            if (inList(paramValue, "le,br,st,mi,no")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of le,br,st,mi,no");
            }
        } else if (paramType.equals("dir")) {
            if (inList(paramValue, "ne,nw,w,se,sw,e,h")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of ne,nw,w,se,sw,e,h");
            }
        } else if (paramType.equals("dirx")) {
            if (isEmpty(paramValue) || inList(paramValue, "ne,nw,w,se,sw,e,h")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of ne,nw,w,se,sw,e,h");
            }
        } else if (paramType.equals("gen")) {
            if (isEmpty(paramValue)) {
            	return new OrderValidationResult(OrderValidationResult.INFO, "may be one of m,f (gender)");
            } else  if (inList(paramValue, "m,f")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of m,f");
            }
        } else if (paramType.equals("genc")) {
        	if (isEmpty(paramValue)) {
            	return new OrderValidationResult(OrderValidationResult.INFO, "may be one of m,f (gender)");
            } else  if (inList(paramValue, "m,f")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of m,f");
            }
        } else if (paramType.equals("map")) {
            if (inList(paramValue, "ne,nw,w,se,sw,e")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of ne,nw,w,se,sw,e");
            }
        } else if (paramType.equals("nat")) {
            Game g = GameHolder.instance().getGame();
            String nations = "";
            if (g.getMetadata().getGameType() == GameTypeEnum.game1650
                    || g.getMetadata().getGameType() == GameTypeEnum.game2950
                    || g.getMetadata().getGameType() == GameTypeEnum.gameFA
                    ) {
                nations = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";
            } else if (g.getMetadata().getGameType() == GameTypeEnum.gameBOFA) {
                nations = "10,11,12,13,14";
            } else if (g.getMetadata().getGameType() == GameTypeEnum.gameKS) {
            	nations = "1,2,3,4,5,6,11,12,13,14,15,16,21,22";
            } else if (g.getMetadata().getGameType() == GameTypeEnum.gameUW) {
            	nations = "1,2,3,5,8,10,11,12,13,18,24";
            }
            if (inList(paramValue, nations)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + nations);
            }
        } else if (paramType.equals("prd")) {
            if (inList(paramValue, "mi,st,br,le,ti,mo,fo")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of mi,st,br,le,ti,mo,fo");
            }
        } else if (paramType.equals("pro")) {
            if (inList(paramValue, "go,mi,st,br,le,ti,mo,fo")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of go,mi,st,br,le,ti,mo,fo");
            }
        } else if (paramType.equals("spc")) {
            String spells = "102,104,106,108,110,112,114,116,202,204,206,208,210,212,214,216,218,220,222,224,226,228,230,232,234,236,238,240,242,244,246,248";
            if (inList(paramValue, spells)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + spells);
            }
        } else if (paramType.equals("sph")) {
            String spells = "2,4,6,8";
            if (inList(paramValue, spells)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + spells);
            }
        } else if (paramType.equals("spl")) {
            String spells = "402,404,406,408,410,412,413,414,415,416,417,418,419,420,422,424,426,428,430,432,434,436";
            if (inList(paramValue, spells)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + spells);
            }
        } else if (paramType.equals("spm")) {
            String spells = "302,304,306,308,310,312,314";
            if (inList(paramValue, spells)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + spells);
            }
        } else if (paramType.equals("spx")) {
            String spells = "502,504,506,508,510,512";
            if (inList(paramValue, spells)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + spells);
            }
        } else if (paramType.equals("spz")) {
            String spells = "2,4,6,8,102,104,106,108,110,112,114,116,202,204,206,208,210,212,214,216,218,220,222,224,226,228,230,232,234,236,238,240,242,244,246,248,402,404,406,408,410,412,413,414,415,416,417,418,419,420,422,424,426,428,430,432,434,436,302,304,306,308,310,312,314,502,504,506,508,510,512";
            if (inList(paramValue, spells)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + spells);
            }
        } else if (paramType.equals("tac")) {
            String list = "ch,fl,st,su,hr,am";
            if (inList(paramValue, list)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + list);
            }
        } else if (paramType.equals("trp")) {
            String list = "hc,lc,hi,li,ar,ma";
            if (inList(paramValue, list)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + list);
            }
        } else if (paramType.equals("wep")) {
            String list = "mi,st,br,wo";
            if (inList(paramValue, list)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + list);
            }
        } else if (paramType.equals("yn")) {
            String list = "y,n";
            if (inList(paramValue, list)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + list);
            }
        } else if (paramType.equals("mt")) {
            String list = "no,ev";
            if (inList(paramValue, list)) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be one of " + list);
            }
        } else if (paramType.equals("")) {
            if (paramValue.equals("-")) {
                return null;
            } else {
                return new OrderValidationResult(OrderValidationResult.ERROR, "must be empty");
            }
        } else {
            return new OrderValidationResult(OrderValidationResult.ERROR, "unchecked");
        }

    }
    

    private boolean isResponse(String v) {
        return v.substring(0, 1).equals(v.substring(0, 1).toUpperCase())
                && v.substring(1).equals(v.substring(1).toLowerCase());
    }

    private boolean inList(String v, String list) {
        return list.startsWith(v + ",") || (list.indexOf("," + v + ",") > -1) || list.endsWith("," + v);
    }

    private boolean isHexNum(String v) {
        return Pattern.matches("^\\d{4}$", v);
    }

    private boolean isCharId(String v) {
        return v.length() == 5 && v.equals(v.toLowerCase());
    }

    private boolean lengthOK(String v, Integer min, Integer max) {
        return isNumberOK(String.valueOf(v.length()), min, max);
    }

    private boolean isNumberOK(String v, Integer min, Integer max) {
        try {
            int i = Integer.parseInt(v);
            if (min != null && min > i) {
                return false;
            }
            if (max != null && max < i) {
                return false;
            }
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    private boolean isEmpty(String v) {
        return v.equals("-");
    }


}
