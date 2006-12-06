package org.joverseer.ui.command;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.application.Application;
import org.springframework.binding.form.FormModel;
import org.joverseer.game.Turn;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 6 Δεκ 2006
 * Time: 10:06:37 μμ
 * To change this template use File | Settings | File Templates.
 */
public class EditRelations extends ActionCommand {
    public EditRelations() {
        super("editRelationsCommand");
    }

    protected void doExecuteCommand() {
//        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
//        final Turn t = g.getTurn();
//        FormModel formModel = FormModelHelper.createFormModel(t);
//        final EditNationRelations form = new EditNationRelations(formModel);
//        form.setFormObject(t);
//        FormBackedDialogPage page = new FormBackedDialogPage(form);
//
//        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
//            protected void onAboutToShow() {
//            }
//
//            protected boolean onFinish() {
//                form.commit();
//                return true;
//            }
//        };
//        dialog.showDialog();
    }
}
