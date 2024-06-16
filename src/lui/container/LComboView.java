package lui.container;

import java.util.ArrayList;

import lui.widget.LCombo;
import lui.widget.LControlWidget;

public class LComboView extends LControlView<Integer> {

	protected LCombo combo;
	
	public LComboView(LContainer parent, int flags) {
		super(parent);
		combo = new LCombo(this, flags);
	}
	
	public void onVisible() {
		combo.setItems(getArray());
		super.onVisible();
	}
	
	public LControlWidget<Integer> getControl() {
		return combo;
	}
	
	protected ArrayList<?> getArray() { return new ArrayList<>(); }
	
}
