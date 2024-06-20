package lui.widget;

import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
class UndoManager extends AbstractUndoableEdit implements UndoableEditListener {
    private String lastEditName = null;
    private final List<MergeComponentEdit> edits = new ArrayList<>(32);
    private MergeComponentEdit current;
    private int pointer = -1;

    private final List<ChangeListener> changeListeners = new ArrayList<>(8);

    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    public void undoableEditHappened(UndoableEditEvent e) {
        UndoableEdit edit = e.getEdit();
        if (edit instanceof AbstractDocument.DefaultDocumentEvent event) {
            try {
                int start = event.getOffset();
                int len = event.getLength();
                if (!event.isSignificant() || event.isInProgress()) {
                    createCompoundEdit();
                    current.addEdit(edit);
                    lastEditName = edit.getPresentationName();
                } else {
                    String text = event.getDocument().getText(start, len);
                    boolean isNeedStart = false;
                    if (current == null) {
                        isNeedStart = true;
                    } else if (text.contains(" ")) {
                        isNeedStart = true;
                    } else if (lastEditName == null || !lastEditName.equals(edit.getPresentationName())) {
                        isNeedStart = true;
                    }
                    while (pointer < edits.size() - 1) {
                        edits.removeLast();
                        isNeedStart = true;
                    }
                    if (isNeedStart) {
                        createCompoundEdit();
                    }
                    current.addEdit(edit);
                    lastEditName = edit.getPresentationName();
                }
                fireStateChanged();
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void createCompoundEdit() {
        if (current == null) {
            current = new MergeComponentEdit();
        } else if (current.getLength() > 0) {
            current = new MergeComponentEdit();
        }

        edits.add(current);
        pointer++;
    }

    public void undo() throws CannotUndoException {
        if (!canUndo()) {
            throw new CannotUndoException();
        }

        MergeComponentEdit u = edits.get(pointer);
        u.undo();
        pointer--;

        fireStateChanged();
    }

    public void redo() throws CannotUndoException {
        if (!canRedo()) {
            throw new CannotUndoException();
        }

        pointer++;
        MergeComponentEdit u = edits.get(pointer);
        u.redo();

        fireStateChanged();
    }

    public boolean canUndo() {
        return pointer >= 0;
    }

    public boolean canRedo() {
        return !edits.isEmpty() && pointer < edits.size() - 1;
    }

    protected void fireStateChanged() {
        if (changeListeners.isEmpty()) {
            return;
        }
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(evt);
        }
    }

    protected static class MergeComponentEdit extends CompoundEdit {
        boolean isUnDone = false;

        public int getLength() {
            return edits.size();
        }

        public void undo() throws CannotUndoException {
            super.undo();
            isUnDone = true;
        }

        public void redo() throws CannotUndoException {
            super.redo();
            isUnDone = false;
        }

        public boolean canUndo() {
            return !edits.isEmpty() && !isUnDone;
        }

        public boolean canRedo() {
            return !edits.isEmpty() && isUnDone;
        }

    }
}