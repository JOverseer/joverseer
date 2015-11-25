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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.richclient.application.PageLayoutBuilder;
import org.springframework.richclient.application.support.AbstractPageDescriptor;

import com.jidesoft.spring.richclient.perspective.PerspectiveManager;

/**
 * An implementation of a Spring RCP PageDescriptor that describes a Spring Page
 * within a JideApplicationWindow.
 * 
 * @author Tom Corbin
 * @author Jonny Wray
 * 
 */
public class JidePageDescriptor extends AbstractPageDescriptor {
	private static final Logger log = Logger.getLogger(JidePageDescriptor.class);

	@SuppressWarnings("unchecked")
	private List _viewDescriptors = new ArrayList();
	private Object initialEditorContents = null;

	private final PerspectiveManager perspectiveManager = new PerspectiveManager();

	public PerspectiveManager getPerspectiveManager() {
		return this.perspectiveManager;
	}

	/**
	 * The bean name is the default id
	 */
	@Override
	public void setBeanName(String beanName) {
		super.setBeanName(beanName);
		this.perspectiveManager.setPageName(beanName);
	}

	/**
	 * Set a list of perspectives for the page described by this class
	 * 
	 * @param perspectives
	 */
	public void setPerspectives(List perspectives) {
		this.perspectiveManager.setPerspectives(perspectives);
	}

	/**
	 * This sets the editor object that should be opened when the application is
	 * started.
	 * 
	 * @param initialEditorContents
	 */
	public void setInitialEditorContents(Object initialEditorContents) {
		this.initialEditorContents = initialEditorContents;
	}

	public Object getInitialEditorContents() {
		return this.initialEditorContents;
	}

	/**
	 * Builds the initial page layout by iterating the collection of view
	 * descriptors.
	 */
	@Override
	public void buildInitialLayout(PageLayoutBuilder pageLayout) {
		log.debug("Building initial layout");
		for (Iterator iter = this._viewDescriptors.iterator(); iter.hasNext();) {
			String viewDescriptorId = (String) iter.next();
			pageLayout.addView(viewDescriptorId);
			if (log.isDebugEnabled()) {
				log.debug("Added " + viewDescriptorId + " to page layout");
			}
		}
	}

	public List getViewDescriptors() {
		return this._viewDescriptors;
	}

	public void setViewDescriptors(List viewDescriptors) {
		this._viewDescriptors = viewDescriptors;
	}
}