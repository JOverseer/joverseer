package org.joverseer.ui.command;

import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.joverseer.domain.Character;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.domain.mapItems.HighlightHexesMapItem;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Highlights a set of characters based on criteria specified using the
 * HighlightOptionsForm form
 * 
 * @author Marios Skounakis
 */
public class HighlightCharacters extends ActionCommand {
	public HighlightCharacters() {
		super("highlightCharactersCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;

		FormModel formModel = FormModelHelper.createFormModel(new HighlightOptions());
		final HighlightOptionsForm form = new HighlightOptionsForm(formModel);
		FormBackedDialogPage page = new FormBackedDialogPage(form);

		TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
			@Override
			protected void onAboutToShow() {
			}

			@Override
			protected boolean onFinish() {
				form.commit();

				HighlightHexesMapItem hhmi = new HighlightHexesMapItem();
				Game g = GameHolder.instance().getGame();
				Container<Character> chars = g.getTurn().getCharacters();
				HighlightOptions opts = (HighlightOptions) form.getFormObject();
				for (Character c : chars.getItems()) {
					if (opts.acceptCharacter(c)) {
						hhmi.addHex(c.getHexNo());
					}
				}
				AbstractMapItem.add(hhmi);

				Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));
				return true;
			}
		};
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		dialog.setTitle(ms.getMessage("highlightOptionsDialog.title", new Object[] {}, Locale.getDefault()));
		dialog.showDialog();
	}

	private class HighlightOptionsForm extends AbstractForm {
		public static final String FORM_PAGE = "highlightOptionsForm";

		public HighlightOptionsForm(FormModel formModel) {
			super(formModel, FORM_PAGE);
		}

		@Override
		protected JComponent createFormControl() {
			TableFormBuilder fb = new TableFormBuilder(getBindingFactory());
			fb.add("minCommandRank");
			fb.row();
			fb.add("minAgentRank");
			fb.row();
			fb.add("minEmmisaryRank");
			fb.row();
			fb.add("minMageRank");
			fb.row();
			fb.add("minStealthRank");
			fb.row();
			fb.add("minChallengeRank");
			fb.row();
			fb.add("nationNo");
			fb.row();
			fb.add("allegiance", new JComboBox(NationAllegianceEnum.values()));
			fb.row();
			fb.add("withinRangeOfHex");

			fb.row();
			fb.add("freeText");
			fb.row();

			return fb.getForm();
		}
	}

	private class HighlightOptions {
		Integer minCommandRank = null;
		Integer minAgentRank = null;
		Integer minEmmisaryRank = null;
		Integer minMageRank = null;
		Integer minStealthRank = null;
		Integer minChallengeRank = null;
		Integer nationNo = null;
		NationAllegianceEnum allegiance = null;
		String freeText = null;
		Integer withinRangeOfHex = null;

		public NationAllegianceEnum getAllegiance() {
			return allegiance;
		}

		public void setAllegiance(NationAllegianceEnum allegiance) {
			this.allegiance = allegiance;
		}

		public Integer getWithinRangeOfHex() {
			return withinRangeOfHex;
		}

		public void setWithinRangeOfHex(Integer withinRangeOfHex) {
			this.withinRangeOfHex = withinRangeOfHex;
		}

		public String getFreeText() {
			return freeText;
		}

		public void setFreeText(String freeText) {
			this.freeText = freeText;
		}

		public Integer getMinAgentRank() {
			return minAgentRank;
		}

		public void setMinAgentRank(Integer minAgentRank) {
			this.minAgentRank = minAgentRank;
		}

		public Integer getMinChallengeRank() {
			return minChallengeRank;
		}

		public void setMinChallengeRank(Integer minChallengeRank) {
			this.minChallengeRank = minChallengeRank;
		}

		public Integer getMinCommandRank() {
			return minCommandRank;
		}

		public void setMinCommandRank(Integer minCommandRank) {
			this.minCommandRank = minCommandRank;
		}

		public Integer getMinEmmisaryRank() {
			return minEmmisaryRank;
		}

		public void setMinEmmisaryRank(Integer minEmmisaryRank) {
			this.minEmmisaryRank = minEmmisaryRank;
		}

		public Integer getMinMageRank() {
			return minMageRank;
		}

		public void setMinMageRank(Integer minMageRank) {
			this.minMageRank = minMageRank;
		}

		public Integer getMinStealthRank() {
			return minStealthRank;
		}

		public void setMinStealthRank(Integer minStealthRank) {
			this.minStealthRank = minStealthRank;
		}

		public Integer getNationNo() {
			return nationNo;
		}

		public void setNationNo(Integer nationNo) {
			this.nationNo = nationNo;
		}

		public boolean acceptCharacter(Character c) {
			if (getMinCommandRank() != null && c.getCommandTotal() < getMinCommandRank()) {
				return false;
			}
			if (getMinAgentRank() != null && c.getAgentTotal() < getMinAgentRank()) {
				return false;
			}
			if (getMinEmmisaryRank() != null && c.getEmmisaryTotal() < getMinEmmisaryRank()) {
				return false;
			}
			if (getMinMageRank() != null && c.getMageTotal() < getMinMageRank()) {
				return false;
			}
			if (getMinStealthRank() != null && c.getStealthTotal() < getMinStealthRank()) {
				return false;
			}
			if (getNationNo() != null && !c.getNationNo().equals(getNationNo())) {
				return false;
			}
			if (getWithinRangeOfHex() != null && MovementUtils.distance(c.getHexNo(), getWithinRangeOfHex()) > 12) {
				return false;
			}

			Game g = GameHolder.instance().getGame();
			Turn t = g.getTurn();
			NationRelations nr = (NationRelations) t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", c.getNationNo());
			NationAllegianceEnum allegiance = (nr != null ? nr.getAllegiance() : null);
			if (getAllegiance() != null && getAllegiance() != allegiance)
				return false;

			String charData = c.getName();
			for (SpellProficiency sp : c.getSpells()) {
				SpellInfo si = g.getMetadata().getSpells().findFirstByProperty("number", sp.getSpellId());
				charData += " " + si.getName();
			}

			for (Integer artino : c.getArtifacts()) {
				ArtifactInfo ai = g.getMetadata().getArtifacts().findFirstByProperty("no", artino);
				charData += " " + ai.getName();
			}

			if (getFreeText() != null && charData.toUpperCase().indexOf(getFreeText().toUpperCase()) == -1) {
				return false;
			}
			return true;
		}
	}
}
