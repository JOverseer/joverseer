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
/**
 *
 * @author mskounak
 */
public class RulesSource extends DefaultRulesSource {
    
    /** Creates a new instance of RulesSource */
    public RulesSource()  {
        super(); 
        addRules(createCustomerRules());
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
}
