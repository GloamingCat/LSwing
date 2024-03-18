package lwt.widget;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import lwt.container.LContainer;

public class LTextBox extends LControlWidget<String> {
	private static final long serialVersionUID = 1L;
	
	private JTextArea text;
	
	public LTextBox(LContainer parent) {
		this(parent, false);
	}
	
	public LTextBox(LContainer parent, int cols, int rows) {
		this(parent, false, cols, rows);
	}
	
	public LTextBox(LContainer parent, boolean readOnly, int cols, int rows) {
		this(parent, readOnly);
		setSpread(cols, rows);
	}

	public LTextBox(LContainer parent, boolean readOnly) {
		super(parent, readOnly ? 1 : 0);
		setExpand(true, false);
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
		add(text);
	}
	
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
			text.setEnabled(true);
			text.setText(s);
			currentValue = s;
		} else {
			text.setEnabled(false);
			text.setText("");
			currentValue = null;
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
	
}
