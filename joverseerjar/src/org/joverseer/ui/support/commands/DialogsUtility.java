package org.joverseer.ui.support.commands;

import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Encounter;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.NarrationForm;
import org.joverseer.ui.views.OrderResultsForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

public class DialogsUtility {
	public static void showCharacterOrderResults(Character character) {
		final Character c = character;
		final OrderResultsForm f = new OrderResultsForm(FormModelHelper.createFormModel(c));
    	FormBackedDialogPage pg = new FormBackedDialogPage(f);
    	CustomTitledPageApplicationDialog dlg = new CustomTitledPageApplicationDialog(pg) {
			@Override
			protected boolean onFinish() {
				return true;
			}

			@Override
			protected void onAboutToShow() {
				super.onAboutToShow();
				f.setFormObject(c);
			}

			@Override
			protected Object[] getCommandGroupMembers() {
	                    return new AbstractCommand[] {
	                            getFinishCommand()
	                    };
	                }

    	};
        dlg.setTitle(Messages.getString("orderResultsDialog.title"));
    	dlg.showDialog();
	}

	public static void showCombatNarration(Combat combat, int nationNo,Game game) {
		if (nationNo == 0) nationNo = combat.getFirstNarrationNation();
		final String narration = combat.getNarrationForNation(nationNo);
		if (narration == null) return;
        FormModel formModel = FormModelHelper.createFormModel(narration);
        final NarrationForm form = new NarrationForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);
        CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
                form.setFormObject(narration);
            }

            @Override
			protected boolean onFinish() {
                return true;
            }

            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }

        };
        Nation n = game.getMetadata().getNationByNum(nationNo);

        dialog.setTitle(Messages.getString("combatNarrationDialog.title", new Object[]{String.valueOf(combat.getHexNo()), n.getName()}));
        dialog.showDialog();
	}

	public static void showEncounterDescription(Encounter encounter) {
		String description = encounter.getDescription();
		if (encounter.getCanInvestigate()) {
			description += Messages.getString("encounterDialog.canInvestigate");
		}
		final String descr = description;
        FormModel formModel = FormModelHelper.createFormModel(descr);
        final NarrationForm form = new NarrationForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);
        CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
                form.setFormObject(descr);
            }

            @Override
			protected boolean onFinish() {
                return true;
            }

            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }
        };
        dialog.setTitle(Messages.getString("encounterDialog.title", new Object[]{encounter.getCharacter(), String.valueOf(encounter.getHexNo())}));
        dialog.showDialog();
	}

	public static void showChallengeDescription(Challenge challenge) {
		showEncounterDescription(challenge);
	}
}
