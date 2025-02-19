package lui.widget;

import java.awt.*;

import javax.swing.*;

import lui.container.LContainer;

public class LCheckBox extends LControlWidget<Boolean> {

	private JCheckBox button;

	public LCheckBox(LContainer parent) {
		super(parent);
	}
	
	@Override
	protected void createContent(int flags) {
		button = new JCheckBox();
		add(button);
		button.addActionListener(e -> {
			Boolean oldValue = currentValue;
            currentValue = button.isSelected();
            if (oldValue == null || oldValue.equals(currentValue))
                return;
            newModifyAction(oldValue, currentValue);
        });
		button.setSelected(false);
	}

	public void setValue(Object obj) {
		if (obj == currentValue)
			return;
		if (obj != null) {;
			Boolean i = (Boolean) obj;
			currentValue = i;
			button.setEnabled(true);
			button.setSelected(i);
		} else {
			currentValue = null;
			button.setEnabled(false);
			button.setSelected(false);
		}
	}
	
	public void setText(String text) {
		button.setText(text);
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		button.setForeground(enabled ?
				UIManager.getColor("text") : UIManager.getColor("textInactiveText"));
	}
	
	@Override
	protected JComponent getControl() {
		return button;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + button.getText();
	}

	@Override
	public String encodeData(Boolean value) {
		return value + "";
	}
	
	@Override
	public Boolean decodeData(String str) {
		return Boolean.parseBoolean(str);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = getMinimumSize();
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

}
