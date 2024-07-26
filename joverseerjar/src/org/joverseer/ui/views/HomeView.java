package org.joverseer.ui.views;

import static org.assertj.core.api.Assertions.setAllowComparingPrivateFields;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.HomeViewInfoCollector;
import org.joverseer.tools.turnReport.BaseReportObject;
import org.joverseer.tools.turnReport.TurnReportCollector;
import org.joverseer.ui.ScalableAbstractView;
import org.joverseer.ui.command.LoadGame;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.jidesoft.spring.richclient.docking.JideApplicationLifecycleAdvisor;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

public class HomeView extends ScalableAbstractView implements ApplicationListener {
	// injected dependency
	GameHolder gameHolder;

	public GameHolder getGameHolder() {
		return this.gameHolder;
	}
	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}
	
	public HomeView() {
		super();
	}

	JEditorPane editor;
	JEditorPane sideEditor;
	
	boolean isReportGenerated = false;
	protected String[] getReportContents() {
		this.isReportGenerated = true;
		HomeViewInfoCollector hvic = new HomeViewInfoCollector();
		return hvic.renderReport();
	}
	
	public void setEditors(String[] content) {
		this.editor.setText(content[0]);
		this.sideEditor.setText(content[1]);
	}
	
	@Override
	public void componentFocusGained() {
		super.componentFocusGained();
		
		if (!this.isReportGenerated) {
			this.setEditors(this.getReportContents());
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				//HomeView.this.getControl().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				System.out.println(HomeView.this.getControl().getClass());
				System.out.println(HomeView.this.getControl().getCursor());
				HomeView.this.p.setDividerLocation(0.75);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						HomeView.this.setImageIcon(HomeView.this.p.getRightComponent().getWidth());
					}
				});
			}
		});
	}
	

	protected void handleHyperlinkEvent(String url) {
		String query = url.substring(url.indexOf("?") + 1);
		String subQ = query.substring(0, query.indexOf("="));
		
		if(subQ.equals("recentgame")) {
			String file = query.substring(11);
			LoadGame loadGame = new LoadGame(file, this.gameHolder);
			loadGame.execute();
		}
		else {
			try {
	            if (Desktop.isDesktopSupported()) {
	                Desktop desktop = Desktop.getDesktop();
	                if (desktop.isSupported(Desktop.Action.BROWSE)) {
	                    desktop.browse(URI.create(url));
	                }
	            }
			} catch (IOException | InternalError e1) {}
		}
	}

	protected JPanel getCriteriaPanel() {
		return null;
	}
	
	public void setImageIcon(int width) {
		ImageIcon i = new ImageIcon("resources/images/melogo_small_tm_black.png");
		Image im = i.getImage().getScaledInstance(width, -1, Image.SCALE_SMOOTH);
		this.lblLogo.setIcon(new ImageIcon(im));
	}

	JSplitPane p;
	JLabel lblLogo;
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected JComponent createControl() {
		this.p = new JSplitPane();
		this.p.setResizeWeight(0.5);
		this.p.setBackground(Color.white);
		this.p.setEnabled(true);
		//this.p.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
		//this.p.setEnabled(false);
		System.out.println(this.p.getCursor().getName());
		
		JPanel panel_L = new JPanel();
		this.p.setLeftComponent(panel_L);
		panel_L.setLayout(new BorderLayout());
		//panel_L.setLayout(new BoxLayout(panel_L, BoxLayout.Y_AXIS));
		panel_L.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		this.editor = new JEditorPane();
		this.editor.setContentType("text/html");
		this.editor.setEditable(false);
		this.editor.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				System.out.println(e.getEventType());
				if(e.getEventType() == EventType.ENTERED) {
					System.out.println("He");
					HomeView.this.p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					
				}
				if(e.getEventType() == EventType.EXITED) {
					HomeView.this.p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
				if (e.getEventType() == EventType.ACTIVATED) {
					String s = e.getURL().toString();
					handleHyperlinkEvent(s);
				}
			}
		});
		panel_L.add(this.editor, BorderLayout.CENTER);
		
		JEditorPane jp = new JEditorPane();
		jp.setContentType("text/html");
		jp.setEditable(false);
		jp.setText("<div style='font-family:MS Sans Serif; font-size:11pt'><i>" + Messages.getString("applicationDescriptor.description"));
		panel_L.add(jp, BorderLayout.PAGE_END);
		
		JPanel panel_R = new JPanel();
		panel_R.setOpaque(true);
		panel_R.setBackground(Color.WHITE);
		this.p.setRightComponent(panel_R);
		panel_R.setLayout(new BoxLayout(panel_R, BoxLayout.Y_AXIS));
		panel_R.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		ImageIcon i = new ImageIcon("resources/images/melogo_small_tm_black.png");
		Image im = i.getImage().getScaledInstance(290, -1, Image.SCALE_SMOOTH);
		this.lblLogo = new JLabel(new ImageIcon(im));
		this.lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.lblLogo.setBackground(Color.WHITE);
		this.lblLogo.setOpaque(true);
		panel_R.add(this.lblLogo);
		
		this.sideEditor = new JEditorPane();
		this.sideEditor.setContentType("text/html");
		this.sideEditor.setEditable(false);
		panel_R.add(this.sideEditor);
		
		return this.p;

	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) applicationEvent);
		}
	}
	public void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case SelectedTurnChangedEvent:
		case GameChangedEvent:
		}
	}
	
}
