package lwt.widget;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import lwt.container.LContainer;

public class LCheckBox extends LControlWidget<Boolean> {

	private JCheckBox button;

	public LCheckBox(LContainer parent) {
		super(parent);
	}
	
	@Override
	protected void createContent(int flags) {
		button = new JCheckBox();
		add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (button.isSelected() == currentValue)
					return;
				newModifyAction(currentValue, button.isSelected());
				currentValue = button.isSelected();
			}
		});
	}

	public void setValue(Object obj) {
		if (obj != null) {
			Boolean i = (Boolean) obj;
			button.setEnabled(true);
			button.setSelected(i);
			currentValue = i;
		} else {
			button.setEnabled(false);
			button.setSelected(false);
			currentValue = null;
		}
	}
	
	public void setText(String text) {
		button.setText(text);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		button.setForeground(enabled ?
				new Color(0, 0, 0) : new Color(100, 100, 100));
	}
	
	@Override
	protected JComponent getControl() {
		return button;
	}

	@Override
	public String encodeData(Boolean value) {
		return value + "";
	}
	
	@Override
	public Boolean decodeData(String str) {
		return Boolean.parseBoolean(str);
	}

}
