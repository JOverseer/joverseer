package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.joverseer.domain.HexInfo;
import org.joverseer.domain.Note;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.tools.HexInfoHistory;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.command.AddEditNoteCommand;
import org.joverseer.ui.command.AddPopCenterCommand;
import org.joverseer.ui.command.CreateCombatForHexCommand;
import org.joverseer.ui.command.ShowCharacterFastStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterLongStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterMovementRangeCommand;
import org.joverseer.ui.command.ShowCharacterPathMasteryRangeCommand;
import org.joverseer.ui.command.range.ShowFedCavalryArmyRangeCommand;
import org.joverseer.ui.command.range.ShowFedInfantryArmyRangeCommand;
import org.joverseer.ui.command.range.ShowFedNavyCoastalRangeCommand;
import org.joverseer.ui.command.range.ShowFedNavyOpenSeasRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedCavalryArmyRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedInfantryArmyRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedNavyCoastalRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedNavyOpenSeasRangeCommand;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Shows hex info in the Current Hex View
 * 
 * @author Marios Skounakis
 */
public class HexInfoViewer extends ObjectViewer {

	public static final String FORM_PAGE = "HexInfoViewer"; //$NON-NLS-1$

	HashMap<MovementDirection, JTextField> infCosts = new HashMap<MovementDirection, JTextField>();
	HashMap<MovementDirection, JTextField> cavCosts = new HashMap<MovementDirection, JTextField>();

	JTextField hexNo;
	JTextField climate;
	JTextField turnInfo;
	JTextField terrain;
	NotesViewer notesViewer;

	RemoveBridgeCommand removeBridgeNE = new RemoveBridgeNE();
	RemoveBridgeCommand removeBridgeE = new RemoveBridgeE();
	RemoveBridgeCommand removeBridgeSE = new RemoveBridgeSE();
	RemoveBridgeCommand removeBridgeSW = new RemoveBridgeSW();
	RemoveBridgeCommand removeBridgeW = new RemoveBridgeW();
	RemoveBridgeCommand removeBridgeNW = new RemoveBridgeNW();

	AddBridgeCommand addBridgeNE = new AddBridgeNE();
	AddBridgeCommand addBridgeE = new AddBridgeE();
	AddBridgeCommand addBridgeSE = new AddBridgeSE();
	AddBridgeCommand addBridgeSW = new AddBridgeSW();
	AddBridgeCommand addBridgeW = new AddBridgeW();
	AddBridgeCommand addBridgeNW = new AddBridgeNW();

	public HexInfoViewer(FormModel formModel) {
		super(formModel, FORM_PAGE);
	}

	@Override
	public boolean appliesTo(Object obj) {
		return HexInfo.class.isInstance(obj);
	}

