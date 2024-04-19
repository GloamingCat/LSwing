package lui.widget;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

import lui.container.LContainer;

public class LTextBox extends LControlWidget<String> {
	
	private JTextArea text;

	//////////////////////////////////////////////////
	//region Constructors

	public LTextBox(LContainer parent) {
		this(parent, false);
	}

	public LTextBox(LContainer parent, boolean readOnly) {
		super(parent, readOnly ? 1 : 0);
		getCellData().setExpand(true, false);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateCurrentText();
			}
		});
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER ||
						e.getKeyCode() == KeyEvent.VK_TAB) {
					updateCurrentText();
				}
			}
		});
		text.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				updateCurrentText();
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				updateCurrentText();
			}
		});
	}


	@Override
	protected void createContent(int flags) {
		setFillLayout(true);
		text = new JTextArea();
		text.setEditable(flags == 0);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		JScrollPane pane = new JScrollPane(text);
		//pane.add(text);
		add(pane);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Value

	public void updateCurrentText() {
		if (!text.getText().equals(currentValue)) {
			newModifyAction(currentValue, text.getText());
			currentValue = text.getText();
		}
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
	
}
