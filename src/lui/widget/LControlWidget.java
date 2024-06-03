package lui.widget;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import lui.LGlobals;
import lui.container.LContainer;
import lui.base.action.LControlAction;
import lui.base.event.LControlEvent;
import lui.base.event.listener.LControlListener;
import lui.base.gui.LControl;
import lui.base.gui.LMenu;
import lui.editor.LPopupMenu;

public abstract class LControlWidget<T> extends LWidget implements LControl<T> {
	
	protected ArrayList<LControlListener<T>> modifyListeners = new ArrayList<>();
	protected T currentValue;
	
	public LControlWidget(LContainer parent) {
		super(parent);
	}
	
	public LControlWidget(LContainer parent, int flags) {
		super(parent, flags);
	}
	
	public void forceModification(T newValue) {
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
		getControl().setToolTipText("<html>" + text.replace("\n", "<br>") + "</html>");
	}
	
	@Override
	public void setComponentPopupMenu(JPopupMenu menu) {
		super.setComponentPopupMenu(menu);
		getControl().setComponentPopupMenu(menu);
	}

	//////////////////////////////////////////////////
	//region Modify Events

	protected void newModifyAction(T oldValue, T newValue) {
		LControlEvent<T> event = new LControlEvent<>(oldValue, newValue);
		newAction(new LControlAction<>(this, event));
		notifyListeners(event);
	}
	
	public LControlEvent<T> createEvent() {
		LControlEvent<T> e = new LControlEvent<>(null, currentValue);
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
		if (e != null)
			notifyListeners(e);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Copy / Paste
	
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
				forceModification(newValue);
		} catch (ClassCastException | UnsupportedFlavorException | IOException e) {
			System.err.println(e.getMessage());
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

	//endregion

	//////////////////////////////////////////////////
	//region Menus

	private LPopupMenu addMenu(JComponent parent) {
		LPopupMenu menu = new LPopupMenu(parent);
		setCopyEnabled(menu, true);
		setPasteEnabled(menu, true);
		addFocusOnClick(parent);
		return menu;
	}

	private void addFocusOnClick(JComponent c) {
		LControlWidget<T> widget = this;
		c.setEnabled(true);
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) { // Left button
					menuInterface.setFocusWidget(widget);
				}
			}
		});
	}

	public void addMenu() {
		addMenu(this);
	}

	public void addMenu(LContainer frame) {
		JComponent c = frame.getContentComposite();
		JPopupMenu menu = getComponentPopupMenu();
		if (menu == null) {
			menu = addMenu(c);
			setComponentPopupMenu(menu);
			addFocusOnClick(this);
		} else if (c.getComponentPopupMenu() == null) {
			c.setComponentPopupMenu(menu);
			addFocusOnClick(c);
		}
	}

	public void addMenu(LWidget widget) {
		JPopupMenu menu = getComponentPopupMenu();
		if (menu == null) {
			menu = addMenu((JComponent) widget);
			setComponentPopupMenu(menu);
			addFocusOnClick(this);
		} else if (widget.getComponentPopupMenu() == null) {
			widget.setComponentPopupMenu(menu);
			addFocusOnClick(widget);
		}
	}

	//endregion
}
