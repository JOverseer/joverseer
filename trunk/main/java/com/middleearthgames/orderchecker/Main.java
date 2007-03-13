// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces fieldsfirst safe 
// Source File Name:   Main.java

package com.middleearthgames.orderchecker;

import com.middleearthgames.orderchecker.gui.ExtraInfoDlg;
import com.middleearthgames.orderchecker.gui.OCDialog;
import com.middleearthgames.orderchecker.io.Data;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Vector;
import javax.swing.*;

// Referenced classes of package com.middleearthgames.orderchecker:
//            SpellList, ArtifactList, Nation, PopCenter, 
//            Map, Ruleset

public class Main
{

    public static final boolean DEBUG = false;
    private static final String dataFile = "orderchecker.dat";
    public static Main main;
    public static JFrame mainFrame;
    private Nation nation;
    private Map map;
    private Ruleset ruleset;
    private SpellList spells;
    private ArtifactList artifacts;
    private Data data;
    private final OCDialog window;

    public static void main(String argv[])
    {
        try
        {
            String systemLF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(systemLF);
        }
        catch(Exception ex) { }
        mainFrame = new JFrame(getVersionString());
        main = new Main();
        mainFrame.addWindowListener(((java.awt.event.WindowListener) (new WindowAdapter() {

            public void windowClosing(WindowEvent e)
            {
                Main.main.getData().setWindowSize(0, Main.mainFrame.getSize());
                Main.main.getData().setWindowLocation(0, Main.mainFrame.getLocationOnScreen());
                Main.main.saveData();
                System.exit(0);
            }

        }
)));
        mainFrame.getContentPane().add("Center", ((java.awt.Component) (main.getWindow())));
        mainFrame.pack();
        java.awt.Dimension size = main.getData().getWindowSize(0);
        mainFrame.setSize(size);
        mainFrame.setLocation(main.getData().getWindowLocation(0));
        mainFrame.setVisible(true);
    }

    public static String getVersionString()
    {
        return "OrderChecker, version 2.4";
    }

    public static String getVersionDate()
    {
        return "April 10, 2005";
    }

    public Main()
    {
        nation = null;
        map = null;
        ruleset = null;
        spells = new SpellList();
        artifacts = new ArtifactList();
        readData();
        window = new OCDialog(mainFrame, data);
    }
    
    //mscoon
    public Main(boolean useMscoonVersion, Data data) {
        nation = null;
        map = null;
        ruleset = null;
        spells = new SpellList();
        artifacts = new ArtifactList();
        this.data = data;
        window = new OCDialog(new JPanel(), this.data);
    }

    public OCDialog getWindow()
    {
        return window;
    }

    public Nation getNation()
    {
        return nation;
    }

    public Map getMap()
    {
        return map;
    }

    public Ruleset getRuleSet()
    {
        return ruleset;
    }

    public SpellList getSpellList()
    {
        return spells;
    }

    public ArtifactList getArtifactList()
    {
        return artifacts;
    }

    public Data getData()
    {
        return data;
    }

    public void setNation(Nation nation)
    {
        this.nation = nation;
    }

    public void setMap(Map map)
    {
        this.map = map;
    }

    public void setRuleSet(Ruleset ruleset)
    {
        this.ruleset = ruleset;
    }

    private void saveData()
    {
        try
        {
            FileOutputStream file = new FileOutputStream("orderchecker.dat");
            ObjectOutputStream objFile = new ObjectOutputStream(((java.io.OutputStream) (file)));
            data.writeObject(objFile);
            objFile.close();
            file.close();
        }
        catch(Exception ex)
        {
            displayErrorMessage("Could not save the data file! (orderchecker.dat)");
        }
    }

    public void readData()
    {
        try
        {
            FileInputStream file = new FileInputStream("orderchecker.dat");
            ObjectInputStream objFile = new ObjectInputStream(((java.io.InputStream) (file)));
            data = new Data();
            data.readObject(objFile);
            objFile.close();
            file.close();
        }
        catch(Exception ex)
        {
            data = new Data();
        }
    }

    public String locationStr(int location)
    {
        if(location == 0)
        {
            return "Hostage";
        }
        PopCenter pc = nation.findPopulationCenter(location);
        if(pc != null)
        {
            return pc.toString();
        }
        String locStr = String.valueOf(location);
        if(locStr.length() == 3)
        {
            return "0" + locStr;
        } else
        {
            return locStr;
        }
    }

    public static void displayErrorMessage(String message)
    {
        JOptionPane msg = new JOptionPane(((Object) (message)), 0, -1);
        JDialog msgDlg = msg.createDialog(((java.awt.Component) (mainFrame)), "Error!");
        msgDlg.show();
    }

    public void processOrders()
    {
        String error = nation.implementPhase(1, this);
        if(error != null)
        {
            displayErrorMessage(error);
            return;
        }
        boolean done;
        int safety;
        Vector requests = nation.getArmyRequests();
        new ExtraInfoDlg(mainFrame, nation, data, requests);
        nation.processArmyRequests(requests);
        done = false;
        safety = 0;
        do {
            if(done || safety >= 20)
            {
                break;
            }
            safety++;
            error = nation.implementPhase(2, this);
            if(error != null)
            {
                displayErrorMessage(error);
                return;
            }
            requests = nation.getInfoRequests();
            if(requests.size() > 0)
            {
                new ExtraInfoDlg(mainFrame, nation, data, requests);
            }
            done = nation.isProcessingDone();
        } while (true);
        if(safety == 20)
        {
            throw new RuntimeException("Maximum state iterations reached.");
        }
        try {
            window.createResultsTree();
        }
        catch (Exception ex) {
            StringBuffer desc = new StringBuffer();
            desc.append(getVersionString() + ", " + getVersionDate() + "\n\n");
            desc.append("Critical error encountered...please send the  contents\nof this message plus your XML and order file to: bernout1@adelphia.net\n\n");
            String message = ex.getMessage();
            desc.append(message + "\n");
            StackTraceElement trace[] = ex.getStackTrace();
            for(int i = 0; i < trace.length && i < 10; i++)
            {
                desc.append(trace[i] + "\n");
            }
    
            displayErrorMessage(desc.toString());
        }
    }

}
