package org.joverseer.ui.command;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.joverseer.joApplication;
import org.joverseer.ui.jide.JOverseerJideViewDescriptor;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;

/**
 * A menu containing a collection of sub-menu items that each display a given
 * view.
 * 
 * @author Keith Donald
 */
public class JOverseerShowViewMenu2 extends CommandGroup implements ApplicationWindowAware {

	/** The identifier of this command. */
	public static final String ID = "showViewMenu2";

	private ApplicationWindow window;
	private ViewDescriptor[] members;
	private String message; // title for menu.

	/**
	 * Creates a new {@code ShowViewMenu} with an id of {@value #ID}.
	 */
	public JOverseerShowViewMenu2() {
		super(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationWindow(ApplicationWindow window) {
		this.window = window;
	}

	/**
	 * Called after dependencies have been set, populates this menu with action
	 * command objects that will each show a given view when executed. The
	 * collection of 'show view' commands will be determined by querying the
	 * {@link ViewDescriptorRegistry} retrieved from {@link ApplicationServices}
	 * .
	 */
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		// TODO should this be confirming that 'this.window' is not null?
		populate();
	}

	private void populate() {

		ImageSource imgSource = joApplication.getImageSource();
		//set the Id to something distinct.
		this.setId(this.message);
		for (ViewDescriptor vd : this.members) {
			if (JOverseerJideViewDescriptor.class.isInstance(vd)) {
				AbstractCommand cmd = vd.createShowViewCommand(this.window);
				this.add(cmd);

				Icon ico = new ImageIcon(imgSource.getImage(vd.getId() + ".icon"));
				cmd.setIcon(ico);
			}

		}
	}

	public ViewDescriptor[] getMembers() {
		return this.members;
	}

	public void setMembers(ViewDescriptor[] members) {
		this.members = members;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
