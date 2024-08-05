package lui.widget;

import java.awt.*;

import javax.swing.*;

import lui.base.LPrefs;
import lui.container.LContainer;
import lui.base.event.listener.LSelectionListener;

public class LButton extends LWidget {
	
	public LSelectionListener onClick = null;
	JButton button;
	private Icon icon;

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

	@Override
	public void setHoverText(String text) {
		button.setToolTipText("<html>" + text.replace("\n", "<br>") + "</html>");
	}

	@Override
	public void setEnabled(boolean v) {
		super.setEnabled(true);
		button.setEnabled(v);
	}

	protected void execute() {
		if (onClick != null)
			onClick.onSelect(null);
	}

	public void setText(String text) {
		button.setText(text);
	}

	public void setIcon(String key) {
		icon = UIManager.getIcon(key);
		button.setText(null);
		button.setIcon(icon);
	}

	//////////////////////////////////////////////////
	//region Properties

	@Override
	public Dimension getMinimumSize() {
		Dimension size = icon == null ? new Dimension(LPrefs.BUTTONWIDTH, LPrefs.WIDGETHEIGHT)
				: new Dimension(icon.getIconWidth(), icon.getIconHeight());
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = button.getPreferredSize();
		size.width = icon == null ? Math.max(size.width, LPrefs.BUTTONWIDTH) : icon.getIconWidth();
		size.height = icon == null ? LPrefs.WIDGETHEIGHT : icon.getIconHeight();
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

	//endregion

}
