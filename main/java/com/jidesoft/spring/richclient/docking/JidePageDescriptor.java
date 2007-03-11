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


import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;

//import com.jidesoft.spring.richclient.docking.editor.DefaultEditorRegistry;
//import com.jidesoft.spring.richclient.docking.editor.EditorRegistry;
import com.jidesoft.spring.richclient.perspective.PerspectiveManager;

/**
 * An implemenatation of a Spring RCP PageDescriptor that
 * describes a Spring Page within a JideApplicationWindow. 
 * 
 * @author Tom Corbin
 * @author Jonny Wray
 * 
 */
public class JidePageDescriptor implements PageDescriptor, BeanNameAware {
	private static final Log log = LogFactory.getLog(JidePageDescriptor.class);

	private List _viewDescriptors = new ArrayList();
	private String _id;
//	private EditorRegistry editorFactory = new DefaultEditorRegistry();
    private Object initialEditorContents = null;

    private PerspectiveManager perspectiveManager = new PerspectiveManager();
    
    public PerspectiveManager getPerspectiveManager(){
    	return perspectiveManager;
    }
    
    /**
	 * The bean name is the default id
	 */
	public void setBeanName(String beanName){
		setId(beanName);
		perspectiveManager.setPageName(beanName);
	}
	
	/**
	 * Set a list of perspectives for the page described by this class
	 * 
	 * @param perspectives
	 */
	public void setPerspectives(List perspectives){
		perspectiveManager.setPerspectives(perspectives);
	}
	
    /**
     * This sets the editor object that should be opened when the
     * application is started.
     * 
     * @param initialEditorContents
     */
    public void setInitialEditorContents(Object initialEditorContents){
    	this.initialEditorContents = initialEditorContents;
    }
    
    public Object getInitialEditorContents(){
    	return initialEditorContents;
    }
	
//	public EditorRegistry getEditorFactory(){
//		return editorFactory;
//	}
	
	/**
	 * Injects an editor factory to be used to obtain editor descriptors
	 * from a given editor object, the details of which is of course 
	 * application specific. If an editor factory is not injected the
	 * default is used which simply returns nothing for every editor 
	 * object
	 * 
	 * @param editorFactory
	 */
//	public void setEditorFactory(EditorRegistry editorFactory){
//		this.editorFactory = editorFactory;
//	}
//	
	public String getId() {
		return _id;
	}
	
	/**
	 * Builds the initial page layout by iterating the
	 * collection of view descriptors.
	 */
	public void buildInitialLayout(PageLayoutBuilder pageLayout) {
		log.debug("Building initial layout");
		for (Iterator iter = _viewDescriptors.iterator(); iter.hasNext();) {
			String viewDescriptorId = (String) iter.next();
			pageLayout.addView(viewDescriptorId); 
			if(log.isDebugEnabled()){
				log.debug("Added "+viewDescriptorId+" to page layout");
			}
		}
	}

	public String getDisplayName() {
		return null;
	}

	public String getCaption() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.richclient.core.DescribedElement#getDescription()
	 */
	public String getDescription() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.richclient.core.VisualizedElement#getImage()
	 */
	public Image getImage() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.richclient.core.VisualizedElement#getIcon()
	 */
	public Icon getIcon() {
		return null;
	}

	public List getViewDescriptors() {
		return _viewDescriptors;
	}

	public void setViewDescriptors(List viewDescriptors) {
		_viewDescriptors = viewDescriptors;
	}

	public void setId(String id) {
		_id = id;
	}
}
