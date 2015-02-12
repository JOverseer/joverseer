/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.middleearthgames.updater;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkListener;

import org.springframework.binding.form.FormModel;
import org.springframework.core.io.Resource;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 *
 * @author Thomas Otero H3R3T1C productised for jOverseer and UAC stuff by Dave Spring.
 */
@SuppressWarnings("serial")
public class UpdateInfo extends JFrame{
    private JEditorPane infoPane;
    private JScrollPane scp;
    private JButton ok;
    private JButton cancel;
    private JPanel pan1;
    private JPanel pan2;

    public UpdateInfo(String info) {
        initComponents();
		this.infoPane.setPreferredSize(new Dimension(800, 600));
		try {
			String text = info;
			String[] lines = text.split("\n");
			String result = "";
			for (String l : lines) {
				if (l.trim().equals(""))
					continue;
				if (l.startsWith("VERSION"))
					l = "<hr/>" + "<b>" + l + "</br>";
				if (l.trim().startsWith("---") && l.trim().endsWith("---"))
					continue;
				int startSpaces = 0;
				int startTabs = 0;
				int j = 0;
				while (l.length() > j && (l.charAt(j) == '\t' || l.charAt(j) == ' ')) {
					if (l.charAt(j) == '\t')
						startTabs++;
					if (l.charAt(j) == ' ')
						startSpaces++;
					j++;
				}
				int index = 0;
				if (startTabs > 0 || startSpaces > 0)
					index++;
				index += startTabs + startSpaces / 4;
				String ll = "";
				// for (j=0; j<index; j++) {
				ll += "<td colspan=" + (index + 1) + ">";
				// }
				ll += "<td colspan=" + (10 - index) + ">" + l.trim() + "</td>";
				result += "<tr>" + ll + "</tr>";
			}
			result = "<html><body><table style='font-family:Tahoma; font-size:11pt' border=0 cellspacing=0 cellpadding=0>" + result + "</table></font></body></html>";
			this.infoPane.setText(result);
			this.infoPane.select(0, 1);
			scp.setPreferredSize(new Dimension(750, 500));
		} catch (Exception exc) {
			System.out.println(exc.getMessage());
		}
    }

    private void initComponents() {

        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("New Update Found");
        this.pan1 = new JPanel();
        this.pan1.setLayout(new BorderLayout());

        this.pan2 = new JPanel();
        this.pan2.setLayout(new FlowLayout());

        this.infoPane = new JEditorPane();
        this.infoPane.setContentType("text/html");

        this.scp = new JScrollPane();
        this.scp.setViewportView(this.infoPane);

        this.ok = new JButton("Update");
        this.ok.addActionListener( new ActionListener(){

            @Override
			public void actionPerformed(ActionEvent e) {
                update();
            }
        });

        this.cancel = new JButton("Cancel");
        this.cancel.addActionListener( new ActionListener(){

            @Override
			public void actionPerformed(ActionEvent e) {
                UpdateInfo.this.dispose();
            }
        });
        this.pan2.add(this.ok);
        this.pan2.add(this.cancel);
        this.pan1.add(this.pan2, BorderLayout.SOUTH);
        this.pan1.add(this.scp, BorderLayout.CENTER);
        this.add(this.pan1);
        pack();
        show();
        this.setSize(300, 200);
    }
    //some serious jiggery-pokery here.
    //on windows Vista, Windows7, 8 etc..
    //when installed under Program Files... you need admin access. and UAC
    // The only way to run any java at elevated privileges is:
    // wrap update.jar in .exe with a manifest asking for raised security
    // (using launch4j) to make jOverseerUpdater.exe
    //The only way to trigger the UAC to appear is to run ShellExecute
    //which we do via windows scripting host (vbscript) to avoid windows
    //specific bindings/JNI. (just Process.exec("jOverseerUpdater.exe") won't do it.
    // clear?
    private void update()
    {
    	String[] run;
    	String[] runJava = {"java","-jar","update/update.jar"};
        String[] runWindows = {"cscript.exe",System.getProperty("java.io.tmpdir")+"\\runJoverseerUpdater.vbs"};
        
        run = runJava;
    	if (System.getProperty("os.name").startsWith("Windows")) {
    		boolean writesok = false;
    		File file = new File("deleteme.txt");
			file.delete();
    		try {
    			file.createNewFile();
    			writesok = true;
    		} catch (IOException e) {}
    		file.delete();
	        System.out.println("windows");
    		if (!writesok) {
    	        System.out.println("needs elevation");
    			// use the version with elevated privileges
    			run = runWindows;
    			File bat = new File(System.getProperty("java.io.tmpdir")+"\\runJoverseerUpdater.vbs");
    			bat.delete();
    	        System.out.println(bat);
    			try {
    				FileWriter fw = new FileWriter(bat);
    				String location = System.getProperty("user.dir");
    				//location = location.replace('/','\\');
					fw.write("Set objShell = CreateObject(\"Shell.Application\")");
					fw.write("\r\n");
					// ShellExecute (file,args,directory,operation,show)
					fw.write("objShell.ShellExecute \""+ location + "\\jOverseerUpdater.exe\" , \"\",\""+location+"\",\"runas\"");
	    			fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    		}
    	}
        ProcessBuilder pb = new ProcessBuilder(run);
        System.out.println(run[0]);
        System.out.println(run[1]);
        System.out.println(System.getProperty("user.dir"));
        System.out.println(pb.directory());
        try {
        	pb.start();
        	//Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}
