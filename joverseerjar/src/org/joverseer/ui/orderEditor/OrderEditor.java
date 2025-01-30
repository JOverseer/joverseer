package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.JOApplication;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.controls.AutocompletionComboBox;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;

/**
 * The order editor Provides an interactive dynamically changing form for
 * filling in orders
 *
 * It uses the OrderEditorData read from file orderEditorData.csv to dynamically
 * decide what subeditors are needed to supply the order parameters and updates
 * the gui on the fly
 *
 * @author Marios Skounakis
 */
public class OrderEditor extends AbstractForm implements ApplicationListener {

	public static final String FORM_PAGE = "orderEditorForm"; //$NON-NLS-1$
	static final int PREFERRED_TEXT_HEIGHT = 23; // was 18 but cuts off descenders
	protected static final Log staticLogger = LogFactory.getLog("org.joverseer.ui.orderEditor.OrderEditor");
    public final static String DEFAULT_BEAN_ID = "orderEditor";
	JComboBox orderCombo;
	JCheckBox chkDraw;
	JTextField parameters;
	JTextField parametersInternal;
	JTextField character;
	JTextArea description;
	JLabel descriptionLabel;
	JPanel subeditorPanel;
	JPanel myPanel;
	int bkOrderNo = -1;
	String bkParams = ""; //$NON-NLS-1$
    protected boolean hideUnspecifiedParameters = false;


	public boolean isHideUnspecifiedParameters() {
		return this.hideUnspecifiedParameters;
	}

	public void setHideUnspecifiedParameters(boolean hideUnspecifiedParameters) {
		this.hideUnspecifiedParameters = hideUnspecifiedParameters;
	}
	String currentOrderNoAndCode = ""; //$NON-NLS-1$

	ArrayList<JComponent> subeditorComponents = new ArrayList<JComponent>();

	Container<OrderEditorData> orderEditorData = null;

	GameMetadata gm;
	//injected dependencies
	GameHolder gameHolder;

	public OrderEditor(GameHolder gameHolder) {
		super(FormModelHelper.createFormModel(new Order(new Character())), FORM_PAGE);
		this.gameHolder = gameHolder;
	}

	public String getParameters() {
		return this.parameters.getText();
	}

	public Container<OrderEditorData> getOrderEditorData() {
		if (this.orderEditorData == null) {
			this.orderEditorData = OrderEditorData.CreateOrderEditorData("orderEditorData.csv");
		}
		return this.orderEditorData;
	}

	private GameMetadata getGameMetadata() {
		if (this.gm == null) {
			Game g = this.gameHolder.getGame();
			if (!Game.isInitialized(g))
				return null;
			this.gm = g.getMetadata();
		}
		return this.gm;
	}

	/**
	 * Refresh the order combo box to show orders valid for the selected
	 * character's skill types
	 */
	private void refreshOrderCombo() {
		Order order = (Order) getFormObject();
		Character c = null;
		if (order != null) {
			c = order.getCharacter();
		}
		this.orderCombo.setModel(createOrderCombo(c,getGameMetadata()));
	}
	// useful utility function
	public static ComboBoxListModelAdapter createOrderCombo(Character c,GameMetadata gm) {
		Container<OrderMetadata> orderMetadata = gm.getOrders();

		ListListModel orders = new ListListModel();
		orders.add(Order.NA);
		for (OrderMetadata om : orderMetadata.getItems()) {
			if (om.orderAllowedForGameType()) {
				if (c != null) {
					if (om.charHasRequiredSkill(c)
						|| om.orderAllowedDueToUncoverSecretsSNA(c)
						|| om.orderAllowedDueToScoutingSNA(c)) {
						orders.add(om.getNumber() + " " + om.getCode()); //$NON-NLS-1$
					}
				} else {
					orders.add(om.getNumber() + " " + om.getCode()); //$NON-NLS-1$
				}
			}
		}
		SortedListModel slm = new SortedListModel(orders);
		return new ComboBoxListModelAdapter(slm);
	}

