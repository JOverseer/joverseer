package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.domain.Note;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.UniqueIdGenerator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.EditNoteForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Add or Edit a note
 * Uses the EditNoteForm
 *
 * @author Marios Skounakis
 */
public class AddEditNoteCommand extends ActionCommand {
    Note note;
    //dependencies
    GameHolder gameHolder;

    public AddEditNoteCommand(Object target,GameHolder gameHolder) {
        this.note = new Note();
        this.note.setId(UniqueIdGenerator.get());
        this.note.setTarget(target);
        this.gameHolder = gameHolder;
    }

    public AddEditNoteCommand(Note note,GameHolder gameHolder) {
        this.note = note;
        this.gameHolder = gameHolder;
    }

    @Override
	protected void doExecuteCommand() {
        FormModel formModel = FormModelHelper.createFormModel(this.note);
        final EditNoteForm form = new EditNoteForm(formModel,this.gameHolder);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
//            protected void onAboutToShow() {
//            }

            @Override
			protected boolean onFinish() {
                form.commit();
                Game g = AddEditNoteCommand.this.gameHolder.getGame();
                Turn t = g.getTurn();
                if (!t.getContainer(TurnElementsEnum.Notes).contains(AddEditNoteCommand.this.note)) {
                    t.getContainer(TurnElementsEnum.Notes).addItem(AddEditNoteCommand.this.note);
                }
                JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
                JOApplication.publishEvent(LifecycleEventsEnum.NoteUpdated, AddEditNoteCommand.this.note, this);
                JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, AddEditNoteCommand.this.note, this);

                //If notes is blank delete it
                if(AddEditNoteCommand.this.note.getText().isEmpty()) {
                	DeleteNoteCommand deleteNote = new DeleteNoteCommand(AddEditNoteCommand.this.note);
                	deleteNote.doExecuteCommand();
                }
                
                return true;
            }
        };
        dialog.setTitle(Messages.getString("editNoteDialog.title"));
        dialog.setModal(false);
        dialog.showDialog();
    }
}
