package org.joverseer.ui.command;

import java.awt.Dimension;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.tools.ordercheckerIntegration.OrdercheckerProxy;
import org.joverseer.tools.ordercheckerIntegration.ReflectionUtils;
import org.joverseer.ui.EditNationAllegiancesForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.middleearthgames.orderchecker.Main;
import com.middleearthgames.orderchecker.Nation;
import com.middleearthgames.orderchecker.PopCenter;
import com.middleearthgames.orderchecker.gui.OCTreeNode;
import com.middleearthgames.orderchecker.io.*;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.Order;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.SpellProficiency;

public class RunOrdercheckerCommand  extends ApplicationWindowAwareCommand{
    private static final Log log = LogFactory.getLog(RestoreDefaultLayoutCommand.class);

    private static final String ID = "runOrdercheckerCommand"; 

    OrdercheckerProxy proxy = null;
    
    public RunOrdercheckerCommand(){
            super(ID);
    }
    
    protected void doExecuteCommand() {
        try {
            
            proxy = new OrdercheckerProxy();
            proxy.updateOrdercheckerGameData(7);
            final Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            final OrdercheckerForm form = new OrdercheckerForm(Main.main);
            FormBackedDialogPage page = new FormBackedDialogPage(form);

            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                    try {
                        
                        proxy.runOrderchecker();
                        
                        Game g = GameHolder.instance().getGame();
    
                        OrderResultContainer cont = (OrderResultContainer)Application.instance().getApplicationContext().getBean("orderResultContainer");
                        
                        ArrayList<OrderResult> resultList = new ArrayList<OrderResult>();
                        ArrayList<Character> chars = (ArrayList<Character>)g.getTurn().getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", 7);
                        for (Character c : chars) {
                            if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) continue;
                            com.middleearthgames.orderchecker.Character mc = Main.main.getNation().findCharacterById(c.getId());
                            for (com.middleearthgames.orderchecker.Order mo : proxy.getCharacterOrders(mc)) {
                                Order order = proxy.getOrderMap().get(mo);
                                cont.removeResultsForOrder(order);
                                for (String msg : proxy.getOrderInfoResults(mo)) {
                                    OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Info);
                                    resultList.add(or);
                                }
                                for (String msg : proxy.getOrderErrorResults(mo)) {
                                    OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Error);
                                    resultList.add(or);
                                }
                                for (String msg : proxy.getOrderHelpResults(mo)) {
                                    OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Help);
                                    resultList.add(or);
                                }
                                for (String msg : proxy.getOrderWarnResults(mo)) {
                                    OrderResult or = new OrderResult(order, msg, OrderResultTypeEnum.Warn);
                                    resultList.add(or);
                                }
                            }
                        }
                        
                        cont.addAll(resultList);
                    }
                    catch (Exception exc) {
                        System.out.println(exc.getMessage());
                    }
                }
                
                protected boolean onFinish() {
                    return true;
                }
                
                protected Object[] getCommandGroupMembers() {
                    return new AbstractCommand[] {
                            getFinishCommand()
                    };
                }
            };
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            dialog.setTitle(ms.getMessage("checkOrdersDialog.title", new Object[]{}, Locale.getDefault()));
            dialog.showDialog();
        }
        catch (Exception exc) {
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }
        
    }

    public class OrdercheckerForm extends AbstractForm {
        public OrdercheckerForm(Main main) {
            super(FormModelHelper.createFormModel(main), "OrdercheckerForm");
        }

        protected JComponent createFormControl() {
            TableLayoutBuilder tlb = new TableLayoutBuilder();
            Main main = (Main)getFormObject();
            try {
                JComponent c = (JComponent)ReflectionUtils.retrieveField(main.getWindow(), "bottomPane");
                c.getParent().remove(c);
                c.setPreferredSize(new Dimension(750, 600));
                tlb.cell(c);
            }
            catch (Exception exc) {
                exc.printStackTrace();
            };
            
            return tlb.getPanel();
        }
        
    }

}
