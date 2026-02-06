package org.joverseer.ui.support.dialogs;

import java.awt.image.BufferedImage;

import org.springframework.richclient.dialog.DialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;

public class CustomTitledPageApplicationDialog extends TitledPageApplicationDialog {

	public CustomTitledPageApplicationDialog(DialogPage page) {
		super(page);
		this.setImage(new BufferedImage(1,1,BufferedImage.TYPE_BYTE_BINARY));
	}
	
	@Override
	protected boolean onFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
