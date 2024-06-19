package org.joverseer.support;

import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.prefs.Preferences;

import org.joverseer.ui.JOverseerJIDEClient;

/**
 * Support for remembering the recent games the user has accessed
 * Recent games are stored in a user preference called "recentGames"
 * as a string with the format:
 * game1No!game1File#game2No!game2File#...
 * 
 * @author Marios Skounakis
 * 
 * Edited by @author Sam Terrett
 * Now includes when the games turns is due. 
 * New format is
 * game1No!game1File?game1Date#game2No!....
 */
public class RecentGames {
	final private int RECENT_GAME_LIMIT=10;
    public ArrayList<RecentGameInfo> getRecentGameInfo(String str) {
        ArrayList<RecentGameInfo> res = new ArrayList<RecentGameInfo>();
        if (str != null) {
            String[] games = str.split("#");
            for (String game : games) {
                int i = game.indexOf("!");
                int j = game.indexOf("?");
                if (i==-1) continue;
                if (j==-1) j = game.length();
                RecentGameInfo rgi = new RecentGameInfo();
                rgi.setNumber(Integer.parseInt(game.substring(0, i)));
                rgi.setFile(game.substring(i+1, j).replace("!-!", "#"));
                
                if(j != game.length()) {
                	rgi.setDate(game.substring(j+1));
                }
                else {
                	rgi.setDate("unknown");
                }
                
                res.add(rgi);
            }
            Collections.sort(res, new DateStrComparator());
        }
        return res;
    }
    
    public ArrayList<RecentGameInfo> getRecentGameInfo() {
        String rgiStr = Preferences.userNodeForPackage(JOverseerJIDEClient.class).get("recentGames", null);
        return getRecentGameInfo(rgiStr);
    }
    
    public String getRecentGameInfoString(ArrayList<RecentGameInfo> rgis) {
        String res = "";
        for (RecentGameInfo rgi : rgis) {
            if (!res.equals("")) {
                res += "#";
            }
            res += rgi.getNumber() + "!" + rgi.getFile().replace("#", "!-!") + "?" + rgi.getDate();
        }
        return res;
    }
    
    public void updateRecentGameInfoPreferenceWithGame(int number, String file, String date) {
        String rgiStr = Preferences.userNodeForPackage(JOverseerJIDEClient.class).get("recentGames", null);
        ArrayList<RecentGameInfo> rgis = getRecentGameInfo(rgiStr);
        RecentGameInfo rgi = new RecentGameInfo();
        rgi.setNumber(number);
        rgi.setFile(file);
        rgi.setDate(date);

        RecentGameInfo toRemove = null;
        for (RecentGameInfo orgi : rgis) {
            if (orgi.getNumber() == rgi.getNumber()) {
                toRemove = orgi;
            }
        }
        if (toRemove != null) {
            rgis.remove(toRemove);
        }
        rgis.add(0, rgi);
        while (rgis.size() > this.RECENT_GAME_LIMIT) {
            rgis.remove(rgis.size() - 1);
        }
        rgiStr = getRecentGameInfoString(rgis);
        Preferences.userNodeForPackage(JOverseerJIDEClient.class).put("recentGames", rgiStr);
    }
    
    public class RecentGameInfo {
        int number;
        String dueDate;
        String file;
        
        public int getNumber() {
            return this.number;
        }
        
        public void setNumber(int number) {
            this.number = number;
        }

        
        public String getFile() {
            return this.file;
        }

        public void setFile(String file) {
            this.file = file;
        }
        
        
        public void setDate(String date) {
            this.dueDate = date;
        }
        
        public String getDate() {
            return this.dueDate;
        }
                
    }
}
