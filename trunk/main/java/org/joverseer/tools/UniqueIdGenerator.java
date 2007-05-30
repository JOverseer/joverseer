package org.joverseer.tools;


/**
 * Utility class that generates unique ids
 * 
 * @author Marios Skounakis
 */
public class UniqueIdGenerator {
    static long current= System.currentTimeMillis();
    static public synchronized long get(){
        return current++;
    }
}
