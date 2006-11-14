package org.joverseer.metadata.orders;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 5 Íןו 2006
 * Time: 12:54:23 לל
 * To change this template use File | Settings | File Templates.
 */
public enum OrderTypeEnum implements Serializable {
    Command,
    CommandMisc,
    Emmisary,
    EmmisaryMisc,
    Mage,
    MageMisc,
    Agent,
    AgentMisc,
    Misc
}
