package org.joverseer.ui.support;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Stroke;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

import org.joverseer.domain.Order;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.map.renderers.OrderRenderer;
import org.joverseer.ui.map.renderers.Renderer;
import org.springframework.jca.cci.object.MappingRecordOperation;
import org.springframework.richclient.application.Application;

import com.jidesoft.docking.DockingManager;
import com.jidesoft.spring.richclient.docking.JideApplicationWindow;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.tipoftheday.ResourceBundleTipOfTheDaySource;
import com.jidesoft.tipoftheday.TipOfTheDayDialog;


public class GraphicUtils {
    public static Font getFont(String name, int style, int size) {
        return new Font(name, style, size);
    }

    public static Stroke getBasicStroke(int width) {
        return new BasicStroke(width);
    }

    public static Stroke getDashStroke(int width, int dashSize) {
        return new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10, new float[]{dashSize, dashSize}, 2);
    }
    
    public static Stroke getDotStroke(int width) {
        return new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 10, new float[]{width, width}, 2);        
    }
    
    public static void addOverwriteDropListener(JTextComponent c) {
        final JTextComponent cc = c;
        c.setDropTarget(new DropTarget(c, new DropTargetListener() {

            public void dragEnter(DropTargetDragEvent arg0) {
            }
    
            public void dragExit(DropTargetEvent arg0) {
            }
    
            public void dragOver(DropTargetDragEvent arg0) {
            }
    
            public void drop(DropTargetDropEvent arg0) {
                try {
                    cc.setText(arg0.getTransferable().getTransferData(DataFlavor.stringFlavor).toString());
                    cc.requestFocus();
                }
                catch (Exception exc) {
                    
                }
            }
            public void dropActionChanged(DropTargetDragEvent arg0) {
            }
        }));
    }
    
    public static String parseName(String name) {
        if (name.equals("Unknown (Map Icon)")) return "Unknown";
        if (name.equals("")) return "Unknown";
        return name;
    }
    
    public static void showView(String id) {
        JideApplicationWindow window = (JideApplicationWindow)Application.instance().getActiveWindow();
        window.getDockingManager().showFrame(id);
    }
    
    public static boolean canRenderOrder(Order o) {
        Renderer orderRenderer = (Renderer)Application.instance().getApplicationContext().getBean("orderRenderer");
        if (orderRenderer == null) return false;
        return ((OrderRenderer)orderRenderer).canRender(o);
    }
    
    public static void showTipOfTheDay() {
    	ResourceBundle rb;
        ResourceBundleTipOfTheDaySource tipOfTheDaySource = new ResourceBundleTipOfTheDaySource(rb = ResourceBundle.getBundle("tips"));
        int count = 0;
        Enumeration e = rb.getKeys();
        while (e.hasMoreElements()) {
        	count++;
        	e.nextElement();
        }
        tipOfTheDaySource.setCurrentTipIndex((int)(Math.random() * count));
        URL styleSheet = TipOfTheDayDialog.class.getResource("/tips.css");
        TipOfTheDayDialog dialog = new TipOfTheDayDialog((Frame) null, tipOfTheDaySource, new AbstractAction("Show Tips on startup") {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) e.getSource();
                    PreferenceRegistry.instance().setPreferenceValue("general.tipOfTheDay", checkBox.isSelected() ? "yes" : "no");
                }
            }
        }, styleSheet);
        dialog.setShowTooltip(PreferenceRegistry.instance().getPreferenceValue("general.tipOfTheDay").equals("yes"));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.pack();
        JideSwingUtilities.globalCenterWindow(dialog);
        dialog.setModal(true);
        dialog.setVisible(true);
    }
    
    public static void setTableColumnRenderer(JTable table, int iColumn, TableCellRenderer renderer) {
        table.getColumnModel().getColumn(iColumn).setCellRenderer(renderer);
    }
}       
