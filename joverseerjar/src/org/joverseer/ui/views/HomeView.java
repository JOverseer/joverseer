package org.joverseer.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Image;
import java.io.IOException;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.joverseer.JOApplication;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.HomeViewInfoCollector;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.ScalableAbstractView;
import org.joverseer.ui.command.LoadGame;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.image.ImageSource;

import javax.swing.JLabel;
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
	
	protected JPanel getCriteriaPanel() {
		return null;
	}

	JEditorPane editor;
	JEditorPane sideEditor;
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
		
		JPanel panel_L = new JPanel();
		this.p.setLeftComponent(panel_L);
		panel_L.setLayout(new BorderLayout());
		panel_L.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		this.editor = new JEditorPane();
		this.editor.setContentType("text/html");
		this.editor.setEditable(false);
		this.editor.setCaretColor(Color.WHITE);
		this.editor.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == EventType.ENTERED) {
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
		JScrollPane scp = new JScrollPane(this.editor);
		panel_L.add(scp, BorderLayout.CENTER);
		
		JEditorPane jp = new JEditorPane();
		jp.setContentType("text/html");
		jp.setEditable(false);
		jp.setCaretColor(Color.WHITE);
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
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		panel_R.add(p1);
		
		this.sideEditor = new JEditorPane();
		this.sideEditor.setContentType("text/html");
		this.sideEditor.setEditable(false);
		this.sideEditor.setCaretColor(Color.WHITE);
		this.sideEditor.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getURL().toString().contains("http://event?orderSentOn")) {
					if(e.getEventType() == EventType.ENTERED) {
						HomeView.this.p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						int ind = e.getURL().toString().indexOf("=") + 1;
						String s = e.getURL().toString().substring(ind);
						HomeView.this.sideEditor.setToolTipText(s);
					}
					if(e.getEventType() == EventType.EXITED) {
						HomeView.this.sideEditor.setToolTipText(null);
					}
					return;
				}
				
				if(e.getEventType() == EventType.ENTERED) {
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
		p1.add(this.sideEditor, BorderLayout.CENTER);
		
		jp = new JEditorPane();
		jp.setContentType("text/html");
		jp.setEditable(false);
		jp.setCaretColor(Color.WHITE);
		jp.setText("<div style='font-family:MS Sans Serif; font-size:9pt'><i>" + Messages.getString("extraLegal.copyright"));
		p1.add(jp, BorderLayout.PAGE_END);
		
		return this.p;

	}
	
	boolean isReportGenerated = false;
	
	protected String[] getReportContents() {
		this.isReportGenerated = true;
		HomeViewInfoCollector hvic = new HomeViewInfoCollector();
		return hvic.renderReport();
	}
	
	@Override
	public void componentFocusGained() {
		super.componentFocusGained();
		
		boolean homePage = true;
		for (String c : JOverseerJIDEClient.cmdLineArgs) {
			if(c.equals("-disableHome")) {
				homePage = false;
				break;
			}
		}
		if(!homePage) return;
		
		if (!this.isReportGenerated) {
			this.setEditors(this.getReportContents());
		}
		
		// Bit of janky code probably... Only way I could make the page be properly sized upon launch wiht the logo
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				HomeView.this.p.setDividerLocation(0.75);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						HomeView.this.setImageIcon(HomeView.this.p.getRightComponent().getWidth());
					}
				});
			}
		});
		this.editor.setCaretPosition(0);
	}
	
	/**
	 * Handles the hyperlinks in the JEditorPanes.
	 * Either opens a game file, or opens the url in the browser
	 * @param url
	 */
	protected void handleHyperlinkEvent(String url) {
		try {
			String query = url.substring(url.indexOf("?") + 1);
			String subQ = query.substring(0, query.indexOf("="));
			
			if(subQ.equals("recentgame")) {
				String file = query.substring(11).replace("~~", "'");;
				LoadGame loadGame = new LoadGame(file, this.gameHolder);
				loadGame.execute();
			}
			else {
				openURL(url);
			}
		} catch(StringIndexOutOfBoundsException e) {
			openURL(url);
		}
	}
	
	/**
	 * Opens a URL in the web browser
	 * @param url
	 */
	public void openURL(String url) {
		try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(URI.create(url));
                }
            }
		} catch (IOException | InternalError e1) {}
	}
	
	public void setEditors(String[] content) {
		this.editor.setText(content[0]);
		this.sideEditor.setText(content[1]);
	}
	
	public void setImageIcon(int width) {
		ImageSource is = JOApplication.getImageSource();
		Image i = is.getImage("homeView.topRight");
		//ImageIcon i = new ImageIcon("resources/images/melogo_small_tm_black.png");
		if (width == 0) width = 300;
		Image im = i.getScaledInstance(width, -1, Image.SCALE_SMOOTH);
		this.lblLogo.setIcon(new ImageIcon(im));
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) applicationEvent);
		}
	}
	public void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case SaveGameEvent:
			this.isReportGenerated = false;
			this.componentFocusGained();
			break;
		case SelectedTurnChangedEvent:
		case GameChangedEvent:
		}
	}
	
}
