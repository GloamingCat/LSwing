package lwt.widget;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import lwt.container.LContainer;
import lbase.event.listener.LSelectionListener;
import lbase.gui.LMenu;

public class LButton extends LWidget {
	private static final long serialVersionUID = 1L;
	
	public LSelectionListener onClick;
	protected JButton button;
	
	public LButton(LContainer parent, String text) {
		super(parent);
		setFillLayout(true);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
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
		button.setToolTipText(text);
	}
	
	@Override
	public void onCopyButton(LMenu menu) {}
	
	@Override
	public void onPasteButton(LMenu menu) {}

	@Override
	public boolean canDecode(String str) {
		return false;
	}

}
