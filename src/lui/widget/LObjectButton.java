package lui.widget;

import java.awt.*;
import java.lang.reflect.Type;
import javax.swing.JComponent;

import lui.base.LPrefs;
import lui.base.LVocab;
import lui.container.LContainer;
import lui.dialog.LWindowFactory;
import gson.GGlobals;

public abstract class LObjectButton<T> extends LControlWidget<T> {
	
	protected LWindowFactory<T> shellFactory;
	private LButton button;

	public LObjectButton(LContainer parent) {
		super(parent);
	}

	@Override
	protected void createContent(int flags) {
		button = new LButton(this, LVocab.instance.SELECT);
		button.onClick = arg0 -> {
            T newValue = shellFactory.openShell(getWindow(), currentValue);
            if (newValue != null) {
                newModifyAction(currentValue, newValue);
                setValue(newValue);
            }
        };
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
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

}
