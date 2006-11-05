/*
 * Customer.java
 *
 * Created on September 10, 2006, 12:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joverseer.ui;

/**
 *
 * @author mskounak
 */
public class Customer {
    String f;
    String l;
    
    /** Creates a new instance of Customer */
    public Customer() {
    }

    public Customer(String f, String l) {
        this.f = f;
        this.l = l;
    }

    public String getFirstName() {
        return f;
    }
    
    public void setFirstName(String n) {
        f = n;
    }
    
    public String getLastName() {
        return l;
    }
    
    public void setLastName(String n) {
        l = n;
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }
}
