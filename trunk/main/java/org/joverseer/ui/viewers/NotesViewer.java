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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.joverseer.domain.Note;
import org.joverseer.ui.command.AddEditNoteCommand;
import org.joverseer.ui.command.DeleteNoteCommand;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class NotesViewer extends ObjectViewer implements ActionListener {

    public static final String FORM_PAGE = "NotesViewer";

    ArrayList<JScrollPane> scps = new ArrayList<JScrollPane>();
    ArrayList<JTextArea> texts = new ArrayList<JTextArea>();
    ArrayList<JLabel> icons = new ArrayList<JLabel>();

    public NotesViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    public boolean appliesTo(Object obj) {
        return Note.class.isInstance(obj);
    }

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
            texts.get(i).setText(notes.get(i).getText());
            texts.get(i).setCaretPosition(0);
            int h = texts.get(i).getPreferredSize().height;
            scps.get(i).setPreferredSize(new Dimension(210, Math.min(h, 36)));
        } else {
            setVisibleByIndex(i, false);
        }
    }

    protected void setVisibleByIndex(int idx, boolean v) {
        icons.get(idx).setVisible(v);
        scps.get(idx).setVisible(v);
    }

    protected JComponent createFormControl() {
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Icon ico = new ImageIcon(imgSource.getImage("note.icon"));

        for (int i = 0; i < 20; i++) {
            final JLabelButton l = new JLabelButton(ico);
            l.setPreferredSize(new Dimension(12, 12));
            l.setVerticalAlignment(JLabel.TOP);
            l.addActionListener(new PopupMenuActionListener() {

                public JPopupMenu getPopupMenu() {
                    int idx = icons.indexOf(l);
                    if (idx >= 0) {
                        return createPopupContextMenu(idx);
                    }
                    return null;
                }
                
            });
            icons.add(l);

            tlb.cell(icons.get(i), "valign=top colspec=left:12px");

            final JTextArea ta = new JTextArea();
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            // ta.setPreferredSize(new Dimension(240, 36));
            Font f = GraphicUtils.getFont(ta.getFont().getName(), Font.ITALIC, ta.getFont().getSize());
            ta.setFont(f);
            Color c = ColorPicker.getInstance().getColor("notes.text");
            ta.setForeground(c);
            texts.add(ta);
            ta.getDocument().addDocumentListener(new DocumentListener() {

                public void changedUpdate(DocumentEvent arg0) {
                    updateNotes(ta);
                }

                public void insertUpdate(DocumentEvent arg0) {
                    updateNotes(ta);
                }

                public void removeUpdate(DocumentEvent arg0) {
                    updateNotes(ta);
                }
            });
            ta.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) {
                }

                public void focusLost(FocusEvent e) {
                    int idx = texts.indexOf(ta);
                    if (idx >= 0) {
                        showNote(idx);
                    }
                }
            });

            JScrollPane notesPane = new JScrollPane(ta);
            // notesPane.setPreferredSize(new Dimension(240, 36));
            notesPane.setMaximumSize(new Dimension(210, 36));
            notesPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            notesPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            notesPane.getVerticalScrollBar().setPreferredSize(new Dimension(16, 10));
            notesPane.setBorder(null);
            scps.add(notesPane);
            tlb.cell(notesPane, "colspec=left:240px");
            tlb.row();
        }
        JPanel p = tlb.getPanel();
        p.setBackground(Color.white);
        return p;
    }

    public void actionPerformed(ActionEvent e) {
    }
    
    protected void updateNotes(JTextArea ta) {
        int idx = texts.indexOf(ta);
        if (idx >= 0) {
            Note n = ((ArrayList<Note>)getFormObject()).get(idx);
            n.setText(ta.getText());
            
        }
        
    }
    
    protected JPopupMenu createPopupContextMenu(int idx) {
        Note n = ((ArrayList<Note>) getFormObject()).get(idx);
        AddEditNoteCommand editNote = new AddEditNoteCommand(n);
        editNote.setLabel("Edit Note");
        DeleteNoteCommand deleteNote = new DeleteNoteCommand(n);
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "noteCommandGroup",
                new Object[] {
                            editNote, deleteNote
                            }
                        );
                
        return group.createPopupMenu();
    }
}
