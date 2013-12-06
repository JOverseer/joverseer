/**
 * 
 */
package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.UserInfoSource;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.EditPopulationCenterForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

public class AddPopCenterCommand extends ActionCommand {
	int hexNo;

	public AddPopCenterCommand(int hexNo) {
		super();
		this.hexNo = hexNo;
	}

	@Override
	protected void doExecuteCommand() {
		final PopulationCenter pc = new PopulationCenter();
		InfoSource is = new UserInfoSource();
		Game g = GameHolder.instance().getGame();
		if (g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", this.hexNo) != null) {
			ErrorDialog md = new ErrorDialog("Cannot add new pop center - there is already a pop center in this hex.");
			md.showDialog();
			return;
		}
		is.setTurnNo(g.getCurrentTurn());
		pc.setInfoSource(is);
		pc.setInformationSource(InformationSourceEnum.exhaustive);
		pc.setHexNo(this.hexNo);
		pc.setSize(PopulationCenterSizeEnum.ruins);
		pc.setFortification(FortificationSizeEnum.none);
		pc.setHarbor(HarborSizeEnum.none);
		pc.setNationNo(0);
		pc.setLoyalty(0);
		pc.setName("-");
		FormModel formModel = FormModelHelper.createFormModel(pc);
		final EditPopulationCenterForm form = new EditPopulationCenterForm(formModel);
		FormBackedDialogPage page = new FormBackedDialogPage(form);

		TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {

			@Override
			protected void onAboutToShow() {
			}

			@Override
			protected boolean onFinish() {
				form.commit();
				GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).addItem(pc);
				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.SelectedTurnChangedEvent.toString(), this, this));
				return true;
			}
		};
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		dialog.setTitle(ms.getMessage("editPopulationCenterDialog.title", new Object[] { String.valueOf(pc.getHexNo()) }, Locale.getDefault()));
		dialog.showDialog();
	}
}