package org.joverseer.metadata.orders;

import java.io.Serializable;

/**
 * Enumeration for order types
 * 
 * TODO: Check if this is used anywhere, if not delete
 * 
 * @author Marios Skounakis
 *
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
