package lui.container;

import java.util.ArrayList;

import lui.widget.LCombo;
import lui.widget.LControlWidget;

public class LComboView extends LControlView<Integer> {

	private static final long serialVersionUID = 1L;
	protected LCombo combo;
	
	public LComboView(LContainer parent) {
		super(parent);
		combo = new LCombo(this);
	}
	
	public void setIncludeID(boolean value) {
		combo.setIncludeID(value);
	}
	
	public void setOptional(boolean value) {
		combo.setOptional(value);
	}
	
	public void onVisible() {
		combo.setItems(getArray());
		super.onVisible();
	}
	
	public LControlWidget<Integer> getControl() {
		return combo;
	}
	
	protected ArrayList<?> getArray() { return new ArrayList<Object>(); }
	
}
