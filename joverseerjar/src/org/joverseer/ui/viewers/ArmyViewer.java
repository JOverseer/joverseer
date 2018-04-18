package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.apache.log4j.LogManager;
import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmyEstimate;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.CombatUtils;
import org.joverseer.tools.armySizeEstimator.ArmySizeEstimate;
import org.joverseer.tools.armySizeEstimator.ArmySizeEstimator;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.ArmyRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.commands.ShowInfoSourcePopupCommand;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.ui.views.EditArmyForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

/**
 * Shows armies in the Current Hex View
 * 
 * @author Marios Skounakis
 */
public class ArmyViewer extends ObjectViewer {

	public static final String FORM_PAGE = "ArmyViewer"; //$NON-NLS-1$

	boolean showColor = true;

	JTextField commanderName;
	JTextField nation;
	JTextField armySize;
	JTextField armyType;
	JTextField extraInfo;
	JTextField food;
	JTextField cav;
	JTextField travellingWith;

	ActionCommand showArmyMovementRangeAction = new ShowArmyMovementRangeAction();
	ActionCommand showArmyMovementIgnorePopsRangeAction = new ShowArmyMovementRangeIgnorePopsAction();
	ActionCommand toggleFedAction = new ToggleFedAction();
	ActionCommand toggleCavAction = new ToggleCavAction();
	ActionCommand deleteArmyCommand = new DeleteArmyCommand();
	ActionCommand editArmyCommand = new EditArmyCommand();
	ActionCommand exportCombatArmyCodeCommand = new ExportCombatArmyCodeCommand();

