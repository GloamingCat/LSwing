package lui.widget;

import lui.container.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
public class LText extends LControlWidget<String> {
	
	JTextField text;

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new LPanel(new lwt.dialog.LShell(400, 200), 2, true)
	 */
	public LText(LContainer parent) {
		this(parent, 1);
	}

	public LText(LContainer parent, int columns) {
		this(parent, columns, false);
	}
	
	public LText(LContainer parent, boolean readOnly) {
		this(parent, 1, readOnly);
	}
	
	public LText(LContainer parent, int columns, boolean readOnly) {
		super(parent, readOnly ? 1 : 0);
		getCellData().setSpread(columns, 1);
		getCellData().setExpand(true, false);
		text.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateCurrentText();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateCurrentText();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateCurrentText();
			}
		});
		if (readOnly) {
			text.setForeground(UIManager.getColor("Label.disabledForeground"));
		}
	}
	
	@Override
	protected void createContent(int flags) {
		setFillLayout(true);
		text = new JTextField("");
		text.setEditable(flags == 0);
		add(text);
	}
	
	private void updateCurrentText() {
		java.awt.EventQueue.invokeLater(() -> {
			String newText = text.getText();
			if (currentValue != null && !newText.equals(currentValue)) {
				newModifyAction(currentValue, newText);
				currentValue = newText;
			}
		});
	}
	
	@Override
	public void setValue(Object value) {
		currentValue = (String) value;
		if (text.getText().equals(value))
			return;
		if (value != null) {
			text.setEnabled(true);
			text.setText(currentValue);
		} else {
			text.setEnabled(false);
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
