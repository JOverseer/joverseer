package org.joverseer.tools;


public class UniqueIdGenerator {
    static long current= System.currentTimeMillis();
    static public synchronized long get(){
        return current++;
    }
}
