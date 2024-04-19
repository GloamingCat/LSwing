package lui.widget;

import javax.swing.JComponent;

import lui.base.LPrefs;
import lui.container.LContainer;
import lui.base.event.LSelectionEvent;
import lui.base.event.listener.LSelectionListener;
import lui.base.gui.LMenu;

import java.awt.*;

public class LActionButton extends LControlWidget<Object> {
	private static final long serialVersionUID = 1L;

	private LButton button;
	
	public LActionButton(LContainer parent, String text) {
		super(parent);
		button.setText(text);
		button.onClick = new LSelectionListener() {
			@Override
			public void onSelect(LSelectionEvent arg0) {
				notifyEmpty();
			}
		};
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
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

}
