package lui.widget;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import lui.base.LPrefs;
import lui.container.LContainer;
import lui.base.event.listener.LSelectionListener;
import lui.base.gui.LMenu;

public class LButton extends LWidget {
	
	public LSelectionListener onClick;
	protected JButton button;
	
	public LButton(LContainer parent, String text) {
		super(parent);
		setFillLayout(true);
		button.addActionListener(e -> execute());
		button.setText(text);
	}

	@Override
	protected void createContent(int flags) {
		button = new JButton();
		add(button);
	}
	
	protected void execute() {
		if (onClick != null) {
			onClick.onSelect(null);
		}
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

	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

}
