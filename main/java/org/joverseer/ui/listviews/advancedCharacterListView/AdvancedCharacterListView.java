package org.joverseer.ui.listviews.advancedCharacterListView;

import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.tools.infoCollectors.characters.AdvancedCharacterWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterAttributeWrapper;
import org.joverseer.tools.infoCollectors.characters.CharacterInfoCollector;
import org.joverseer.ui.listviews.AbstractListViewFilter;
import org.joverseer.ui.listviews.BaseItemListView;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.HexFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractApplicationPage;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.table.SortableTableModel;

/**
 * Advanced Character information tab
 * Shows AdvancedCharacterWrappers in a jtable
 * 
 * @author Marios Skounakis
 */
public class AdvancedCharacterListView extends BaseItemListView {

    public AdvancedCharacterListView() {
        super(AdvancedCharacterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {96, 48, 48, 48, 48, 48, 48, 48, 48, 48, 32, 32, 32, 32, 32, 32, 120, 48, 96, 48, 32, 48};
    }

    protected boolean hasTextFilter() {
    	return true;
    }
    
    
    
	protected AbstractListViewFilter getTextFilter(String txt) {
		if (txt == null || txt.equals("")) return super.getTextFilter(txt);
		try {
			int hexNo = Integer.parseInt(txt.trim());
			return new HexFilter("", hexNo);
		}
		catch (Exception exc) {
			// do nothing
		}
		return new TextFilter("Name", "name", txt);
	}

	protected void setItems() {
        ArrayList items = CharacterInfoCollector.instance().getWrappers();
        ArrayList filteredItems = new ArrayList();
        AbstractListViewFilter activeFilter = getActiveFilter(); 
        for (Object o : items) {
            if (activeFilter == null || activeFilter.accept(o)) {
                filteredItems.add(o);
            }
        }
        tableModel.setRows(filteredItems);
    }

    protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        table.setDefaultRenderer(CharacterAttributeWrapper.class, new CharacterAttributeWrapperTableCellRenderer(
                tableModel));
        table.setDefaultRenderer(ArtifactWrapper.class, new ArtifactWrapperTableCellRenderer(tableModel));
        table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(tableModel) {

            public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3,
                    int arg4, int arg5) {
                Component c = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
                JLabel lbl = (JLabel) c;
                Integer v = (Integer) arg1;
                if (v == null || v.equals(0)) {
                    lbl.setText("");
                }
                if (arg5 == 1) {
                    // render capital with bold
                    int idx = ((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(arg4);
                    Object obj = tableModel.getRow(idx);
                    AdvancedCharacterWrapper ch = (AdvancedCharacterWrapper)obj;
                    PopulationCenter capital = (PopulationCenter)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[]{"nationNo", "capital"}, new Object[]{ch.getNationNo(), Boolean.TRUE});
                    if (capital != null && ch.getHexNo() == capital.getHexNo()) {
                        lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
                    }

                }
                
                return c;
            }

        });
        
