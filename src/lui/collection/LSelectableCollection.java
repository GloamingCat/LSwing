package lui.collection;

import java.util.ArrayList;

import lui.base.gui.LCollection;
import lui.container.LContainer;
import lui.base.data.LPath;
import lui.base.event.LSelectionEvent;
import lui.base.event.listener.LSelectionListener;
import lui.widget.LWidget;

public abstract class LSelectableCollection<T, ST> extends LWidget implements LCollection<T, ST> {

	public LSelectableCollection(LContainer parent) {
		super(parent);
	}

	public LSelectableCollection(LContainer parent, int flags) {
		super(parent, flags);
	}
	
	protected ArrayList<LSelectionListener> selectionListeners = new ArrayList<>();
	public void addSelectionListener(LSelectionListener listener) {
		selectionListeners.add(listener);
	}
	
	public void notifySelectionListeners(LSelectionEvent event) {
		for(LSelectionListener listener : selectionListeners) {
			listener.onSelect(event);
		}
	}

	protected ArrayList<LSelectionListener> checkListeners = new ArrayList<>();
	public void addCheckListener(LSelectionListener listener) {
		checkListeners.add(listener);
	}

	public void notifyCheckListeners(LSelectionEvent event) {
		for(LSelectionListener listener : checkListeners) {
			listener.onSelect(event);
		}
	}
	
	//-------------------------------------------------------------------------------------
	// Selection
	//-------------------------------------------------------------------------------------
	
	public abstract LPath getSelectedPath();
	public abstract LSelectionEvent select(LPath path);

	public LSelectionEvent select(LPath parent, int index) {
		if (parent == null) {
			parent = new LPath(index);
		} else {
			parent = parent.addLast(index);
		}
		return select(parent);
	}

	public T getSelectedObject() {
		LPath path = getSelectedPath();
		if (path == null)
			return null;
		return toObject(path);
	}

	public void forceSelection(LPath path) {
		LSelectionEvent e = select(path);
		notifySelectionListeners(e);
	}

	public void forceSelection(LPath parent, int index) {
		if (parent == null) {
			parent = new LPath(index);
		} else {
			parent = parent.addLast(index);
		}
		forceSelection(parent);
	}

}