	@Override
	protected JComponent createFormControl() {
		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		glb.setDefaultInsets(new Insets(5, 5, 0, 0));
		glb.append(new JLabel(Messages.getString("OrderEditor.43"))); //$NON-NLS-1$

		glb.append(this.character = new JTextField());
		this.character.setEditable(false);
		this.character.setPreferredSize(new Dimension(200, PREFERRED_TEXT_HEIGHT));

		JButton btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ImageSource imgSource = JOApplication.getImageSource();
		Icon ico = new ImageIcon(imgSource.getImage("SaveGameCommand.icon")); //$NON-NLS-1$
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateParameters();
				saveOrder();
			}
		});
		btn.setToolTipText(Messages.getString("OrderEditor.46")); //$NON-NLS-1$

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("char.icon")); //$NON-NLS-1$
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOApplication.publishEvent(LifecycleEventsEnum.SelectCharEvent, ((Order) getFormObject()).getCharacter(), this);

			}
		});
		btn.setToolTipText(Messages.getString("OrderEditor.48")); //$NON-NLS-1$

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("clear.icon")); //$NON-NLS-1$
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearOrder();
			}
		});
		btn.setToolTipText(Messages.getString("OrderEditor.50")); //$NON-NLS-1$

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("revert.icon")); //$NON-NLS-1$
		btn.setIcon(ico);
		glb.append(btn);
		btn.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				revertOrder();
			}
		});
		btn.setToolTipText(Messages.getString("OrderEditor.52")); //$NON-NLS-1$

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("ShowHideOrderDescription.icon")); //$NON-NLS-1$
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OrderEditor.this.descriptionLabel.setVisible(!OrderEditor.this.descriptionLabel.isVisible());
				OrderEditor.this.description.setVisible(!OrderEditor.this.description.isVisible());
			}
		});
		btn.setToolTipText(Messages.getString("OrderEditor.54")); //$NON-NLS-1$

		glb.nextLine();

		glb.append(new JLabel(Messages.getString("OrderEditor.55"))); //$NON-NLS-1$

		// orderCombo = new JComboBox();
		// orderCombo.setEditable(true);
		// new EditableComboBoxAutoCompletion(orderCombo);
		this.orderCombo = new AutocompletionComboBox();
		glb.append(this.orderCombo);

		this.orderCombo.setPreferredSize(new Dimension(70, PREFERRED_TEXT_HEIGHT));
		this.orderCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!OrderEditor.this.currentOrderNoAndCode.equals(OrderEditor.this.orderCombo.getSelectedItem())) {
					OrderEditor.this.currentOrderNoAndCode = OrderEditor.this.orderCombo.getSelectedItem().toString();
					refreshOrder();
					refreshDescription();
					refreshSubeditor();
					updateParameters();
					refreshDrawCheck();
					if (getAutoSave()) {
						saveOrder();
					}
				}
			}
		});

		this.chkDraw = new JCheckBox(Messages.getString("OrderEditor.56")); //$NON-NLS-1$
		this.chkDraw.setPreferredSize(new Dimension(60, 18));
		this.chkDraw.setBackground(Color.white);
		glb.append(this.chkDraw, 3, 1);
		this.chkDraw.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Order o = (Order) getFormObject();
				OrderVisualizationData ovd = OrderVisualizationData.instance();
				if (!ovd.contains(o)) {
					ovd.addOrder((Order) getFormObject());
				} else {
					ovd.removeOrder((Order) getFormObject());
				}
				JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, getFormObject(), this);

			}
		});
		this.chkDraw.setFocusable(false);
		btn.setToolTipText(Messages.getString("OrderEditor.58")); //$NON-NLS-1$

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("displace.icon")); //$NON-NLS-1$
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Order o = (Order) getFormObject();
				if (o.isArmyMovementOrderCapableOfEvasion()) {
					OrderVisualizationData ovd = OrderVisualizationData.instance();
					if (!ovd.contains(o) && ovd.getOrderEditorOrder() != o) {
						ovd.addOrder((Order) getFormObject());
					}
					Object d = ovd.getAdditionalInfo(o, "displacement"); //$NON-NLS-1$
					if (d == null) {
						d = 1;
					} else {
						d = ((Integer) d) + 1;
						if ((Integer) d > 4)
							d = 0;
					}
					if (d.equals(0)) {
						ovd.removeAdditionalInfo(o, "displacement"); //$NON-NLS-1$
					} else {
						ovd.setAdditionalInfo(o, "displacement", d); //$NON-NLS-1$
					}
					JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, getFormObject(), this);
				}
			}
		});
		btn.setToolTipText(Messages.getString("OrderEditor.64")); //$NON-NLS-1$

		glb.nextLine();

		glb.append(new JLabel(Messages.getString("OrderEditor.65"))); //$NON-NLS-1$
		glb.append(this.parameters = new JTextField());
		this.parameters.setEditable(false);
		this.parameters.setPreferredSize(new Dimension(70, PREFERRED_TEXT_HEIGHT));

		this.parametersInternal = new JTextField();
		this.parametersInternal.setVisible(false);
		glb.append(this.parametersInternal);
		glb.nextLine();

		glb.append(this.descriptionLabel = new JLabel(Messages.getString("OrderEditor.66"))); //$NON-NLS-1$
		glb.append(this.description = new JTextArea());
		this.descriptionLabel.setVerticalAlignment(SwingConstants.NORTH);
		this.descriptionLabel.setVisible(true);
		this.description.setLineWrap(true);
		this.description.setWrapStyleWord(true);
		this.description.setPreferredSize(new Dimension(200, 50));
		this.description.setEditable(false);
		this.description.setBorder(this.parameters.getBorder());
		Font f = new Font(this.description.getFont().getName(), Font.ITALIC, this.description.getFont().getSize() - 1);
		this.description.setFont(f);
		this.description.setVisible(true);

		glb.nextLine();
		glb.append(this.subeditorPanel = new JPanel(), 2, 1);
		this.subeditorPanel.setBackground(Color.white);
		glb.nextLine();

		this.myPanel = glb.getPanel();
		this.myPanel.setBackground(Color.white);

		this.descriptionLabel.setVisible(true);
		this.description.setVisible(true);
		this.descriptionLabel.setVisible(false);
		this.description.setVisible(false);
		return this.myPanel;
	}

	/**
	 * Called when the selected order has been changed in the combo Refreshes
	 * the order and initializes the parameters Unless the change was between
	 * compatible orders such as 850 and 860 or 810 and 820
	 *  returns true if order was initialised.
	 */
	private boolean refreshOrder() {
		Order o = (Order) getFormObject();
		if (getSelectedOrderNo().equals("") || o.getOrderNo() != Integer.parseInt(getSelectedOrderNo())) { //$NON-NLS-1$
			int orderNo = getSelectedOrderNo().equals("") ? -1 : Integer.parseInt(getSelectedOrderNo()); //$NON-NLS-1$
			// exclude changes between
			// 830, 850, 860
			// 810, 820 and 870
			String[] equivalentOrders = new String[] { ",830,850,860,", ",810,820,870,", ",230,240,250,255," }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			boolean areEquivalent = false;
			for (int i = 0; i < equivalentOrders.length; i++) {
				int j = equivalentOrders[i].indexOf("," + String.valueOf(o.getOrderNo()) + ","); //$NON-NLS-1$ //$NON-NLS-2$
				int k = equivalentOrders[i].indexOf("," + String.valueOf(getSelectedOrderNo()) + ","); //$NON-NLS-1$ //$NON-NLS-2$
				if (j >= 0 && k >= 0) {
					areEquivalent = true;
				}
			}
			if (areEquivalent)
				return false;

			o.setParameters(""); //$NON-NLS-1$
			if (Arrays.binarySearch(new int[] { 830, 850, 860 }, orderNo) > -1) {
				o.setP0("no"); //$NON-NLS-1$
			}
			this.parameters.setText(Order.getParametersAsString(o.getParameters()));
			this.parametersInternal.setText(o.getParameters());
			return true;
		}
		return false;
	}

	public void refreshDrawCheck() {
		Order o = (Order) getFormObject();
		OrderVisualizationData ovd = OrderVisualizationData.instance();
		this.chkDraw.setSelected(ovd.contains(o));
		Order no = new Order(o.getCharacter());
		try {
			no.setOrderNo(Integer.parseInt(getSelectedOrderNo()));
			this.chkDraw.setEnabled(GraphicUtils.canRenderOrder(no));
		} catch (Exception e) {
			this.chkDraw.setEnabled(false);
		}
	}

	private String getSelectedOrderNo() {
		String noAndCode = this.orderCombo.getSelectedItem().toString();
		int i = noAndCode.indexOf(" "); //$NON-NLS-1$
		if (i > 0) {
			return noAndCode.substring(0, i);
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Refreshes the subeditors according to the selected order number
	 */
	protected void refreshSubeditor() {
		// clear the panel and the component array list
		this.subeditorPanel.removeAll();
		this.subeditorComponents.clear();
		Order o = (Order) getFormObject();
		boolean paramsEditable = true;
		if (!getSelectedOrderNo().equals("") && getOrderEditorData() != null) { //$NON-NLS-1$
			OrderEditorData oed = getOrderEditorData().findFirstByProperty("orderNo", Integer.parseInt(getSelectedOrderNo())); //$NON-NLS-1$
			if (oed != null) {
				paramsEditable = false;
				TableLayoutBuilder tlb = new TableLayoutBuilder(this.subeditorPanel);
				orderEditorFactory(oed.getOrderNo(),oed,tlb,this,o,this.gameHolder);

				// hack in order to show all of the subeditor fields
				tlb.cell(new JLabel(" ")); //$NON-NLS-1$
				tlb.row();
				tlb.cell(new JLabel(" ")); //$NON-NLS-1$
				tlb.row();
				tlb.cell(new JLabel(" ")); //$NON-NLS-1$
				tlb.row();
				tlb.cell(new JLabel(" ")); //$NON-NLS-1$
				tlb.row();
				tlb.cell(new JLabel(" ")); //$NON-NLS-1$
				tlb.row();
				tlb.getPanel();
				// subeditorPanel.invalidate();
				// myPanel.invalidate();
			}
		}
		this.parameters.setEditable(paramsEditable);
		this.parameters.setFocusable(paramsEditable);
		this.subeditorPanel.requestFocusInWindow();
	}

	private void refreshDescription() {
		String txt = ""; //$NON-NLS-1$
		try {
			String selOrder = (String) this.orderCombo.getSelectedItem();
			int i = selOrder.indexOf(' ');
			int no = Integer.parseInt(selOrder.substring(0, i));
			Container<OrderMetadata> orderMetadata = getGameMetadata().getOrders();

			OrderMetadata om = orderMetadata.findFirstByProperty("number", no); //$NON-NLS-1$
			txt = om.getName() + ", " + om.getDifficulty() + ", " + om.getRequirement() + "\n" + om.getParameters(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (Exception exc) {
			// do nothing
		}
		this.description.setText(txt);
	}

	private void saveOrder() {
		Order o = (Order) getFormObject();
		if (this.orderCombo.getSelectedItem() == null)
			return;
		o.setNoAndCode(this.orderCombo.getSelectedItem().toString());
		o.setParameters(this.parametersInternal.getText());
		System.out.println("Order");
		//remove order checker data for this order if present
		OrderResultContainer container = this.gameHolder.getGame().getTurn().getOrderResults().getResultCont();
		if (container.getResultTypeForOrder(o)!=null) {
			container.removeResultsForOrder(o);
		}
		System.out.println("Size " + container.getContainer().size()+ "    And bool "  + container.getResultsForOrder(o).toString());
		
		// throw an order changed event
		this.gameHolder.getGame().getTurn().getOrderResults().getResultCont().removeResultsForOrder(o);
		JOApplication.publishEvent(LifecycleEventsEnum.OrderChangedEvent, o, this);
		// Point selectedHex = new Point(o.getCharacter().getX(),
		// o.getCharacter().getY());
		// joApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent,selectedHex, this);
	}

	private void clearOrder() {
		this.orderCombo.setSelectedItem(Order.NA);
		this.parameters.setText(""); //$NON-NLS-1$
		this.parametersInternal.setText(""); //$NON-NLS-1$
		refreshSubeditor();
		refreshDescription();
		Order o = (Order) getFormObject();
		o.setNoAndCode(this.orderCombo.getSelectedItem().toString());
		o.setParameters(this.parametersInternal.getText());
		this.currentOrderNoAndCode = ""; //$NON-NLS-1$
		// throw an order changed event
		this.gameHolder.getGame().getTurn().getOrderResults().getResultCont().removeResultsForOrder(o);
		JOApplication.publishEvent(LifecycleEventsEnum.OrderChangedEvent, o, this);
		// Point selectedHex = new Point(o.getCharacter().getX(),
		// o.getCharacter().getY());
		// joApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent,selectedHex, this);
	}

	private void revertOrder() {
		Order o = (Order) getFormObject();
		o.setOrderNo(this.bkOrderNo);
		o.setParameters(this.bkParams);
		setFormObject(o);
		if (getAutoSave()) {
			saveOrder();
		}
	}

	@Override
	public void setFormObject(Object obj) {
		super.setFormObject(obj);
		Order o = (Order) obj;
		this.bkOrderNo = o.getOrderNo();
		this.bkParams = o.getParameters();
		refreshOrderCombo();
		this.parameters.setText(Order.getParametersAsString(o.getParameters()));
		this.parametersInternal.setText(o.getParameters());
		if (!o.isBlank()) {
			this.orderCombo.setSelectedItem(o.getNoAndCode());
		} else {
			this.orderCombo.setSelectedIndex(0);
		}
		refreshOrder();
		refreshDescription();
		refreshSubeditor();
		updateParameters();
		this.currentOrderNoAndCode = o.getNoAndCode();
		Character c = o.getCharacter();
		if (c == null) {
			this.character.setText(""); //$NON-NLS-1$
		} else {
			this.character.setText(c.getName() + " - " + c.getHexNo()); //$NON-NLS-1$
		}
		this.chkDraw.setEnabled(GraphicUtils.canRenderOrder(o));
		OrderVisualizationData ovd = OrderVisualizationData.instance();
		if (PreferenceRegistry.instance().getPreferenceValue("orderEditor.autoDraw").equals("yes") && PreferenceRegistry.instance().getPreferenceValue(Messages.getString("OrderEditor.233")).equals("yes")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ovd.setOrderEditorOrder(o);
			JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, getFormObject(), this);
		} else {
			ovd.setOrderEditorOrder(null);
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) applicationEvent);
		}
	}
	protected void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case GameChangedEvent:
			refreshOrderCombo();
			break;
		case EditOrderEvent:
			setFormObject(e.getObject());
			refreshDrawCheck();
			// OrderEditorView oev =
			// (OrderEditorView)Application.instance().getApplicationContext().getBean("orderEditorView");
			// mscoon
			// DockingManager.display(DockingManager.getDockable("orderEditorView"));
			break;
		case RefreshMapItems:
			refreshDrawCheck();
			break;
		}
	}

	public void updateParameters() {
		String v = ""; //$NON-NLS-1$
		for (JComponent c : this.subeditorComponents) {
			String val = ""; //$NON-NLS-1$
			if (JTextField.class.isInstance(c)) {
				val = ((JTextField) c).getText();
			} else if (JComboBox.class.isInstance(c)) {
				if (((JComboBox) c).getSelectedItem() == null) {
					val = ""; //$NON-NLS-1$
				} else {
					val = ((JComboBox) c).getSelectedItem().toString();
				}
			}
			
			if (val.equals("")) { //$NON-NLS-1$
				if (this.hideUnspecifiedParameters) {
					continue;
				}
				val = "-"; //$NON-NLS-1$
			}
			v += (v.equals("") ? "" : Order.DELIM) + val; //$NON-NLS-1$ //$NON-NLS-2$
		}
		this.parametersInternal.setText(v);
		this.parameters.setText(v.replace(Order.DELIM, " ")); //$NON-NLS-1$
		if (getAutoSave()) {
			saveOrder();
		}

	}

