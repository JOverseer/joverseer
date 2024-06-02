package org.joverseer.ui.listviews;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import org.joverseer.JOApplication;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactInfoCollector;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactUserInfo;
import org.joverseer.tools.infoCollectors.artifacts.ArtifactWrapper;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.domain.Character;
import org.springframework.context.MessageSource;

/**
 * Table model for the AdvancedArtifactListView
 * 
 * Extended functionality to allow the user to update and edit information presented in the table,
 * saving and managing the info accordingly
 * 
 * @author Marios Skounakis
 * @author Sam Terrett
 */
public class AdvancedArtifactTableModel extends ItemTableModel {

	private static final long serialVersionUID = 1L;
	public static final int iHexNo = 4;
	public static final int iNo = 0;
	public static final int iName = 1;
	public static final int iNation = 2;
	public static final int iOwner = 3;
	public static final int iAlign = 5;
	public static final int iPower1 = 6;
	public static final int iPower2 = 7;
	public static final int iTurn = 8;
	public static final int iInfoSource = 9;
	
	public boolean editMode = false;
	public AdvancedArtifactTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(ArtifactWrapper.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "number", "name", "nationNo", "owner", "hexNo", "alignment", "power1", "power2", "turnNo", "infoSource" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, String.class, Integer.class, String.class, String.class, String.class, Integer.class, InfoSource.class };
	}
	
	@Override
	protected boolean isCellEditableInternal(Object object, int i) {
		if(this.editMode) {
			if(i == iNo) {
				ArtifactWrapper aw = (ArtifactWrapper) object;
				if(aw.getNumber() == 0) return true;
				return (aw.wasNoZero());
			}
			if(i == iName) {
				ArtifactWrapper aw = (ArtifactWrapper) object;
				return (!this.loadCorrsepondingAUI(aw, this.gameHolder.getGame().getTurn()).wasNoZero());
			}
			if (i == iTurn || i == iNation) return false;
			return true;
		}
		return false;
	}
	
	public void setEditable(boolean editable) {
		this.editMode = editable;
	}
	
	public boolean getEditable() {
		return this.editMode;
	}
	
	@Override
	protected Object getValueAtInternal(Object object, int i) {
		return super.getValueAtInternal(object, i);
	}
	
	@Override
	protected void setValueAtInternal(Object v, Object obj, int col) {
		Game game = this.gameHolder.getGame();
		ArtifactInfoCollector.instance().refreshWrappers();
		ArtifactWrapper aw = (ArtifactWrapper) obj;
		Turn t = game.getTurn();
		if (col == iNo) {
			if(((Integer) v).intValue() == (aw.getNumber())) return;
			if (this.getColumnData(iNo).contains(v)) return;
			
			ArtifactUserInfo aui = this.loadCorrsepondingAUI(aw, t);
			aui.setNumber(((Integer) v).intValue());
			t.getArtifactsUser().refreshItem(aui);
			
			JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
		}else if (col == iName) {
			if(v.toString().equals(aw.getName())) return;
			ArtifactUserInfo aui = this.loadCorrsepondingAUI(aw, t);
			aui.setName(v.toString());
			
			t.getArtifactsUser().refreshItem(aui);
			JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
		}else if (col == iNation) {
			
		}else if (col == iOwner) {
			if(v.toString().equals(aw.getOwner())) return;
			ArtifactUserInfo aui = this.loadCorrsepondingAUI(aw, t);
			aui.setOwner(v.toString());
			
			Character c = null;
			for (int i = this.gameHolder.getGame().getCurrentTurn(); i>=0 ; i--) {
				c = this.gameHolder.getGame().getTurn(i).getCharByName(v.toString());
				if (c != null) {
					if(c.getNationNo() != 0) aui.setNationNo(c.getNationNo());
					aui.setHexNo(c.getHexNo());
					break;
				}
			}
			
			t.getArtifactsUser().refreshItem(aui);
			JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
		}else if (col == iHexNo) {
			if(((Integer) v).intValue()  == aw.getHexNo()) return;
			ArtifactUserInfo aui = this.loadCorrsepondingAUI(aw, t);
			aui.setHexNo(((Integer) v).intValue());
			
			t.getArtifactsUser().refreshItem(aui);
			JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
		}else if (col == iPower1) {
			if(v.toString().equals(aw.getPower1())) return;
			ArtifactUserInfo aui = this.loadCorrsepondingAUI(aw, t);
			aui.setPower1(v.toString());
			
			t.getArtifactsUser().refreshItem(aui);
			JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
		}else if (col == iPower2) {
			if(v.toString().equals(aw.getPower2())) return;
			ArtifactUserInfo aui = this.loadCorrsepondingAUI(aw, t);
			aui.setPower2(v.toString());
			
			t.getArtifactsUser().refreshItem(aui);
			JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
		}
	}
	
	private ArtifactUserInfo loadCorrsepondingAUI(ArtifactWrapper aw, Turn t) {
		ArtifactUserInfo aui;
		if(aw.getNumber() == 0) {
			aui = t.getArtifactsUser().findFirstByProperty("name", aw.getName());
		}
		else {
			aui = t.getArtifactsUser().findFirstByProperty("number", aw.getNumber());
		}
		
		if(aui == null) {
			aui = new ArtifactUserInfo(aw, this.gameHolder.getGame().getTurn().getTurnNo());
		}
		
		return aui;
	}
	
}
