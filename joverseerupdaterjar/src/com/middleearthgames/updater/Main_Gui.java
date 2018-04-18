
package com.middleearthgames.updater;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 *
 * @author Dave Spring productized from the original by Thomas Otero (H3R3T1C)
 * 
 * package just this class into 'update.jar' which should live in a subdirectory 'update'
 * under the main application.
 * Don't forget to package 'update/update.jar' into the download zip bundle.
 * 
 * com.middleearthgames.update.UpdateChecker is used to check an RSS feed to determine the latest
 * version and display the changelog
 * When the new version is to be downloaded and run, UpdateChecker will invoke 'update/update.jar'
 * and close joverseer.
 * 
 * This class is thus called in a separate process to download the zip bundle from the indicated URL,
 * unzipped and run.
 */
@SuppressWarnings("serial")
public class Main_Gui extends JFrame{

//	private String latestUpdateZip = "latestupdate.zip";
	private String targetjar = "joverseer.jar";
	private String downloadPath;
	
    private Thread worker;
    private final String root = "update/";

    private JTextArea outText;
    private JButton cancel;
    private JButton launch;
    private JScrollPane sp;
    private JPanel pan1;
    private JPanel pan2;

     public Main_Gui(String downloadPath) {
        initComponents();
        outText.setText("3 Contacting Download Server...");
        this.downloadPath = downloadPath;
        download();
    }
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pan1 = new JPanel();
        pan1.setLayout(new BorderLayout());

        pan2 = new JPanel();
        pan2.setLayout(new FlowLayout());

        outText = new JTextArea();
        sp = new JScrollPane();
        sp.setViewportView(outText);
        
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
        	launch = new JButton("Close and Launch App");
        } else { 
        	launch = new JButton("Launch App");
        }
        
        launch.setEnabled(false);
        launch.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                launch();
            }
        });
        pan2.add(launch);

        cancel = new JButton("Close");
        cancel.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        pan2.add(cancel);
        pan1.add(sp,BorderLayout.CENTER);
        pan1.add(pan2,BorderLayout.SOUTH);

        add(pan1);
        pack();
        this.setSize(500, 400);
    }

    private void download()
    {
        worker = new Thread(
        new Runnable(){
            public void run()
            {
                try {
                	File file = downloadFile(getDownloadLinkFromHost());
                    unzip(file);
                    copyFiles(new File(root),new File("").getAbsolutePath());
                    cleanup(file);
                    launch.setEnabled(true);
                    logInfo("\nUpdate Finished!");
                } catch (Exception ex) {
                	logInfo("\n"+ex.getMessage());
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occured while performing update!");
                }
            }
        });
        worker.start();
    }
    private void launch()
    {
		// if Mac then don't launch from inside update.jar, as we running as root and the preferences are different.
    	if (!(System.getProperty("os.name").startsWith("Mac OS X"))) {
			String[] run = { "java", "-Xmx512M", "-jar", targetjar };
			try {
				Runtime.getRuntime().exec(run);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
    	}
        System.exit(0);
    }
    private void cleanup(File file)
    {
    	logInfo("\nPerforming clean up...");
        file.delete();
        remove(new File(root));
        // don't actually delete the update directory..cos we want the update.jar.
        //new File(root).delete();
    }
    private void remove(File f)
    {
        File[]files = f.listFiles();
        for(File ff:files)
        {
            if(ff.isDirectory())
            {
                remove(ff);
                ff.delete();
                logDebug("\n Deleted "+ff.toString());
            }
            else
            {
            	// leave the update.jar behind as the update checker expects it here.
            	if (!ff.getName().equalsIgnoreCase("update.jar")) {
            		ff.delete();
                	logDebug("\n Deleted "+ff.toString());
            	}
            }
        }
    }
    private void copyFiles(File f,String dir) throws IOException
    {
        File[]files = f.listFiles();
        for(File ff:files)
        {
            if(ff.isDirectory()){
                new File(dir+"/"+ff.getName()).mkdir();
                copyFiles(ff,dir+"/"+ff.getName());
            }
            else
            {
                copy(ff.getAbsolutePath(),dir+"/"+ff.getName());
            }

        }
    }
    public void copy(String srFile, String dtFile) throws FileNotFoundException, IOException{

          File f1 = new File(srFile);
          File f2 = new File(dtFile);

          InputStream in = new FileInputStream(f1);

          OutputStream out = new FileOutputStream(f2);

          byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
          }
          in.close();
          out.close();
      }
    private void unzip(File file) throws IOException
    {
         int BUFFER = 2048;
         BufferedOutputStream dest = null;
         BufferedInputStream is = null;
         ZipEntry entry;
         ZipFile zipfile = new ZipFile(file);
         Enumeration e = zipfile.entries();
         (new File(root)).mkdir();
         while(e.hasMoreElements()) {
            entry = (ZipEntry) e.nextElement();
            logInfo("\nExtracting: " +entry);
            if(entry.isDirectory())
                (new File(root+entry.getName())).mkdir();
            else{
                (new File(root+entry.getName())).createNewFile();
                is = new BufferedInputStream
                  (zipfile.getInputStream(entry));
                int count;
                byte data[] = new byte[BUFFER];
                FileOutputStream fos = new
                  FileOutputStream(root+entry.getName());
                dest = new
                  BufferedOutputStream(fos, BUFFER);
                while ((count = is.read(data, 0, BUFFER))
                  != -1) {
                   dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
                is.close();
            }
         }
         zipfile.close();
    }
    private File downloadFile(String link) throws MalformedURLException, IOException
    {
        URL url = new URL(link);
        File file = File.createTempFile("joverseerupdate", ".zip");
        logDebug("\nDownloading "+link+" to "+file.toString());
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        long max = conn.getContentLength();
        logInfo("\n"+"Downloading file...\nUpdate Size(compressed): "+max+" Bytes");
        BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[32 * 1024];
        int bytesRead = 0;
//        int in = 0;
        while ((bytesRead = is.read(buffer)) != -1) {
//            in += bytesRead;
            fOut.write(buffer, 0, bytesRead);
        }
        fOut.flush();
        fOut.close();
        is.close();
        logInfo("\nDownload Complete!");
        return file;
    }
    private String getDownloadLinkFromHost() throws MalformedURLException, IOException, FileNotFoundException
    {
        URL url = new URL(this.downloadPath);

        InputStream html = null;
        String tag = "url";

      	html = url.openStream();

        int c = 0;
        StringBuilder buffer = new StringBuilder("");

        while(c != -1) {
            c = html.read();
        buffer.append((char)c);

        }
        return buffer.substring(buffer.indexOf("<"+tag+">")+5,buffer.indexOf("</"+tag+">"));
    }
    private void logInfo(String message)
    {
        this.outText.setText(this.outText.getText()+message);
    }
    private void logDebug(String message)
    {
        this.outText.setText(this.outText.getText()+message);
    }
    public static void main(String args[]) {
    	String downloadPath = "http://www.middleearthgames.com/software/joverseer/url.html";
    	
    	class UpdateRunnable implements Runnable {
    		private final String downloadPath;
    		public UpdateRunnable(final String downloadPath) {
    			super();
    			this.downloadPath = downloadPath;
    		}
            @Override
			public void run() {
                new Main_Gui(this.downloadPath).setVisible(true);
            }
    		
    	}
    	UpdateRunnable runner;
    	if (args.length >0) {
    		downloadPath = args[0];
            }
    	runner = new UpdateRunnable(downloadPath);
        java.awt.EventQueue.invokeLater(runner);
    }

}