	@Override
	protected JComponent createFormControl() {
		GridBagLayoutBuilder lb = new GridBagLayoutBuilder();
		JLabel l;
		String text;
		lb.append(l = new JLabel(Messages.getString("HexInfoViewer.HexNumColon")), 2, 1); //$NON-NLS-1$
		lb.append(this.hexNo = new JTextField(), 2, 1);
		this.hexNo.setBorder(null);
		lb.append(this.terrain = new JTextField(), 2, 1);
		this.terrain.setPreferredSize(this.uiSizes.newDimension(60/12, this.uiSizes.getHeight3()));
		this.terrain.setBorder(null);
		lb.append(this.climate = new JTextField(), 2, 1);
		this.climate.setPreferredSize(this.uiSizes.newDimension(50/12, this.uiSizes.getHeight3()));
		this.climate.setBorder(null);

		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource"); //$NON-NLS-1$
		JButton btnMenu = new JButton();
		Icon ico = new ImageIcon(imgSource.getImage("menu.icon")); //$NON-NLS-1$
		btnMenu.setIcon(ico);
		btnMenu.setPreferredSize(this.uiSizes.newIconDimension(this.uiSizes.getHeight4()));
		lb.append(btnMenu);
		btnMenu.addActionListener(new PopupMenuActionListener() {

			@Override
			public JPopupMenu getPopupMenu() {
				return createHexInfoPopupContextMenu();
			}
		});

		lb.nextLine();

		lb.append(l = new JLabel(Messages.getString("HexInfoViewer.LatestInfoColon")), 2, 1); //$NON-NLS-1$
		lb.append(this.turnInfo = new JTextField(), 4, 1);
		this.turnInfo.setPreferredSize(this.uiSizes.newDimension(80/12, this.uiSizes.getHeight3()));
		this.turnInfo.setBorder(null);
		lb.nextLine();

		lb.append(new JSeparator(), 5, 1);
		lb.nextLine();

		lb.append(l = new JLabel(Messages.getString("HexInfoViewer.AbbInfantry")), 2, 1); //$NON-NLS-1$
		Font f = new Font(l.getFont().getName(), Font.BOLD, l.getFont().getSize());
		l.setFont(f);
		lb.append(l = new JLabel(" ")); //$NON-NLS-1$
		l.setPreferredSize(this.uiSizes.newDimension(20/12, this.uiSizes.getHeight3()));
		lb.append(l = new JLabel(Messages.getString("HexInfoViewer.AbbCavalry")), 2, 1); //$NON-NLS-1$
		l.setFont(f);
		lb.nextLine();
		for (MovementDirection md : MovementDirection.values()) {
			text = Messages.getString("movementCosts." + md.getDir());
			lb.append(new JLabel(text + " :")); //$NON-NLS-1$
			JTextField tf = new JTextField();
			tf.setHorizontalAlignment(SwingConstants.RIGHT);
			this.infCosts.put(md, tf);
			tf.setBorder(null);
			tf.setPreferredSize(this.uiSizes.newDimension(20/12, this.uiSizes.getHeight3()));
			lb.append(tf);

			lb.append(l = new JLabel(" ")); //$NON-NLS-1$

			lb.append(new JLabel(text + " :")); //$NON-NLS-1$
			tf = new JTextField();
			tf.setHorizontalAlignment(SwingConstants.RIGHT);
			this.cavCosts.put(md, tf);
			tf.setBorder(null);
			tf.setPreferredSize(this.uiSizes.newDimension(20/12, this.uiSizes.getHeight3()));
			lb.append(tf);
			lb.nextLine();
		}

		this.notesViewer = new NotesViewer(FormModelHelper.createFormModel(new ArrayList<Note>()));
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(this.notesViewer.createFormControl(), "colspec=left:240px"); //$NON-NLS-1$
		JPanel notesPanel = tlb.getPanel();
		notesPanel.setBackground(Color.white);
		lb.append(notesPanel, 8, 1);
		lb.nextLine();

		JPanel panel = lb.getPanel();
		panel.setBackground(Color.WHITE);
		return panel;
	}

