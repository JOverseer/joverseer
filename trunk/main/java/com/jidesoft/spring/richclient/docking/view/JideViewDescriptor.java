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
package com.jidesoft.spring.richclient.docking.view;

import java.awt.Rectangle;

import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.support.DefaultViewDescriptor;

import com.jidesoft.docking.DockContext;

/**
 * Extends the default view descriptor to add the ability to compare the view
 * descriptors, and thus produce alphabetical lists, and support for the
 * generation of a show view command. The ability to specify dockingframe init
 * parameters is also handled.
 * 
 * @author Jonny Wray
 * 
 */
@SuppressWarnings("unchecked")
public class JideViewDescriptor extends DefaultViewDescriptor implements Comparable {

	private boolean isWorkspace = false;
	private int initMode = DockContext.STATE_FRAMEDOCKED;
	private int initSide = DockContext.DOCK_SIDE_EAST;
	private int initIndex = 0;
	private boolean floatOnShow = false;
	private Rectangle floatBounds = new Rectangle(100, 200, 200, 200);

	public void setFloatBounds(Rectangle floatBounds) {
		this.floatBounds = floatBounds;
	}

	public Rectangle getFloatBounds() {
		return this.floatBounds;
	}

	public void setFloatOnShow(boolean floatOnShow) {
		this.floatOnShow = floatOnShow;
	}

	public boolean isFloatOnShow() {
		return this.floatOnShow;
	}

	/**
	 * @return is the view to be treated as a JIDE workspace
	 */
	public boolean isWorkspace() {
		return this.isWorkspace;
	}

	/**
	 * Specify if the view is to be treated as a JIDE workspace
	 * 
	 * @param isWorkspace
	 *            true if the view is to be treated as a JIDE workspace
	 */
	public void setIsWorkspace(boolean isWorkspace) {
		this.isWorkspace = isWorkspace;
	}

	/**
	 * Specify the initMode of the resultant DockableFrame
	 * 
	 * @param initMode
	 *            the required initMode, as a constant from DockContext
	 */
	public void setInitMode(int initMode) {
		this.initMode = initMode;
	}

	/**
	 * Specify the initSide of the resultant DockableFrame
	 * 
	 * @param initSide
	 *            the required initSide, as a constant from DockContext
	 */
	public void setInitSide(int initSide) {
		this.initSide = initSide;
	}

	/**
	 * Specify the initIndex of the resultant DockableFrame.
	 * 
	 * @param initIndex
	 *            the required initIndex, usually 0 or 1.
	 */
	public void setInitIndex(int initIndex) {
		this.initIndex = initIndex;
	}

	public int getInitMode() {
		return this.initMode;
	}

	public int getInitSide() {
		return this.initSide;
	}

	public int getInitIndex() {
		return this.initIndex;
	}

	/**
	 * Compares the display names of the view descriptors
	 */
	@Override
	public int compareTo(Object o) {
		ViewDescriptor castObj = (ViewDescriptor) o;
		return this.getDisplayName().compareToIgnoreCase(castObj.getDisplayName());
	}

}
