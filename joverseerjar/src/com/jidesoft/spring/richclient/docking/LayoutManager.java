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

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.perspective.Perspective;

/**
 * A simple manager of JIDE layouts that has the ability to save and restore
 * layouts based on page ids and a perspective id, allowing multiple saved
 * layouts per page.
 * 
 * @author Jonny Wray
 *
 */
public class LayoutManager {
	private static final Log logger = LogFactory.getLog(LayoutManager.class);

	private static final String PAGE_LAYOUT = "page_{0}_layout_{1}";
	
	public static boolean isValidLayout(DockingManager manager, String pageLayout){
		return manager.isLayoutAvailable(pageLayout) && 
				manager.isLayoutDataVersionValid(pageLayout);
		
	}
	private static String getPageLayoutId(String pageId, String perspectiveId) {
		return MessageFormat.format(PAGE_LAYOUT, new Object[]{pageId, perspectiveId});
	}
	private static String getPageLayoutId(String pageId, Perspective perspective) {
		return getPageLayoutId(pageId, perspective.getId());
	}
	public static boolean isValidLayout(DockingManager manager, String pageId, Perspective perspective){

		return isValidLayout(manager,getPageLayoutId(pageId, perspective)); 
	}
	
	/**
	 * Loads a the previously saved layout for the current page. If no 
	 * previously persisted layout exists for the given page the built 
	 * in default layout is used.
	 * 
	 * @param manager The docking manager to use
	 * @param pageId The page to get the layout for
	 * @return a boolean saying if the layout requested was previously saved
	 */
	public static boolean loadPageLayoutData(DockingManager manager, String pageId, Perspective perspective){
		manager.beginLoadLayoutData();
		try{
            try {
				String pageLayout = getPageLayoutId(pageId, perspective);
				String path =getSaveLayoutDirectory();
				manager.setUsePref(false);
				manager.setLayoutDirectory(path); // must be after setting false
				if(isValidLayout(manager, pageLayout)){
					manager.loadLayoutDataFrom(pageLayout);
					logger.info("Used existing layout " + path);
// expected normal exit.
					return true;
				}
            }
            catch (Exception exc) { }
                
			logger.info("Using default layout 1");
            Resource r = Application.instance().getApplicationContext().getResource("classpath:layout/default.layout");
            manager.loadLayoutFrom(r.getInputStream());
			return false;
			
		}
		catch(Exception e){
			logger.info("Using default layout 2");
			manager.setUsePref(true);
			manager.loadLayoutData();
			return false;
		}
	}
	
	public static boolean loadPageLayoutData(DockingManager manager, String pageId, Perspective perspective, String name){
		manager.beginLoadLayoutData();
        try {
        	String path =getSaveLayoutDirectory();
			manager.setUsePref(false);
			manager.setLayoutDirectory(path); // must be after setting false
			if(isValidLayout(manager, name)){
				manager.loadLayoutDataFrom(name);
				logger.info("Used existing layout " + path);
// expected normal exit.
				return true;
			}
        } catch (Exception exc) { 
                
			logger.info("Failed, falling back on default load method");

        }
        return loadPageLayoutData(manager, pageId, perspective);
	}
	
	/**
	 * Saves the current page layout.
	 * 
	 * @param manager The current docking manager
	 * @param pageId The page to saved the layout for
	 */
	public static void savePageLayoutData(DockingManager manager, String pageId, String perspectiveId){
		logger.info("Saving layout for page "+pageId);
		savePageLayoutData(manager, getPageLayoutId(pageId, perspectiveId));
		
	}
	
	public static void savePageLayoutData(DockingManager manager, String name){
		manager.setUsePref(false);
		manager.setLayoutDirectory(getSaveLayoutDirectory()); // must be after setting false
		manager.saveLayoutDataAs(name);		
	}
	
	private static String getSaveLayoutDirectory()
	{
		String path = "";
		path = System.getenv("APPDATA");
		if ((path == null) || path.isEmpty()) {
			// assume unix/mac
			path = System.getProperty("user.home");
			if (System.getProperty("os.name").toLowerCase().contains("mac")) {
				path = path +"/Library/Application Support/jOverseer";
			} else {
				path = path +"/.jOverseer";
			}
		} else {
			// assume windows
			path = path + "\\jOverseer";
		}
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}
	
		return path;
	}
}
