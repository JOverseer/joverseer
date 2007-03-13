package org.joverseer.ui.command;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.EditNationAllegiancesForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
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
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.SpellProficiency;

public class RunOrdercheckerCommand  extends ApplicationWindowAwareCommand{
    private static final Log log = LogFactory.getLog(RestoreDefaultLayoutCommand.class);

    private static final String ID = "runOrdercheckerCommand"; 

    public RunOrdercheckerCommand(){
            super(ID);
    }
    
    protected void doExecuteCommand() {
        Resource res = Application.instance().getApplicationContext().getResource("classpath:metadata/orderchecker/orderchecker.dat");
        try {
            ObjectInputStream ois = new ObjectInputStream(res.getInputStream());
            Data data = new Data();
            data.readObject(ois);
            ois.close();
            data.setDataDirectory("bin/metadata/orderchecker/");
            
            final Main main = new Main(true, data);
            Main.main = main;
            
            final Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            final OrdercheckerForm form = new OrdercheckerForm(main);
            FormBackedDialogPage page = new FormBackedDialogPage(form);

            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                    main.getWindow().getOrdersPath().setText("D:\\data\\mscoon\\MiddleEarth\\g26\\t14\\me07v1.026");
                    updateOrdercheckerGameData(7);
                    main.getWindow().goActionPerformedMscoon();
                }
                
                protected boolean onFinish() {
                    Game g = GameHolder.instance().getGame();
                    
                    ArrayList<Character> chars = (ArrayList<Character>)g.getTurn().getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", 7);
                    for (Character c : chars) {
                        if (c.getDeathReason() != CharacterDeathReasonEnum.NotDead) continue;
                        com.middleearthgames.orderchecker.Character mc = main.getNation().findCharacterById(c.getId());
                        for (com.middleearthgames.orderchecker.Order mo : (Vector<com.middleearthgames.orderchecker.Order>)mc.getOrders()) {
                            JTree t = new JTree();
                            OCTreeNode p = new OCTreeNode(t, mo, true);
                            mo.addTreeNodes(t, p);
                        }
                    }
                    return true;
                }
            };
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            dialog.setTitle(ms.getMessage("changeNationAllegiancesDialog.title", new Object[]{}, Locale.getDefault()));
            dialog.showDialog();
        }
        catch (Exception exc) {
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }
        
    }
    
    protected void updateOrdercheckerGameData(int nationNo) {
        Game g = GameHolder.instance().getGame();
        Turn t = g.getTurn();
        
        PlayerInfo pi = (PlayerInfo)t.getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", nationNo);
        
        Nation nation = new Nation();
        nation.SetNation(nationNo);
        nation.setGame(g.getMetadata().getGameNo());
        nation.setTurn(t.getTurnNo());
        // add other nation info
        
        for (PopulationCenter popCenter : (ArrayList<PopulationCenter>)t.getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
            PopCenter pc = new PopCenter(popCenter.getHexNo());
            pc.setCapital(popCenter.getCapital() ? 1 : 0);
            pc.setDock(popCenter.getHarbor().getSize());
            pc.setName(popCenter.getName());
            pc.setFortification(popCenter.getFortification().getSize());
            pc.setSize(popCenter.getSize().getCode());
            pc.setNation(popCenter.getNationNo());
            pc.setHidden(popCenter.getHidden() ? 1 : 0);
            pc.setLoyalty(popCenter.getLoyalty());
            nation.addPopulationCenter(pc);
            
            if (popCenter.getCapital() && popCenter.getNationNo() == nationNo) {
                nation.setCapital(popCenter.getHexNo());
            }
        }
        
        for (Character ch : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
            if (ch.getDeathReason() != CharacterDeathReasonEnum.NotDead) continue;
            com.middleearthgames.orderchecker.Character mc = new com.middleearthgames.orderchecker.Character(ch.getId());
            mc.setNation(ch.getNationNo());
            mc.setName(ch.getName());
            mc.setAgentRank(ch.getAgent());
            mc.setTotalAgentRank(ch.getAgentTotal());
            mc.setCommandRank(ch.getCommand());
            mc.setTotalCommandRank(ch.getCommandTotal());
            mc.setMageRank(ch.getMage());
            mc.setTotalMageRank(ch.getMageTotal());
            mc.setEmissaryRank(ch.getEmmisary());
            mc.setTotalEmissaryRank(ch.getEmmisaryTotal());
            mc.setChallenge(ch.getChallenge());
            mc.setHealth(ch.getHealth() == null ? 0 : ch.getHealth());
            mc.setStealth(ch.getStealth());
            mc.setTotalStealth(ch.getStealthTotal());
            
            //TODO double check
            for (Integer artiNo : ch.getArtifacts()) {
                ArtifactInfo ai = (ArtifactInfo)g.getMetadata().getArtifacts().findFirstByProperty("no", artiNo);
                mc.addArtifact(ai.getNo(), ai.getName());
            }
            
            //TODO double check
            for (SpellProficiency sp : ch.getSpells()) {
                mc.addSpell(sp.getSpellId(), sp.getName());
            }
            nation.addCharacter(mc);
        }
        
        for (Army army : (ArrayList<Army>)t.getContainer(TurnElementsEnum.Army).getItems()) {
            com.middleearthgames.orderchecker.Army a = new com.middleearthgames.orderchecker.Army(Integer.parseInt(army.getHexNo()));
            a.setCommander(army.getCommanderName());
            a.setNation(army.getNationNo());
            //TODO fill in other details
            nation.addArmy(a);
        }
        Main.main.setNation(nation);
    }
    
    public class OrdercheckerForm extends AbstractForm {
        public OrdercheckerForm(Main main) {
            super(FormModelHelper.createFormModel(main), "OrdercheckerForm");
        }

        protected JComponent createFormControl() {
            TableLayoutBuilder tlb = new TableLayoutBuilder();
            Main main = (Main)getFormObject();
            tlb.cell(main.getWindow());
            main.getWindow().setFrame(Application.instance().getActiveWindow().getControl());
            return tlb.getPanel();
        }
        
    }

}