//	protected void addValidationResult(Order o, OrderValidationResult res, Integer paramI) {
//		if (res == null)
//			return;
//		OrderResultContainer cont = this.gameHolder.getGame().getTurn().getOrderResults().getResultCont();
//		String e = ""; //$NON-NLS-1$
//		if (paramI != null) {
//			e = Messages.getString("OrderEditor.245") + paramI + ": "; //$NON-NLS-1$ //$NON-NLS-2$
//		}
//		e += res.getMessage();
//		if (res.getLevel() == OrderValidationResult.ERROR) {
//			OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Error);
//			cont.addResult(or);
//		} else if (res.getLevel() == OrderValidationResult.WARNING) {
//			OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Warning);
//			cont.addResult(or);
//		} else if (res.getLevel() == OrderValidationResult.INFO) {
//			OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Info);
//			cont.addResult(or);
//
//		}
//
//	}

//	public void validateOrder() {
//		Order o = (Order) getFormObject();
//		OrderParameterValidator opv = new OrderParameterValidator();
//		OrderValidationResult ovr = opv.checkOrder(o);
//		addValidationResult(o, ovr, null);
//		for (int i = 0; i <= o.getLastParamIndex(); i++) {
//			ovr = opv.checkParam(o, i);
//			if (ovr != null) {
//				addValidationResult(o, ovr, i);
//			}
//		}
//	}

	public ArrayList<JComponent> getSubeditorComponents() {
		return this.subeditorComponents;
	}

	public void giveFocus() {
		this.orderCombo.requestFocusInWindow();
	}

	public boolean getAutoSave() {
		String pval = PreferenceRegistry.instance().getPreferenceValue("orderEditor.autoSave"); //$NON-NLS-1$
		return !pval.equals("no"); //$NON-NLS-1$
	}

	public static AbstractOrderSubeditor orderEditorFactory(int orderNumber,OrderEditorData oed, TableLayoutBuilder tlb, OrderEditor oe,Order o,GameHolder gameHolder) {
		AbstractOrderSubeditor sub = null;
		switch (orderNumber) {
		case 850:
		case 860:
		case 830:
			sub = new MoveArmyOrderSubeditor(oe,o,gameHolder);
			sub.addComponents(tlb, oe.subeditorComponents, o, 0,false);
			break;
		case 940:
			sub = new CastLoSpellOrderSubeditor(oe,o,gameHolder);
			sub.addComponents(tlb, oe.subeditorComponents, o, 0,false);
			break;
		default:
			int paramNo = 0;
			for (int i = 0; i < oed.getParamTypes().size(); i++) {
				sub = parameterEditorFactory(oe,oed.getParamTypes().get(i),oed.getParamDescriptions().get(i),o,oed.getOrderNo(),gameHolder);
				if (sub != null) {
					sub.addComponents(tlb, oe.subeditorComponents, o, paramNo,false);
					sub.valueChanged();
				}
				paramNo++;
			}
		}
		return sub;

	}
	public static AbstractOrderSubeditor parameterEditorFactory(OrderEditor oe,String paramType,String paramDescription,Order o,int orderNumber,GameHolder gameHolder) {
		if (paramType.equals("")) {
			return null;
		} else if (paramType.equals("hex")) { //$NON-NLS-1$
			return new SingleParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("cid") || paramType.equals("xid")) { //$NON-NLS-1$ //$NON-NLS-2$
			return new SingleParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("tac")) { //$NON-NLS-1$
			return tacTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("prd")) { //$NON-NLS-1$ // le..mo => leather..mounts
			return productTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("pro")) { //$NON-NLS-1$ // le..go => leather..gold
			return product2Types(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("%")) { //$NON-NLS-1$
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("g")) { //$NON-NLS-1$
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("yn")) { //$NON-NLS-1$
			return ynTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("wep")) { //$NON-NLS-1$
			return WeaponTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("arm")) { //$NON-NLS-1$
			return ArmourTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("trp")) { //$NON-NLS-1$
			return trpTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("gen")) { //$NON-NLS-1$ // m/f
			return genderTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("alg")) { //$NON-NLS-1$ // g,n,e
			return allegianceTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("nam")) { //$NON-NLS-1$
			return new SingleParameterOrderSubeditor(oe,paramDescription, o, 160,gameHolder);
		} else if (paramType.equals("rsp")) { //$NON-NLS-1$
			return new SingleParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("a")) { //$NON-NLS-1$
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("ae")) { //$NON-NLS-1$
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("b")) { //$NON-NLS-1$ //number of troops
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("e")) { //$NON-NLS-1$
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("d")) { //$NON-NLS-1$
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("de")) { //$NON-NLS-1$
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("dcid")) { //$NON-NLS-1$
			return new SingleParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("i")) { //$NON-NLS-1$
			return new NumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("dir") || paramType.equals("dirx")) { //$NON-NLS-1$ //$NON-NLS-2$
			// sub = new
			// SingleParameterOrderSubeditor(paramDescription,
			// o);
			return directionTypes(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("nat")) { //$NON-NLS-1$
			return new NationParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		} else if (paramType.equals("spx") || paramType.equals("spc") || paramType.equals("sph") || paramType.equals("spm") || paramType.equals("spl")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			return new SpellNumberParameterOrderSubeditor(oe, paramDescription, o, orderNumber,gameHolder,true);
		} else if (paramType.equals("spz")) { //$NON-NLS-1$
			return new SpellNumberParameterOrderSubeditor(oe, paramDescription, o, orderNumber,gameHolder,false);
		} else if (paramType.equals("art")) { //$NON-NLS-1$
			return new ArtifactNumberParameterOrderSubeditor(oe, paramDescription, o,gameHolder);
		}
		OrderEditor.staticLogger.error("unexpected parameter type " + paramType);
		return null;
	}
	static public AbstractOrderSubeditor WeaponTypes(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] weapTypeAbbrevations = new String[] { "OrderEditor.139","OrderEditor.140","OrderEditor.141","OrderEditor.142"}; // wo,br,st,mi
		final String[] weapTypeDescriptions = new String[] { "OrderEditor.143","OrderEditor.144","OrderEditor.145","OrderEditor.146"}; // Wooden,Bronze,Steel,Mithril
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(weapTypeAbbrevations),
				UIUtils.createMessages(weapTypeDescriptions),
				0,gameHolder);
	}
	static public AbstractOrderSubeditor ArmourTypes(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] armourTypeAbbrevations = new String[] { "OrderEditor.148","OrderEditor.149","OrderEditor.150","OrderEditor.151","OrderEditor.152"};
		final String[] armourTypeDescriptions = new String[] { "OrderEditor.153","OrderEditor.154","OrderEditor.155","OrderEditor.156","OrderEditor.157"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(armourTypeAbbrevations),
				UIUtils.createMessages(armourTypeDescriptions),
			0,gameHolder);
	}
	static public AbstractOrderSubeditor tacTypes(OrderEditor oe,String paramDescription, Order o,GameHolder gameHolder) {
		final String[] tacTypeAbbrevations = new String[] { "OrderEditor.87","OrderEditor.88","OrderEditor.89","OrderEditor.90","OrderEditor.91","OrderEditor.92"};
		final String[] tacTypeDescriptions = new String[] { "OrderEditor.93","OrderEditor.94","OrderEditor.95","OrderEditor.96","OrderEditor.97","OrderEditor.98"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(tacTypeAbbrevations),
				UIUtils.createMessages(tacTypeDescriptions),
			0,gameHolder);
	}
	static public AbstractOrderSubeditor productTypes(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] productTypeAbbrevations = new String[] { "OrderEditor.100","OrderEditor.101","OrderEditor.102","OrderEditor.103","OrderEditor.104","OrderEditor.105","OrderEditor.106"};
		final String[] productTypeDescriptions = new String[] { "OrderEditor.107","OrderEditor.108","OrderEditor.109","OrderEditor.110","OrderEditor.111","OrderEditor.112","OrderEditor.113"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(productTypeAbbrevations),
				UIUtils.createMessages(productTypeDescriptions),
			0,gameHolder);
	}
	static public AbstractOrderSubeditor product2Types(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] product2TypeAbbrevations = new String[] { "OrderEditor.115","OrderEditor.116","OrderEditor.117","OrderEditor.118","OrderEditor.119","OrderEditor.120","OrderEditor.121","OrderEditor.122"};
		final String[] product2TypeDescriptions = new String[] { "OrderEditor.123","OrderEditor.124","OrderEditor.125","OrderEditor.126","OrderEditor.127","OrderEditor.128","OrderEditor.129","OrderEditor.130"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(product2TypeAbbrevations),
				UIUtils.createMessages(product2TypeDescriptions),
			0,gameHolder);
	}
	static public AbstractOrderSubeditor ynTypes(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] ynTypeAbbrevations = new String[] { "OrderEditor.134","OrderEditor.135"};
		final String[] ynTypeDescriptions = new String[] { "OrderEditor.136","OrderEditor.137"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(ynTypeAbbrevations),
				UIUtils.createMessages(ynTypeDescriptions),
			0,gameHolder);
	}
	static public AbstractOrderSubeditor trpTypes(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] trpTypeAbbrevations = new String[] { "OrderEditor.159","OrderEditor.160","OrderEditor.161","OrderEditor.162","OrderEditor.163","OrderEditor.164"};
		final String[] trpTypeDescriptions = new String[] { "OrderEditor.165","OrderEditor.166","OrderEditor.167","OrderEditor.168","OrderEditor.169","OrderEditor.170"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(trpTypeAbbrevations),
				UIUtils.createMessages(trpTypeDescriptions),
			0,gameHolder);
	}
	static public AbstractOrderSubeditor genderTypes(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] genderTypeAbbrevations = new String[] { "OrderEditor.172","OrderEditor.173"};
		final String[] genderTypeDescriptions = new String[] { "OrderEditor.174","OrderEditor.175"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(genderTypeAbbrevations),
				UIUtils.createMessages(genderTypeDescriptions),
			0,gameHolder);
	}
	static public AbstractOrderSubeditor allegianceTypes(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] allegianceTypeAbbrevations = new String[] { "OrderEditor.177","OrderEditor.178","OrderEditor.179"};
		final String[] allegianceTypeDescriptions = new String[] { "OrderEditor.180","OrderEditor.181","OrderEditor.182"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(allegianceTypeAbbrevations),
				UIUtils.createMessages(allegianceTypeDescriptions),
			0,gameHolder);
	}
	static public AbstractOrderSubeditor directionTypes(OrderEditor oe,String paramDescription,Order o,GameHolder gameHolder) {
		final String[] directionTypeAbbrevations = new String[] { "OrderEditor.195","OrderEditor.196","OrderEditor.197","OrderEditor.198","OrderEditor.199","OrderEditor.200"};
		final String[] directionTypeDescriptions = new String[] { "OrderEditor.201","OrderEditor.202","OrderEditor.203","OrderEditor.204","OrderEditor.205","OrderEditor.206"};
		return new DropDownParameterOrderSubeditor(oe,paramDescription, o,
				UIUtils.createMessages(directionTypeAbbrevations),
				UIUtils.createMessages(directionTypeDescriptions),
			0,gameHolder);
	}
}
