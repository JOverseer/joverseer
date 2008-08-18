package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.EditCharacterForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class CreateCharacterCommand extends ActionCommand {
    
    
    public CreateCharacterCommand() {
        super("createCharacterCommand");
    }
    
    protected void doExecuteCommand() {
        final Character character = new Character();
        if (MapPanel.instance().getSelectedHex() != null) {
            character.setHexNo(MapPanel.instance().getSelectedHex().x * 100 + MapPanel.instance().getSelectedHex().y);
        };
        FormModel formModel = FormModelHelper.createFormModel(character);
        final EditCharacterForm form = new EditCharacterForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() { 
                this.setDescription(this.getDescription().getMessage());
            }

            protected boolean onFinish() {
                form.commit();
                character.setId(Character.getIdFromName(character.getName()));
                
                Game g = GameHolder.instance().getGame();
                Turn t = g.getTurn();
                if (t.getContainer(TurnElementsEnum.Character).findFirstByProperty("id", character.getId()) != null) {
                    ErrorDialog errDlg = new ErrorDialog("A character with id '" + character.getId() + "' already exists.");
                    errDlg.showDialog();
                    return false;
                }
                t.getContainer(TurnElementsEnum.Character).addItem(character);
                
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));
                return true;
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("addCharacterDialog.title", new Object[]{""}, Locale.getDefault()));
        dialog.setModal(true);
        dialog.showDialog();
    }

}