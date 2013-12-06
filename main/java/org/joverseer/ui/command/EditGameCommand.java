package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.NewGame;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.views.NewGameForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

public class EditGameCommand extends ActionCommand {
    public EditGameCommand() {
        super("editGameCommand");
    }

    @Override
	protected void doExecuteCommand() {
    	if (!ActiveGameChecker.checkActiveGameExists()) return;
    	final Game g = GameHolder.instance().getGame();
        final NewGame ng = new NewGame();
        ng.setGameType(g.getMetadata().getGameType());
        ng.setNationNo(g.getMetadata().getNationNo());
        ng.setNumber(g.getMetadata().getGameNo());
        ng.setNewXmlFormat(g.getMetadata().getNewXmlFormat());
        FormModel formModel = FormModelHelper.createFormModel(ng);
        final NewGameForm form = new NewGameForm(formModel, true);
        final FormBackedDialogPage page = new FormBackedDialogPage(form);

        final MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        final TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
                setDescription(ms.getMessage(form.getId() + ".description", null, Locale.getDefault()));
            }

            @Override
			protected boolean onFinish() {
                form.commit();
                
                GameMetadata gm = g.getMetadata();
                gm.setGameNo(ng.getNumber());
                gm.setNationNo(ng.getNationNo());
                gm.setGameType(ng.getGameType());
                gm.setAdditionalNations(ng.getAdditionalNations());
                gm.setNewXmlFormat(ng.getNewXmlFormat());
                
                GameHolder gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
                gh.setGame(g);
                
                Application.instance().getApplicationContext().publishEvent(
                                    new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), g, this));

                return true;
            }
        };
        dialog.setTitle(ms.getMessage("editGameDialog.title", new Object[]{}, Locale.getDefault()));
        dialog.showDialog();

    }
}
