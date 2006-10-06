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

import javax.swing.*;

import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.events.SelectedHexChangedListener;
import org.joverseer.ui.events.SelectedHexChangedEvent;
import org.joverseer.ui.viewers.PopulationCenterViewer;


/**
 * This class defines the initial view to be presented in the archetypeapplication. It is
 * constructed automatically by the platform and configured according to the bean
 * specification in the application context.
 * 
 * @author Larry Streepy
 * 
 */
public class InitialView extends AbstractView  implements SelectedHexChangedListener {

    MapPanel mapPanel;
    PopulationCenterViewer pcViewer;
    /**
     * Create the actual UI control for this view. It will be placed into the window
     * according to the layout of the page holding this view.
     */
    protected JComponent createControl() {
        // In this view, we're just going to use standard Swing to place a
        // few controls.
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();

        JPanel panel;
//        JScrollPane scp = new JScrollPane(mapPanel = new MapPanel());
//        mapPanel.setPreferredSize(new Dimension(2000, 2000));
//        mapPanel.addSelectedHexChangedEventListener(this);
//        glb.append(scp, 1, 1, 8, 8);
//        glb.append(new JScrollPane(panel = new JPanel()), 1, 1, 2, 8);
        //pcViewer = new PopulationCenterViewer(FormModelHelper.createFormModel(new PopulationCenter()));
        //panel.add(pcViewer.getControl());
        //panel.setBorder(BorderFactory.createLineBorder(Color.black));
        glb.nextLine();
        glb.append(new JScrollPane(new JPanel()), 2, 1, 10, 2);
        return glb.getPanel();
    }

    public void eventOccured(SelectedHexChangedEvent ev) {
//        Point p = mapPanel.getSelectedHex();
//        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
//        Turn t = g.getTurn();
//        org.joverseer.support.Container c = t.getContainer(TurnElementsEnum.PopulationCenter);
//        PopulationCenter pc = (PopulationCenter)c.findFirstByProperties(new String[]{"x", "y"}, new Object[]{p.x, p.y});
//        if (pc != null) {
//            pcViewer.setFormObject(pc);
//            pcViewer.getControl().setVisible(true);
//            //pcViewerHolder.setVisible(true);
//        } else {
//            pcViewer.getControl().setVisible(false);
//            //pcViewerHolder.setVisible(false);
//            //pcViewerHolder.add(pcViewer.getControl());
//        }
    }


}