	@Override
	public void setFormObject(Object object) {
		super.setFormObject(object);
		if (object != null) {
			Hex h = (Hex) object;
			String hexNoStr = String.valueOf(h.getColumn());
			if (h.getColumn() < 10) {
				hexNoStr = "0" + hexNoStr; //$NON-NLS-1$
			}
			if (h.getRow() < 10) {
				hexNoStr = hexNoStr + "0"; //$NON-NLS-1$
			}
			hexNoStr += String.valueOf(h.getRow());
			this.hexNo.setText(hexNoStr);

			Integer latestTurnInfo = HexInfoHistory.getLatestHexInfoTurnNoForHex(h.getHexNo());
			if (latestTurnInfo == null || latestTurnInfo == -1) {
				this.turnInfo.setText(Messages.getString("HexInfoViewer.Never")); //$NON-NLS-1$
			} else {
				this.turnInfo.setText(Messages.getString("HexInfoViewer.Turn") + latestTurnInfo); //$NON-NLS-1$
			}

			Game g = GameHolder.instance().getGame();
			HexInfo hi = (HexInfo) g.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", h.getHexNo()); //$NON-NLS-1$
			this.climate.setText(hi.getClimate() != null ? hi.getClimate().toString() : ""); //$NON-NLS-1$
			this.terrain.setText(UIUtils.enumToString(h.getTerrain()));
			this.terrain.setCaretPosition(0);
			int startHexNo = h.getColumn() * 100 + h.getRow();
			if (startHexNo > 0) {
				for (MovementDirection md : MovementDirection.values()) {
					int cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), false, true, true, null, startHexNo);
					JTextField tf = this.infCosts.get(md);
					String costStr = (cost > 0 ? String.valueOf(cost) : "-"); //$NON-NLS-1$
					tf.setText(String.valueOf(costStr));

					cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), true, true, true, null, startHexNo);
					tf = this.cavCosts.get(md);
					costStr = (cost > 0 ? String.valueOf(cost) : "-"); //$NON-NLS-1$
					tf.setText(String.valueOf(costStr));
				}
			} else {
				for (JTextField tf : this.infCosts.values()) {
					tf.setText(""); //$NON-NLS-1$
				}
				for (JTextField tf : this.cavCosts.values()) {
					tf.setText(""); //$NON-NLS-1$
				}
			}

			ArrayList<Note> notes = g.getTurn().getContainer(TurnElementsEnum.Notes).findAllByProperty("target", h.getHexNo()); //$NON-NLS-1$
			this.notesViewer.setFormObject(notes);
		}
	}

	public JPopupMenu createHexInfoPopupContextMenu() {
		Hex hex = (Hex) getFormObject();
		ActionCommand showCharacterLongStrideRangeCommand = new ShowCharacterLongStrideRangeCommand(hex.getHexNo());
		ActionCommand showCharacterFastStrideRangeCommand = new ShowCharacterFastStrideRangeCommand(hex.getHexNo());
		ActionCommand showCharacterPathMasteryRangeCommand = new ShowCharacterPathMasteryRangeCommand(hex.getHexNo());
		ActionCommand showCharacterRangeOnMapCommand = new ShowCharacterMovementRangeCommand(hex.getHexNo(), 12);

		CommandGroup bridges = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("hexInfoBridgeGroup", new Object[] { this.addBridgeNE, this.addBridgeE, this.addBridgeSE, this.addBridgeSW, this.addBridgeW, this.addBridgeNW, "separator", this.removeBridgeNE, this.removeBridgeE, this.removeBridgeSE, this.removeBridgeSW, this.removeBridgeW, this.removeBridgeNW }); //$NON-NLS-1$ //$NON-NLS-2$
		int hexNo1 = hex.getHexNo();
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("hexInfoCommandGroup",  //$NON-NLS-1$
				new Object[] { showCharacterRangeOnMapCommand, showCharacterLongStrideRangeCommand, showCharacterFastStrideRangeCommand, showCharacterPathMasteryRangeCommand, "separator", new ShowFedInfantryArmyRangeCommand(hexNo1), new ShowUnfedInfantryArmyRangeCommand(hexNo1), new ShowFedCavalryArmyRangeCommand(hexNo1), new ShowUnfedCavalryArmyRangeCommand(hexNo1), "separator", new ShowFedNavyCoastalRangeCommand(hexNo1), new ShowUnfedNavyCoastalRangeCommand(hexNo1), new ShowFedNavyOpenSeasRangeCommand(hexNo1), new ShowUnfedNavyOpenSeasRangeCommand(hexNo1), "separator", new AddPopCenterCommand(hexNo1), new AddEditNoteCommand(hexNo1), "separator", new CreateCombatForHexCommand(hexNo1), "separator", bridges //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

		});
		return group.createPopupMenu();
	}

	public class RemoveBridgeSE extends RemoveBridgeCommand {
		public RemoveBridgeSE() {
			super(HexSideEnum.BottomRight);
		}
	}

	public class RemoveBridgeE extends RemoveBridgeCommand {
		public RemoveBridgeE() {
			super(HexSideEnum.Right);
		}
	}

	public class RemoveBridgeSW extends RemoveBridgeCommand {
		public RemoveBridgeSW() {
			super(HexSideEnum.BottomLeft);
		}
	}

	public class RemoveBridgeNE extends RemoveBridgeCommand {
		public RemoveBridgeNE() {
			super(HexSideEnum.TopRight);
		}
	}

	public class RemoveBridgeW extends RemoveBridgeCommand {
		public RemoveBridgeW() {
			super(HexSideEnum.Left);
		}
	}

	public class RemoveBridgeNW extends RemoveBridgeCommand {
		public RemoveBridgeNW() {
			super(HexSideEnum.TopLeft);
		}
	}

	public class RemoveBridgeCommand extends ActionCommand {
		HexSideEnum side;

		public RemoveBridgeCommand(HexSideEnum side) {
			super();
			this.side = side;
		}

		@Override
		protected void doExecuteCommand() {
			Hex hex = (Hex) getFormObject();
			HexSideElementEnum hse = null;
			for (HexSideElementEnum hsei : hex.getHexSideElements(this.side)) {
				if (hsei == HexSideElementEnum.Bridge) {
					hse = hsei;
				}
			}
			if (hse != null) {
				Game g = GameHolder.instance().getGame();
				GameMetadata gm = g.getMetadata();
				Hex nh = hex.clone();
				nh.getHexSideElements(this.side).remove(hse);
				gm.addHexOverride(g.getCurrentTurn(), nh);
				Hex neighbor = gm.getHex(this.side.getHexNoAtSide(hex.getHexNo())).clone();
				neighbor.getHexSideElements(this.side.getOppositeSide()).remove(HexSideElementEnum.Bridge);
				gm.addHexOverride(g.getCurrentTurn(), neighbor);

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));

			}
		}
	}

	public class AddBridgeSE extends AddBridgeCommand {
		public AddBridgeSE() {
			super(HexSideEnum.BottomRight);
		}
	}

	public class AddBridgeE extends AddBridgeCommand {
		public AddBridgeE() {
			super(HexSideEnum.Right);
		}
	}

	public class AddBridgeSW extends AddBridgeCommand {
		public AddBridgeSW() {
			super(HexSideEnum.BottomLeft);
		}
	}

	public class AddBridgeNE extends AddBridgeCommand {
		public AddBridgeNE() {
			super(HexSideEnum.TopRight);
		}
	}

	public class AddBridgeW extends AddBridgeCommand {
		public AddBridgeW() {
			super(HexSideEnum.Left);
		}
	}

	public class AddBridgeNW extends AddBridgeCommand {
		public AddBridgeNW() {
			super(HexSideEnum.TopLeft);
		}
	}

	public class AddBridgeCommand extends ActionCommand {
		HexSideEnum side;

		public AddBridgeCommand(HexSideEnum side) {
			super();
			this.side = side;
		}

		@Override
		protected void doExecuteCommand() {
			Hex hex = (Hex) getFormObject();
			boolean hasRoad = false;
			boolean hasMinorRiver = false;
			boolean hasMajorRiver = false;
			for (HexSideElementEnum hsei : hex.getHexSideElements(this.side)) {
				if (hsei == HexSideElementEnum.Bridge) {
					return;
				}
				if (hsei == HexSideElementEnum.MinorRiver) {
					hasMinorRiver = true;
				}
				if (hsei == HexSideElementEnum.Road) {
					hasRoad = true;
				}
				if (hsei == HexSideElementEnum.MajorRiver) {
					hasMajorRiver = true;
				}
			}
			if (!hasMinorRiver && !(hasMajorRiver && hasRoad))
				return;
			Hex nh = hex.clone();
			nh.addHexSideElement(this.side, HexSideElementEnum.Bridge);
			Game g = GameHolder.instance().getGame();
			GameMetadata gm = g.getMetadata();
			gm.addHexOverride(g.getCurrentTurn(), nh);
			Hex neighbor = gm.getHex(this.side.getHexNoAtSide(nh.getHexNo())).clone();
			neighbor.addHexSideElement(this.side.getOppositeSide(), HexSideElementEnum.Bridge);
			gm.addHexOverride(g.getCurrentTurn(), neighbor);

			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));
		}
	}

}
