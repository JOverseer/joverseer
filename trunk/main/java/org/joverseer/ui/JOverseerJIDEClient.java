/*
 * Copyright 2002-2004 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationLauncher;

import com.jidesoft.plaf.LookAndFeelFactory;

/**
 * This is an archetype application using the Spring Richclient platform.
 * <p>
 * The Spring Rich platform relies on the <a href="http://www.springframework.org/">Spring</a>
 * project to manage the application context with all the associated benefits it offers.
 * <p>
 * A start at the Spring Rich Client documentation can be found on the <a
 * href="http://opensource.atlassian.com/confluence/spring/display/RCP/Home">wiki</a>.
 * </p>
 * 
 * @author Larry Streepy
 * @see The <a
 *      href="http://www.springframework.org/">Spring project</a>
 * @see The <a
 *      href="http://opensource.atlassian.com/confluence/spring/display/RCP/Home">Spring
 *      Rich Wiki</a>
 */
public class JOverseerJIDEClient {
	private static final String CONTEXT_ROOT = "/ctx";
	private static final String APPLICATION_CONTEXT = CONTEXT_ROOT + "/jideApplicationContext.xml";
	private static final String PAGE_CONTEXT = CONTEXT_ROOT + "/richclient-page-application-context.xml";
        private static final String PREFERENCES_CONTEXT = CONTEXT_ROOT + "/preferences-context.xml";
	private static final String STARTUP_CONTEXT = CONTEXT_ROOT + "/richclient-startup-context.xml";
	
    public void Test() {};

    public static String[] cmdLineArgs;
    
    private static final Log _logger = LogFactory.getLog(JOverseerClient.class);

    /**
     * Main routine for the simple sample application.
     * 
     * @param args
     */
    public static void main( String[] args ) throws Exception {
        try {
            cmdLineArgs = args;
            _logger.info("JOverseer Client starting up");
            
            com.jidesoft.utils.Lm.verifyLicense("Marios Skounakis", "JOverseer", "L1R4Nx7vEp0nMbsoaHdH7nkRrx5F.dO");
            LookAndFeelFactory.installDefaultLookAndFeelAndExtension();

            // In order to launch the platform, we have to construct an
            // application context that defines the beans (services) and
            // wiring. This is pretty much straight Spring.
            //
            // Part of this configuration will indicate the initial page to be
            // displayed.

            String rootContextDirectoryClassPath = "/ctx";

            // The startup context defines elements that should be available
            // quickly such as a splash screen image.

            String startupContextPath = rootContextDirectoryClassPath + "/richclient-startup-context.xml";

            String richclientApplicationContextPath = rootContextDirectoryClassPath
                    + "/richclient-application-context.xml";

            // The ApplicationLauncher is responsible for loading the contexts,
            // presenting the splash screen, initializing the Application
            // singleton instance, creating the application window to display
            // the initial page.
            //Application.instance().

            new ApplicationLauncher(STARTUP_CONTEXT, new String[] { APPLICATION_CONTEXT, PAGE_CONTEXT, PREFERENCES_CONTEXT });

        } catch( Exception e ) {
            System.out.println(e);
            throw e;
            //System.exit(1);
        }
    }

    /**
     * A class to handle uncaught exceptions on the AWT event thread. This is registered
     * (wired in) in the application context as part of the definition of the application
     * lifecycle advisor.
     */
    public static class AWTExceptionHandler {

        public void handle( Throwable e ) {
            // You should probably do something more clever than this.
            _logger.error("Exception on AWT Event Thread", e);
        }
    }
}