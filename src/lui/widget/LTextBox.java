package lui.widget;

import java.awt.*;

import javax.swing.*;

import lui.base.LPrefs;
import lui.container.LContainer;

public class LTextBox extends LControlWidget<String> {
	
	JTextArea text;

	//////////////////////////////////////////////////
	//region Constructors

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new LPanel(new lwt.dialog.LShell(400, 200))
	 */
	public LTextBox(LContainer parent) {
		this(parent, false);
	}

	public LTextBox(LContainer parent, boolean readOnly) {
		super(parent, readOnly ? 1 : 0);
	}


	@Override
	protected void createContent(int flags) {
		setFillLayout(true);
		text = new JTextArea();
		text.setEditable(flags == 0);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		JScrollPane pane = new JScrollPane(text);
		add(pane);
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
		}
	}

	//endregion

	//////////////////////////////////////////////////
	//region Value

	public void updateCurrentText() {
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
		if (value != null) {
			String s = (String) value;
			currentValue = s;
			text.setEnabled(true);
			text.setText(s);
		} else {
			currentValue = null;
			text.setEnabled(false);
			text.setText("");
		}
	}

	//endregion

	//////////////////////////////////////////////////
	//region LControlWidget

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

	//endregion

	//////////////////////////////////////////////////
	//region Properties

	@Override
	public Dimension getMinimumSize() {
		Dimension size = new Dimension(LPrefs.LISTWIDTH, LPrefs.LISTHEIGHT);
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width = Math.max(size.width, LPrefs.LISTWIDTH);
		size.height = Math.max(size.height, LPrefs.LISTHEIGHT);
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

	//endregion

}
