package lwt.widget;

import lwt.container.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JTextField;
public class LText extends LControlWidget<String> {
	private static final long serialVersionUID = 1L;
	
	protected JTextField text;

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
		setSpread(columns, 1);
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
		text = new JTextField();
		text.setEditable(flags == 0);
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
