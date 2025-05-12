package org.joverseer.ui.command;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.joverseer.JOApplication;
import org.joverseer.domain.Character;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
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
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
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
	//dependencies
	GameHolder gameHolder;
	public HighlightCharacters(GameHolder gameHolder) {
		super("highlightCharactersCommand");
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;

		FormModel formModel = FormModelHelper.createFormModel(new HighlightOptions());
		final HighlightOptionsForm form = new HighlightOptionsForm(formModel);
		FormBackedDialogPage page = new FormBackedDialogPage(form);

		CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
			@Override
			protected void onAboutToShow() {
			}

			@Override
			protected boolean onFinish() {
				form.commit();

				HighlightHexesMapItem hhmi = new HighlightHexesMapItem();
				Game g = HighlightCharacters.this.gameHolder.getGame();
				Container<Character> chars = g.getTurn().getCharacters();
				HighlightOptions opts = (HighlightOptions) form.getFormObject();
				for (Character c : chars.getItems()) {
					if (opts.acceptCharacter(c)) {
						hhmi.addHex(c.getHexNo());
					}
				}
				AbstractMapItem.add(hhmi);

				JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, this, this);
				return true;
			}
		};
		dialog.setTitle(Messages.getString("highlightOptionsDialog.title"));
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
			return this.allegiance;
		}

		@SuppressWarnings("unused")
		public void setAllegiance(NationAllegianceEnum allegiance) {
			this.allegiance = allegiance;
		}

		public Integer getWithinRangeOfHex() {
			return this.withinRangeOfHex;
		}

		@SuppressWarnings("unused")
		public void setWithinRangeOfHex(Integer withinRangeOfHex) {
			this.withinRangeOfHex = withinRangeOfHex;
		}

		public String getFreeText() {
			return this.freeText;
		}

		@SuppressWarnings("unused")
		public void setFreeText(String freeText) {
			this.freeText = freeText;
		}

		public Integer getMinAgentRank() {
			return this.minAgentRank;
		}

		@SuppressWarnings("unused")
		public void setMinAgentRank(Integer minAgentRank) {
			this.minAgentRank = minAgentRank;
		}

		@SuppressWarnings("unused")
		public Integer getMinChallengeRank() {
			return this.minChallengeRank;
		}

		@SuppressWarnings("unused")
		public void setMinChallengeRank(Integer minChallengeRank) {
			this.minChallengeRank = minChallengeRank;
		}

		public Integer getMinCommandRank() {
			return this.minCommandRank;
		}

		@SuppressWarnings("unused")
		public void setMinCommandRank(Integer minCommandRank) {
			this.minCommandRank = minCommandRank;
		}

		public Integer getMinEmmisaryRank() {
			return this.minEmmisaryRank;
		}

		@SuppressWarnings("unused")
		public void setMinEmmisaryRank(Integer minEmmisaryRank) {
			this.minEmmisaryRank = minEmmisaryRank;
		}

		public Integer getMinMageRank() {
			return this.minMageRank;
		}

		@SuppressWarnings("unused")
		public void setMinMageRank(Integer minMageRank) {
			this.minMageRank = minMageRank;
		}

		public Integer getMinStealthRank() {
			return this.minStealthRank;
		}

		@SuppressWarnings("unused")
		public void setMinStealthRank(Integer minStealthRank) {
			this.minStealthRank = minStealthRank;
		}

		public Integer getNationNo() {
			return this.nationNo;
		}

		@SuppressWarnings("unused")
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

			Game g = HighlightCharacters.this.gameHolder.getGame();
			Turn t = g.getTurn();
			NationRelations nr = t.getNationRelations(c.getNationNo());
			NationAllegianceEnum allegiance1 = (nr != null ? nr.getAllegiance() : null);
			if (getAllegiance() != null && getAllegiance() != allegiance1)
				return false;

			String charData = c.getName();
			for (SpellProficiency sp : c.getSpells()) {
				SpellInfo si = g.getMetadata().getSpells().findFirstByProperty("number", sp.getSpellId());
				charData += " " + si.getName();
			}

			for (Integer artino : c.getArtifacts()) {
				ArtifactInfo ai = g.getMetadata().findFirstArtifactByNumber(artino);
				charData += " " + ai.getName();
			}

			if (getFreeText() != null && charData.toUpperCase().indexOf(getFreeText().toUpperCase()) == -1) {
				return false;
			}
			return true;
		}
	}
}
