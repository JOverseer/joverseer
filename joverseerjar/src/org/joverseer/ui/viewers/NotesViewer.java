package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.joverseer.JOApplication;
import org.joverseer.domain.Note;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.command.AddEditNoteCommand;
import org.joverseer.ui.command.DeleteNoteCommand;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Shows notes
 * Used in other viewers whose objects can be targets of notes
 *
 * @author Marios Skounakis
 */
public class NotesViewer extends ObjectViewer implements ActionListener {

    public static final String FORM_PAGE = "NotesViewer"; //$NON-NLS-1$

    ArrayList<JScrollPane> scps = new ArrayList<JScrollPane>();
    ArrayList<JTextArea> texts = new ArrayList<JTextArea>();
    ArrayList<JLabel> icons = new ArrayList<JLabel>();

    public NotesViewer(FormModel formModel,GameHolder gameHolder) {
        super(formModel, FORM_PAGE,gameHolder);
    }

    @Override
	public boolean appliesTo(Object obj) {
        return Note.class.isInstance(obj);
    }

    @Override
	public void setFormObject(Object object) {
        super.setFormObject(object);
        for (int i = 0; i < 20; i++) {
            showNote(i);
        }
    }

    protected void showNote(int i) {
        ArrayList<Note> notes = (ArrayList<Note>) getFormObject();
        if (i < notes.size()) {
            setVisibleByIndex(i, true);
            this.texts.get(i).setText(notes.get(i).getText());
            this.texts.get(i).setCaretPosition(0);
            int h = this.texts.get(i).getPreferredSize().height;
            this.scps.get(i).setPreferredSize(new Dimension(260, Math.min(h, 36)));
        } else {
            setVisibleByIndex(i, false);
        }
    }

    protected void setVisibleByIndex(int idx, boolean v) {
        this.icons.get(idx).setVisible(v);
        this.scps.get(idx).setVisible(v);
    }

    @Override
	protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        ImageSource imgSource = JOApplication.getImageSource();
        Icon ico = new ImageIcon(imgSource.getImage("note.icon")); //$NON-NLS-1$

        for (int i = 0; i < 20; i++) {
            final JLabelButton l = new JLabelButton(ico);
            l.setPreferredSize(this.uiSizes.newIconDimension(this.uiSizes.getHeight5()));
            l.setVerticalAlignment(SwingConstants.TOP);
            l.addActionListener(new PopupMenuActionListener() {

                @Override
				public JPopupMenu getPopupMenu() {
                    int idx = NotesViewer.this.icons.indexOf(l);
                    if (idx >= 0) {
                        return createPopupContextMenu(idx);
                    }
                    return null;
                }

            });
            this.icons.add(l);

            tlb.cell(this.icons.get(i), "valign=top colspec=left:50px"); //$NON-NLS-1$

            final JTextArea ta = new JTextArea();
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            // ta.setPreferredSize(new Dimension(240, 36));
            Font f = GraphicUtils.getFont(ta.getFont().getName(), Font.ITALIC, ta.getFont().getSize());
            ta.setFont(f);
            Color c = ColorPicker.getInstance().getColor("notes.text");
            ta.setForeground(c);
            this.texts.add(ta);
            ta.getDocument().addDocumentListener(new DocumentListener() {

                @Override
				public void changedUpdate(DocumentEvent arg0) {
                    updateNotes(ta);
                }

                @Override
				public void insertUpdate(DocumentEvent arg0) {
                    updateNotes(ta);
                }

                @Override
				public void removeUpdate(DocumentEvent arg0) {
                    updateNotes(ta);
                }
            });
            ta.addFocusListener(new FocusListener() {
                @Override
				public void focusGained(FocusEvent e) {
                }

                @Override
				public void focusLost(FocusEvent e) {
                    int idx = NotesViewer.this.texts.indexOf(ta);
                    if (idx >= 0) {
                        showNote(idx);
                    }
                }
            });

            JScrollPane notesPane = new JScrollPane(ta);
            // notesPane.setPreferredSize(new Dimension(240, 36));
            notesPane.setMaximumSize(new Dimension(270, 36));
            notesPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            notesPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            notesPane.getVerticalScrollBar().setPreferredSize(new Dimension(16, 10));
            notesPane.setBorder(null);
            this.scps.add(notesPane);
            tlb.cell(notesPane, "colspec=left:280px"); //$NON-NLS-1$
            tlb.row();
        }
        JPanel p = tlb.getPanel();
        p.setBackground(UIManager.getColor("TextArea.background"));
        return p;
    }

    @Override
	public void actionPerformed(ActionEvent e) {
    }

    protected void updateNotes(JTextArea ta) {
        int idx = this.texts.indexOf(ta);
        if (idx >= 0) {
            Note n = ((ArrayList<Note>)getFormObject()).get(idx);
            n.setText(ta.getText());

        }

    }

    protected JPopupMenu createPopupContextMenu(int idx) {
        Note n = ((ArrayList<Note>) getFormObject()).get(idx);
        AddEditNoteCommand editNote = new AddEditNoteCommand(n,this.gameHolder);
        editNote.setLabel(Messages.getString("NotesViewer.EditNote")); //$NON-NLS-1$
        DeleteNoteCommand deleteNote = new DeleteNoteCommand(n);
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "noteCommandGroup", //$NON-NLS-1$
                new Object[] {
                            editNote, deleteNote
                            }
                        );

        return group.createPopupMenu();
    }
}
