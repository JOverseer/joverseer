package org.joverseer.ui.command;

import java.util.ArrayList;
import java.util.Locale;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.EditNationAllegiancesForm;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class ChangeNationAllegiances extends ActionCommand {
    
    public ChangeNationAllegiances() {
        super("changeNationAllegiancesCommand");
    }

    protected void doExecuteCommand() {
        final Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        FormModel formModel = FormModelHelper.createFormModel(g.getMetadata());
        final EditNationAllegiancesForm form = new EditNationAllegiancesForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
                form.setFormObject(g.getMetadata());
            }

            protected boolean onFinish() {
                form.commit();
                return true;
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("changeNationAllegiancesDialog.title", new Object[]{}, Locale.getDefault()));
        dialog.showDialog();
    }

}
