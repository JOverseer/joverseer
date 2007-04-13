package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.TurnInitializer;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.NewGame;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapMetadataUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.NewGameForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class CreateGame extends ActionCommand {
    public CreateGame() {
        super("createGameCommand");
    }

    protected void doExecuteCommand() {
        final NewGame ng = new NewGame();
        FormModel formModel = FormModelHelper.createFormModel(ng);
        final NewGameForm form = new NewGameForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        final MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        final TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
                setDescription(ms.getMessage(form.getId() + ".description", null, Locale.getDefault()));
            }

            protected boolean onFinish() {
                form.commit();
                Game game = new Game();
                GameMetadata gm = (GameMetadata)Application.instance().getApplicationContext().getBean("gameMetadata");
                gm.setGameNo(ng.getNumber());
                gm.setNationNo(ng.getNationNo());
                gm.setGameType(ng.getGameType());
                try {
                    gm.load();
                } catch (Exception e) {
                    ErrorDialog dlg = new ErrorDialog(e);
                    dlg.showDialog();
                    return true;
                } 

                MapMetadataUtils mmu = new MapMetadataUtils();
                MapMetadata mm = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
                mmu.setMapSize(mm, gm.getGameType());
                
                game.setMetadata(gm);
                game.setMaxTurn(0);
                GameHolder gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
                gh.setGame(game);

                Turn t0 = new Turn();
                t0.setTurnNo(0);
                TurnInitializer ti = new TurnInitializer();
                ti.initializeTurnWith(t0, null);
                try {
                    game.addTurn(t0);
                }
                catch (Exception exc) {
                    // do nothing, exception cannoit really occur
                }

                Application.instance().getApplicationContext().publishEvent(
                                    new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), game, this));

                return true;
            }
        };
        dialog.setTitle(ms.getMessage("newGameDialog.title", new Object[]{}, Locale.getDefault()));
        dialog.showDialog();

    }
}
