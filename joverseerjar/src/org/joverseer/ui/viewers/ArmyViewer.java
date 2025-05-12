package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.log4j.LogManager;
import org.joverseer.JOApplication;
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
import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.CombatUtils;
import org.joverseer.tools.armySizeEstimator.ArmySizeEstimate;
import org.joverseer.tools.armySizeEstimator.ArmySizeEstimator;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.command.range.ShowFedNavyCoastalRangeCommand;
import org.joverseer.ui.command.range.ShowFedNavyOpenSeasRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedNavyCoastalRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedNavyOpenSeasRangeCommand;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.ArmyRangeMapItem;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.commands.ShowInfoSourcePopupCommand;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.ui.views.EditArmyForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
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
	JTextField extraInfo;
	JTextField extraInfo2;
	JTextField food;
	JTextField travellingWith;
	
	int infoFieldIter = 0;
	
	String armySizeStr;
	String armyTypeStr;
	String cavStr;
	JTextField armyInfoText;
	JTextField movementType;

	ActionCommand showArmyMovementRangeAction = new ShowArmyMovementRangeAction();
	ActionCommand showArmyMovementIgnorePopsRangeAction = new ShowArmyMovementRangeIgnorePopsAction();
	ActionCommand toggleFedAction = new ToggleFedAction();
	ActionCommand toggleCavAction = new ToggleCavAction();
	ActionCommand deleteArmyCommand = new DeleteArmyCommand();
	ActionCommand editArmyCommand = new EditArmyCommand();
	ActionCommand exportCombatArmyCodeCommand = new ExportCombatArmyCodeCommand();

	public ArmyViewer(FormModel formModel,GameHolder gameHolder) {
		super(formModel, FORM_PAGE,gameHolder);
	}

	@Override
	public boolean appliesTo(Object obj) {
		return Army.class.isInstance(obj);
	}

	@Override
	protected JComponent createFormControl() {
		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		glb.setDefaultInsets(new Insets(0, 0, 0, 5));

		glb.append(this.commanderName = new JTextField(), 1, 1, 4, 0);
		this.commanderName.setFont(new Font(this.commanderName.getFont().getName(), Font.BOLD, this.commanderName.getFont().getSize()));

		this.commanderName.setPreferredSize(this.uiSizes.newDimension((this.commanderName.getFontMetrics(this.commanderName.getFont()).charWidth('M') * 24)/12, this.uiSizes.getHeight3()));
		glb.append(this.nation = new JTextField());
		this.nation.setPreferredSize(this.uiSizes.newDimension((this.nation.getFontMetrics(this.nation.getFont()).charWidth('M') * 6)/12, this.uiSizes.getHeight3()));

		// button to show range of army on map
		ImageSource imgSource = JOApplication.getImageSource();
		JButton btnMenu = new JButton();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon")); //$NON-NLS-1$
		btnMenu.setPreferredSize(this.uiSizes.newIconDimension(this.uiSizes.getHeight4()));
		btnMenu.setIcon(ico);
		glb.append(btnMenu);
		btnMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				return createArmyPopupContextMenu();
			}
		});
		
		glb.nextLine();
		glb.append(this.travellingWith = new JTextField(), 2, 1);
		this.travellingWith.setPreferredSize(this.uiSizes.newDimension(150/12, this.uiSizes.getHeight3()));
		Font f = GraphicUtils.getFont(this.travellingWith.getFont().getName(), Font.ITALIC, 11);
		this.travellingWith.setFont(f);
		
		glb.nextLine();
		glb.append(this.armyInfoText = new JTextField(), 2, 1);
		this.armyInfoText.setFont(new Font(this.armyInfoText.getFont().getName(), Font.BOLD, this.armyInfoText.getFont().getSize()));
		this.armyInfoText.setPreferredSize(this.uiSizes.newDimension(120/12, this.uiSizes.getHeight3()));
		
		glb.nextLine();
		glb.nextLine();
		glb.append(this.extraInfo = new JTextField(), 2, 1);
		this.extraInfo.setPreferredSize(this.uiSizes.newDimension((this.commanderName.getFontMetrics(this.commanderName.getFont()).charWidth('M') * 20)/12, this.uiSizes.getHeight3() +2));
		glb.nextLine();
		glb.append(this.extraInfo2 = new JTextField(), 2, 1);
		this.extraInfo2.setPreferredSize(this.uiSizes.newDimension((this.commanderName.getFontMetrics(this.commanderName.getFont()).charWidth('M') * 20)/12, this.uiSizes.getHeight3() +2));
		glb.nextLine();
		
		
		glb.append(this.movementType = new JTextField());
		this.movementType.setPreferredSize(this.uiSizes.newDimension(100/12, this.uiSizes.getHeight3()));
		Font fon = new Font(this.movementType.getFont().getName(),this.movementType.getFont().getStyle(),11);
		this.movementType.setFont(fon);
		this.movementType.setBorder(null);
		
		this.armyInfoText.setBorder(null);
		this.commanderName.setBorder(null);
		this.nation.setBorder(null);
		this.extraInfo.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
		this.extraInfo2.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
		this.travellingWith.setBorder(null);

		JPanel panel = glb.getPanel();
		panel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.setBackground(UIManager.getColor("Panel.background"));
		return panel;
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
		
		Game game = this.gameHolder.getGame();
		if (game == null)
			return;
		game.getMetadata();
		Nation armyNation = army.getNation();
		this.nation.setText(armyNation.getShortName());
		this.nation.setCaretPosition(0);
		this.nation.setToolTipText(armyNation.getName());

		this.armySizeStr = army.getSize().toString();
		this.armyTypeStr = army.isNavy() ? Messages.getString("ArmyViewer.Navy") : Messages.getString("ArmyViewer.Army");
		this.resetExtraInfo();

		if (army.getElements().size() > 0) {
			for (ArmyElement element : army.getElements()) {
				this.appendItemExtraInfo(element.getLocalizedDescription() + " ");
			}
			String pval = PreferenceRegistry.instance().getPreferenceValue("currentHexView.showNHIEquivalents"); //$NON-NLS-1$
			if (pval != null && pval.equals("yes")) { //$NON-NLS-1$
//				GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", army.getCommanderName()); //$NON-NLS-1$
				int nhi = army.getENHI();
				if (nhi > 0) {
					this.appendItemExtraInfo(Messages.getString("ArmyViewer.enHI",new Object[] { nhi}));
				}
			}
		} else if (army.getTroopCount() > 0) {
			this.appendItemExtraInfo(Messages.getString("ArmyViewer.aboutNMen",new Object[] {army.getTroopCount()}));
		} else if (army.getSize() != ArmySizeEnum.unknown && !army.isNavy()) {
			ArmySizeEstimate ae = (new ArmySizeEstimator()).getSizeEstimateForArmySize(army.getSize(), ArmySizeEstimate.ARMY_TYPE);
			if (ae == null || ae.getMin() == null) {
				this.extraInfo.setVisible(false);
				this.extraInfo2.setVisible(false);
			} else {
				this.appendItemExtraInfo(Messages.getString("ArmyViewer.estMinToMaxMen", new Object[] {ae.getMin(), ae.getMax()}));
			}
		} else {
			this.extraInfo.setVisible(false);
			this.extraInfo2.setVisible(false);
		}
		if (army.getElements().size() == 0 && "yes".equals(PreferenceRegistry.instance().getPreferenceValue("currentHexView.showArmyEstimate"))) { //$NON-NLS-1$ //$NON-NLS-2$
			ArmyEstimate estimate = (ArmyEstimate) game.getTurn().getContainer(TurnElementsEnum.ArmyEstimate).findFirstByProperty("commanderName", army.getCommanderName()); //$NON-NLS-1$
			if (estimate != null) {
				String troopInfo = estimate.getTroopInfo();
				if (!troopInfo.equals("")) { //$NON-NLS-1$
					this.appendItemExtraInfo(" [~" + troopInfo + "]");
				}
			}
		}

		this.extraInfo.setCaretPosition(0);
		this.extraInfo2.setCaretPosition(0);
				
		String foodStr = ""; //$NON-NLS-1$
		String foodTooltip = ""; //$NON-NLS-1$
		String foodNum = "";
		if (army.getFood() != null) {
			foodNum = "(" + army.getFood().toString() + " food) "; //$NON-NLS-1$
		}
		Boolean fed = army.computeFed();

		foodStr += (fed != null && fed == true ? Messages.getString("ArmyViewer.Fed") : Messages.getString("ArmyViewer.Unfed")); //$NON-NLS-1$ //$NON-NLS-2$
		foodStr += (foodStr == "" ? "" : " ");
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
					if (turns==0) turns=1;
					foodStr += " (" + turns + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					foodTooltip = "<html>" + Messages.getString("ArmyViewer.ArmyFoodForNTurns", new Object[] {turns}) + foodTooltip + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		}
		
		this.cavStr = ""; //$NON-NLS-1$

		this.cavStr = this.computeMovementTypeUnit(army);
		
		this.armyInfoText.setText(this.armyTypeStr + " of " + this.armySizeStr + " size.");
		this.movementType.setText("Movement Type: " + foodStr + foodNum + this.cavStr);
		
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
	
	/*
	 * Computes and returns what the movement type of the army, outputing whether it is 'Navy' 'Inf' or 'Cav
	 */
	private String computeMovementTypeUnit(Army a) {
		if(a.isNavy()) {
			HexTerrainEnum ter = this.gameHolder.getGame().getMetadata().getHex(Integer.parseInt(a.getHexNo())).getTerrain();
			if(ter.isOpenSea()) {
				return Messages.getString("ArmyViewer.Navy");
			}
			if (a.getElements().size() == 0) {}
			else if (a.getElement(ArmyElementType.Transports) == null) {}
			else if(a.getNumberOfRequiredTransports() <= a.getElement(ArmyElementType.Transports).getNumber()) {
				return Messages.getString("ArmyViewer.Navy");
			}
		}
		Boolean isCav = a.computeCavalry();
		
		if (isCav == null) {
			return ""; //$NON-NLS-1$
		} else if (isCav) {
			return Messages.getString("ArmyViewer.AbbCavalry") + " "; //$NON-NLS-1$
		} else {
			return Messages.getString("ArmyViewer.AbbInfantry") + " "; //$NON-NLS-1$
		}
	}
	
	private void appendItemExtraInfo(String str) {
		if (this.infoFieldIter <= 5) {
			this.extraInfo.setVisible(true);
			this.extraInfo.setText(this.extraInfo.getText() + str);
		}
		else {
			this.extraInfo2.setVisible(true);
			this.extraInfo2.setText(this.extraInfo2.getText() + str);
		}
		this.infoFieldIter += 1;
	}
	
	private void resetExtraInfo() {
		this.infoFieldIter = 0;
		this.extraInfo2.setVisible(false);
		this.extraInfo.setText("");	//$NON-NLS-1$
		this.extraInfo2.setText("");	//$NON-NLS-1$
	}

	private JPopupMenu createArmyPopupContextMenu() {
		
		Army myA = (Army) getFormObject();
		int hexNo = Integer.valueOf(myA.getHexNo());
		
		ArrayList<Object> commands;
		if (myA.isNavy()) {
			commands = new ArrayList<Object>(Arrays.asList(this.toggleFedAction, this.toggleCavAction, this.editArmyCommand, 
			this.deleteArmyCommand, "separator", this.showArmyMovementRangeAction, this.showArmyMovementIgnorePopsRangeAction, "separator", 
			new ShowFedNavyCoastalRangeCommand(hexNo), new ShowUnfedNavyCoastalRangeCommand(hexNo), new ShowFedNavyOpenSeasRangeCommand(hexNo), new ShowUnfedNavyOpenSeasRangeCommand(hexNo), "separator", 
			new ShowCanCaptureAction(), new ShowRequiredTransportsCommand(), new ShowRequiredFoodCommand(), "separator", 
			new ShowInfoSourcePopupCommand(((Army) getFormObject()).getInfoSource()))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else {
			commands = new ArrayList<Object>(Arrays.asList(this.toggleFedAction, this.toggleCavAction, this.editArmyCommand, 
					this.deleteArmyCommand, "separator", this.showArmyMovementRangeAction, this.showArmyMovementIgnorePopsRangeAction, "separator",  
					new ShowCanCaptureAction(), new ShowRequiredTransportsCommand(), new ShowRequiredFoodCommand(), "separator", 
					new ShowInfoSourcePopupCommand(((Army) getFormObject()).getInfoSource()))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
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

			JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, MapPanel.instance().getSelectedHex(), this);
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
			JOApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent, MapPanel.instance().getSelectedHex(), this);
			JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, MapPanel.instance().getSelectedHex(), this);
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
			JOApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent, MapPanel.instance().getSelectedHex(), this);
			JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, MapPanel.instance().getSelectedHex(), this);
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
			Game g = ArmyViewer.this.gameHolder.getGame();
			Turn t = g.getTurn();
			Container<Army> armies = t.getArmies();
			armies.removeItem(a);
			JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, MapPanel.instance().getSelectedHex(), this);
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
			
			String message = Messages.getString("ArmyViewer.RequiredFood.text",new Object[] { food1 }); //$NON-NLS-1$
			if (a.getFoodFromPop() > 0) {
				message += " The population center is providing " + a.getFoodFromPop() + " food this turn.";
			}
			
			MessageDialog dlg = new MessageDialog(Messages.getString("ArmyViewer.RequiredFood.title"),
				message);	
			dlg.showDialog();
		}
	}

	private class EditArmyCommand extends ActionCommand {
		@Override
		protected void doExecuteCommand() {
			Army a = (Army) getFormObject();
			final EditArmyForm form = new EditArmyForm(FormModelHelper.createFormModel(a),ArmyViewer.this.gameHolder);
			FormBackedDialogPage pg = new FormBackedDialogPage(form);
			CustomTitledPageApplicationDialog dlg = new CustomTitledPageApplicationDialog(pg) {

				@Override
				protected void onAboutToShow() {
					super.onAboutToShow();
					form.setFormObject(getFormObject());
				}

				@Override
				protected boolean onFinish() {
					form.commit();
					Army a1 = (Army) getFormObject();
					Game g = ArmyViewer.this.gameHolder.getGame();
					Turn t = g.getTurn();
					Container<Army> armies = t.getArmies();
					armies.removeItem(a1);
					armies.addItem(a1);
					JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, MapPanel.instance().getSelectedHex(), this);
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
