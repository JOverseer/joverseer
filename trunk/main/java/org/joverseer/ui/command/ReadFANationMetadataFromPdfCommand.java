package org.joverseer.ui.command;

import java.io.File;
import java.io.FileFilter;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.pdf.TurnPdfReader;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;


public class ReadFANationMetadataFromPdfCommand extends ActionCommand {
    
    public ReadFANationMetadataFromPdfCommand() {
        super("readFANationMetadataFromPdfCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        final Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        Preferences prefs = Preferences.userNodeForPackage(OpenXmlDir.class);
        String lastDir = prefs.get("importDir", null);
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }
        if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            // list pdfs
            final File file = fileChooser.getSelectedFile();
            class PdfFileFilter implements FileFilter {
                public boolean accept(File file) {
                    return !file.isDirectory() && file.getName().endsWith(".pdf");
                }
            }
            //files = file.listFiles(new XmlAndPdfFileFilter());
            File[] files = file.listFiles(new PdfFileFilter());
            // loop over files, read nation names and SNAs
            for (File f : files) {
                try {
                    TurnPdfReader reader = new TurnPdfReader(g, f.getAbsolutePath());
                    String pdfcontents = reader.parsePdf();
                    int idx1 = pdfcontents.indexOf("Special Nation Abilities :");
                    int idx2 = pdfcontents.indexOf("Game #", idx1);
                    //parse SNAs
                    String regex = "#(\\d{2}) \\w+";
                    Pattern p = Pattern.compile(regex);
                    Matcher matcher = p.matcher(pdfcontents.subSequence(idx1, idx2));
                    while (matcher.matches()) {
                        System.out.println("SNA: " + matcher.group(1));
                    }
                    
                }
                catch (Throwable exc) {
                    //TODO
                }
                
            }
        }
    };
    
}
