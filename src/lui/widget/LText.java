package lui.widget;

import lui.container.*;

import javax.swing.*;
import java.awt.*;

public class LText extends LControlWidget<String> {
	
	JTextField text;

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new LPanel(new lwt.dialog.LShell(400, 200))
	 */
	public LText(LContainer parent) {
		this(parent, false);
	}
	
	public LText(LContainer parent, boolean readOnly) {
		super(parent, readOnly ? 1 : 0);
	}
	
	@Override
	protected void createContent(int flags) {
		setFillLayout(true);
		text = new JTextField("");
		text.setEditable(flags == 0);
		add(text);
		UndoManager undoManager = new UndoManager();
		text.getDocument().addUndoableEditListener(undoManager);
		undoManager.addChangeListener(e -> updateCurrentText());
		if (flags == 1) {
			Color fg = UIManager.getColor("Label.disabledForeground");
			if (fg == null)
				fg = UIManager.getColor("MenuItem.disabledForeground");
			text.setForeground(fg);
			Color bg = UIManager.getColor("Label.disabledBackground");
			if (bg == null)
				bg = UIManager.getColor("MenuItem.disabledBackground");
			text.setBackground(bg);
		} else {
			text.setEnabled(true);
		}
	}
	
	private void updateCurrentText() {
		java.awt.EventQueue.invokeLater(() -> {
			String oldValue = currentValue;
            currentValue = text.getText();
			if (oldValue == null || oldValue.equals(currentValue))
				return;
			newModifyAction(oldValue, currentValue);
		});
	}
	
	@Override
	public void setValue(Object value) {
		currentValue = (String) value;
		setEnabled(value != null);
		if (text.getText().equals(value))
			return;
		if (value != null) {
			text.setText(currentValue);
		} else {
			text.setText("");
		}
	}
	
	@Override
	protected JComponent getControl() {
		return text;
	}

	@Override
	public String encodeData(String value) {
		return value;
	}
	
	@Override
	public String decodeData(String str) {
		return str;
	}
	
	@Override
	public boolean canDecode(String str) {
		return true;
	}

	@Override
	public String toString() {
		return "LText: " + text.getText();
	}

}
