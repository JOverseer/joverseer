package org.joverseer.ui.viewers;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.form.AbstractForm;

public class MovementCostViewer  extends AbstractForm implements ApplicationListener {
	public static final String FORM_PAGE = "MovementCostViewer";
	
	public MovementCostViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

	
	protected JComponent createFormControl() {
		return null;
	}



	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
