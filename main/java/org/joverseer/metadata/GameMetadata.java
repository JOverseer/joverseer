package org.joverseer.metadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

/**
 * Holds metadata about the game such as
 * 1. the game type and other game instance stuff
 * 2. information that depends on the game type, such as the hexes, the artifacts, etc
 * 
 * @author Marios Skounakis
 */
public class GameMetadata implements Serializable {
	private static final long serialVersionUID = 8007105168749869584L;
	GameTypeEnum gameType;
    int gameNo;
    int nationNo;
    String additionalNations;
    boolean newXmlFormat = false;

    ArrayList nations = new ArrayList();
    Container hexes = new Container(new String[]{"hexNo"});
    Container artifacts = new Container(new String[]{"no"});
    Container orders = new Container(new String[]{"number"});
    Container characters = new Container(new String[]{"id", "name"});
    Container startDummyCharacters = new Container(new String[]{"id", "name"});
    Container populationCenters = new Container(new String[]{"hexNo"});
    Container nationMapRanges = new Container(new String[]{"nationNo"});
    Container spells = new Container(new String[]{"no"});
    Container armies = new Container(new String[]{"hexNo"});
    
    ArrayList readers = new ArrayList();

    String basePath;

    public GameTypeEnum getGameType() {
        return gameType;
    }

    public void setGameType(GameTypeEnum gameType) {
        this.gameType = gameType;
    }

    public String getAdditionalNations() {
		return additionalNations;
	}

	public void setAdditionalNations(String additionalNations) {
		this.additionalNations = additionalNations;
		if (additionalNations != null) {
			String[] ps = additionalNations.split(",");
			additionalNations = "";
			for (String p : ps) {
				additionalNations = additionalNations + (additionalNations.equals("") ? "" : ",") + p;
			}
		}
	}

	public Collection getHexes() {
        return hexes.getItems();
    }
    
    public Hex getHex(int hexNo) {
    	Hex h = (Hex)hexes.findFirstByProperties(new String[]{"hexNo"}, new Object[]{hexNo});
    	return h;
    }

    public void setHexes(Collection hexes) {
    	for (Object h : hexes) {
    		this.hexes.addItem(h);
    	}
    }

    public ArrayList getNations() {
        return nations;
    }

    public void setNations(Collection nations) {
    	this.nations.clear();
        this.nations.addAll(nations);
    }

    public void load() throws IOException, MetadataReaderException {
        for (MetadataReader r : (Collection<MetadataReader>)getReaders()) {
            r.load(this);
        }
    }

    public ArrayList getReaders() {
        return readers;
    }

    public void setReaders(ArrayList readers) {
        this.readers = readers;
    }

    public int getGameNo() {
        return gameNo;
    }

    public void setGameNo(int gameNo) {
        this.gameNo = gameNo;
    }
    
    

    public Container getStartDummyCharacters() {
		return startDummyCharacters;
	}

	public void setStartDummyCharacters(Container startDummyCharacters) {
		this.startDummyCharacters = startDummyCharacters;
	}

	public boolean getNewXmlFormat() {
		return newXmlFormat;
	}

	public void setNewXmlFormat(boolean newXmlFormat) {
		this.newXmlFormat = newXmlFormat;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(getCharacters());
        out.writeObject(getArtifacts());
        out.writeObject(getSpells());
        out.writeObject(getOrders());
        out.writeObject(hexes);
        out.writeObject(getNations());
        out.writeObject(getNationMapRanges());
        out.writeObject(getGameType());
        out.writeObject(getGameNo());
        out.writeObject(getNationNo());
        out.writeObject(getNewXmlFormat());
        out.writeObject(getStartDummyCharacters());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        characters = (Container)in.readObject();
        artifacts = (Container)in.readObject();
        spells = (Container)in.readObject();
        orders = (Container)in.readObject();
        hexes = (Container)in.readObject();
        nations = (ArrayList)in.readObject();
        nationMapRanges = (Container)in.readObject();
        setGameType((GameTypeEnum)in.readObject());
        setGameNo((Integer)in.readObject());
        setNationNo((Integer)in.readObject());
        try {
        	setNewXmlFormat((Boolean)in.readObject());
        }
        catch (Exception e) {
        	// do nothing, this may have not been set
        }
        try {
        	setStartDummyCharacters((Container)in.readObject());
        }
        catch (Exception e) {
        	// do nothing, this may have not been set
        }
    }

    public Nation getNationByNum(int number) {
        for (Nation n : (ArrayList<Nation>)getNations()) {
            if (n.getNumber() == number) {
                return n;
            }
        }
        return null;
    }

    public Nation getNationByName(String name) {
       for (Nation n : (ArrayList<Nation>)getNations()) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }

    public Container getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Container artifacts) {
        this.artifacts = artifacts;
    }

    public Container getPopulationCenters() {
        return populationCenters;
    }

    public void setPopulationCenters(Container populationCenters) {
        this.populationCenters = populationCenters;
    }

    public Container getOrders() {
    	if (orders != null) {
    		OrderMetadata om = (OrderMetadata)orders.findFirstByProperty("number", 225);
    		if (om.getSkillRequirement().equals("MS")) {
    			GameMetadata gm = (GameMetadata)Application.instance().getApplicationContext().getBean("gameMetadata");
    			gm.setGameType(getGameType());
    			
    			try {
    			gm.load();
    			orders = gm.getOrders(); 
    			}
    			catch (Exception exc) {
    				// do nothing
    			}
    		}
    	}
        return orders;
    }

    public void setOrders(Container orders) {
        this.orders = orders;
    }

    public String getBasePath() {
        File f = new File(basePath);
        try {
            if (f.exists()) return f.getCanonicalPath();
            File cd = new File(".");
            String p = cd.getCanonicalPath() + "/" + basePath;
            return p;
        }
        catch (IOException exc) {
            return basePath;
        }
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Container getCharacters() {
        return characters;
    }

    public void setCharacters(Container characters) {
        this.characters = characters;
    }

    public int getNationNo() {
        return nationNo;
    }

    public void setNationNo(int nationNo) {
        this.nationNo = nationNo;
    }



    public Container getNationMapRanges() {
        return nationMapRanges;
    }

    public void setNationMapRanges(Container nationMapRanges) {
        this.nationMapRanges = nationMapRanges;
    }


    
    
    public Container getArmies() {
		return armies;
	}

	public void setArmies(Container armies) {
		this.armies = armies;
	}

	public Container getSpells() {
        return spells;
    }

    
    public void setSpells(Container spells) {
        this.spells = spells;
    }
    
    public Resource getResource(String resourceName) {
        try {
            Resource r = Application.instance().getApplicationContext().getResource("file:///" + getBasePath() + "/" + resourceName);
            new InputStreamReader(r.getInputStream());
            return r;
        }
        catch (Exception exc) {
            try {
                System.out.println(exc.getMessage());
                Resource r = Application.instance().getApplicationContext().getResource("classpath:" + basePath + resourceName);
                new InputStreamReader(r.getInputStream());
                return r;
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
                return null;
            }
        }
    }
}