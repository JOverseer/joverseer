package org.joverseer.ui.orderEditor;

import java.util.ArrayList;

import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;

/**
 * Container that holds the "Order Editor Auto Nations"
 * For these nations, characters are automatically shown with their orders in the Character Viewer
 * saving the user the need to click on the "Show Orders" option
 * 
 * Basically allows quicker order editing for chars of this nation
 * 
 * @author Marios Skounakis
 */
public class OrderEditorAutoNations implements ApplicationListener {
    private static OrderEditorAutoNations _instance = null;
    
    ArrayList<Integer> nations = new ArrayList<Integer>();
    
    public static OrderEditorAutoNations instance() {
        if (_instance == null) {
            _instance = (OrderEditorAutoNations)Application.instance().getApplicationContext().getBean("orderEditorAutoNations");
        }
        return _instance;
    }
    
    public void addNation(int n) {
        if (!this.nations.contains(n)) {
            this.nations.add(n);
        }
    }
    
    public void removeNation(int n) {
        if (this.nations.contains(n)) {
            this.nations.remove((Object)n);
        }
    }
    
    public boolean containsNation(int n) {
        return this.nations.contains(n);
    }

    @Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                this.nations.clear();
            }
        }
        
    }

}
