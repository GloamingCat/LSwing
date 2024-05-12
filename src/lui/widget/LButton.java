package lui.widget;

import java.awt.*;

import javax.swing.JButton;

import lui.base.LPrefs;
import lui.container.LContainer;
import lui.base.event.listener.LSelectionListener;
import lui.base.gui.LMenu;

public class LButton extends LWidget {
	
	public LSelectionListener onClick = null;
	JButton button;

	public LButton(LContainer parent, String text) {
		super(parent);
		button.setText(text);
	}

	@Override
	protected void createContent(int flags) {
		button = new JButton();
		add(button);
		button.addActionListener(e -> execute());
	}
	
	protected void execute() {
		if (onClick != null)
			onClick.onSelect(null);
	}
	
	public void setText(String text) {
		button.setText(text);
	}
	
	public void setHoverText(String text) {
		button.setToolTipText("<html>" + text.replace("\n", "<br>") + "</html>");
	}
	
	@Override
	public void onCopyButton(LMenu menu) {}
	
	@Override
	public void onPasteButton(LMenu menu) {}

	@Override
	public boolean canDecode(String str) {
		return false;
	}

	//////////////////////////////////////////////////
	//region Properties

	@Override
	public Dimension getMinimumSize() {
		Dimension size = new Dimension(LPrefs.BUTTONWIDTH, LPrefs.WIDGETHEIGHT);
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = button.getPreferredSize();
		size.width = Math.max(size.width, LPrefs.BUTTONWIDTH);
		size.height = LPrefs.WIDGETHEIGHT;
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

	//endregion

}
