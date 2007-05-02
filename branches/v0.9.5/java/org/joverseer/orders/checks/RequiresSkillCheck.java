package org.joverseer.orders.checks;

import org.joverseer.domain.Order;


public class RequiresSkillCheck extends AbstractCheck {
    public static String COMMAND_SKILL = "command skill";
    public static String AGENT_SKILL = "agent skill";
    public static String MAGE_SKILL = "mage skill";
    public static String EMISSARY_SKILL = "emissary skill";
    
    String skillReq = "";
    
    public RequiresSkillCheck(String req) {
        super();
        skillReq=req;
    }

    public boolean check(Order o) {
        if (skillReq.equals(COMMAND_SKILL)) {
            return o.getCharacter().getCommandTotal() > 0;
        }
        if (skillReq.equals(AGENT_SKILL)) {
            return o.getCharacter().getAgentTotal() > 0;
        }
        if (skillReq.equals(MAGE_SKILL)) {
            return o.getCharacter().getMageTotal() > 0;
        }
        if (skillReq.equals(EMISSARY_SKILL)) {
            return o.getCharacter().getEmmisaryTotal() > 0;
        }
        return true;
    }

    public String getMessage() {
        return "The order requires " + skillReq + ".";
    }
    
}
