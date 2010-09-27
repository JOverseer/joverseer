package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

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
import org.joverseer.domain.Character;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
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
import org.joverseer.ui.support.commands.ShowInfoSourcePopupCommand;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.ui.views.EditArmyForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
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

	public static final String FORM_PAGE = "ArmyViewer";

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
		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		glb.setDefaultInsets(new Insets(0, 0, 0, 5));

		glb.append(commanderName = new JTextField());
		commanderName.setPreferredSize(new Dimension(160, 12));
		glb.append(nation = new JTextField());
		nation.setPreferredSize(new Dimension(30, 12));

		// button to show range of army on map
		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
		JButton btnMenu = new JButton();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
		btnMenu.setPreferredSize(new Dimension(16, 16));
		btnMenu.setIcon(ico);
		glb.append(btnMenu);
		btnMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				return createArmyPopupContextMenu();
			}
		});

		glb.nextLine();
		glb.append(armySize = new JTextField(), 1, 1);
		armySize.setPreferredSize(new Dimension(100, 12));
		glb.append(armyType = new JTextField());
		armyType.setPreferredSize(new Dimension(50, 12));
		glb.nextLine();
		glb.append(extraInfo = new JTextField(), 2, 1);
		extraInfo.setPreferredSize(new Dimension(150, 12));
		glb.nextLine();
		glb.append(food = new JTextField());
		food.setPreferredSize(new Dimension(100, 12));
		glb.append(cav = new JTextField());
		cav.setPreferredSize(new Dimension(40, 12));
		glb.nextLine();
		glb.append(travellingWith = new JTextField(), 2, 1);
		travellingWith.setPreferredSize(new Dimension(150, 12));

		commanderName.setBorder(null);
		commanderName.setFont(new Font(commanderName.getFont().getName(), Font.BOLD, commanderName.getFont().getSize()));
		nation.setBorder(null);
		armySize.setBorder(null);
		armyType.setBorder(null);
		extraInfo.setBorder(null);
		food.setBorder(null);
		cav.setBorder(null);
		travellingWith.setBorder(null);

		JPanel panel = glb.getPanel();
		panel.setBackground(Color.white);
		return panel;
	}

	@Override
	public void setFormObject(Object object) {
		super.setFormObject(object);

		Army army = (Army) object;
		commanderName.setText((army.getCommanderTitle() + " " + GraphicUtils.parseName(army.getCommanderName())).trim());

		if (getShowColor()) {
			Color c = ColorPicker.getInstance().getColor(army.getNationAllegiance().toString());
			commanderName.setForeground(c);
		}

		Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (game == null)
			return;
		GameMetadata gm = game.getMetadata();
		nation.setText(gm.getNationByNum(army.getNationNo()).getShortName());
		nation.setCaretPosition(0);

		armySize.setText("Size: " + army.getSize().toString());
		armySize.setCaretPosition(0);
		armyType.setText(army.isNavy() ? "Navy" : "Army");
		if (army.getElements().size() > 0) {
			extraInfo.setText("");
			extraInfo.setVisible(true);
			for (ArmyElement element : army.getElements()) {
				extraInfo.setText(extraInfo.getText() + (extraInfo.getText().equals("") ? "" : " ") + element.getDescription());
			}

			String pval = PreferenceRegistry.instance().getPreferenceValue("currentHexView.showNHIEquivalents");
			if (pval != null && pval.equals("yes")) {
				Character commander = (Character) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", army.getCommanderName());
				int nhi = army.getENHI();
				if (nhi > 0) {
					extraInfo.setText(extraInfo.getText() + " (" + nhi + "enHI)");
				}
			}
		} else if (army.getTroopCount() > 0) {
			extraInfo.setVisible(true);
			extraInfo.setText("~ " + army.getTroopCount() + " men");
		} else if (army.getSize() != ArmySizeEnum.unknown && !army.isNavy()) {
			ArmySizeEstimate ae = (new ArmySizeEstimator()).getSizeEstimateForArmySize(army.getSize(), ArmySizeEstimate.ARMY_TYPE);
			if (ae == null || ae.getMin() == null) {
				extraInfo.setVisible(false);
			} else {
				extraInfo.setVisible(true);
				extraInfo.setText("est. " + ae.getMin() + "-" + ae.getMax() + " men");
			}
		} else {
			extraInfo.setVisible(false);
		}
		if (army.getElements().size() == 0 && "yes".equals(PreferenceRegistry.instance().getPreferenceValue("currentHexView.showArmyEstimate"))) {
			ArmyEstimate estimate = (ArmyEstimate) game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).findFirstByProperty("commanderName", army.getCommanderName());
			if (estimate != null) {
				String troopInfo = estimate.getTroopInfo();
				if (!troopInfo.equals("")) {
					extraInfo.setText(extraInfo.getText() + " [~" + troopInfo + "]");
					extraInfo.setVisible(true);
				}
			}
		}
		extraInfo.setCaretPosition(0);
		String foodStr = "";
		String foodTooltip = "";
		if (army.getFood() != null) {
			foodStr = army.getFood().toString() + " ";
		}
		Boolean fed = army.computeFed();

		foodStr += (fed != null && fed == true ? "Fed" : "Unfed");
		if ("yes".equals(PreferenceRegistry.instance().getPreferenceValue("currentHexView.showNumberOfFedTurnsForArmies"))) {
			if (Boolean.TRUE.equals(fed)) {
				Integer food = army.getFood();
				Integer consumption = army.computeFoodConsumption();
				if (food != null && consumption != null && consumption > 0) {
					int turns = (food - 1) / consumption;
					foodStr += " (" + turns + ")";
				}
			}
		}
		if (fed != null && fed == true) {
			foodTooltip = "Move orders treat this army as fed.";
		} else {
			foodTooltip = "Move orders treat this army as unfed.";
		}
		food.setText(foodStr);
		food.setToolTipText(foodTooltip);
		// armyMorale.setText("M: 0");

		String cavString = "";
		String cavTooltip = "";
		Boolean isCav = army.computeCavalry();
		if (isCav == null) {
			cavString = "";
			cavTooltip = "";
		} else if (isCav) {
			cavString = "Cav";
			cavTooltip = "Move orders treat this army as cavalry.";
		} else {
			cavString = "Inf";
			cavTooltip = "Move orders treat this army as infantry.";
		}
		cav.setText(cavString);
		cav.setToolTipText(cavTooltip);

		if (army.getCharacters().size() > 0) {
			travellingWith.setVisible(true);
			String txt = "";
			for (String cn : army.getCharacters()) {
				txt += (txt.equals("") ? "" : ",") + cn;
			}
			travellingWith.setText("With army: " + txt);
		} else {
			travellingWith.setVisible(false);
		}
	}

	private JPopupMenu createArmyPopupContextMenu() {
		ArrayList<Object> commands = new ArrayList<Object>(Arrays.asList(toggleFedAction, toggleCavAction, editArmyCommand, deleteArmyCommand, "separator", showArmyMovementRangeAction, showArmyMovementIgnorePopsRangeAction, "separator", new ShowCanCaptureAction(), new ShowRequiredTransportsCommand(), new ShowRequiredFoodCommand(), "separator", new ShowInfoSourcePopupCommand(((Army) getFormObject()).getInfoSource())));
		if ("yes".equals(PreferenceRegistry.instance().getPreferenceValue("general.developerOptions"))) {
			commands.add("separator");
			commands.add(exportCombatArmyCodeCommand);
		}
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("armyCommandGroup", commands.toArray());
		return group.createPopupMenu();
	}

	private class ShowArmyMovementRangeAction extends ShowArmyMovementRangeGenericAction {
		public ShowArmyMovementRangeAction() {
			super("showArmyMovementRangeAction", false);
		}
	}

	private class ShowArmyMovementRangeIgnorePopsAction extends ShowArmyMovementRangeGenericAction {
		public ShowArmyMovementRangeIgnorePopsAction() {
			super("showArmyMovementRangeIgnorePopsAction", true);
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
			ArmyRangeMapItem armi = new ArmyRangeMapItem(army, ignoreEnemyPops);
			AbstractMapItem.add(armi);

			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance().getSelectedHex(), this));
		}
	}

	private class ToggleFedAction extends ActionCommand {

		public ToggleFedAction() {
			super("toggleFedAction");
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
			super("showCanCaptureAction");
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (org.joverseer.domain.Army) getFormObject();
			String str = "";
			for (PopulationCenterSizeEnum pcSize : PopulationCenterSizeEnum.values()) {
				for (int f = FortificationSizeEnum.values().length - 1; f >= 0; f--) {
					FortificationSizeEnum fort = FortificationSizeEnum.values()[f];
					int i = CombatUtils.canCapturePopCenter(a, pcSize, fort);
					if (i > -1) {
						str = pcSize + "/" + fort + " at loyalty " + i + "\n" + str;
					}
					if (i == 100)
						break;
				}
			}
			if (!str.equals("")) {
				str = "Army can capture (assume Hated relations and no defending army):\n" + str;
				str += "Note: The numbers are rough estimates. To get accurate numbers it is advised to do the calculations by hand.";
				MessageDialog dlg = new MessageDialog("Army vs Pop Center", str);
				dlg.showDialog();
			}
		}
	}

	private class ToggleCavAction extends ActionCommand {

		public ToggleCavAction() {
			super("toggleCavAction");
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (org.joverseer.domain.Army) getFormObject();
			Boolean cav = a.computeCavalry();
			a.setCavalry(cav == null || cav != true ? true : false);
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), MapPanel.instance().getSelectedHex(), this));
		}
	}

	private class DeleteArmyCommand extends ActionCommand {
		boolean cancel = true;

		@Override
		protected void doExecuteCommand() {
			cancel = true;
			Army a = (Army) getFormObject();
			ConfirmationDialog cdlg = new ConfirmationDialog("Warning", "Are you sure you want to delete army '" + a.getCommanderName() + "'?") {
				@Override
				protected void onCancel() {
					super.onCancel();
				}

				@Override
				protected void onConfirm() {
					cancel = false;
				}

			};
			cdlg.showDialog();
			if (cancel)
				return;
			Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
			Turn t = g.getTurn();
			Container armies = t.getContainer(TurnElementsEnum.Army);
			armies.removeItem(a);
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
		}
	}

	private class ExportCombatArmyCodeCommand extends ActionCommand {
		public ExportCombatArmyCodeCommand() {
			super("exportCombatArmyCodeCommand");
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (Army) getFormObject();
			CombatArmy ca = new CombatArmy(a);
			LogManager.getLogger(this.getClass()).info("Exporting code for Combat Army under " + ca.getCommander());
			LogManager.getLogger(this.getClass()).info(ca.getCode());
			LogManager.getLogger(this.getClass()).info("Done exporting code for Combat Army under " + ca.getCommander());
			MessageDialog dlg = new MessageDialog("Army code", ca.getCode());
			dlg.showDialog();
		}
	}

	private class ShowRequiredTransportsCommand extends ActionCommand {
		public ShowRequiredTransportsCommand() {
			super("showRequiredTransportsCommand");
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (Army) getFormObject();
			int requiredTransportCapacity = 0;
			for (ArmyElement ae : a.getElements()) {
				requiredTransportCapacity += ae.getRequiredTransportCapacity();
			}
			int requiredTransports = a.getNumberOfRequiredTransports();
			String msg = "The army requires " + requiredTransports + " transports.";
			if (a.isNavy()) {
				int transports = a.getElement(ArmyElementType.Transports).getNumber();
				int transportInfCapacity = transports * 250;
				int transportCavCapacity = transports * 150;
				msg += "\n" + "The army current has " + transports + " transports and can carry a total of " + transportInfCapacity + " infantry or " + transportCavCapacity + " cavalry.";
				int freeInfCapacity = transportInfCapacity - requiredTransportCapacity;
				int freeCavCapacity = freeInfCapacity * 150 / 250;
				if (freeInfCapacity > 0) {
					msg += "\n" + "There is room for extra " + freeInfCapacity + " infantry or " + freeCavCapacity + " cavalry in the army's transports.";
				} else if (freeInfCapacity == 0) {
					msg += "\n" + "There is no room for extra troops in the army's transports.";
				} else if (freeInfCapacity < 0) {
					msg += "\n" + "The army's troops are too many to be carried by the army's transports.";
				}
			}
			MessageDialog dlg = new MessageDialog("Required Transports", msg);
			dlg.showDialog();
		}
	}

	private class ShowRequiredFoodCommand extends ActionCommand {
		public ShowRequiredFoodCommand() {
			super("showRequiredFoodCommand");
		}

		@Override
		protected void doExecuteCommand() {
			Army a = (Army) getFormObject();
			Integer food = a.computeFoodConsumption();
			if (food == null)
				return;
			String msg = "The army requires " + food + " food per turn.";
			MessageDialog dlg = new MessageDialog("Required Food", msg);
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
					Army a = (Army) getFormObject();
					Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
					Turn t = g.getTurn();
					Container armies = t.getContainer(TurnElementsEnum.Army);
					armies.removeItem(a);
					armies.addItem(a);
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), MapPanel.instance().getSelectedHex(), this));
					return true;
				}
			};
			MessageSource ms = (MessageSource) Application.instance().getApplicationContext().getBean("messageSource");
			dlg.setTitle(ms.getMessage("editArmyDialog.title", new Object[] {}, Locale.getDefault()));
			dlg.showDialog();
		}
	}

	public boolean getShowColor() {
		return showColor;
	}

	public void setShowColor(boolean showColor) {
		this.showColor = showColor;
	}

}
