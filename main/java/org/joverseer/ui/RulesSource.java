/*
 * RulesSource.java
 *
 * Created on September 10, 2006, 12:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joverseer.ui;

import org.springframework.core.closure.Constraint;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;

/**
 *
 * @author mskounak
 */
public class RulesSource extends DefaultRulesSource {

    /** Creates a new instance of RulesSource */
    public RulesSource()  {
        super();
        addRules(createCustomerRules());
        addRules(createPopulationCenterRules());
    }

    private Rules createCustomerRules() {
        return new Rules(Customer.class) {
            protected void initRules() {
                add("firstName", getNameValueConstraint());
                add("lastName", getNameValueConstraint());
                add(not(eqProperty("firstName", "lastName")));
            }
        };
    }

    private Constraint getNameValueConstraint() {
        return all(new Constraint[] {required(), maxLength(25), regexp("[a-zA-Z]*", "alphabetic")});
    }

    private Rules createPopulationCenterRules() {
        return new Rules(PopulationCenter.class) {
            protected void initRules() {
                add("name", getNameValueConstraint());
                add("size", not(eq(PopulationCenterSizeEnum.ruins)));
                add("loyalty", gt((int)0));
            }
        };
    }
}
