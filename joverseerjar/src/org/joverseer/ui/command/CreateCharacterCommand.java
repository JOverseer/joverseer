package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.EditCharacterForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

public class CreateCharacterCommand extends ActionCommand {
	//dependencies
	GameHolder gameHolder;

	public CreateCharacterCommand(GameHolder gameHolder) {
		super("createCharacterCommand");
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		final Character character = new Character();
		if (MapPanel.instance().getSelectedHex() != null) {
			character.setHexNo(MapPanel.instance().getSelectedHex().x * 100 + MapPanel.instance().getSelectedHex().y);
		}
		;
		FormModel formModel = FormModelHelper.createFormModel(character);
		final EditCharacterForm form = new EditCharacterForm(formModel,this.gameHolder);
		FormBackedDialogPage page = new FormBackedDialogPage(form);

		CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
			@Override
			protected void onAboutToShow() {
				this.setDescription(this.getDescription().getMessage());
			}

			@Override
			protected boolean onFinish() {
				form.commit();
				character.setId(Character.getIdFromName(character.getName()));

				Game g = CreateCharacterCommand.this.gameHolder.getGame();
				Turn t = g.getTurn();
				if (t.getContainer(TurnElementsEnum.Character).findFirstByProperty("id", character.getId()) != null) {
					ErrorDialog ed = new ErrorDialog(Messages.getString("addCharacterDialog.error.DuplicateCharacterId", new Object[] { character.getId() }));
					ed.showDialog();
					return false;
				}
				t.getContainer(TurnElementsEnum.Character).addItem(character);

				JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, this, this);
				return true;
			}
		};
		dialog.setTitle(Messages.getString("addCharacterDialog.title"));
		dialog.setModal(true);
		dialog.showDialog();
	}

}
