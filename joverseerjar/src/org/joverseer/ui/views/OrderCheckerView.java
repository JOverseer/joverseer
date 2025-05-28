package org.joverseer.ui.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ordercheckerIntegration.OrdercheckerProxy;
import org.joverseer.ui.ScalableAbstractView;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.NationComboBox;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.middleearthgames.orderchecker.Main;

public class OrderCheckerView extends ScalableAbstractView implements ApplicationListener{
	OrdercheckerProxy proxy = null;
	int selectedNation = -1;
	NationComboBox nationCombo;
	JPanel armyRequests;
	JPanel infoRequests;
	// injected dependencies
	GameHolder gameHolder;

	public GameHolder getGameHolder() {
		return this.gameHolder;
	}

	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	public OrderCheckerView() {
		super();
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) event);
		}
	}
	public void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case GameChangedEvent:
			this.nationCombo.load(true, true);
		}
	}

	@Override
	protected JComponent createControl() {
		this.proxy = new OrdercheckerProxy() {

			@Override
			protected void armyRequests(Main main, Vector requests) {
				Iterator<JCheckBox> itr = requests.iterator();
		        while(itr.hasNext()){
					OrderCheckerView.this.armyRequests.add(itr.next());
		        }
		        SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
				        OrderCheckerView.this.armyRequests.invalidate();
				        OrderCheckerView.this.armyRequests.repaint();
					}
		        });
			}

			@Override
			protected void infoRequests(Main main, Vector requests) {
				// TODO Auto-generated method stub
				super.infoRequests(main, requests);
			}

			@Override
			protected void displayErrorMessage(String error) {
				// TODO Auto-generated method stub
				super.displayErrorMessage(error);
			}};

		TableLayoutBuilder tlb = new TableLayoutBuilder();
        JPanel nationPanel = new JPanel();
        nationPanel.setLayout(new BoxLayout(nationPanel, BoxLayout.LINE_AXIS));
        nationPanel.add(new JLabel(Messages.getString("SelectOrderchekerNationForm.2")));
        nationPanel.add(this.nationCombo = new NationComboBox(this.gameHolder));
        this.nationCombo.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                Nation n = OrderCheckerView.this.nationCombo.getSelectedNation();
                if (n != null) {
                	OrderCheckerView.this.selectedNation = n.getNumber();
                	try {
						OrderCheckerView.this.proxy.updateOrdercheckerGameData(OrderCheckerView.this.selectedNation);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
            }

        });
    	JButton run = new JButton("run");
		run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				OrderCheckerView.this.armyRequests.removeAll();
				OrderCheckerView.this.infoRequests.removeAll();
				OrderCheckerView.this.proxy.runOrderchecker();

			}}
		);
		nationPanel.add(run);
        tlb.cell(nationPanel, "align=left"); //$NON-NLS-1$
        this.nationCombo.load(true, false);
        tlb.relatedGapRow();
        this.armyRequests = new JPanel();
    	tlb.cell(this.armyRequests);
    	tlb.relatedGapRow();
    	this.infoRequests = new JPanel();
    	tlb.relatedGapRow();
    	tlb.cell(this.infoRequests);
    	tlb.relatedGapRow();
		Main main = Main.main;
		try {
			JComponent c = (JComponent) main.getWindow().getBottomPane();
			c.getParent().remove(c);
			//c.setPreferredSize(new Dimension(750, 600));
			tlb.cell(c);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		;
		System.out.println("View");
		return tlb.getPanel();
    }

}
