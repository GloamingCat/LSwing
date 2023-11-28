package lwt.widget;

import lwt.LImageHelper;
import lwt.container.LContainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class LText extends LControlWidget<String> {
	
	private Text text;

	public LText(LContainer parent) {
		this(parent, 1, false);
	}
	
	public LText(LContainer parent, int columns) {
		this(parent, columns, false);
	}
	
	public LText(LContainer parent, boolean readOnly) {
		this(parent, 1, readOnly);
	}
	
	public LText(LContainer parent, int columns, boolean readOnly) {
		super(parent);
		setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, columns, 1));
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);
		text = new Text(this, readOnly ? (SWT.BORDER | SWT.READ_ONLY) : SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		if (!LImageHelper.onWindows)
			gd_text.heightHint = 16;
		text.setLayoutData(gd_text);
		
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateCurrentText();
			}
		});
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) { // Enter
					updateCurrentText();
				}
			}
		});
		text.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				updateCurrentText();
			}
		});
		text.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				updateCurrentText();
			}
		});
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

}
