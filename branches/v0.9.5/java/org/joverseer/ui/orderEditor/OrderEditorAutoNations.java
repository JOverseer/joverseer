package org.joverseer.ui.orderEditor;

import java.util.ArrayList;

import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;


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
        if (!nations.contains(n)) {
            nations.add(n);
        }
    }
    
    public void removeNation(int n) {
        if (nations.contains(n)) {
            nations.remove(n);
        }
    }
    
    public boolean containsNation(int n) {
        return nations.contains(n);
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                nations.clear();
            }
        }
        
    }

}
