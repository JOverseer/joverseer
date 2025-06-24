/*
 * Copyright 2005 the original author or authors.
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
package com.jidesoft.spring.richclient.docking;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.support.AbstractApplicationWindow;

import com.jidesoft.docking.DefaultDockableHolder;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.perspective.NullPerspective;
import com.jidesoft.spring.richclient.perspective.Perspective;
import com.jidesoft.spring.richclient.perspective.PerspectiveManager;

/**
 * An implementation of the Spring RCP ApplicationWindow that uses
 * the JIDE docking framework as the underlying manager. This class
 * adds the ability to specify the active page from the collection
 * of configured pages, and to reload the layout data for a specific
 * page and perspective.
 * 
 * @author Tom Corbin
 * @author Jonny Wray
 *
 */
public class JideApplicationWindow extends AbstractApplicationWindow {

	private DefaultDockableHolder dockableHolder;

	public JideApplicationWindow(DefaultDockableHolder dockableHolder){
        this(dockableHolder, Application.instance().getWindowManager().size());
        this.logger.debug("Constructing JIDE Application Window");
	}
	
	public JideApplicationWindow(DefaultDockableHolder dockableHolder, int number){
		super(number);
		this.dockableHolder = dockableHolder;
	}
	
	/**
     * Overrides the applyStandardLayout by removing the 
     * setting of the layout manager and the insertion of
     * the center part of the frame. The JIDE docking framework
     * actually sets these, via the DefaultDockableHolder.
     */
    @Override
	protected void applyStandardLayout(JFrame windowControl,
                                       ApplicationWindowConfigurer configurer) {
    	this.logger.info("Applying standard layout");
    	windowControl.setTitle(configurer.getTitle());
        windowControl.setIconImage(configurer.getImage());
        windowControl.setJMenuBar(createMenuBarControl());
        windowControl.getContentPane().add(createToolBarControl(), BorderLayout.NORTH);
        //windowControl.getContentPane().add(createStatusBarControl(), BorderLayout.SOUTH);
    }

    /**
     * This returns null since it is not actually used as the applyStandardLayout has
     * been overridden to pass control for the standard layout to the JIDE framework
     */
	@Override
	protected JComponent createWindowContentPane() {
		return null;
	}
	
	/**
	 * The window control is the JIDE dockable holder, so return that.
	 */
    @Override
	protected JFrame createNewWindowControl() {
		return (JFrame)this.dockableHolder;
    }
    
    /**
     * Sets the active page by loading that page's components and
     * applying the layout. Also updates the show view command menu
     * to list the views within the page.
     */
	@Override
	protected void setActivePage(ApplicationPage page) {
		this.logger.debug("Setting active page to "+page.getId());
    	getPage().getControl(); 
    	loadLayoutData(page.getId());
    	((JideApplicationPage)getPage()).updateShowViewCommands();
	}

    public DockingManager getDockingManager(){
    	return this.dockableHolder.getDockingManager();
    }
    
    public void loadLayoutData(String pageId) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Loading layout data for page "+pageId);
		}
	/*
	 * Logic: if the current perspective is either the null on (first time
	 * 		use) or the layout is invalid then use the default perspective.
	 */
		PerspectiveManager perspectiveManager = ((JideApplicationPage)getPage()).getPerspectiveManager();
		Perspective perspective = perspectiveManager.getCurrentPerspective();
		if(perspective == NullPerspective.NULL_PERSPECTIVE || 
				(!LayoutManager.isValidLayout(this.dockableHolder.getDockingManager(), pageId, perspective) || !LayoutManager.isValidLayout(this.dockableHolder.getDockingManager(), pageId))){
			perspective = perspectiveManager.getDefaultPerspective();
		}
		perspective.switchPerspective(this, pageId, false);
	}

    public void saveLayoutData(String name) {
    	Perspective perspective = ((JideApplicationPage)getPage()).getPerspectiveManager().getCurrentPerspective();
		if (name == null) LayoutManager.savePageLayoutData(getDockingManager(), getPage().getId(), perspective.getId());
		else LayoutManager.savePageLayoutData(getDockingManager(), name);
		if(this.logger.isDebugEnabled()){
			this.logger.debug("Saving page layout for page "+ getPage().getId()+" and perspective "+perspective.getId());
		}	
    }
}
