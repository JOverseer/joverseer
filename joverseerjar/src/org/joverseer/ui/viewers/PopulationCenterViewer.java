package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.joverseer.JOApplication;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.InfoSourceValue;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.DerivedFromInfluenceOtherInfoSource;
import org.joverseer.support.infoSources.PopCenterXmlInfoSource;
import org.joverseer.tools.CombatUtils;
import org.joverseer.tools.PopulationCenterLoyaltyEstimator;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.tools.combatCalc.CombatPopCenter;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.commands.ShowInfoSourcePopupCommand;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.joverseer.ui.views.EditPopulationCenterForm;
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
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Shows pcs in the Current Hex View
 *
 * @author Marios Skounakis
 */
public class PopulationCenterViewer extends ObjectViewer {
	public static final String FORM_PAGE = "PopulationCenterViewer"; //$NON-NLS-1$

	boolean showColor = true;

	JTextField loyalty;
	JTextField name;
	JTextField nation;
	JTextField sizeFort;
	JTextField lostThisTurn;
	JTextField productionDescription;
	JTextField turnInfo;
	HashMap<ProductEnum, JTextField> production = new HashMap<ProductEnum, JTextField>();
	HashMap<ProductEnum, JTextField> stores = new HashMap<ProductEnum, JTextField>();
	ArrayList<JLabel> productionLabels = new ArrayList<JLabel>();

	ActionCommand editPopulationCenter = new EditPopulationCenterCommand();
	ActionCommand toggleLostThisTurnCommand = new ToggleLostThisTurnCommand();
	ActionCommand deletePopulationCenterCommand = new DeletePopulationCenterCommand();

	public PopulationCenterViewer(FormModel formModel,GameHolder gameHolder) {
		super(formModel, FORM_PAGE,gameHolder);
	}

	@Override
	public boolean appliesTo(Object obj) {
		return PopulationCenter.class.isInstance(obj);
	}

