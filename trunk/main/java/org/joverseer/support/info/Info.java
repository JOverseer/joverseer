package org.joverseer.support.info ;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

/**
 * Basic class for background game information (such as generic order costs, movement costs, etc).
 * It basically serves to store tabular data that is accessed from the program.
 * The data resides in csv files (resources)
 * 
 *  Each info is identified by a string key.
 * 
 * @author Marios Skounakis
 *
 */
public class Info {
    public String key;
    String resourcePath;
    
    ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>(); 
    
    public ArrayList<String> getColumnHeaders() {
        return values.get(0);
    }
    
    public ArrayList<String> getRowHeaders() {
        ArrayList<String> ret = new ArrayList<String>();
        for (ArrayList<String> l : values) {
            ret.add(l.get(0));
        }
        return ret;
    }
    
    public int getColumnIdx(String header) {
        return getColumnHeaders().indexOf(header);
    }
    
    public int getRowIdx(String header) {
        return getRowHeaders().indexOf(header);
    }
    
    public String getValue(String h1, String h2) {
        int i = getColumnIdx(h1);
        int j = 0;
        if (i < 0) {
            i = getColumnIdx(h2);
            if (i < 0) return null;
            j = getRowIdx(h1);
            if (j < 0) return null;
        } else {
            j = getRowIdx(h2);
            if (j < 0) return null;
        }
        return getValue(j, i);
    }
    
    public String getValue(int row, int col) {
        return values.get(row).get(col);
    }

    
    public String getKey() {
        return key;
    }

    
    public void setKey(String key) {
        this.key = key;
    }

    
    public String getResourcePath() {
        return resourcePath;
    }

    
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
        load();
    }
    
    protected void load() {
        Resource res = Application.instance().getApplicationContext().getResource(this.resourcePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(res.getInputStream()));

            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(";");
                ArrayList<String> l = new ArrayList<String>();
                l.addAll(Arrays.asList(parts));
                values.add(l);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            values.clear();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception exc) {};
            }
        }
    }
    
    
    
}
