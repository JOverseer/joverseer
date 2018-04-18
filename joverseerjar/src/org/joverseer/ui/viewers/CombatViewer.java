package org.joverseer.ui.viewers;

import java.awt.Color;
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
import org.joverseer.ui.support.Messages;
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

	public static final String FORM_PAGE = "CombatViewer"; //$NON-NLS-1$

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

		glb.append(this.description = new JTextField());
		this.description.setPreferredSize(this.uiSizes.newDimension(200/12, this.uiSizes.getHeight3()));
		this.description.setBorder(null);

		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource"); //$NON-NLS-1$
		JButton btnMenu = new JButton();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon")); //$NON-NLS-1$
		btnMenu.setPreferredSize(this.uiSizes.newIconDimension(this.uiSizes.getHeight4()));
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
		for (Integer nationNo : c.getNarrations().keySet()) {
			narrationActions.add(new ShowDescriptionCommand(nationNo));
		}
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("combatCommandGroup", narrationActions.toArray()); //$NON-NLS-1$
		return group.createPopupMenu();
	}

	@Override
	public void setFormObject(Object obj) {
		super.setFormObject(obj);
		Combat c = (Combat) obj;
		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$

		String d = ""; //$NON-NLS-1$
		for (Integer nationNo : c.getNarrations().keySet()) {
			Nation n = game.getMetadata().getNationByNum(nationNo);
			d += (!d.equals("") ? ", " : "") + n.getShortName(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		this.description.setText(Messages.getString("CombatViewer.CombatColon") + d); //$NON-NLS-1$
		this.description.setCaretPosition(0);
	}

	private class ShowDescriptionCommand extends ActionCommand {
		int nationNo;

		public ShowDescriptionCommand(int nationNo) {
			super("showDescriptionCommand" + nationNo); //$NON-NLS-1$
			this.nationNo = nationNo;
			Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
			Nation n = game.getMetadata().getNationByNum(nationNo);
			setLabel(Messages.getString("CombatViewer.Narration", new Object[] {n.getName()})); //$NON-NLS-1$
		}

		@Override
		protected void doExecuteCommand() {
			Combat c = (org.joverseer.domain.Combat) getFormObject();
			DialogsUtility.showCombatNarration(c, this.nationNo);
		}

	}
}
