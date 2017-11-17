package org.joverseer.ui.command;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.support.TurnInitializer;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.NewGame;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapMetadataUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.NewGameForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Create new game using the NewGameForm
 * 
 * @author Marios Skounakis
 */
public class CreateGame extends ActionCommand {
    public CreateGame() {
        super("createGameCommand");
    }

    @Override
	protected void doExecuteCommand() {
        final NewGame ng = new NewGame();
        FormModel formModel = FormModelHelper.createFormModel(ng);
        final NewGameForm form = new NewGameForm(formModel);
        final FormBackedDialogPage page = new FormBackedDialogPage(form);

        final TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
                setDescription(Messages.getString(form.getId() + ".description"));
            }

            @Override
			protected boolean onFinish() {
                form.commit();
                Game game = new Game();
                GameMetadata gm = (GameMetadata)Application.instance().getApplicationContext().getBean("gameMetadata");
                gm.getHexes().clear(); // without this the number of hexes keeps accumulating!
                gm.setGame(game);
                gm.setGameNo(ng.getNumber());
                gm.setNationNo(ng.getNationNo());
                gm.setGameType(ng.getGameType());
                gm.setAdditionalNations(ng.getAdditionalNations());
                gm.setNewXmlFormat(ng.getNewXmlFormat());
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
                gh.setFile(null);

                Turn t0 = new Turn();
                t0.setTurnNo(0);
                TurnInitializer ti = new TurnInitializer();
                ti.initializeTurnWith(t0, null);
                try {
                    game.addTurn(t0);
                }
                catch (Exception exc) {
                    // do nothing, exception cannot really occur
                }

                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), game, this));
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.GameLoadedEvent.toString(), game, this));

                return true;
            }
        };
        dialog.setTitle(Messages.getString("newGameDialog.title"));
        dialog.showDialog();

    }
}