	@Override
	public void setFormObject(Object object) {
		super.setFormObject(object);

		PopulationCenter pc = (PopulationCenter) object;
		Game game = this.gameHolder.getGame();
		if (game == null)
			return;

		this.name.setText(GraphicUtils.parseName(pc.getName()));
		this.name.setCaretPosition(0);
		GameMetadata gm = game.getMetadata();

		int nationNo = 0;
		int loyaltyNo = 0;
		if (pc.getSize() != PopulationCenterSizeEnum.ruins) {
			nationNo = pc.getNationNo();
			loyaltyNo = pc.getLoyalty();
		}
		if (loyaltyNo == 0) {
			InfoSourceValue isv = PopulationCenterLoyaltyEstimator.getLoyaltyEstimateForPopCenter(pc);
			if (isv != null) {
				String txt = Messages.getString("PopulationCenterViewer.FromTurn", new Object[] {isv.getValue(),((DerivedFromInfluenceOtherInfoSource) isv.getInfoSource()).getTurnNo()}); //$NON-NLS-1$
				this.loyalty.setText(txt);
			} else {
				this.loyalty.setText(String.valueOf(loyaltyNo));
			}
		} else {
			this.loyalty.setText(String.valueOf(loyaltyNo));
		}

		Nation pcNation = gm.getNationByNum(nationNo);
		if (pcNation == null) {
			pcNation = gm.getNationByNum(0);
		}
		this.nation.setText(pcNation.getShortName());
		this.nation.setCaretPosition(0);
		this.nation.setToolTipText(pcNation.getName());
		this.sizeFort.setText(UIUtils.enumToString(pc.getSize()));
		if (!pc.getFortification().equals(FortificationSizeEnum.none)) {
			this.sizeFort.setText(this.sizeFort.getText() + " - " + UIUtils.enumToString(pc.getFortification())); //$NON-NLS-1$
		}
		if (pc.getHarbor() != HarborSizeEnum.none) {
			this.sizeFort.setText(this.sizeFort.getText() + " - " + UIUtils.enumToString(pc.getHarbor())); //$NON-NLS-1$
		}
		this.sizeFort.setCaretPosition(0);

		// show production
		// if the pop center is a ruin, search in past turns and find the first
		// turn where the pop center was not a ruin and report that production
		PopulationCenter pcForProduction = pc;
		Turn productionTurn = null;
		if (!hasProduction(pcForProduction)) {
			for (int i = game.getMaxTurn() - 1; i >= 0; i--) {
				productionTurn = game.getTurn(i);
				if (productionTurn == null)
					continue;
				PopulationCenter pop = productionTurn.getPopCenter(pc.getHexNo()); //$NON-NLS-1$
				if (pop == null)
					continue;
				if (pop.getSize() != PopulationCenterSizeEnum.ruins) {
					if (hasProduction(pop)) {
						pcForProduction = pop;
						break;
					}
				}
			}
		}
		// one of the tests will be redundant, but it keeps the compiler quiet.
		if ((pcForProduction != pc) && (productionTurn != null)) {
			this.productionDescription.setText(Messages.getString("PopulationCenterViewer.ProductionAtTurn", new Object[] { productionTurn.getTurnNo(), pcForProduction.getSize()})); //$NON-NLS-1$
			this.productionDescription.setVisible(true);
			setStoresVisible(false);
			setProductionLabelsVisible(true);
			setProductionVisible(true);
		} else {
			this.productionDescription.setVisible(false);
			// see if we have any production
			boolean hasProduction = false;
			for (ProductEnum pe : ProductEnum.values()) {
				if (pc.getProduction(pe) != null) {
					hasProduction = true;
					break;
				}
			}
			setAllProductionVisible(hasProduction);
		}

		for (ProductEnum p : ProductEnum.values()) {
			JTextField tf = this.production.get(p);
			Integer amt = pcForProduction.getProduction(p);
			String amtStr;
			if (amt == null || amt == 0) {
				amtStr = "  -"; //$NON-NLS-1$
			} else {
				amtStr = String.valueOf(amt);
			}
			tf.setText(amtStr);

			tf = this.stores.get(p);
			amt = pcForProduction.getStores(p);
			if (amt == null || amt == 0) {
				amtStr = "  -"; //$NON-NLS-1$
			} else {
				amtStr = String.valueOf(amt);
			}
			tf.setText(amtStr);

		}

		if (pc.getLostThisTurn()) {
			this.lostThisTurn.setText(Messages.getString("PopulationCenterViewer.ExpectedToLose")); //$NON-NLS-1$
			this.lostThisTurn.setVisible(true);
		} else {
			this.lostThisTurn.setVisible(false);
		}
		String turnInfoStr = ""; //$NON-NLS-1$
		if (pc.getSize() != PopulationCenterSizeEnum.ruins) {
			if (pc.getInfoSource().getTurnNo() < game.getCurrentTurn()) {
				turnInfoStr = Messages.getString("PopulationCenterViewer.InfoFromT", new Object[] { Math.max(pc.getInfoSource().getTurnNo(), 0)}); //$NON-NLS-1$
			}
			;
			if (PopCenterXmlInfoSource.class.isInstance(pc.getInfoSource())) {
				PopCenterXmlInfoSource is = (PopCenterXmlInfoSource) pc.getInfoSource();
				if (is.getTurnNo() != is.getPreviousTurnNo()) {
					turnInfoStr += (turnInfoStr.equals("") ? "" : " - ") + Messages.getString("PopulationCenterViewer.OwnerFromT", new Object[] { Math.max(is.getPreviousTurnNo(), 0)}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
			}
		}
		this.turnInfo.setText(turnInfoStr);
		this.turnInfo.setVisible(!turnInfoStr.equals("")); //$NON-NLS-1$
		// else if (PopCenterXmlInfoSource.class.isInstance(pc.getInfoSource())
		// &&
		// ((PopCenterXmlInfoSource)pc.getInfoSource()).getPreviousTurnNo() <
		// game.getCurrentTurn()) {
		// turnInfo.setVisible(true);
		// turnInfo.setText("Info from turn " +
		// Math.max(((PopCenterXmlInfoSource)pc.getInfoSource()).getPreviousTurnNo(),
		// 0));
		//
		// }

		if (getShowColor()) {
			Game g = this.gameHolder.getGame();
			Turn t = g.getTurn();

			NationRelations nr = t.getNationRelations(nationNo);
			Color col;
			if (nr == null) {
				col = ColorPicker.getInstance().getColor(NationAllegianceEnum.Neutral.toString());
			} else {
				col = ColorPicker.getInstance().getColor(nr.getAllegiance().toString());
			}
			this.name.setForeground(col);
		}
	}

	protected boolean hasProduction(PopulationCenter pop) {
		for (ProductEnum p : ProductEnum.values()) {
			if (pop.getProduction(p) != null && pop.getProduction(p) > 0) {
				return true;
			}
		}
		return false;
	}

	protected void setStoresVisible(boolean visible) {
		for (JTextField f : this.stores.values()) {
			f.setVisible(visible);
		}
	}

	protected void setProductionVisible(boolean visible) {
		for (JTextField f : this.production.values()) {
			f.setVisible(visible);
		}
	}

	protected void setProductionLabelsVisible(boolean visible) {
		for (JLabel lbl : this.productionLabels) {
			lbl.setVisible(visible);
		}
	}

	protected void setAllProductionVisible(boolean visible) {
		setStoresVisible(visible);
		setProductionVisible(visible);
		setProductionLabelsVisible(visible);
	}

	@Override
	protected JComponent createFormControl() {
		Color col = UIManager.getColor("Panel.background");
		getFormModel().setValidating(false);
		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		glb.setDefaultInsets(new Insets(0, 0, 0, 5));

		JComponent c;

		glb.append(this.name = new JTextField());
		c = this.name;
		c.setPreferredSize(this.uiSizes.newDimension(100/12, this.uiSizes.getHeight3()));
		c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
		c.setBorder(null);
		c.setOpaque(false);

		glb.append(this.sizeFort = new JTextField());
		c = this.sizeFort;
		c.setPreferredSize(this.uiSizes.newDimension(120/12, this.uiSizes.getHeight3()));
		c.setBorder(null);

		ImageSource imgSource = JOApplication.getImageSource();

		JButton btnMenu = new JButton();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon")); //$NON-NLS-1$
		btnMenu.setIcon(ico);
		btnMenu.setPreferredSize(this.uiSizes.newIconDimension(this.uiSizes.getHeight4()));
		glb.append(btnMenu);
		btnMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				return createPopulationCenterPopupContextMenu();
			}
		});

