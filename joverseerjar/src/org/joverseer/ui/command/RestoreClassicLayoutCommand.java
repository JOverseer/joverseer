package org.joverseer.ui.command;

import java.util.Locale;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;

import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.docking.JideApplicationWindow;

/**
 * Restores the default layout of the main application window as stored in the
 * default layout file
 * 
 * @author Marios Skounakis
 */
public class RestoreClassicLayoutCommand extends ApplicationWindowAwareCommand {
	private static final Log log = LogFactory.getLog(RestoreClassicLayoutCommand.class);

	private static final String ID = "restoreClassicLayoutCommand";
	
	public RestoreClassicLayoutCommand() {
		super(ID);
	}

	@Override
	protected void doExecuteCommand() {
		log.debug("Execute command");
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		ConfirmationDialog md = new ConfirmationDialog(ms.getMessage("confirmRestoreLayoutDialog.title", new String[] {}, Locale.getDefault()), ms.getMessage("confirmRestoreLayoutDialog.message", new String[] {}, Locale.getDefault())) {

			@Override
			protected void onConfirm() {
				DockingManager manager = ((JideApplicationWindow) getApplicationWindow()).getDockingManager();
				Resource r = Application.instance().getApplicationContext().getResource("classpath:layout/ClassicDefault.layout");
				try {
					manager.loadLayoutFrom(r.getInputStream());
				} catch (Exception exc) {
					log.error("Failed to load original layout from layout file " + exc.getMessage());
				}
			}

		};
		md.showDialog();

	}
}
