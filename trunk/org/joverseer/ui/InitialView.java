/*
 * Copyright 2002-2006 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.joverseer.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.form.*;
import org.springframework.richclient.dialog.*;
import org.springframework.binding.form.*;


/**
 * This class defines the initial view to be presented in the archetypeapplication. It is
 * constructed automatically by the platform and configured according to the bean
 * specification in the application context.
 * 
 * @author Larry Streepy
 * 
 */
public class InitialView extends AbstractView  implements ActionListener {

    /**
     * Create the actual UI control for this view. It will be placed into the window
     * according to the layout of the page holding this view.
     */
    protected JComponent createControl() {
        // In this view, we're just going to use standard Swing to place a
        // few controls.


        JLabel lblMessage = getComponentFactory().createLabel("initialView.message");
        lblMessage.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JButton btnTest = getComponentFactory().createButton("Test");
        btnTest.setBorder(BorderFactory.createBevelBorder(5, Color.LIGHT_GRAY, Color.DARK_GRAY));
        btnTest.addActionListener(this);
        
        
        JPanel panel = getComponentFactory().createPanel(new BorderLayout());
        panel.add(lblMessage);
        panel.add(btnTest);

        return panel;
    }
    
    public void actionPerformed(ActionEvent e) {
        ArrayList customers = new ArrayList();
        customers.add(new Customer("marios", "s"));
        customers.add(new Customer("f", "p"));

        final FormModel model = FormModelHelper.createFormModel(customers);
//        EditPopulationCenter form = new EditPopulationCenter(model);
//
//        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(new FormBackedDialogPage(form), getWindowControl()) {
//            protected void onAboutToShow() {
//            }
//
//            protected boolean onFinish() {
//                model.commit();
//                return true;
//            }
//        };
//        dialog.showDialog();

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(new FormBackedDialogPage(new CustomerTableView(model)), getWindowControl()) {
            protected void onAboutToShow() {
            }

            protected boolean onFinish() {
                int a = 1;
                return true;
            }
        };
        dialog.showDialog();
        int a = 1;

        
    }
    
}
