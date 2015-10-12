package org.joverseer.ui.views;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.springframework.binding.form.FormModel;
import org.springframework.core.io.Resource;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

public class ChangelogForm extends AbstractForm implements HyperlinkListener {
	public static String PAGE_NAME = "ChangelogForm";

	JEditorPane editor;

	public ChangelogForm(FormModel arg0) {
		super(arg0, PAGE_NAME);
	}

	@Override
	protected JComponent createFormControl() {
		GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
		this.editor = new JEditorPane();
		this.editor.setEditable(false);
		this.editor.setPreferredSize(new Dimension(800, 600));
		try {
			this.editor.setPage(((Resource) getFormObject()).getURL());
			String text = this.editor.getText();
			String[] lines = text.split("\n");
			String result = "";
			for (String l : lines) {
				if (l.trim().equals(""))
					continue;
				if (l.startsWith("VERSION"))
					l = "<hr/>" + "<b>" + l + "</br>";
				if (l.trim().startsWith("---") && l.trim().endsWith("---"))
					continue;
				int startSpaces = 0;
				int startTabs = 0;
				int j = 0;
				while (l.length() > j && (l.charAt(j) == '\t' || l.charAt(j) == ' ')) {
					if (l.charAt(j) == '\t')
						startTabs++;
					if (l.charAt(j) == ' ')
						startSpaces++;
					j++;
				}
				int index = 0;
				if (startTabs > 0 || startSpaces > 0)
					index++;
				index += startTabs + startSpaces / 4;
				String ll = "";
				// for (j=0; j<index; j++) {
				ll += "<td colspan=" + (index + 1) + ">";
				// }
				ll += "<td colspan=" + (10 - index) + ">" + l.trim() + "</td>";
				result += "<tr>" + ll + "</tr>";
			}
			result = "<html><body><table style='font-family:Tahoma; font-size:11pt' border=0 cellspacing=0 cellpadding=0>" + result + "</table></font></body></html>";
			this.editor.setContentType("text/html");
			this.editor.setText(result);
			this.editor.select(0, 1);
		} catch (Exception exc) {
			System.out.println(exc.getMessage());
		}
		JScrollPane scp = new JScrollPane(this.editor);
		scp.setPreferredSize(new Dimension(750, 500));
		glb.append(scp);
		glb.nextLine();
		return glb.getPanel();
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
		try {
			this.editor.setPage(arg0.getURL());
		} catch (Exception exc) {
			System.out.println(exc.getMessage());
		}
	}

}