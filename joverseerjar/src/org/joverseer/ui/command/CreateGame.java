package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.support.TurnInitializer;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.NewGame;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapMetadataUtils;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.NewGameForm;
import org.springframework.binding.form.FormModel;
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

            // must return true, otherwise the dialog is never dismissed.
            @Override
			protected boolean onFinish() {
                form.commit();
                Game game = new Game();
                GameMetadata gm = GameMetadata.instance();
                gm.getHexes().clear(); // without this the number of hexes keeps accumulating!
                gm.setGame(game);
                gm.setGameNo(ng.getNumber());
                gm.setNationNo(ng.getNationNo());
                gm.setGameType(ng.getGameType());
                gm.setAdditionalNations(ng.getAdditionalNations());
                gm.setNewXmlFormat(ng.getNewXmlFormat());
                try {
                    gm.load();
                    MapMetadataUtils mmu = new MapMetadataUtils();
                    MapMetadata mm = MapMetadata.instance();
                    mmu.setMapSize(mm, gm.getGameType());

                    game.setMetadata(gm);
                    game.setMaxTurn(0);
                    GameHolder gh = GameHolder.instance();
                    gh.setGame(game);
                    gh.setFile(null);

                    Turn t0 = new Turn();
                    t0.setTurnNo(0);
                    TurnInitializer ti = new TurnInitializer();
                    ti.initializeTurnWith(t0, null);
                    game.addTurn(t0);

                    joApplication.publishEvent(LifecycleEventsEnum.GameChangedEvent, game, this);
                    joApplication.publishEvent(LifecycleEventsEnum.GameLoadedEvent, game, this);

                } catch (Exception e) {
                    ErrorDialog.showErrorDialog(e);
                    return true;
                } 

                return true;
            }
        };
        dialog.setTitle(Messages.getString("newGameDialog.title"));
        dialog.showDialog();

    }
}
