/*
 * txt2xml: convert arbitrary text into XML.
 * Copyright (c) 2002, Steve Meyfroidt
 * All rights reserved.
 * Email: smeyfroi@users.sourceforge.net
 * 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name "txt2xml" nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package org.txt2xml.gui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scopemvc.core.ModelChangeTypes;
import org.scopemvc.core.Selector;
import org.scopemvc.model.basic.BasicModel;
import org.txt2xml.config.ProcessorFactory;
import org.txt2xml.core.Processor;
import org.txt2xml.driver.StreamDriver;

/**
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class GuiModel extends BasicModel {
    
    private static final Logger LOG = Logger.getLogger(GuiModel.class.getName());
    
    public static final Selector SOURCE_TEXT = Selector.fromString("sourceText");
    public static final Selector CONFIG_TEXT = Selector.fromString("configText");
    public static final Selector DEST_TEXT = Selector.fromString("destText");
    public static final Selector ERROR_MESSAGE = Selector.fromString("errorMessage");
    
    private File sourceFile;
    private File configFile;
    
    private String sourceText;
    private String configText;
    private String destText;
    
    private String errorMessage;
    
    public GuiModel() {
    }
    
    // ----------------------------- Accessors
    
    public String getSourceText() {
        return this.sourceText;
    }
    
    /**
     * Set a new source text and then {@link #process()}.
     * @param sourceText the text 
     */
    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
        fireModelChange(ModelChangeTypes.VALUE_CHANGED, SOURCE_TEXT);
		process();
    }
    
    public String getConfigText() {
        return this.configText;
    }
    
    /**
     * Set a new config text and then {@link #process()}.
     * @param configText the text 
     */
    public void setConfigText(String configText) {
        this.configText = configText;
        fireModelChange(ModelChangeTypes.VALUE_CHANGED, CONFIG_TEXT);
        process();
    }
    
    public String getDestText() {
        return this.destText;
    }
    
    protected void setDestText(String destText) {
        this.destText = destText;
        fireModelChange(ModelChangeTypes.VALUE_CHANGED, DEST_TEXT);
    }

    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        fireModelChange(ModelChangeTypes.VALUE_CHANGED, ERROR_MESSAGE);
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    // --------------------------- Applying Processor

    /**
     * Create a Processor from the current config text and
     * apply it to the current source text, putting the result
     * into the dest text. On any error, put the error message
     * in errorMessage text.
     */    
    public void process() {
        if (LOG.isLoggable(Level.FINER)) LOG.finer("Processing");
        setErrorMessage("");
        try {
            // Create the Processor from config text
            Reader reader = new StringReader(getConfigText());
            Processor processor = ProcessorFactory.getInstance().createProcessor(reader);
    
            // Process the source text
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            StreamDriver driver = new StreamDriver(processor);
            driver.useDebugOutputProperties();
            driver.generateXmlDocument(this.sourceText, outStream);
            outStream.close();
            
            // Put the result into dest text
            setDestText(outStream.toString("UTF-8"));
            if (LOG.isLoggable(Level.FINER)) LOG.finer("Done processing");
        } catch(Exception e) {
            setErrorMessage(e.getLocalizedMessage());
        }
    }
    
    // --------------------------- Loading from files.
    
    public void loadSourceFromFile(File file) {
        try {
            this.sourceFile = file;
            setSourceText(loadTextFromFile(this.sourceFile));
        } catch(Exception e) {
            setErrorMessage(e.getLocalizedMessage());
        }
    }
    
    public void loadConfigFromFile(File file) {
        try {
            this.configFile = file;
            setConfigText(loadTextFromFile(this.configFile));
        } catch(Exception e) {
            setErrorMessage(e.getLocalizedMessage());
        }
    }
    
    /**
     * Read contents of a File into a String.
     * 
     * @param File the file to read as a String.
     * @return String the contents of the passed File read
     * as bytes and turned into a String (ASCII, not Unicode).
     */
    private String loadTextFromFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
        int length = (int)file.length();
        //byte[] content = new byte[length];
        char[] contentC = new char[length];
        //stream.read(content);
        isr.read(contentC);
        isr.close();
        stream.close();
        return new String(contentC);
    }
    
    // --------------------------- Saving to files.
    
    public void saveSourceToFile() {
        try {
            saveTextToFile(this.sourceFile, getSourceText());
        } catch(Exception e) {
            setErrorMessage(e.getLocalizedMessage());
        }
    }
    
    public void saveConfigToFile() {
        try {
            saveTextToFile(this.configFile, getConfigText());
        } catch(Exception e) {
            setErrorMessage(e.getLocalizedMessage());
        }
    }
    
    /**
     * Read contents of a File into a String.
     * 
     * @param File the file to read as a String.
     * @return String the contents of the passed File read
     * as bytes and turned into a String (ASCII, not Unicode).
     */
    private void saveTextToFile(File file, String text) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(text);
        writer.close();
    }
}
