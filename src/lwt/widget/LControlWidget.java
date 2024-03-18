package lwt.widget;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import lwt.LGlobals;
import lwt.container.LContainer;
import lbase.action.LControlAction;
import lbase.event.LControlEvent;
import lbase.event.listener.LControlListener;
import lbase.gui.LControl;
import lbase.gui.LMenu;

public abstract class LControlWidget<T> extends LWidget implements LControl<T> {
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<LControlListener<T>> modifyListeners = new ArrayList<>();
	protected T currentValue;
	
	public LControlWidget(LContainer parent) {
		super(parent);
	}
	
	public LControlWidget(LContainer parent, int flags) {
		super(parent, flags);
	}
	
	public void modify(T newValue) {
		T oldValue = currentValue;
		setValue(newValue);
		newModifyAction(oldValue, newValue);
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		currentValue = (T) value;
	}
	
	public T getValue() {
		return currentValue;
	}
	
	public boolean isEnabled() {
		return currentValue != null;
	}
	
	public void setEnabled(boolean value) {}
	
	protected JComponent getControl() {
		return this;
	}
	
	@Override
	public void setHoverText(String text) {
		getControl().setToolTipText(text);
	}
	
	@Override
	public void setComponentPopupMenu(JPopupMenu menu) {
		super.setComponentPopupMenu(menu);
		getControl().setComponentPopupMenu(menu);
	}

	
	//-------------------------------------------------------------------------------------
	// Modify Events
	//-------------------------------------------------------------------------------------

	protected void newModifyAction(T oldValue, T newValue) {
		LControlEvent<T> event = new LControlEvent<T>(oldValue, newValue);
		newAction(new LControlAction<T>(this, event));
		notifyListeners(event);
	}
	
	public LControlEvent<T> createEvent() {
		LControlEvent<T> e = new LControlEvent<T>(null, currentValue);
		e.detail = -1;
		return e;
	}
	
	public void addModifyListener(LControlListener<T> listener) {
		modifyListeners.add(listener);
	}
	
	public void removeModifyListener(LControlListener<T> listener) {
		modifyListeners.remove(listener);
	}
	
	public void notifyListeners(LControlEvent<T> event) {
		for(LControlListener<T> listener : modifyListeners) {
			listener.onModify(event);
		}
	}
	
	public void notifyEmpty() {
		LControlEvent<T> e = createEvent();
		notifyListeners(e);
	}
	
	//-------------------------------------------------------------------------------------
	// Copy / Paste
	//-------------------------------------------------------------------------------------
	
	@Override
	public void onCopyButton(LMenu menu) {
		String str = encodeData(currentValue);
		LGlobals.clipboard.setContents(new StringSelection(str), null);
	}
	
	@Override
	public void onPasteButton(LMenu menu) {
		DataFlavor dataFlavor = DataFlavor.stringFlavor;
		if (!LGlobals.clipboard.isDataFlavorAvailable(dataFlavor))
			return;
		try {
			String str = (String) LGlobals.clipboard.getData(dataFlavor);
			if (str == null)
				return;
			T newValue = decodeData(str);
			if (newValue != null && !newValue.equals(currentValue))
				modify(newValue);	
		} catch (ClassCastException | UnsupportedFlavorException | IOException e) {
			System.err.println(e.getMessage());
			return;
		}
	}
	
	protected abstract String encodeData(T value);
	protected abstract T decodeData(String str);
	
	public boolean canDecode(String str) {
		try {
			decodeData(str);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

}
