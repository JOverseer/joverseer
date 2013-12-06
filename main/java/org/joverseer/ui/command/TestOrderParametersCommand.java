package org.joverseer.ui.command;

import org.joverseer.ui.orderEditor.test.TestOrderParameters;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.richclient.command.ActionCommand;

/**
 * Admin command
 * 
 * Tests the order parameters for the order editor and order parameter validator
 * using the TestOrderParameters class
 * 
 * @author Marios Skounakis
 */
public class TestOrderParametersCommand  extends ActionCommand {
    
    public TestOrderParametersCommand() {
        super("testOrderParametersCommand");
    }

    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;

        TestOrderParameters test = new TestOrderParameters();
        test.testOrderParametersForOrderEditor();
            
    }
}
    