		glb.nextLine();

		glb.append(this.nation = new JTextField());
		c = this.nation;
		c.setBorder(null);

		glb.append(this.loyalty = new JTextField());
		this.loyalty.setBorder(null);
		glb.nextLine();

		glb.append(this.productionDescription = new JTextField(), 2, 1);
		this.productionDescription.setBorder(null);
		Font f = GraphicUtils.getFont(this.productionDescription.getFont().getName(), Font.ITALIC, 11);
		this.productionDescription.setFont(f);
		glb.nextLine();
		f = GraphicUtils.getFont("Arial", Font.PLAIN, 9); //$NON-NLS-1$

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		for (ProductEnum p : ProductEnum.values()) {
			JLabel label = new JLabel(" " + p.getLocalized()); //$NON-NLS-1$
			this.productionLabels.add(label);
			label.setPreferredSize(this.uiSizes.newDimension(28/12, this.uiSizes.getHeight3()));
			label.setFont(f);
			tlb.cell(label);
		}
		tlb.row();
		for (ProductEnum p : ProductEnum.values()) {
			JTextField tf = new JTextField();
			tf.setBorder(null);
			//tf.setPreferredSize(this.uiSizes.newDimension(28/16, this.uiSizes.getHeight4()));
			
			tf.setFont(f);
			tf.setColumns(4);
			tlb.cell(tf);
			this.production.put(p, tf);
		}
		tlb.row();
		for (ProductEnum p : ProductEnum.values()) {
			JTextField tf = new JTextField();
			tf.setBorder(null);
			//tf.setPreferredSize(this.uiSizes.newDimension(28/16, this.uiSizes.getHeight4()));
			tf.setFont(f);
			tf.setColumns(4);
			tlb.cell(tf);
			this.stores.put(p, tf);
		}
		tlb.row();

		tlb.cell(this.lostThisTurn = new JTextField());
		this.lostThisTurn.setBorder(null);
		this.lostThisTurn.setFont(GraphicUtils.getFont(this.lostThisTurn.getFont().getName(), Font.ITALIC, this.lostThisTurn.getFont().getSize()));
		this.lostThisTurn.setPreferredSize(this.uiSizes.newDimension(100/12, this.uiSizes.getHeight4()));

		tlb.row();
		tlb.cell(this.turnInfo = new JTextField());
		this.turnInfo.setBorder(null);
		this.turnInfo.setFont(GraphicUtils.getFont(this.lostThisTurn.getFont().getName(), Font.ITALIC, this.lostThisTurn.getFont().getSize()));
		this.turnInfo.setPreferredSize(this.uiSizes.newDimension(100/12, this.uiSizes.getHeight4()));

		JPanel pnl = tlb.getPanel();
		pnl.setBackground(col);
		glb.append(pnl, 2, 1);

