package org.joverseer.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;

import org.joverseer.preferences.PreferenceRegistry;
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
            		//TODO: I18N
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
    
    public boolean shouldDeleteRecentGame(RecentGameInfo rg) {
    	if(rg.getDate().equals("unknown")) return true;
    	Date dateToCompare = Date.from(ZonedDateTime.now().minusMonths(1).toInstant());
    	
    	SimpleDateFormat formatter = new SimpleDateFormat("MMMMMMMMM dd yyyy", Locale.ENGLISH);

    	try {
			Date date = formatter.parse(rg.getDate());
			if(date.before(dateToCompare)) return true;
			
		} catch (ParseException e) {}
    	
    	return false;
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
        
        if (PreferenceRegistry.instance().getPreferenceValue("general.autoDeleteRecentGames").equals("yes")) {
	        ArrayList<RecentGameInfo> rgisToRemove = new ArrayList<RecentGameInfo>();
	        for (RecentGameInfo rg : rgis) {
	        	if(shouldDeleteRecentGame(rg)) rgisToRemove.add(rg);
	        }
	        rgis.removeAll(rgisToRemove);
	        
	        rgiStr = getRecentGameInfoString(rgis);
        }
        
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
    
    public class DateStrComparator implements Comparator<RecentGameInfo> {
		@Override
		public int compare(RecentGameInfo o1, RecentGameInfo o2) {
			// TODO Auto-generated method stub
			if(o2.getDate().equals("unknown")) return 1;
			if(o1.getDate().equals("unknown")) return -1;
			
			String[] split1 = o1.getDate().split("\\s+");
			String[] split2 = o2.getDate().split("\\s+");
			
			String o1Date = split1[2];
			String o2Date = split2[2];
			if (!o1Date.equals(o2Date)) return Integer.valueOf(o2Date).compareTo(Integer.valueOf(o1Date));
			
			o1Date = split1[0];
			o2Date = split2[0];
			if (!o1Date.equals(o2Date)) return Month.valueOf(o2Date).compareTo(Month.valueOf(o1Date));
			
			o1Date = split1[1];
			o2Date = split2[1];
			return Integer.valueOf(o2Date).compareTo(Integer.valueOf(o1Date));
		}
    }
}
