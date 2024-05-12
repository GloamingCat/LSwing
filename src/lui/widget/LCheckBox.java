package lui.widget;

import java.awt.*;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import lui.container.LContainer;

public class LCheckBox extends LControlWidget<Boolean> {

	JCheckBox button;

	public LCheckBox(LContainer parent) {
		super(parent);
	}
	
	@Override
	protected void createContent(int flags) {
		button = new JCheckBox();
		add(button);
		button.addActionListener(e -> {
            if (button.isSelected() == currentValue)
                return;
            newModifyAction(currentValue, button.isSelected());
            currentValue = button.isSelected();
        });
	}

	public void setValue(Object obj) {
		if (obj != null) {
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

	@Override
	public Dimension getPreferredSize() {
		Dimension size = getMinimumSize();
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

}
