package lui.widget;

import javax.swing.JComponent;

import lui.container.LContainer;
import lui.base.gui.LMenu;

import java.awt.*;

public class LActionButton extends LControlWidget<Object> {

	protected LButton button;

	@SuppressWarnings({"DataFlowIssue"})
	public LActionButton(LContainer parent, String text) {
		super(parent);
		button.setText(text);
		button.onClick = arg0 -> notifyEmpty();
	}

	@Override
	protected void createContent(int flags) {
		button = new LButton(this, "");
	}

	@Override
	public void onCopyButton(LMenu menu) {}
	
	@Override
	public void onPasteButton(LMenu menu) {}
	
	@Override
	protected JComponent getControl() {
		return button;
	}

	@Override
	public String encodeData(Object value) {
		return null;
	}
	
	@Override
	public Object decodeData(String str) {
		return null;
	}

	@Override
	public Dimension getMinimumSize() {
		return button.getMinimumSize();
	}

	@Override
	public Dimension getPreferredSize() {
		return button.getPreferredSize();
	}

}