        table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(tableModel) {
            public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3,
                    int arg4, int arg5) {
                Component c = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
                JLabel lbl = (JLabel) c;
                
                if (arg5 == 21) {
                    String results = (String)arg1;
                    if (results != null) {
                        results = "<html><body>" + results.replace("\n", "<br>") + "</body></html>";
                        lbl.setToolTipText(results);
                    }
                } else {
                	lbl.setToolTipText("");
                }
                return c;
            } 
        });
        return c;
    }


    protected AbstractListViewFilter[][] getFilters() {
        ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
        filters.addAll(Arrays.asList(NationFilter.createNationFilters()));
        filters.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
        return new AbstractListViewFilter[][] {
                filters.toArray(new AbstractListViewFilter[] {}),
                TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(),
                new AbstractListViewFilter[]{
                    new DeathFilter("Not Dead & Dead", null),
                    new DeathFilter("Not Dead", new CharacterDeathReasonEnum[]{CharacterDeathReasonEnum.NotDead, null}),
                    new DeathFilter("Dead", new CharacterDeathReasonEnum[]{CharacterDeathReasonEnum.Assassinated, CharacterDeathReasonEnum.Executed, CharacterDeathReasonEnum.Dead, CharacterDeathReasonEnum.Cursed}),
                },
                new AbstractListViewFilter[]{
                	new ArmyCommanderFilter("", null),
                	new ArmyCommanderFilter("Army commander", true),
                	new ArmyCommanderFilter("Not army commander", false)
                },
        };
    }

    protected JComponent[] getButtons() {
        ArrayList<JComponent> comps = new ArrayList<JComponent>();
        comps.addAll(Arrays.asList(super.getButtons()));
        JLabelButton popupMenu = new JLabelButton();
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
        popupMenu.setIcon(ico);
        popupMenu.addActionListener(new PopupMenuActionListener() {

            public JPopupMenu getPopupMenu() {
                CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                        "advancedCharacterListViewCommandGroup", new Object[] {
                        		new CopyToClipboardCommand(),
                        		new ToggleAttributeSortModeCommand((AdvancedCharacterTableModel)tableModel),
                        		});
                return group.createPopupMenu();
            }
        });
        comps.add(popupMenu);
        return comps.toArray(new JComponent[] {});
    }
    
    class ToggleAttributeSortModeCommand extends ActionCommand {
    	AdvancedCharacterTableModel model;
    	
		public ToggleAttributeSortModeCommand(AdvancedCharacterTableModel model) {
			super("toggleAttributeSortModeCommand");
			this.model = model;
		}

		protected void doExecuteCommand() {
			if (CharacterAttributeWrapper.COMPARIZON_MODE == CharacterAttributeWrapper.COMPARE_BY_TOTAL_VALUE) {
				CharacterAttributeWrapper.COMPARIZON_MODE = CharacterAttributeWrapper.COMPARE_BY_NET_VALUE;
			} else {
				CharacterAttributeWrapper.COMPARIZON_MODE = CharacterAttributeWrapper.COMPARE_BY_TOTAL_VALUE;
			}
			model.fireTableDataChanged();
		}
    }
    
	class CopyToClipboardCommand extends ActionCommand implements ClipboardOwner {

        String DELIM = "\t";
        String NL = "\n";
        Game game;

        protected void doExecuteCommand() {
            game = GameHolder.instance().getGame();
            String txt = "";
            for (int j=0; j<tableModel.getDataColumnCount(); j++) {
            	txt += (txt.equals("") ? "" : DELIM) + tableModel.getDataColumnHeaders()[j];
            	if (j == 2) {
            		// duplicate column "nation"
            		txt += (txt.equals("") ? "" : DELIM) + tableModel.getDataColumnHeaders()[j];
            	}
            }
            txt += NL;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(i);
                AdvancedCharacterWrapper aw = (AdvancedCharacterWrapper) tableModel.getRow(idx);
                txt += getRow(aw) + NL;
            }
            StringSelection stringSelection = new StringSelection(txt);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }

        private String getStatText(CharacterAttributeWrapper caw) {
            String v = caw == null || caw.getValue() == null ? "" : caw.getValue().toString();
            if (caw != null && caw.getValue() != null) {
                InfoSource is = caw.getInfoSource();
                if (DerivedFromTitleInfoSource.class.isInstance(is)) {
                    v += "+";
                } else if (RumorActionInfoSource.class.isInstance(is)) {
                    v += "+";
                }
            }
            if (caw != null && caw.getTotalValue() != null) {
                if (!caw.getTotalValue().toString().equals(caw.getValue().toString())
                        && !caw.getTotalValue().toString().equals("0")) {
                    v += "(" + caw.getTotalValue().toString() + ")";
                }
            }
            if (v == null || v.equals("0"))
                return v;

            if (caw != null && caw.getInfoSource() != null) {
                InfoSource is = caw.getInfoSource();
                if (MetadataSource.class.isInstance(is)) {
                    v += "*";
                } else if (DerivedFromTitleInfoSource.class.isInstance(is)) {
                    v += "*";
                } else if (RumorActionInfoSource.class.isInstance(is)) {
                    v += "*";
                }
            }
            return v;
        }

        private String getArtifactText(ArtifactWrapper aw) {
            if (aw == null)
                return "";
            String txt = String.valueOf(aw.getNumber());
            if (aw.getInfoSource().getTurnNo() < game.getCurrentTurn()) {
                txt += " (t" + aw.getInfoSource().getTurnNo() + ")";
            }
            return txt;
        }

        private String getDeathReasonStr(CharacterDeathReasonEnum cd) {
            if (cd == null)
                return "?";
            if (cd == CharacterDeathReasonEnum.NotDead)
                return "";
            return cd.toString();
        }

        private String getRow(AdvancedCharacterWrapper aw) {
            Nation n = game.getMetadata().getNationByNum(aw.getNationNo());
            String nationName = n == null || n.getNumber() == 0 ? "" : n.getShortName();
            String orderResults = aw.getOrderResults();
            if (orderResults == null) orderResults = "";
            return aw.getHexNo() + DELIM + aw.getName() + DELIM + aw.getNationNo() + DELIM + nationName + DELIM
                    + getStatText(aw.getCommand()) + DELIM + getStatText(aw.getAgent()) + DELIM
                    + getStatText(aw.getEmmisary()) + DELIM + getStatText(aw.getMage()) + DELIM
                    + getStatText(aw.getStealth()) + DELIM + getStatText(aw.getHealth()) + DELIM
                    + getStatText(aw.getChallenge()) + DELIM + getArtifactText(aw.getA0()) + DELIM
                    + getArtifactText(aw.getA1()) + DELIM + getArtifactText(aw.getA2()) + DELIM
                    + getArtifactText(aw.getA3()) + DELIM + getArtifactText(aw.getA4()) + DELIM
                    + getArtifactText(aw.getA5()) + DELIM + aw.getTravellingWith() + DELIM
                    + getDeathReasonStr(aw.getDeathReason()) + DELIM 
                    + InfoSourceTableCellRenderer.getInfoSourceDescription(aw.getInfoSource())+ DELIM
                    + aw.getTurnNo() + DELIM
                    + (aw.getDragonPotential() == null ? "" : aw.getDragonPotential()) + DELIM
                    + orderResults.replace("\n", " ").replace("\r", " ");
        }

        public void lostOwnership(Clipboard arg0, Transferable arg1) {
        }
    }

    class DeathFilter extends AbstractListViewFilter {
        ArrayList<CharacterDeathReasonEnum> deathReasons = new ArrayList<CharacterDeathReasonEnum>();
        
        public DeathFilter(String descr, CharacterDeathReasonEnum[] deathReasons) {
            super(descr);
            if (deathReasons == null) {
                this.deathReasons = null;
            } else {
                this.deathReasons.addAll(Arrays.asList(deathReasons));
            }
        }

        public boolean accept(Object obj) {
            return deathReasons == null || deathReasons.contains(((AdvancedCharacterWrapper)obj).getDeathReason());
        }
    }
    
    class ArmyCommanderFilter extends AbstractListViewFilter {
    	Boolean isArmyCommander = null;
    	
    	public ArmyCommanderFilter(String descr, Boolean isArmyCommander) {
    		super(descr);
    		this.isArmyCommander = isArmyCommander;
    	}
    	
    	public boolean accept(Object obj) {
    		if (isArmyCommander == null) return true;
    		Turn turn = GameHolder.instance().getGame().getTurn();
    		if (turn.getContainer(TurnElementsEnum.Army).findAllByProperty("commanderName", ((AdvancedCharacterWrapper)obj).getName()).size() > 0) {
    			return isArmyCommander;
    		} else {
    			return !isArmyCommander;
    		}
    	}
    }

}
