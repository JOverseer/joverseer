package org.joverseer.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.views.Messages;

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
                int m = game.indexOf("¬");
                int n = game.indexOf("|");
                if (i==-1) continue;
                if (m == -1 && n == -1) ;
//                if (j==-1) continue;
                if (m==-1) m = game.length();
                if (n==-1) n = game.length();
                RecentGameInfo rgi = new RecentGameInfo();
                rgi.setNumber(Integer.parseInt(game.substring(0, i)));
                rgi.setFile(game.substring(i+1, j).replace("!-!", "#"));
                
                if(j != game.length()) {
                	rgi.setDate(game.substring(j+1, m));
                }
                else {
            		//TODO: I18N
                	rgi.setDate("unknown");
                }                
                
                if(m == game.length() && n == game.length()) {
                	rgi.setSentOrd(true);
                	rgi.setOrdersSentDate(Messages.getString("recentGames.defaultMessageNoOrdersSent"));
                	res.add(rgi);
                	continue;
                }
                
                if(m != game.length()) rgi.setSentOrd(game.substring(m + 1, n).equals("Y"));
                else rgi.setSentOrd(true);
                
                if(n != game.length()) {
                	if(game.substring(n + 1).equals(Messages.getString("recentGames.defaultMessageNoOrdersSent"))) rgi.setOrdersSentDate(Messages.getString("recentGames.defaultMessageNoOrdersSent"));
                	else {
                		String st = game.substring(n + 1);
                		if (st.lastIndexOf(":") == -1) rgi.setOrdersSentDate(st);
                		else rgi.setOrdersSentDate(st.substring(st.lastIndexOf(":") + 2));
                	}
                }
                else rgi.setOrdersSentDate(Messages.getString("recentGames.defaultMessageNoOrdersSent"));
                
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
            String sentOrdBool = rgi.hasSentOrd() ? "Y" : "N";
            String sentOrderDateStr = rgi.getOrdersSentDate() == null ? "null": rgi.getOrdersSentDate();
            res += rgi.getNumber() + "!" + rgi.getFile().replace("#", "!-!") + "?" + rgi.getDate() + "¬" + sentOrdBool + "|" + sentOrderDateStr;
        }
        return res;
    }
    
    public void updateRecentGameInfoPreferenceWithGame(int number, String file, String date, boolean sentOrd, String ordersSentOn) {
        String rgiStr = Preferences.userNodeForPackage(JOverseerJIDEClient.class).get("recentGames", null);
        ArrayList<RecentGameInfo> rgis = getRecentGameInfo(rgiStr);
        RecentGameInfo rgi = new RecentGameInfo();
        rgi.setNumber(number);
        rgi.setFile(file);
        rgi.setDate(date);
        rgi.setSentOrd(sentOrd);
        if(ordersSentOn == null) rgi.setOrdersSentDate(Messages.getString("recentGames.defaultMessageNoOrdersSent"));
        else rgi.setOrdersSentDate(ordersSentOn);

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
        boolean sentOrd;
        String ordersSentDate;
        
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
        
        public void setSentOrd(boolean val) {
        	this.sentOrd = val;
        }
        
        public boolean hasSentOrd() {
        	if(!this.pastDueDate()) return false;
        	return this.sentOrd;
        }
        
        public void setOrdersSentDate(String date) {
            this.ordersSentDate = date;
        }
        
        public String getOrdersSentDate() {
            return this.ordersSentDate;
        }
        
        public String formatOrdersSentDateInf() {
        	if (this.ordersSentDate.equals(Messages.getString("recentGames.defaultMessageNoOrdersSent"))) return "Orders have not been sent for this turn (remember to save)";

            if(this.pastDueDate()) return ("Last Orders Sent: " + this.ordersSentDate);
            else return "Import new results.";
        }
        
        public boolean pastDueDate() {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy").withLocale(Locale.ENGLISH);
            LocalDate dueDateD = LocalDate.parse(toTitleCase(this.dueDate), formatter);
            
            LocalDate now = LocalDate.now();
            
            return now.isEqual(dueDateD) || now.isBefore(dueDateD);
        }
        
        public String toTitleCase(String input) {
            if (input == null || input.isEmpty()) return input;
            return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
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
			if (!o1Date.equals(o2Date)) return Integer.valueOf(o1Date).compareTo(Integer.valueOf(o2Date));
			
			o1Date = split1[0];
			o2Date = split2[0];
			if (!o1Date.equals(o2Date)) return Month.valueOf(o1Date).compareTo(Month.valueOf(o2Date));
			
			o1Date = split1[1];
			o2Date = split2[1];
			return Integer.valueOf(o1Date).compareTo(Integer.valueOf(o2Date));
		}
    }
}
