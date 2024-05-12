package lui.widget;

import java.util.ArrayList;

import lui.container.LContainer;
import lui.base.data.LPath;
import lui.base.event.LSelectionEvent;
import lui.base.event.listener.LSelectionListener;

public abstract class LSelectableCollection<T, ST> extends LCollection<T, ST> {
	
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
	
	public LSelectionEvent select(LPath parent, int index) {
		if (parent == null) {
			parent = new LPath(index);
		} else {
			parent = parent.addLast(index);
		}
		return select(parent);
	}
	
	public abstract LSelectionEvent select(LPath path);
	
	public abstract T getSelectedObject();
	
	public abstract LPath getSelectedPath();
	
}