		JPanel panel = glb.getPanel();
		panel.setBackground(col);
		return panel;
	}

	private JPopupMenu createPopulationCenterPopupContextMenu() {
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("populationCenterCommandGroup",
				new Object[] { this.toggleLostThisTurnCommand, "separator", this.editPopulationCenter, this.deletePopulationCenterCommand, "separator", new ShowCaptureInformationCommand(), "separator", new ShowInfoSourcePopupCommand(((PopulationCenter) getFormObject()).getInfoSource()) }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		return group.createPopupMenu();
	}

	private class DeletePopulationCenterCommand extends ActionCommand {
		boolean cancelAction;

		@Override
		protected void doExecuteCommand() {
			this.cancelAction = true;
			PopulationCenter pc = (PopulationCenter) getFormObject();
			ConfirmationDialog cdlg = new ConfirmationDialog(Messages.getString("standardMessages.Warning"),
					Messages.getString("PopulationCenterViewer.AreYouSure", new Object[] { pc.getName() })) { //$NON-NLS-1$
				@Override
				protected void onCancel() {
					super.onCancel();
				}

				@Override
				protected void onConfirm() {
					DeletePopulationCenterCommand.this.cancelAction = false;
				}

			};
			cdlg.showDialog();
			if (this.cancelAction)
				return;
			Game g = PopulationCenterViewer.this.gameHolder.getGame();
			Turn t = g.getTurn();
			Container<PopulationCenter> pcs = t.getPopulationCenters();
			pcs.removeItem(pc);
			JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, MapPanel.instance().getSelectedHex(), this);
		}
	}

	private class ShowCaptureInformationCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			PopulationCenter pc = (PopulationCenter) getFormObject();
			if (pc.getSize().equals(PopulationCenterSizeEnum.ruins))
				return;
			int loyalty1 = pc.getLoyalty();
			boolean estimateLoyalty = false;
			if (loyalty1 == 0) {
				estimateLoyalty = true;
				loyalty1 = pc.lookupSize(new int[]{0,20,35,50,70,90});
			}

			CombatPopCenter combatPc = new CombatPopCenter(pc);
			combatPc.setLoyalty(loyalty1);

			Combat combat = new Combat();
			combat.setSide2Pc(combatPc);
			int strength = combat.computePopCenterStrength(combatPc);

			Game game = PopulationCenterViewer.this.gameHolder.getGame();
			Hex hex = game.getMetadata().getHex(pc.getHexNo());
			HexInfo hi = game.getTurn().getHexInfo(pc.getHexNo());

			boolean estimateClimate = false;
			ClimateEnum climate = ClimateEnum.Mild;
			if (hi == null || hi.getClimate() == null) {
				estimateClimate = true;
				climate = ClimateEnum.Mild;
			}

			CombatArmy ca = CombatUtils.findCombatArmyRequiredToCapturePopCenter(pc.getSize(), pc.getFortification(), loyalty1, climate, hex.getTerrain());
			if (ca == null)
				return;
			int captureEHI = ca.getHI().getNumber();
			int lostEHI = (int) Math.round(captureEHI * ca.getLosses() / 100);

			String msg = Messages.getString("PopulationCenterViewer.CombatStats", new Object[] {
					pc.getName(),strength,captureEHI,lostEHI}); //$NON-NLS-1$
			if (estimateLoyalty) {
				msg += Messages.getString("PopulationCenterViewer.Loyalty", new Object[] {loyalty1}); //$NON-NLS-1$
			}
			if (estimateClimate) {
				msg += Messages.getString("PopulationCenterViewer.UnknownClimate"); //$NON-NLS-1$
			}
			msg += Messages.getString("PopulationCenterViewer.Estimates"); //$NON-NLS-1$

			MessageDialog dlg = new MessageDialog(Messages.getString("PopulationCenterViewer.CombatDialog.title"), msg); //$NON-NLS-1$
			dlg.showDialog();
		}
	}

	private class ToggleLostThisTurnCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			PopulationCenter pc = (PopulationCenter) getFormObject();
			pc.setLostThisTurn(!pc.getLostThisTurn());
			//TODO: just send a message to the EconomyCalculator
			//JOverseerJideViewDescriptor view = JOApplication.findViewInstance("economyCalculatorView");
			JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, MapPanel.instance().getSelectedHex(), this);
		}
	}

	private class EditPopulationCenterCommand extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			final PopulationCenter pc = (PopulationCenter) getFormObject();
			FormModel formModel = FormModelHelper.createFormModel(pc);
			final EditPopulationCenterForm form = new EditPopulationCenterForm(formModel,PopulationCenterViewer.this.gameHolder);
			FormBackedDialogPage page = new FormBackedDialogPage(form);

			CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {

				@Override
				protected void onAboutToShow() {
				}

				@Override
				protected boolean onFinish() {
					form.commit();
					PopulationCenterViewer.this.gameHolder.getGame().getTurn().getPopulationCenters().refreshItem(pc);
					JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, this, this);
					return true;
				}
			};
			dialog.setTitle(Messages.getString("editPopulationCenterDialog.title", new Object[] { String.valueOf(pc.getHexNo()) })); //$NON-NLS-1$
			dialog.showDialog();
		}
	}

	public boolean getShowColor() {
		return this.showColor;
	}

	public void setShowColor(boolean showColor) {
		this.showColor = showColor;
	}

}
