package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.domain.Note;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.UniqueIdGenerator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.views.EditNoteForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class AddEditNoteCommand extends ActionCommand {
    Note note;
    
    public AddEditNoteCommand(Object target) {
        note = new Note();
        note.setId(UniqueIdGenerator.get());
        note.setTarget(target);
    }
    
    public AddEditNoteCommand(Note note) {
        this.note = note;
    }
    
    protected void doExecuteCommand() {
        FormModel formModel = FormModelHelper.createFormModel(note);
        final EditNoteForm form = new EditNoteForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
            }

            protected boolean onFinish() {
                form.commit();
                Game g = GameHolder.instance().getGame();
                Turn t = g.getTurn();
                if (!t.getContainer(TurnElementsEnum.Notes).contains(note)) {
                    t.getContainer(TurnElementsEnum.Notes).addItem(note);
                }
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.ListviewRefreshItems.toString(), this, this));
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.NoteAddedOrUpdated.toString(), note, this));

                return true;
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("editNoteDialog.title", new Object[]{""}, Locale.getDefault()));
        dialog.setModal(false);
        dialog.showDialog();
    }
}
