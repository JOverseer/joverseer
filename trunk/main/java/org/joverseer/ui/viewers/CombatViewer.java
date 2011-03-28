package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.joverseer.domain.Combat;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.commands.DialogsUtility;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Shows combats in the Current Hex View
 * 
 * @author Marios Skounakis
 */
public class CombatViewer extends ObjectViewer {

	public static final String FORM_PAGE = "CombatViewer";

	JTextField description;

	ActionCommand showDescriptionCommand;

	public CombatViewer(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	@Override
	public boolean appliesTo(Object obj) {
		return Combat.class.isInstance(obj);
	}

	@Override
	protected JComponent createFormControl() {
		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		glb.setDefaultInsets(new Insets(0, 0, 0, 5));

		glb.append(description = new JTextField());
		description.setPreferredSize(new Dimension(200, 12));
		description.setBorder(null);

		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
		JButton btnMenu = new JButton();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
		btnMenu.setPreferredSize(new Dimension(16, 16));
		btnMenu.setIcon(ico);
		glb.append(btnMenu);
		btnMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				return createCombatPopupContextMenu();
			}
		});

		glb.nextLine();

		JPanel p = glb.getPanel();
		p.setBackground(Color.white);
		return p;
	}

	private JPopupMenu createCombatPopupContextMenu() {
		Combat c = (Combat) getFormObject();
		ArrayList<Object> narrationActions = new ArrayList<Object>();
		int i = 0;
		for (Integer nationNo : c.getNarrations().keySet()) {
			narrationActions.add(new ShowDescriptionCommand(nationNo));
		}
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("combatCommandGroup", narrationActions.toArray());
		return group.createPopupMenu();
	}

	@Override
	public void setFormObject(Object obj) {
		super.setFormObject(obj);
		Combat c = (Combat) obj;
		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();

		String d = "";
		for (Integer nationNo : c.getNarrations().keySet()) {
			Nation n = game.getMetadata().getNationByNum(nationNo);
			d += (!d.equals("") ? ", " : "") + n.getShortName();
		}
		description.setText("Combat: " + d);
		description.setCaretPosition(0);
	}

	private class ShowDescriptionCommand extends ActionCommand {
		int nationNo;

		public ShowDescriptionCommand(int nationNo) {
			super("showDescriptionCommand" + nationNo);
			this.nationNo = nationNo;
			Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
			Nation n = game.getMetadata().getNationByNum(nationNo);
			setLabel(n.getName() + " Narration");
		}

		@Override
		protected void doExecuteCommand() {
			Combat c = (org.joverseer.domain.Combat) getFormObject();
			DialogsUtility.showCombatNarration(c, nationNo);
		}

	}
}
