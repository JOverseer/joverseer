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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.scopemvc.controller.basic.BasicController;
import org.scopemvc.core.Control;
import org.scopemvc.view.swing.SLabel;
import org.scopemvc.view.swing.SMenuItem;
import org.scopemvc.view.swing.SPanel;
import org.scopemvc.view.swing.STextArea;

/**
 * @author <A HREF="mailto:smeyfroi@users.sourceforge.net">Steve Meyfroidt</A>
 */
@SuppressWarnings("serial")
public class GuiView extends SPanel {
    
    public GuiView() {
        setLayout(new GridBagLayout());
        
        SLabel errorLabel = new SLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setSelector(GuiModel.ERROR_MESSAGE);
        add(errorLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0.0, 
                    GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 12, 0), 0, 0));
        
        STextArea sourceTextArea = new STextArea();
        sourceTextArea.setSelector(GuiModel.SOURCE_TEXT);
        add(new JScrollPane(sourceTextArea), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, 
                    GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 8), 0, 0));
        
        STextArea configTextArea = new STextArea();
        configTextArea.setSelector(GuiModel.CONFIG_TEXT);
        add(new JScrollPane(configTextArea), new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, 
                    GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 8), 0, 0));
        
        STextArea destTextArea = new STextArea();
        destTextArea.setEditable(false);
        destTextArea.setSelector(GuiModel.DEST_TEXT);
        add(new JScrollPane(destTextArea), new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, 
                    GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        
        setPreferredSize(new Dimension(640, 480));
    }

    @Override
	public String getTitle() {
        return "txt2xml";
    }

    @Override
	public Control getCloseControl() {
        return new Control(BasicController.EXIT_CONTROL_ID);
    }

    @Override
	public JMenuBar getMenuBar() {
        JMenu menu = new JMenu("File");
        menu.add(new SMenuItem(GuiController.LOAD_SOURCE, this, KeyStroke.getKeyStroke("ctrl O")));
        menu.add(new SMenuItem(GuiController.LOAD_CONFIG, this, KeyStroke.getKeyStroke("ctrl L")));
        menu.add(new SMenuItem(GuiController.SAVE_SOURCE, this, KeyStroke.getKeyStroke("ctrl S")));
        menu.add(new SMenuItem(GuiController.SAVE_CONFIG, this, KeyStroke.getKeyStroke("ctrl L")));
        menu.add(new SMenuItem(GuiController.PROCESS, this, KeyStroke.getKeyStroke("ctrl P")));
        menu.addSeparator();
        menu.add(new SMenuItem(BasicController.EXIT_CONTROL_ID, this, KeyStroke.getKeyStroke("ctrl Q")));

        JMenuBar menubar = new JMenuBar();
        menubar.add(menu);

        return menubar;
    }
}
