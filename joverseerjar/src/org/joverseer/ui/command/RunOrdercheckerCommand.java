package org.joverseer.ui.command;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import org.joverseer.JOApplication;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.tools.ordercheckerIntegration.OrdercheckerProxy;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.SelectOrderchekerNationForm;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.middleearthgames.orderchecker.Main;

/**
 * Runs OrderChecker using the OrderCheckerProxy class.
 *
 * It also takes care of updating the orders with the results stored in the
 * OrderCheckerProxy and OrderChecker objects
 *
 * @author Marios Skounakis
 */
public class RunOrdercheckerCommand extends ApplicationWindowAwareCommand {
	private static final String ID = "runOrdercheckerCommand";

	OrdercheckerProxy proxy = null;
	int selectedNation = -1;
	//injected
	final GameHolder gh;

	public RunOrdercheckerCommand(GameHolder gh) {
		super(ID);
		this.gh = gh;
	}

	@Override
	protected void doExecuteCommand() {
		try {
			if (!ActiveGameChecker.checkActiveGameExists())
				return;

			// show a form so that the user selects the desired nation
			final SelectOrderchekerNationForm frm = new SelectOrderchekerNationForm(FormModelHelper.createFormModel(0),this.gh);
			FormBackedDialogPage pg = new FormBackedDialogPage(frm);
			TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
				@Override
				protected boolean onFinish() {
					RunOrdercheckerCommand.this.selectedNation = ((org.joverseer.metadata.domain.Nation) frm.getFormObject()).getNumber();
					return true;
				}
			};
			dlg.setTitle(Messages.getString("selectOrdercheckerNation.title"));
			dlg.showDialog();
			if (this.selectedNation == -1)
				return;

			// create new order checker proxy
			this.proxy = new OrdercheckerProxy();
			this.proxy.updateOrdercheckerGameData(this.selectedNation);
			final OrdercheckerForm form = new OrdercheckerForm(Main.main);
			FormBackedDialogPage page = new FormBackedDialogPage(form);
			page.setTitle(this.gh.getGame().getMetadata().getNationByNum(this.selectedNation).getName());

			TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
				@Override
				protected void onAboutToShow() {
					try {

						RunOrdercheckerCommand.this.proxy.runOrderchecker();

						Game g = RunOrdercheckerCommand.this.gh.getGame();

						// update the order result container with the order
						// results
						// the order results are retrieved from the order
						// checker proxy
						OrderResultContainer cont = RunOrdercheckerCommand.this.gh.getGame().getTurn().getOrderResults().getResultCont();

						int nationNo = Main.main.getNation().getNation();
						cont.clearAllOrdersForNation(nationNo);
						ArrayList<OrderResult> resultList = new ArrayList<OrderResult>();
						ArrayList<Character> chars = g.getTurn().getCharacters().findAllByProperty("nationNo", nationNo);
						for (Character c : chars) {
							if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead)
								continue;
							com.middleearthgames.orderchecker.Character mc = Main.main.getNation().findCharacterById(c.getId() + "     ".substring(0, 5 - c.getId().length()));
							for (com.middleearthgames.orderchecker.Order mo : RunOrdercheckerCommand.this.proxy.getCharacterOrders(mc)) {
								Order order = RunOrdercheckerCommand.this.proxy.getOrderMap().get(mo);
								boolean resultFound = false;
								cont.removeResultsForOrder(order);
								for (String msg : RunOrdercheckerCommand.this.proxy.getOrderInfoResults(mo)) {
									OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Info, nationNo);
									resultList.add(or);
									resultFound = true;
								}
								for (String msg : RunOrdercheckerCommand.this.proxy.getOrderErrorResults(mo)) {
									OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Error, nationNo);
									resultList.add(or);
									resultFound = true;
								}
								for (String msg : RunOrdercheckerCommand.this.proxy.getOrderHelpResults(mo)) {
									OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Help, nationNo);
									resultList.add(or);
									resultFound = true;
								}
								for (String msg : RunOrdercheckerCommand.this.proxy.getOrderWarnResults(mo)) {
									OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Warning, nationNo);
									resultList.add(or);
									resultFound = true;
								}
								if (!resultFound) {
									OrderResult or = new OrderResult(order, "Checked okay.", OrderResultTypeEnum.Okay, nationNo);
									resultList.add(or);
								}
							}
						}

						cont.addAll(resultList);
						//do this synchronously.
						JOApplication.publishEvent(LifecycleEventsEnum.OrderCheckerRunEvent, cont);
					} catch (Exception exc) {
//						ErrorDialog.showErrorDialog(exc);
					}
				}

				@Override
				protected boolean onFinish() {
					RunOrdercheckerCommand.this.gh.getGame().getTurn().getOrderResults().getResultCont().overrideResultForNation(RunOrdercheckerCommand.this.selectedNation, form.ch.isSelected());
					return true;
				}

				@Override
				protected Object[] getCommandGroupMembers() {
					return new AbstractCommand[] { getFinishCommand() };
				}
			};
			dialog.setTitle(Messages.getString("checkOrdersDialog.title"));
			dialog.showDialog();
		} catch (Exception exc) {
			ErrorDialog.showErrorDialog(exc);
		}

	}

	/**
	 * Simple form for displaying the OrderChecker windows. Basically embeds the
	 * information request and relation order checker windows into a dialog and
	 * shows them in JOverseer.
	 *
	 * @author Marios Skounakis
	 */
	public class OrdercheckerForm extends AbstractForm {
		JCheckBox ch;
		public OrdercheckerForm(Main main) {
			super(FormModelHelper.createFormModel(main), "OrdercheckerForm");
		}

		@Override
		protected JComponent createFormControl() {
			TableLayoutBuilder tlb = new TableLayoutBuilder();
			Main main = (Main) getFormObject();
			try {
				JComponent c = (JComponent) main.getWindow().getBottomPane();
				c.getParent().remove(c);
				c.setPreferredSize(new Dimension(750, 600));
				tlb.cell(c);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			;
			tlb.relatedGapRow();
			this.ch = new JCheckBox(Messages.getString("ordersOK.label"));
			tlb.cell(this.ch);

			return tlb.getPanel();
		}
		
		public boolean userOrderApproval() {
			return true;
		}

	}

}
