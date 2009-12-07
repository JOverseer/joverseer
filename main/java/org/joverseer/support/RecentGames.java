package org.joverseer.support;

import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.joverseer.ui.JOverseerJIDEClient;

/**
 * Support for remembering the recent games the user has accessed
 * Recent games are stored in a user preference called "recentGames"
 * as a string with the format:
 * game1No!game1File#game2No!game2File#...
 * 
 * @author Marios Skounakis
 */
public class RecentGames {
    public ArrayList<RecentGameInfo> getRecentGameInfo(String str) {
        ArrayList<RecentGameInfo> res = new ArrayList<RecentGameInfo>();
        if (str != null) {
            String[] games = str.split("#");
            for (String game : games) {
                int i = game.indexOf("!");
                if (i==-1) continue;
                RecentGameInfo rgi = new RecentGameInfo();
                rgi.setNumber(Integer.parseInt(game.substring(0, i)));
                rgi.setFile(game.substring(i+1).replace("!-!", "#"));
                res.add(rgi);
            }
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
            res += rgi.getNumber() + "!" + rgi.getFile().replace("#", "!-!");
        }
        return res;
    }
    
    public void updateRecentGameInfoPreferenceWithGame(int number, String file) {
        String rgiStr = Preferences.userNodeForPackage(JOverseerJIDEClient.class).get("recentGames", null);
        ArrayList<RecentGameInfo> rgis = getRecentGameInfo(rgiStr);
        RecentGameInfo rgi = new RecentGameInfo();
        rgi.setNumber(number);
        rgi.setFile(file);

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
        while (rgis.size() > 5) {
            rgis.remove(rgis.size() - 1);
        }
        rgiStr = getRecentGameInfoString(rgis);
        Preferences.userNodeForPackage(JOverseerJIDEClient.class).put("recentGames", rgiStr);
    }
    
    public class RecentGameInfo {
        int number;
        String file;
        
        public int getNumber() {
            return number;
        }
        
        public void setNumber(int number) {
            this.number = number;
        }

        
        public String getFile() {
            return file;
        }

        
        public void setFile(String file) {
            this.file = file;
        }
        
        
    }
}
