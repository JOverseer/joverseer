package org.joverseer.ui.command;

import org.joverseer.ui.orderEditor.OrderEditor;
import org.joverseer.ui.orderEditor.test.TestOrderParameters;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.application.Application;

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

        TestOrderParameters test = new TestOrderParameters((OrderEditor)Application.instance().getApplicationContext().getBean(OrderEditor.DEFAULT_BEAN_ID));
        test.testOrderParametersForOrderEditor();

    }
}


