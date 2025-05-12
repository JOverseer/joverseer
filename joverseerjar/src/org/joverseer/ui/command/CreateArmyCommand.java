package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.EditArmyForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

public class CreateArmyCommand extends ActionCommand {

	//dependencies
	GameHolder gameHolder;

	public CreateArmyCommand(GameHolder gameHolder) {
		super("createArmyCommand");
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		final Army army = new Army();
		if (MapPanel.instance().getSelectedHex() != null) {
			army.setHexNo(String.valueOf(MapPanel.instance().getSelectedHex().x * 100 + MapPanel.instance().getSelectedHex().y));
		}
		;
		army.setCommanderName("");
		army.setNationNo(0);
		army.setFood(0);
		army.setMorale(0);
		army.setCommanderTitle("");
		army.setSize(ArmySizeEnum.unknown);

		FormModel formModel = FormModelHelper.createFormModel(army);
		final EditArmyForm form = new EditArmyForm(formModel,this.gameHolder);
		FormBackedDialogPage page = new FormBackedDialogPage(form);

		CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
			@Override
			protected void onAboutToShow() {
				this.setDescription(this.getDescription().getMessage());
				form.setFormObject(null);
				form.setFormObject(army);
			}

			@Override
			protected boolean onFinish() {
				form.commit();

				Game g = CreateArmyCommand.this.gameHolder.getGame();
				Turn t = g.getTurn();
				if (!army.getCommanderName().toLowerCase().startsWith("unknown") && t.getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", army.getCommanderName()) != null) {
					ErrorDialog ed = new ErrorDialog(Messages.getString("addArmyDialog.error.DuplicateCommanderName", new Object[] { army.getCommanderName() }));
					ed.showDialog();
					return false;
				}
				t.getContainer(TurnElementsEnum.Army).addItem(army);

				JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, this, this);
				return true;
			}
		};
		dialog.setTitle(Messages.getString("addArmyDialog.title"));
		dialog.setModal(true);
		dialog.showDialog();
	}
}