	public ArmyViewer(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	@Override
	public boolean appliesTo(Object obj) {
		return Army.class.isInstance(obj);
	}

	@Override
	protected JComponent createFormControl() {
		Box box = Box.createHorizontalBox();

		box.add(this.commanderName = new JTextField());
		this.commanderName.setPreferredSize(this.uiSizes.newDimension(160/12, this.uiSizes.getHeight3()));
		box.add(Box.createHorizontalStrut(5));
		box.add(this.nation = new JTextField());
		this.nation.setPreferredSize(this.uiSizes.newDimension(30/12, this.uiSizes.getHeight3()));

		// button to show range of army on map
		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource"); //$NON-NLS-1$
		JButton btnMenu = new JButton();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon")); //$NON-NLS-1$
		btnMenu.setPreferredSize(this.uiSizes.newIconDimension(this.uiSizes.getHeight4()));
		btnMenu.setIcon(ico);
		box.add(Box.createHorizontalGlue());
		box.add(btnMenu);
		btnMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				return createArmyPopupContextMenu();
			}
		});
		box.add(Box.createHorizontalStrut(5));

		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		glb.setDefaultInsets(new Insets(0, 0, 0, 5));
		glb.append(this.armySize = new JTextField(), 1, 1);
		this.armySize.setPreferredSize(this.uiSizes.newDimension(100/12, this.uiSizes.getHeight3()));
		glb.append(this.armyType = new JTextField());
		this.armyType.setPreferredSize(this.uiSizes.newDimension(50/12, this.uiSizes.getHeight3()));
		glb.nextLine();
		glb.append(this.extraInfo = new JTextField(), 2, 1);
		this.extraInfo.setPreferredSize(this.uiSizes.newDimension(150/12, this.uiSizes.getHeight3()));
		glb.nextLine();
		glb.append(this.food = new JTextField());
		this.food.setPreferredSize(this.uiSizes.newDimension(100/12, this.uiSizes.getHeight3()));
		glb.append(this.cav = new JTextField());
		this.cav.setPreferredSize(this.uiSizes.newDimension(40/12, this.uiSizes.getHeight3()));
		glb.nextLine();
		glb.append(this.travellingWith = new JTextField(), 2, 1);
		this.travellingWith.setPreferredSize(this.uiSizes.newDimension(150/12, this.uiSizes.getHeight3()));

		this.commanderName.setBorder(null);
		this.commanderName.setFont(new Font(this.commanderName.getFont().getName(), Font.BOLD, this.commanderName.getFont().getSize()));
		this.nation.setBorder(null);
		this.armySize.setBorder(null);
		this.armyType.setBorder(null);
		this.extraInfo.setBorder(null);
		this.food.setBorder(null);
		this.cav.setBorder(null);
		this.travellingWith.setBorder(null);

		JPanel panel = glb.getPanel();
		panel.setBackground(Color.white);
		Box vBox = Box.createVerticalBox();
		vBox.add(box);
		vBox.add(panel);
		return vBox;
	}

	@Override
	public void setFormObject(Object object) {
		super.setFormObject(object);

		Army army = (Army) object;
		this.commanderName.setText((army.getCommanderTitle() + " " + GraphicUtils.parseName(army.getCommanderName())).trim()); //$NON-NLS-1$

		if (getShowColor()) {
			Color c = ColorPicker.getInstance().getColor(army.getNationAllegiance().toString());
			this.commanderName.setForeground(c);
		}

		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
		if (game == null)
			return;
		game.getMetadata();
		Nation armyNation = army.getNation();
		this.nation.setText(armyNation.getShortName());
		this.nation.setCaretPosition(0);
		this.nation.setToolTipText(armyNation.getName());

		this.armySize.setText(Messages.getString("ArmyViewer.SizeColon") + army.getSize().toString()); //$NON-NLS-1$
		this.armySize.setCaretPosition(0);
		this.armyType.setText(army.isNavy() ? Messages.getString("ArmyViewer.Navy") : Messages.getString("ArmyViewer.Army")); //$NON-NLS-1$ //$NON-NLS-2$
		if (army.getElements().size() > 0) {
			this.extraInfo.setText(""); //$NON-NLS-1$
			this.extraInfo.setVisible(true);
			for (ArmyElement element : army.getElements()) {
				this.extraInfo.setText(UIUtils.OptSpace(this.extraInfo.getText(), element.getLocalizedDescription()));
			}
			String pval = PreferenceRegistry.instance().getPreferenceValue("currentHexView.showNHIEquivalents"); //$NON-NLS-1$
			if (pval != null && pval.equals("yes")) { //$NON-NLS-1$
				GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", army.getCommanderName()); //$NON-NLS-1$
				int nhi = army.getENHI();
				if (nhi > 0) {
					this.extraInfo.setText(this.extraInfo.getText() + Messages.getString("ArmyViewer.enHI",new Object[] { nhi})); //$NON-NLS-1$
				}
			}
		} else if (army.getTroopCount() > 0) {
			this.extraInfo.setVisible(true);
			this.extraInfo.setText(Messages.getString("ArmyViewer.aboutNMen",new Object[] {army.getTroopCount()})); //$NON-NLS-1$ //$NON-NLS-2$
		} else if (army.getSize() != ArmySizeEnum.unknown && !army.isNavy()) {
			ArmySizeEstimate ae = (new ArmySizeEstimator()).getSizeEstimateForArmySize(army.getSize(), ArmySizeEstimate.ARMY_TYPE);
			if (ae == null || ae.getMin() == null) {
				this.extraInfo.setVisible(false);
			} else {
				this.extraInfo.setVisible(true);
				this.extraInfo.setText(Messages.getString("ArmyViewer.estMinToMaxMen", new Object[] {ae.getMin(), ae.getMax()})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		} else {
			this.extraInfo.setVisible(false);
		}
		if (army.getElements().size() == 0 && "yes".equals(PreferenceRegistry.instance().getPreferenceValue("currentHexView.showArmyEstimate"))) { //$NON-NLS-1$ //$NON-NLS-2$
			ArmyEstimate estimate = (ArmyEstimate) game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).findFirstByProperty("commanderName", army.getCommanderName()); //$NON-NLS-1$
			if (estimate != null) {
				String troopInfo = estimate.getTroopInfo();
				if (!troopInfo.equals("")) { //$NON-NLS-1$
					this.extraInfo.setText(this.extraInfo.getText() + " [~" + troopInfo + "]"); //$NON-NLS-1$ //$NON-NLS-2$
					this.extraInfo.setVisible(true);
				}
			}
		}
		this.extraInfo.setCaretPosition(0);
		String foodStr = ""; //$NON-NLS-1$
		String foodTooltip = ""; //$NON-NLS-1$
		if (army.getFood() != null) {
			foodStr = army.getFood().toString() + " "; //$NON-NLS-1$
		}
		Boolean fed = army.computeFed();

		foodStr += (fed != null && fed == true ? Messages.getString("ArmyViewer.Fed") : Messages.getString("ArmyViewer.Unfed")); //$NON-NLS-1$ //$NON-NLS-2$
		if (fed != null && fed == true) {
			foodTooltip = Messages.getString("ArmyViewer.TreatAsFed"); //$NON-NLS-1$
		} else {
			foodTooltip = Messages.getString("ArmyViewer.TreatAsUnfed"); //$NON-NLS-1$
		}
		if ("yes".equals(PreferenceRegistry.instance().getPreferenceValue("currentHexView.showNumberOfFedTurnsForArmies"))) { //$NON-NLS-1$ //$NON-NLS-2$
			if (Boolean.TRUE.equals(fed)) {
				Integer food1 = army.getFood();
				Integer consumption = army.computeFoodConsumption();
				if (food1 != null && consumption != null && consumption > 0) {
					int turns = (food1 - 1) / consumption;
					foodStr += " (" + turns + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					foodTooltip = "<html>" + Messages.getString("ArmyViewer.ArmyFoodForNTurns", new Object[] {turns}) + foodTooltip + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}
		this.food.setText(foodStr);
		this.food.setToolTipText(foodTooltip);
		// armyMorale.setText("M: 0");

		String cavString = ""; //$NON-NLS-1$
		String cavTooltip = ""; //$NON-NLS-1$
		Boolean isCav = army.computeCavalry();
		if (isCav == null) {
			cavString = ""; //$NON-NLS-1$
			cavTooltip = ""; //$NON-NLS-1$
		} else if (isCav) {
			cavString = Messages.getString("ArmyViewer.AbbCavalry"); //$NON-NLS-1$
			cavTooltip = Messages.getString("ArmyViewer.TreatAsCavalry"); //$NON-NLS-1$
		} else {
			cavString = Messages.getString("ArmyViewer.AbbInfantry"); //$NON-NLS-1$
			cavTooltip = Messages.getString("ArmyViewer.TreatAsInfantry"); //$NON-NLS-1$
		}
		this.cav.setText(cavString);
		this.cav.setToolTipText(cavTooltip);

		if (army.getCharacters().size() > 0) {
			this.travellingWith.setVisible(true);
			String txt = ""; //$NON-NLS-1$
			for (String cn : army.getCharacters()) {
				txt += (txt.equals("") ? "" : ",") + cn; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			this.travellingWith.setText(Messages.getString("ArmyViewer.WithArmyColon") + txt); //$NON-NLS-1$
		} else {
			this.travellingWith.setVisible(false);
		}
	}

	private JPopupMenu createArmyPopupContextMenu() {
		ArrayList<Object> commands = new ArrayList<Object>(Arrays.asList(this.toggleFedAction, this.toggleCavAction, this.editArmyCommand, this.deleteArmyCommand, "separator", this.showArmyMovementRangeAction, this.showArmyMovementIgnorePopsRangeAction, "separator", new ShowCanCaptureAction(), new ShowRequiredTransportsCommand(), new ShowRequiredFoodCommand(), "separator", new ShowInfoSourcePopupCommand(((Army) getFormObject()).getInfoSource()))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if ("yes".equals(PreferenceRegistry.instance().getPreferenceValue("general.developerOptions"))) { //$NON-NLS-1$ //$NON-NLS-2$
			commands.add("separator"); //$NON-NLS-1$
			commands.add(this.exportCombatArmyCodeCommand);
		}
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("armyCommandGroup", commands.toArray()); //$NON-NLS-1$
		return group.createPopupMenu();
	}

	private class ShowArmyMovementRangeAction extends ShowArmyMovementRangeGenericAction {
		public ShowArmyMovementRangeAction() {
			super("showArmyMovementRangeAction", false); //$NON-NLS-1$
		}
	}

	private class ShowArmyMovementRangeIgnorePopsAction extends ShowArmyMovementRangeGenericAction {
		public ShowArmyMovementRangeIgnorePopsAction() {
			super("showArmyMovementRangeIgnorePopsAction", true); //$NON-NLS-1$
		}
	}

	private class ShowArmyMovementRangeGenericAction extends ActionCommand {
		boolean ignoreEnemyPops;

		public ShowArmyMovementRangeGenericAction(String id, boolean ignoreEnemyPops) {
			super(id);
			this.ignoreEnemyPops = ignoreEnemyPops;
		}

		@Override
		protected void doExecuteCommand() {
			org.joverseer.domain.Army army = (org.joverseer.domain.Army) getFormObject();
			ArmyRangeMapItem armi = new ArmyRangeMapItem(army, this.ignoreEnemyPops);
			AbstractMapItem.add(armi);

			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance().getSelectedHex(), this));
		}
	}

	private class ToggleFedAction extends ActionCommand {

		public ToggleFedAction() {
			super("toggleFedAction"); //$NON-NLS-1$
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (org.joverseer.domain.Army) getFormObject();
			Boolean fed = a.computeFed();
			a.setFed(fed == null || fed != true ? true : false);
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance().getSelectedHex(), this));
		}
	}

	private class ShowCanCaptureAction extends ActionCommand {

		public ShowCanCaptureAction() {
			super("showCanCaptureAction"); //$NON-NLS-1$
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (org.joverseer.domain.Army) getFormObject();
			String str = ""; //$NON-NLS-1$
			for (PopulationCenterSizeEnum pcSize : PopulationCenterSizeEnum.values()) {
				for (int f = FortificationSizeEnum.values().length - 1; f >= 0; f--) {
					FortificationSizeEnum fort = FortificationSizeEnum.values()[f];
					int i = CombatUtils.canCapturePopCenter(a, pcSize, fort);
					if (i > -1) {
						String fortString = UIUtils.enumToString(fort);
						if (fortString.equals("-")) //$NON-NLS-1$
							fortString = Messages.getString("ArmyViewer.NoFort"); //$NON-NLS-1$
						str = UIUtils.enumToString(pcSize) + "/" + fortString + Messages.getString("ArmyViewer.LoyaltyIs", new Object[] {i}) +"\n" + str; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					if (i == 100)
						break;
				}
			}
			if (!str.equals("")) { //$NON-NLS-1$
				str = Messages.getString("ArmyViewer.ArmyCanCapture") + str; //$NON-NLS-1$
				str += Messages.getString("ArmyViewer.Caveat"); //$NON-NLS-1$
				MessageDialog dlg = new MessageDialog(Messages.getString("ArmyViewer.ArmyVsPC.title"), str); //$NON-NLS-1$
				dlg.showDialog();
			}
		}
	}

	private class ToggleCavAction extends ActionCommand {

		public ToggleCavAction() {
			super("toggleCavAction"); //$NON-NLS-1$
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (org.joverseer.domain.Army) getFormObject();
			Boolean cav1 = a.computeCavalry();
			a.setCavalry(cav1 == null || cav1 != true ? true : false);
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance().getSelectedHex(), this));
		}
	}

	private class DeleteArmyCommand extends ActionCommand {
		boolean cancel = true;

		@Override
		protected void doExecuteCommand() {
			this.cancel = true;
			Army a = (Army) getFormObject();
			ConfirmationDialog cdlg = new ConfirmationDialog(Messages.getString("standardMessages.Warning"), Messages.getString("ArmyViewer.Warning.text",new Object[] { a.getCommanderName()})) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				@Override
				protected void onCancel() {
					super.onCancel();
				}

				@Override
				protected void onConfirm() {
					DeleteArmyCommand.this.cancel = false;
				}

			};
			cdlg.showDialog();
			if (this.cancel)
				return;
			Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
			Turn t = g.getTurn();
			Container<Army> armies = t.getArmies();
			armies.removeItem(a);
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
		}
	}

	private class ExportCombatArmyCodeCommand extends ActionCommand {
		public ExportCombatArmyCodeCommand() {
			super("exportCombatArmyCodeCommand"); //$NON-NLS-1$
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (Army) getFormObject();
			CombatArmy ca = new CombatArmy(a);
			LogManager.getLogger(this.getClass()).info("Exporting code for Combat Army under " + ca.getCommander()); //$NON-NLS-1$
			LogManager.getLogger(this.getClass()).info(ca.getCode());
			LogManager.getLogger(this.getClass()).info("Done exporting code for Combat Army under " + ca.getCommander()); //$NON-NLS-1$
			MessageDialog dlg = new MessageDialog(Messages.getString("ArmyViewer.ArmyCode.title"), ca.getCode()); //$NON-NLS-1$
			dlg.showDialog();
		}
	}

	private class ShowRequiredTransportsCommand extends ActionCommand {
		public ShowRequiredTransportsCommand() {
			super("showRequiredTransportsCommand"); //$NON-NLS-1$
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (Army) getFormObject();
			int requiredTransportCapacity = 0;
			for (ArmyElement ae : a.getElements()) {
				requiredTransportCapacity += ae.getRequiredTransportCapacity();
			}
			int requiredTransports = a.getNumberOfRequiredTransports();
			String msg = Messages.getString("ArmyViewer.TransportRequired",new Object[] { requiredTransports }); //$NON-NLS-1$
			if (a.isNavy()) {
				int transports = a.getElement(ArmyElementType.Transports).getNumber();
				int transportInfCapacity = transports * 250;
				int transportCavCapacity = transports * 150;
				msg += Messages.getString("ArmyViewer.CurrentTransport", new Object[] { transports, transportInfCapacity, transportCavCapacity }); //$NON-NLS-1$
				int freeInfCapacity = transportInfCapacity - requiredTransportCapacity;
				int freeCavCapacity = freeInfCapacity * 150 / 250;
				if (freeInfCapacity > 0) {
					msg += Messages.getString("ArmyViewer.TransportSpare", new Object[] {freeInfCapacity,freeCavCapacity}); //$NON-NLS-1$
				} else if (freeInfCapacity == 0) {
					msg += Messages.getString("ArmyViewer.TransportSpareNone"); //$NON-NLS-1$
				} else if (freeInfCapacity < 0) {
					msg += Messages.getString("ArmyViewer.InsufficientTransport"); //$NON-NLS-1$
				}
			}
			MessageDialog dlg = new MessageDialog(Messages.getString("ArmyViewer.RequiredTransports.title"), msg); //$NON-NLS-1$
			dlg.showDialog();
		}
	}

	private class ShowRequiredFoodCommand extends ActionCommand {
		public ShowRequiredFoodCommand() {
			super("showRequiredFoodCommand"); //$NON-NLS-1$
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (Army) getFormObject();
			Integer food1 = a.computeFoodConsumption();
			if (food1 == null)
				return;
			MessageDialog dlg = new MessageDialog(Messages.getString("ArmyViewer.RequiredFood.title"), 
					Messages.getString("ArmyViewer.RequiredFood.text",new Object[] { food1 })); //$NON-NLS-1$
			dlg.showDialog();
		}
	}

	private class EditArmyCommand extends ActionCommand {
		@Override
		protected void doExecuteCommand() {
			Army a = (Army) getFormObject();
			final EditArmyForm form = new EditArmyForm(FormModelHelper.createFormModel(a));
			FormBackedDialogPage pg = new FormBackedDialogPage(form);
			TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {

				@Override
				protected void onAboutToShow() {
					super.onAboutToShow();
					form.setFormObject(getFormObject());
				}

				@Override
				protected boolean onFinish() {
					form.commit();
					Army a1 = (Army) getFormObject();
					Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
					Turn t = g.getTurn();
					Container<Army> armies = t.getArmies();
					armies.removeItem(a1);
					armies.addItem(a1);
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
					return true;
				}
			};
			dlg.setTitle(Messages.getString("editArmyDialog.title")); //$NON-NLS-1$
			dlg.showDialog();
		}
	}

	public boolean getShowColor() {
		return this.showColor;
	}

	public void setShowColor(boolean showColor) {
		this.showColor = showColor;
	}

}
