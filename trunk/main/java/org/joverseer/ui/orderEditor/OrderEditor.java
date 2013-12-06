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
import java.io.InputStreamReader;
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
import org.joverseer.ui.support.controls.AutocompletionComboBox;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
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

	public static final String FORM_PAGE = "orderEditorForm";
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
	String bkParams = "";

	String currentOrderNoAndCode = "";

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
			this.orderEditorData = new Container<OrderEditorData>(new String[] { "orderNo" });
			try {
				GameMetadata gm1 = (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata");
				Resource resource = gm1.getResource("orderEditorData.csv");
				BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

				String ln;
				while ((ln = reader.readLine()) != null) {
					try {
						String[] partsL = ln.split(";");
						String[] parts = new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
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
							ln = "";
						}
						partsL = ln.split(";");
						parts = new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" };
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
			Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
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
			if (o.getCharacter() != null && (om.charHasRequiredSkill(o.getCharacter()) || om.orderAllowedDueToScoutingSNA(o.getCharacter()))) {
				if (om.orderAllowedForGameType()) {
					orders.add(om.getNumber() + " " + om.getCode());
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
		glb.append(new JLabel("Character :"));

		glb.append(this.character = new JTextField());
		this.character.setEditable(false);
		this.character.setPreferredSize(new Dimension(200, PREFERRED_TEXT_HEIGHT));

		JButton btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
		Icon ico = new ImageIcon(imgSource.getImage("SaveGameCommand.icon"));
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateParameters();
				saveOrder();
			}
		});
		btn.setToolTipText("Save order");

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("char.icon"));
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectCharEvent.toString(), ((Order) getFormObject()).getCharacter(), this));

			}
		});
		btn.setToolTipText("Goto chars");

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("clear.icon"));
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearOrder();
			}
		});
		btn.setToolTipText("Delete order");

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("revert.icon"));
		btn.setIcon(ico);
		glb.append(btn);
		btn.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				revertOrder();
			}
		});
		btn.setToolTipText("Revert order");

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("ShowHideOrderDescription.icon"));
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OrderEditor.this.descriptionLabel.setVisible(!OrderEditor.this.descriptionLabel.isVisible());
				OrderEditor.this.description.setVisible(!OrderEditor.this.description.isVisible());
			}
		});
		btn.setToolTipText("Show / hide description");

		glb.nextLine();

		glb.append(new JLabel("Order :"));

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

		this.chkDraw = new JCheckBox("Draw");
		this.chkDraw.setPreferredSize(new Dimension(60, 18));
		this.chkDraw.setBackground(Color.white);
		glb.append(this.chkDraw, 3, 1);
		this.chkDraw.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Order o = (Order) getFormObject();
				OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
				if (!ovd.contains(o)) {
					ovd.addOrder((Order) getFormObject());
				} else {
					ovd.removeOrder((Order) getFormObject());
				}
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), getFormObject(), this));

			}
		});
		this.chkDraw.setFocusable(false);
		btn.setToolTipText("Draw");

		btn = new JButton();
		btn.setPreferredSize(new Dimension(18, 18));
		ico = new ImageIcon(imgSource.getImage("displace.icon"));
		btn.setIcon(ico);
		glb.append(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Order o = (Order) getFormObject();
				if (o.getOrderNo() == 830 || o.getOrderNo() == 850 || o.getOrderNo() == 860) {
					OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
					if (!ovd.contains(o) && ovd.getOrderEditorOrder() != o) {
						ovd.addOrder((Order) getFormObject());
					}
					Object d = ovd.getAdditionalInfo(o, "displacement");
					if (d == null) {
						d = 1;
					} else {
						d = ((Integer) d) + 1;
						if ((Integer) d > 4)
							d = 0;
					}
					if (d.equals(0)) {
						ovd.removeAdditionalInfo(o, "displacement");
					} else {
						ovd.setAdditionalInfo(o, "displacement", d);
					}
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), getFormObject(), this));
				}
			}
		});
		btn.setToolTipText("Draw displaced");

		glb.nextLine();

		glb.append(new JLabel("Parameters :"));
		glb.append(this.parameters = new JTextField());
		this.parameters.setEditable(false);
		this.parameters.setPreferredSize(new Dimension(70, PREFERRED_TEXT_HEIGHT));

		this.parametersInternal = new JTextField();
		this.parametersInternal.setVisible(false);
		glb.append(this.parametersInternal);
		glb.nextLine();

		glb.append(this.descriptionLabel = new JLabel("Description :"));
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
		if (getSelectedOrderNo().equals("") || o.getOrderNo() != Integer.parseInt(getSelectedOrderNo())) {
			int orderNo = getSelectedOrderNo().equals("") ? -1 : Integer.parseInt(getSelectedOrderNo());
			// exclude changes between
			// 830, 850, 860
			// 810, 820 and 870
			String[] equivalentOrders = new String[] { ",830,850,860,", ",810,820,870,", ",230,240,250,255," };
			boolean areEquivalent = false;
			for (int i = 0; i < equivalentOrders.length; i++) {
				int j = equivalentOrders[i].indexOf("," + String.valueOf(o.getOrderNo()) + ",");
				int k = equivalentOrders[i].indexOf("," + String.valueOf(getSelectedOrderNo()) + ",");
				if (j >= 0 && k >= 0) {
					areEquivalent = true;
				}
			}
			if (areEquivalent)
				return false;

			o.setParameters("");
			if (Arrays.binarySearch(new int[] { 830, 850, 860 }, orderNo) > -1) {
				o.setP0("no");
			}
			this.parameters.setText(Order.getParametersAsString(o.getParameters()));
			this.parametersInternal.setText(o.getParameters());
			return true;
		}
		return false;
	}

	public void refreshDrawCheck() {
		Order o = (Order) getFormObject();
		OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
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
		int i = noAndCode.indexOf(" ");
		if (i > 0) {
			return noAndCode.substring(0, i);
		} else {
			return "";
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
		if (!getSelectedOrderNo().equals("") && getOrderEditorData() != null) {
			OrderEditorData oed = getOrderEditorData().findFirstByProperty("orderNo", Integer.parseInt(getSelectedOrderNo()));
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
						if (paramType.equals("hex")) {
							sub = new SingleParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("cid") || paramType.equals("xid")) {
							sub = new SingleParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("tac")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "ch", "fl", "st", "su", "hr", "am" }, new String[] { "Charge", "Flank", "Standard", "Surround", "Hit and Run", "Ambush" });
						} else if (paramType.equals("prd")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "le", "br", "st", "mi", "fo", "ti", "mo" }, new String[] { "Leather", "Bronze", "Steel", "Mithril", "Food", "Timber", "Mounts" });
						} else if (paramType.equals("pro")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "le", "br", "st", "mi", "fo", "ti", "mo", "go" }, new String[] { "Leather", "Bronze", "Steel", "Mithril", "Food", "Timber", "Mounts", "Gold" });
						} else if (paramType.equals("%")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("g")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("yn")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "y", "n" }, new String[] { "Yes", "No" });
						} else if (paramType.equals("wep")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "wo", "br", "st", "mi" }, new String[] { "Wooden", "Bronze", "Steel", "Mithril" });
						} else if (paramType.equals("arm")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "no", "le", "br", "st", "mi" }, new String[] { "None", "Leather", "Bronze", "Steel", "Mithril" });
						} else if (paramType.equals("trp")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "hc", "lc", "hi", "li", "ar", "ma" }, new String[] { "HC", "LC", "HI", "LI", "AR", "MA" });
						} else if (paramType.equals("gen")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "m", "f" }, new String[] { "Male", "Female" });
						} else if (paramType.equals("alg")) {
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "g", "e", "n" }, new String[] { "Good", "Evil", "Neutral" });
						} else if (paramType.equals("nam")) {
							sub = new SingleParameterOrderSubeditor(paramDescription, o, 160);
						} else if (paramType.equals("rsp")) {
							sub = new SingleParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("a")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("ae")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("b")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("e")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("d")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("de")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("dcid")) {
							sub = new SingleParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("i")) {
							sub = new NumberParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("dir") || paramType.equals("dirx")) {
							// sub = new
							// SingleParameterOrderSubeditor(paramDescription,
							// o);
							sub = new DropDownParameterOrderSubeditor(paramDescription, o, new String[] { "ne", "e", "se", "sw", "w", "nw" }, new String[] { "ne", "e", "se", "sw", "w", "nw" });
						} else if (paramType.equals("nat")) {
							sub = new NationParameterOrderSubeditor(paramDescription, o);
						} else if (paramType.equals("spx") || paramType.equals("spc") || paramType.equals("sph") || paramType.equals("spm") || paramType.equals("spl")) {
							sub = new SpellNumberParameterOrderSubeditor(paramDescription, o, oed.getOrderNo());
						} else if (paramType.equals("spz")) {
							sub = new ResearchSpellNumberParameterOrderSubeditor(paramDescription, o, oed.getOrderNo());
						} else if (paramType.equals("art")) {
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
				tlb.cell(new JLabel(" "));
				tlb.row();
				tlb.cell(new JLabel(" "));
				tlb.row();
				tlb.cell(new JLabel(" "));
				tlb.row();
				tlb.cell(new JLabel(" "));
				tlb.row();
				tlb.cell(new JLabel(" "));
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
		String txt = "";
		try {
			String selOrder = (String) this.orderCombo.getSelectedItem();
			int i = selOrder.indexOf(' ');
			int no = Integer.parseInt(selOrder.substring(0, i));
			Container<OrderMetadata> orderMetadata = getGameMetadata().getOrders();

			OrderMetadata om = orderMetadata.findFirstByProperty("number", no);
			txt = om.getName() + ", " + om.getDifficulty() + ", " + om.getRequirement() + "\n" + om.getParameters();
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
		this.parameters.setText("");
		this.parametersInternal.setText("");
		refreshSubeditor();
		refreshDescription();
		Order o = (Order) getFormObject();
		o.setNoAndCode(this.orderCombo.getSelectedItem().toString());
		o.setParameters(this.parametersInternal.getText());
		this.currentOrderNoAndCode = "";
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
			this.character.setText("");
		} else {
			this.character.setText(c.getName() + " - " + c.getHexNo());
		}
		this.chkDraw.setEnabled(GraphicUtils.canRenderOrder(o));
		OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
		if (PreferenceRegistry.instance().getPreferenceValue("orderEditor.autoDraw").equals("yes") && PreferenceRegistry.instance().getPreferenceValue("orderEditor.autoSave").equals("yes")) {
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
		String v = "";
		for (JComponent c : this.subeditorComponents) {
			String val = "";
			if (JTextField.class.isInstance(c)) {
				val = ((JTextField) c).getText();
			} else if (JComboBox.class.isInstance(c)) {
				if (((JComboBox) c).getSelectedItem() == null) {
					val = "";
				} else {
					val = ((JComboBox) c).getSelectedItem().toString();
				}
			}
			if (val.equals("")) {
				val = "-";
			}
			v += (v.equals("") ? "" : Order.DELIM) + val;
		}
		this.parametersInternal.setText(v);
		this.parameters.setText(v.replace(Order.DELIM, " "));
		if (getAutoSave()) {
			saveOrder();
		}

	}

	protected void addValidationResult(Order o, OrderValidationResult res, Integer paramI) {
		if (res == null)
			return;
		OrderResultContainer cont = (OrderResultContainer) Application.instance().getApplicationContext().getBean("orderResultContainer");
		String e = "";
		if (paramI != null) {
			e = "Param " + paramI + ": ";
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
		String pval = PreferenceRegistry.instance().getPreferenceValue("orderEditor.autoSave");
		return !pval.equals("no");
	}
}
