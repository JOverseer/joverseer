package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
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

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.OrderParameterValidator;
import org.joverseer.tools.OrderValidationResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.AutocompletionComboBox;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
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

	String currentOrderNoAndCode = ""; //$NON-NLS-1$

	ArrayList<JComponent> subeditorComponents = new ArrayList<JComponent>();

	Container<OrderEditorData> orderEditorData = null;

	GameMetadata gm;

	public OrderEditor() {
		super(FormModelHelper.createFormModel(new Order(new Character())), FORM_PAGE);
	}

	public String getParameters() {
		return this.parameters.getText();
	}

	public Container<OrderEditorData> getOrderEditorData() {
		if (this.orderEditorData == null) {
			this.orderEditorData = new Container<OrderEditorData>(new String[] { "orderNo" }); //$NON-NLS-1$
			try {
				GameMetadata gm1 = (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata"); //$NON-NLS-1$
				BufferedReader reader = gm1.getUTF8Resource("orderEditorData.csv"); 

				String ln;
				while ((ln = reader.readLine()) != null) {
					try {
						String[] partsL = ln.split(";"); //$NON-NLS-1$
						String[] parts = new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
						for (int i = 0; i < partsL.length; i++) {
							parts[i] = partsL[i];
						}
						OrderEditorData oed = new OrderEditorData();
						oed.setOrderNo(Integer.parseInt(parts[0]));
						oed.setOrderDescr(parts[1]);
						oed.setParameterDescription(parts[2]);
						oed.setOrderType(parts[3]);
						oed.getParamTypes().add(parts[4]);
						oed.getParamTypes().add(parts[5]);
						oed.getParamTypes().add(parts[6]);
						oed.getParamTypes().add(parts[7]);
						oed.getParamTypes().add(parts[8]);
						oed.getParamTypes().add(parts[9]);
						oed.getParamTypes().add(parts[10]);
						oed.setMajorSkill(parts[11]);
						oed.setSkill(parts[12]);
						this.orderEditorData.addItem(oed);
						ln = reader.readLine();
						if (ln == null) {
							ln = ""; //$NON-NLS-1$
						}
						partsL = ln.split(";"); //$NON-NLS-1$
						parts = new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
						for (int i = 0; i < partsL.length; i++) {
							parts[i] = partsL[i];
						}
						oed.getParamDescriptions().add(parts[4]);
						oed.getParamDescriptions().add(parts[5]);
						oed.getParamDescriptions().add(parts[6]);
						oed.getParamDescriptions().add(parts[7]);
						oed.getParamDescriptions().add(parts[8]);
						oed.getParamDescriptions().add(parts[9]);
						oed.getParamDescriptions().add(parts[10]);
					} catch (Exception exc) {
						System.out.println(ln);
					}
				}
			} catch (Exception exc) {
				System.out.println(exc.getMessage());
				this.orderEditorData = null;
			}
		}
		return this.orderEditorData;
	}

	private GameMetadata getGameMetadata() {
		if (this.gm == null) {
			Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame(); //$NON-NLS-1$
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
		Container<OrderMetadata> orderMetadata = getGameMetadata().getOrders();
		Order o = (Order) getFormObject();
		ListListModel orders = new ListListModel();
		orders.add(Order.NA);
		for (OrderMetadata om : orderMetadata.getItems()) {
			if (o.getCharacter() != null && (om.charHasRequiredSkill(o.getCharacter()) ||
					om.orderAllowedDueToUncoverSecretsSNA(o.getCharacter())
					|| om.orderAllowedDueToScoutingSNA(o.getCharacter()))) {
				if (om.orderAllowedForGameType()) {
					orders.add(om.getNumber() + " " + om.getCode()); //$NON-NLS-1$
				}
			}
		}
		SortedListModel slm = new SortedListModel(orders);
		this.orderCombo.setModel(new ComboBoxListModelAdapter(slm));
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
		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource"); //$NON-NLS-1$
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
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectCharEvent.toString(), ((Order) getFormObject()).getCharacter(), this));

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
				OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData"); //$NON-NLS-1$
				if (!ovd.contains(o)) {
					ovd.addOrder((Order) getFormObject());
				} else {
					ovd.removeOrder((Order) getFormObject());
				}
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), getFormObject(), this));

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
				if (o.getOrderNo() == 830 || o.getOrderNo() == 850 || o.getOrderNo() == 860) {
					OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData"); //$NON-NLS-1$
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
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), getFormObject(), this));
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
		OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData"); //$NON-NLS-1$
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
				int paramNo = 0;
				if (oed.getOrderNo() == 850 || oed.getOrderNo() == 860 || oed.getOrderNo() == 830) {
					AbstractOrderSubeditor sub = new MoveArmyOrderSubeditor(o);
					sub.addComponents(tlb, this.subeditorComponents, o, 0);
					sub.setEditor(this);
				} else if (oed.getOrderNo() == 940) {
					AbstractOrderSubeditor sub = new CastLoSpellOrderSubeditor(o);
					sub.setEditor(this);
					sub.addComponents(tlb, this.subeditorComponents, o, 0);
				} else {
					for (int i = 0; i < oed.getParamTypes().size(); i++) {
						String paramType = oed.getParamTypes().get(i);
						String paramDescription = oed.getParamDescriptions().get(i);
						AbstractOrderSubeditor sub = null;
						if (paramType.equals("hex")) { //$NON-NLS-1$
							sub = new SingleParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("cid") || paramType.equals("xid")) { //$NON-NLS-1$ //$NON-NLS-2$
							sub = new SingleParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("tac")) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.87"), Messages.getString("OrderEditor.88"), Messages.getString("OrderEditor.89"), Messages.getString("OrderEditor.90"), Messages.getString("OrderEditor.91"), Messages.getString("OrderEditor.92") }, new String[] { Messages.getString("OrderEditor.93"), Messages.getString("OrderEditor.94"), Messages.getString("OrderEditor.95"), Messages.getString("OrderEditor.96"), Messages.getString("OrderEditor.97"), Messages.getString("OrderEditor.98") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
						} else if (paramType.equals(Messages.getString("OrderEditor.99"))) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.100"), Messages.getString("OrderEditor.101"), Messages.getString("OrderEditor.102"), Messages.getString("OrderEditor.103"), Messages.getString("OrderEditor.104"), Messages.getString("OrderEditor.105"), Messages.getString("OrderEditor.106") }, new String[] { Messages.getString("OrderEditor.107"), Messages.getString("OrderEditor.108"), Messages.getString("OrderEditor.109"), Messages.getString("OrderEditor.110"), Messages.getString("OrderEditor.111"), Messages.getString("OrderEditor.112"), Messages.getString("OrderEditor.113") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$
						} else if (paramType.equals(Messages.getString("OrderEditor.114"))) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.115"), Messages.getString("OrderEditor.116"), Messages.getString("OrderEditor.117"), Messages.getString("OrderEditor.118"), Messages.getString("OrderEditor.119"), Messages.getString("OrderEditor.120"), Messages.getString("OrderEditor.121"), Messages.getString("OrderEditor.122") }, new String[] { Messages.getString("OrderEditor.123"), Messages.getString("OrderEditor.124"), Messages.getString("OrderEditor.125"), Messages.getString("OrderEditor.126"), Messages.getString("OrderEditor.127"), Messages.getString("OrderEditor.128"), Messages.getString("OrderEditor.129"), Messages.getString("OrderEditor.130") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$
						} else if (paramType.equals(Messages.getString("OrderEditor.131"))) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals(Messages.getString("OrderEditor.132"))) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals(Messages.getString("OrderEditor.133"))) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.134"), Messages.getString("OrderEditor.135") }, new String[] { Messages.getString("OrderEditor.136"), Messages.getString("OrderEditor.137") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						} else if (paramType.equals(Messages.getString("OrderEditor.138"))) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.139"), Messages.getString("OrderEditor.140"), Messages.getString("OrderEditor.141"), Messages.getString("OrderEditor.142") }, new String[] { Messages.getString("OrderEditor.143"), Messages.getString("OrderEditor.144"), Messages.getString("OrderEditor.145"), Messages.getString("OrderEditor.146") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
						} else if (paramType.equals(Messages.getString("OrderEditor.147"))) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.148"), Messages.getString("OrderEditor.149"), Messages.getString("OrderEditor.150"), Messages.getString("OrderEditor.151"), Messages.getString("OrderEditor.152") }, new String[] { Messages.getString("OrderEditor.153"), Messages.getString("OrderEditor.154"), Messages.getString("OrderEditor.155"), Messages.getString("OrderEditor.156"), Messages.getString("OrderEditor.157") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
						} else if (paramType.equals(Messages.getString("OrderEditor.158"))) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.159"), Messages.getString("OrderEditor.160"), Messages.getString("OrderEditor.161"), Messages.getString("OrderEditor.162"), Messages.getString("OrderEditor.163"), Messages.getString("OrderEditor.164") }, new String[] { Messages.getString("OrderEditor.165"), Messages.getString("OrderEditor.166"), Messages.getString("OrderEditor.167"), Messages.getString("OrderEditor.168"), Messages.getString("OrderEditor.169"), Messages.getString("OrderEditor.170") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
						} else if (paramType.equals(Messages.getString("OrderEditor.171"))) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.172"), Messages.getString("OrderEditor.173") }, new String[] { Messages.getString("OrderEditor.174"), Messages.getString("OrderEditor.175") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						} else if (paramType.equals(Messages.getString("OrderEditor.176"))) { //$NON-NLS-1$
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.177"), Messages.getString("OrderEditor.178"), Messages.getString("OrderEditor.179") }, new String[] { Messages.getString("OrderEditor.180"), Messages.getString("OrderEditor.181"), Messages.getString("OrderEditor.182") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
						} else if (paramType.equals(Messages.getString("OrderEditor.183"))) { //$NON-NLS-1$
							sub = new SingleParameterOrderSubeditor(paramDescription, o, 160);
						} else if (paramType.equals(Messages.getString("OrderEditor.184"))) { //$NON-NLS-1$
							sub = new SingleParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals(Messages.getString("OrderEditor.185"))) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals(Messages.getString("OrderEditor.186"))) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals(Messages.getString("OrderEditor.187"))) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals(Messages.getString("OrderEditor.188"))) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals(Messages.getString("OrderEditor.189"))) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals(Messages.getString("OrderEditor.190"))) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("dcid")) { //$NON-NLS-1$
							sub = new SingleParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("i")) { //$NON-NLS-1$
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("dir") || paramType.equals("dirx")) { //$NON-NLS-1$ //$NON-NLS-2$
							// sub = new
							// SingleParameterOrderSubeditor(paramDescription,
							// o);
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { Messages.getString("OrderEditor.195"), Messages.getString("OrderEditor.196"), Messages.getString("OrderEditor.197"), Messages.getString("OrderEditor.198"), Messages.getString("OrderEditor.199"), Messages.getString("OrderEditor.200") }, new String[] { Messages.getString("OrderEditor.201"), Messages.getString("OrderEditor.202"), Messages.getString("OrderEditor.203"), Messages.getString("OrderEditor.204"), Messages.getString("OrderEditor.205"), Messages.getString("OrderEditor.206") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
						} else if (paramType.equals("nat")) { //$NON-NLS-1$
							sub = new NationParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("spx") || paramType.equals("spc") || paramType.equals("sph") || paramType.equals("spm") || paramType.equals("spl")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
							sub = new SpellNumberParameterOrderSubeditor(paramDescription, o, oed.getOrderNo());
						} else if (paramType.equals("spz")) { //$NON-NLS-1$
							sub = new ResearchSpellNumberParameterOrderSubeditor(paramDescription, o, oed.getOrderNo());
						} else if (paramType.equals("art")) { //$NON-NLS-1$
							sub = new ArtifactNumberParameterOrderSubeditor(paramDescription, o);
						}
						if (sub != null) {
							sub.addComponents(tlb, this.subeditorComponents, o, paramNo);
							sub.setEditor(this);
						}
						paramNo++;
					}
				}
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
		// validateOrder();
		// throw an order changed event
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), o, this));
		// Point selectedHex = new Point(o.getCharacter().getX(),
		// o.getCharacter().getY());
		// Application.instance().getApplicationContext().publishEvent(
		// new
		// JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(),
		// selectedHex, this));
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
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), o, this));
		// Point selectedHex = new Point(o.getCharacter().getX(),
		// o.getCharacter().getY());
		// Application.instance().getApplicationContext().publishEvent(
		// new
		// JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(),
		// selectedHex, this));
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
		this.currentOrderNoAndCode = o.getNoAndCode();
		Character c = o.getCharacter();
		if (c == null) {
			this.character.setText(""); //$NON-NLS-1$
		} else {
			this.character.setText(c.getName() + " - " + c.getHexNo()); //$NON-NLS-1$
		}
		this.chkDraw.setEnabled(GraphicUtils.canRenderOrder(o));
		OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData"); //$NON-NLS-1$
		if (PreferenceRegistry.instance().getPreferenceValue("orderEditor.autoDraw").equals("yes") && PreferenceRegistry.instance().getPreferenceValue(Messages.getString("OrderEditor.233")).equals("yes")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ovd.setOrderEditorOrder(o);
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), getFormObject(), this));
		} else {
			ovd.setOrderEditorOrder(null);
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				refreshOrderCombo();
			} else if (e.getEventType().equals(LifecycleEventsEnum.EditOrderEvent.toString())) {
				setFormObject(e.getObject());
				refreshDrawCheck();
				// OrderEditorView oev =
				// (OrderEditorView)Application.instance().getApplicationContext().getBean("orderEditorView");
				// mscoon
				// DockingManager.display(DockingManager.getDockable("orderEditorView"));
			} else if (e.getEventType().equals(LifecycleEventsEnum.RefreshMapItems)) {
				refreshDrawCheck();
			}
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

	protected void addValidationResult(Order o, OrderValidationResult res, Integer paramI) {
		if (res == null)
			return;
		OrderResultContainer cont = (OrderResultContainer) Application.instance().getApplicationContext().getBean("orderResultContainer"); //$NON-NLS-1$
		String e = ""; //$NON-NLS-1$
		if (paramI != null) {
			e = Messages.getString("OrderEditor.245") + paramI + ": "; //$NON-NLS-1$ //$NON-NLS-2$
		}
		e += res.getMessage();
		if (res.getLevel() == OrderValidationResult.ERROR) {
			OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Error);
			cont.addResult(or);
		} else if (res.getLevel() == OrderValidationResult.WARNING) {
			OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Warning);
			cont.addResult(or);
		} else if (res.getLevel() == OrderValidationResult.INFO) {
			OrderResult or = new OrderResult(o, e, OrderResultTypeEnum.Info);
			cont.addResult(or);

		}

	}

	public void validateOrder() {
		Order o = (Order) getFormObject();
		OrderParameterValidator opv = new OrderParameterValidator();
		OrderValidationResult ovr = opv.checkOrder(o);
		addValidationResult(o, ovr, null);
		for (int i = 0; i <= o.getLastParamIndex(); i++) {
			ovr = opv.checkParam(o, i);
			if (ovr != null) {
				addValidationResult(o, ovr, i);
			}
		}
	}

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
}
