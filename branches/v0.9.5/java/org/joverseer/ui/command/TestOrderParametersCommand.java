package org.joverseer.ui.command;

import org.joverseer.ui.orderEditor.test.TestOrderParameters;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.richclient.command.ActionCommand;


public class TestOrderParametersCommand  extends ActionCommand {
    
    public TestOrderParametersCommand() {
        super("testOrderParametersCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;

        TestOrderParameters test = new TestOrderParameters();
        test.testOrderParametersForOrderEditor();
            
    }
}
    

