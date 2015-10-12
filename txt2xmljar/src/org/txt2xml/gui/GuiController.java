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

import java.io.File;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.scopemvc.controller.basic.BasicController;
import org.scopemvc.core.Control;
import org.scopemvc.core.ControlException;
import org.scopemvc.util.UIStrings;

/**
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
public class GuiController extends BasicController {
    
    @SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(GuiController.class.getName());
    
    public static final String PROCESS = "Process";
    public static final String LOAD_SOURCE = "LoadSource";
    public static final String LOAD_CONFIG = "LoadConfig";
    public static final String SAVE_SOURCE = "SaveSource";
    public static final String SAVE_CONFIG = "SaveConfig";

    private File currentDirectory;

    GuiController() {
        setView(new GuiView());
        
        File sourceFile = new File(getClass().getResource("/org/txt2xml/gui/sample.txt").getFile());
        File configFile = new File(getClass().getResource("/org/txt2xml/gui/sample.xml").getFile());
        GuiModel guiModel = new GuiModel();
        guiModel.loadSourceFromFile(sourceFile);
        guiModel.loadConfigFromFile(configFile);
        setModel(guiModel);
        
        showView();
    }
    
    @Override
	protected void doHandleControl(Control inControl) throws ControlException {
        if (inControl.matchesID(PROCESS)) {
            doProcess();
        } else if (inControl.matchesID(LOAD_SOURCE)) {
            doLoadSource();
        } else if (inControl.matchesID(LOAD_CONFIG)) {
            doLoadConfig();
        } else if (inControl.matchesID(SAVE_SOURCE)) {
            doSaveSource();
        } else if (inControl.matchesID(SAVE_CONFIG)) {
            doSaveConfig();
        }
    }
    
    private void doProcess() {
        ((GuiView)getView()).requestFocus();    // hack because STextArea syncs to model on focus lost and we might not have lost focus if got here with menu accelerator
        ((GuiModel)getModel()).process();
    }

    private void doLoadSource() {
        JFileChooser chooser = new JFileChooser(this.currentDirectory);
        chooser.setDialogTitle("Choose source text file");
        int returnValue = chooser.showOpenDialog((GuiView)getView());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.currentDirectory = chooser.getCurrentDirectory();
            ((GuiModel)getModel()).loadSourceFromFile(chooser.getSelectedFile());
        }
    }
    
    private void doLoadConfig() {
        JFileChooser chooser = new JFileChooser(this.currentDirectory);
        chooser.setDialogTitle("Choose txt2xml config file");
        int returnValue = chooser.showOpenDialog((GuiView)getView());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.currentDirectory = chooser.getCurrentDirectory();
            ((GuiModel)getModel()).loadConfigFromFile(chooser.getSelectedFile());
        }
    }
    
    private void doSaveSource() {
        ((GuiModel)getModel()).saveSourceToFile();
    }
    
    private void doSaveConfig() {
        ((GuiModel)getModel()).saveConfigToFile();
    }

    public static void main(String[] args) {
        UIStrings.setPropertiesName("org.txt2xml.gui.uistrings");
        new GuiController().startup();
    }
}
