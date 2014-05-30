package org.joverseer.ui.command;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComponent;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.tools.ordercheckerIntegration.OrdercheckerProxy;
import org.joverseer.tools.ordercheckerIntegration.ReflectionUtils;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.views.SelectOrderchekerNationForm;
import org.springframework.richclient.application.Application;
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

	public RunOrdercheckerCommand() {
		super(ID);
	}

	@Override
	protected void doExecuteCommand() {
		try {
			if (!ActiveGameChecker.checkActiveGameExists())
				return;

			// if
			// (GameHolder.instance().getGame().getMetadata().getGameType().equals(GameTypeEnum.gameUW))
			// {
			// MessageSource ms =
			// (MessageSource)Application.services().getService(MessageSource.class);
			// MessageDialog md = new MessageDialog(
			// ms.getMessage("errorDialog.title", new String[]{},
			// Locale.getDefault()),
			// ms.getMessage("errorOrdercheckerDoesNotSupportUW", new
			// String[]{}, Locale.getDefault()));
			// md.showDialog();
			// return;
			// }
			
			// show a form so that the user selects the desired nation
			final SelectOrderchekerNationForm frm = new SelectOrderchekerNationForm(FormModelHelper.createFormModel(0));
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

			TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
				@Override
				protected void onAboutToShow() {
					try {

						RunOrdercheckerCommand.this.proxy.runOrderchecker();

						Game g = GameHolder.instance().getGame();

						// update the order result container with the order
						// results
						// the order results are retrieved from the order
						// checker proxy
						OrderResultContainer cont = (OrderResultContainer) Application.instance().getApplicationContext().getBean("orderResultContainer");

						ArrayList<OrderResult> resultList = new ArrayList<OrderResult>();
						ArrayList<Character> chars = g.getTurn().getCharacters().findAllByProperty("nationNo", Main.main.getNation().getNation());
						for (Character c : chars) {
							if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead)
								continue;
							com.middleearthgames.orderchecker.Character mc = Main.main.getNation().findCharacterById(c.getId() + "     ".substring(0, 5 - c.getId().length()));
							for (com.middleearthgames.orderchecker.Order mo : RunOrdercheckerCommand.this.proxy.getCharacterOrders(mc)) {
								Order order = RunOrdercheckerCommand.this.proxy.getOrderMap().get(mo);
								boolean resultFound = false;
								cont.removeResultsForOrder(order);
								for (String msg : RunOrdercheckerCommand.this.proxy.getOrderInfoResults(mo)) {
									OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Info);
									resultList.add(or);
									resultFound = true;
								}
								for (String msg : RunOrdercheckerCommand.this.proxy.getOrderErrorResults(mo)) {
									OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Error);
									resultList.add(or);
									resultFound = true;
								}
								for (String msg : RunOrdercheckerCommand.this.proxy.getOrderHelpResults(mo)) {
									OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Help);
									resultList.add(or);
									resultFound = true;
								}
								for (String msg : RunOrdercheckerCommand.this.proxy.getOrderWarnResults(mo)) {
									OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Warning);
									resultList.add(or);
									resultFound = true;
								}
								if (!resultFound) {
									OrderResult or = new OrderResult(order, "Checked okay.", OrderResultTypeEnum.Okay);
									resultList.add(or);
								}
							}
						}

						cont.addAll(resultList);
					} catch (Exception exc) {
						System.out.println(exc.getMessage());
					}
				}

				@Override
				protected boolean onFinish() {
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
			System.out.println(exc.getMessage());
			exc.printStackTrace();
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
		public OrdercheckerForm(Main main) {
			super(FormModelHelper.createFormModel(main), "OrdercheckerForm");
		}

		@Override
		protected JComponent createFormControl() {
			TableLayoutBuilder tlb = new TableLayoutBuilder();
			Main main = (Main) getFormObject();
			try {
				JComponent c = (JComponent) ReflectionUtils.retrieveField(main.getWindow(), "bottomPane");
				c.getParent().remove(c);
				c.setPreferredSize(new Dimension(750, 600));
				tlb.cell(c);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			;

			return tlb.getPanel();
		}

	}

}
