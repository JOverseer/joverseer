package org.joverseer.ui.support.commands;

import java.util.Locale;

import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Encounter;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.views.NarrationForm;
import org.joverseer.ui.views.OrderResultsForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

public class DialogsUtility {
	public static void showCharacterOrderResults(Character character) {
		final Character c = character;
		final OrderResultsForm f = new OrderResultsForm(FormModelHelper.createFormModel(c));
    	FormBackedDialogPage pg = new FormBackedDialogPage(f);
    	TitledPageApplicationDialog dlg = new TitledPageApplicationDialog(pg) {
			protected boolean onFinish() {
				return true;
			}

			protected void onAboutToShow() {
				super.onAboutToShow();
				f.setFormObject(c);
			}
			
			protected Object[] getCommandGroupMembers() {
	                    return new AbstractCommand[] {
	                            getFinishCommand()
	                    };
	                }

    	};
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dlg.setTitle(ms.getMessage("orderResultsDialog.title", new Object[]{}, Locale.getDefault()));
    	dlg.showDialog();
	}
	
	public static void showCombatNarration(Combat combat) {
		showCombatNarration(combat, 0);
	}
	
	public static void showCombatNarration(Combat combat, int nationNo) {
		if (nationNo == 0) nationNo = combat.getFirstNarrationNation();
		final String narration = combat.getNarrationForNation(nationNo);
		if (narration == null) return;
        FormModel formModel = FormModelHelper.createFormModel(narration);
        final NarrationForm form = new NarrationForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);
        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
                form.setFormObject(narration);
            }

            protected boolean onFinish() {
                return true;
            }

            protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }

        };
        Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Nation n = game.getMetadata().getNationByNum(nationNo);
        
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("combatNarrationDialog.title", new Object[]{String.valueOf(combat.getHexNo()), n.getName()}, Locale.getDefault()));
        dialog.showDialog();
	}
	
	public static void showEncounterDescription(Encounter encounter) {
		String description = encounter.getDescription();
		if (encounter.getCanInvestigate()) {
			description += "\n\n(This is not an actual encounter but a report that the character can issue 290 \norders to investigate the encounter)";
		}
		final String descr = description;
        FormModel formModel = FormModelHelper.createFormModel(descr);
        final NarrationForm form = new NarrationForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);
        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
                form.setFormObject(descr);
            }

            protected boolean onFinish() {
                return true;
            }
            
            protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("encounterDialog.title", new Object[]{encounter.getCharacter(), String.valueOf(encounter.getHexNo())}, Locale.getDefault()));
        dialog.showDialog();
	}
	
	public static void showChallengeDescription(Challenge challenge) {
		showEncounterDescription(challenge);
	}
}
