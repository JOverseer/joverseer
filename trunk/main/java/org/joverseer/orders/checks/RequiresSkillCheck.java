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
        this.skillReq=req;
    }

    @Override
	public boolean check(Order o) {
        if (this.skillReq.equals(COMMAND_SKILL)) {
            return o.getCharacter().getCommandTotal() > 0;
        }
        if (this.skillReq.equals(AGENT_SKILL)) {
            return o.getCharacter().getAgentTotal() > 0;
        }
        if (this.skillReq.equals(MAGE_SKILL)) {
            return o.getCharacter().getMageTotal() > 0;
        }
        if (this.skillReq.equals(EMISSARY_SKILL)) {
            return o.getCharacter().getEmmisaryTotal() > 0;
        }
        return true;
    }

    @Override
	public String getMessage() {
        return "The order requires " + this.skillReq + ".";
    }
    
}
