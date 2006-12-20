package org.joverseer.metadata.orders;

import java.io.Serializable;


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
