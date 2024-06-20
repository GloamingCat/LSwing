package lui.widget;

import java.awt.*;
import java.lang.reflect.Type;
import javax.swing.JComponent;

import lui.base.LVocab;
import lui.container.LContainer;
import lui.dialog.LWindowFactory;
import gson.GGlobals;

public abstract class LObjectButton<T> extends LControlWidget<T> {
	
	protected LWindowFactory<T> shellFactory;
	protected LButton button;

	public LObjectButton(LContainer parent) {
		super(parent);
	}

	@Override
	protected void createContent(int flags) {
		setFillLayout(true);
		button = new LButton(this, LVocab.instance.SELECT);
		button.onClick = arg0 -> LObjectButton.this.onClick();
	}

	protected void onClick() {
		T newValue = shellFactory.openWindow(getWindow(), currentValue);
		if (newValue != null) {
			newModifyAction(currentValue, newValue);
			setValue(newValue);
		}
	}

	public LWindowFactory<T> getShellFactory() {
		return shellFactory;
	}

	public void setShellFactory(LWindowFactory<T> factory) {
		shellFactory = factory;
	}
	
	public void setText(String text) {
		button.setText(text);
	}
	
	@Override
	public void setValue(Object obj) {
		if (obj != null) {
			button.setEnabled(true);
			@SuppressWarnings("unchecked")
			T value = (T) obj;
			currentValue = value;
		} else {
			button.setEnabled(false);
			currentValue = null;
		}
	}
	
	@Override
	protected JComponent getControl() {
		return button;
	}

	@Override
	public String encodeData(T value) {
		return GGlobals.gson.toJson(value, value.getClass());
	}
	
	@Override
	public T decodeData(String str) {
		return GGlobals.gson.fromJson(str, getType());
	}
	
	@Override
	public boolean canDecode(String str) {
		try {
			T newValue = GGlobals.gson.fromJson(str, getType());
			return newValue != null;
		} catch (ClassCastException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	protected abstract Type getType();

	@Override
	public Dimension getMinimumSize() {
		Dimension size = button.getMinimumSize();
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = button.getPreferredSize();
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

}
