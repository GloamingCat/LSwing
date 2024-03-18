package lwt.widget;

import lwt.container.LContainer;
import lwt.dialog.LShellFactory;
import lbase.LVocab;
import lbase.event.LSelectionEvent;
import lbase.event.listener.LSelectionListener;

import java.lang.reflect.Type;

import javax.swing.JComponent;

import gson.GGlobals;

public abstract class LObjectButton<T> extends LControlWidget<T> {
	private static final long serialVersionUID = 1L;
	
	protected LShellFactory<T> shellFactory;
	private LButton button;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LObjectButton(LContainer parent) {
		super(parent);
		button.onClick = new LSelectionListener() {
			@Override
			public void onSelect(LSelectionEvent arg0) {
				T newValue = shellFactory.openShell(getWindow(), currentValue);
				if (newValue != null) {
					newModifyAction(currentValue, newValue);
					setValue(newValue);
				}
			}
		};
	}

	@Override
	protected void createContent(int flags) {
		button = new LButton(this, LVocab.instance.SELECT);
	}

	public void setShellFactory(LShellFactory<T> factory) {
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
		@SuppressWarnings("unchecked")
		T fromJson = (T) GGlobals.gson.fromJson(str, getType());
		return fromJson;
	}
	
	@Override
	public boolean canDecode(String str) {
		try {
			@SuppressWarnings("unchecked")
			T newValue = (T) GGlobals.gson.fromJson(str, getType());	
			return newValue != null;
		} catch (ClassCastException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	protected abstract Type getType();

}
