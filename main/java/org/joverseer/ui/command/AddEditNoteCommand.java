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

/**
 * Add or Edit a note
 * Uses the EditNoteForm
 * 
 * @author Marios Skounakis
 */
public class AddEditNoteCommand extends ActionCommand {
    Note note;
    
    public AddEditNoteCommand(Object target) {
        this.note = new Note();
        this.note.setId(UniqueIdGenerator.get());
        this.note.setTarget(target);
    }
    
    public AddEditNoteCommand(Note note) {
        this.note = note;
    }
    
    @Override
	protected void doExecuteCommand() {
        FormModel formModel = FormModelHelper.createFormModel(this.note);
        final EditNoteForm form = new EditNoteForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
//            protected void onAboutToShow() { 
//            }

            @Override
			protected boolean onFinish() {
                form.commit();
                Game g = GameHolder.instance().getGame();
                Turn t = g.getTurn();
                if (!t.getContainer(TurnElementsEnum.Notes).contains(AddEditNoteCommand.this.note)) {
                    t.getContainer(TurnElementsEnum.Notes).addItem(AddEditNoteCommand.this.note);
                }
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.ListviewRefreshItems.toString(), this, this));
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.NoteUpdated.toString(), AddEditNoteCommand.this.note, this));
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), AddEditNoteCommand.this.note, this));

                return true;
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("editNoteDialog.title", new Object[]{""}, Locale.getDefault()));
        dialog.setModal(false);
        dialog.showDialog();
    }
}
