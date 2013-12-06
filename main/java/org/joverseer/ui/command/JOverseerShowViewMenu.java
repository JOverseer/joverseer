package org.joverseer.ui.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.joverseer.ui.jide.JOverseerJideViewDescriptor;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;

import com.jidesoft.spring.richclient.docking.JideApplicationLifecycleAdvisor;

/**
 * A menu containing a collection of sub-menu items that each display a given
 * view.
 * 
 * @author Keith Donald
 */
public class JOverseerShowViewMenu extends CommandGroup implements ApplicationWindowAware {

	/** The identifier of this command. */
	public static final String ID = "showViewMenu";

	private ApplicationWindow window;

	/**
	 * Creates a new {@code ShowViewMenu} with an id of {@value #ID}.
	 */
	public JOverseerShowViewMenu() {
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
		ViewDescriptorRegistry viewDescriptorRegistry = (ViewDescriptorRegistry) ApplicationServicesLocator.services().getService(ViewDescriptorRegistry.class);

		ViewDescriptor[] views = viewDescriptorRegistry.getViewDescriptors();
		ArrayList<String> viewGroups = new ArrayList<String>();

		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
		for (ViewDescriptor vd : views) {
			String group = "";
			if (JOverseerJideViewDescriptor.class.isInstance(vd)) {
				group = ((JOverseerJideViewDescriptor) vd).getViewGroup();
				if (group == null) {
					group = "";
				}
			}
			if (!viewGroups.contains(group)) {
				viewGroups.add(group);
			}
		}
		Collections.sort(viewGroups);

		for (String viewGroup : viewGroups) {
			HashMap<String, ViewDescriptor> viewMap = new HashMap<String, ViewDescriptor>();
			for (ViewDescriptor vd : views) {
				String cViewGroup = "";
				if (JOverseerJideViewDescriptor.class.isInstance(vd)) {
					cViewGroup = ((JOverseerJideViewDescriptor) vd).getViewGroup();
					if (cViewGroup == null) {
						cViewGroup = "";
					}
				}
				if (viewGroup.equals(cViewGroup)) {
					viewMap.put(vd.getCaption().replace("&", ""), vd);
				}
			}
			ArrayList<String> captions = new ArrayList<String>();
			captions.addAll(viewMap.keySet());

			if (!viewGroup.equals("")) {
				if (viewGroup.equals("Admin") && !JideApplicationLifecycleAdvisor.devOption)
					continue;
				CommandGroup cg = new CommandGroup(viewGroup);
				Collections.sort(captions);
				for (String caption : captions) {
					ViewDescriptor vd = viewMap.get(caption);
					AbstractCommand cmd = vd.createShowViewCommand(this.window);
					cg.add(cmd);

					Icon ico = new ImageIcon(imgSource.getImage(viewMap.get(caption).getId() + ".icon"));
					cmd.setIcon(ico);
				}
				cg.setLabel(viewGroup);

				addInternal(cg);

			} else {
				for (String caption : captions) {
					AbstractCommand cmd = viewMap.get(caption).createShowViewCommand(this.window);
					addInternal(cmd);
					Icon ico = new ImageIcon(imgSource.getImage(viewMap.get(caption).getId() + ".icon"));
					cmd.setIcon(ico);
				}
				addSeparator();
			}

		}

		// add a final menu that contains ALL VIEWS
		CommandGroup allViews = new CommandGroup("All");
		allViews.setLabel("All");
		ArrayList<String> allCaptions = new ArrayList<String>();
		for (ViewDescriptor vd : views) {
			if (JOverseerJideViewDescriptor.class.isInstance(vd)) {
				if ("Admin".equals(((JOverseerJideViewDescriptor) vd).getViewGroup()))
					continue;
			}
			allCaptions.add(vd.getCaption().replace("&", ""));
		}
		Collections.sort(allCaptions);
		for (String c : allCaptions) {
			for (ViewDescriptor vd : views) {
				if (vd.getCaption().replace("&", "").equals(c)) {
					AbstractCommand cmd = vd.createShowViewCommand(this.window);
					allViews.add(cmd);

					Icon ico = new ImageIcon(imgSource.getImage(vd.getId() + ".icon"));
					cmd.setIcon(ico);
				}
			}
		}
		addInternal(allViews);

	}
}
