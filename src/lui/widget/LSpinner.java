package lui.widget;

import lui.container.*;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;

public class LSpinner extends LControlWidget<Integer> {

	private JSpinner spinner;
	private Integer minimum = 0;
	private Integer maximum = null;
	private final int step = 1;

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new LPanel(new lwt.dialog.LShell(400, 200), 2, true)
	 */
	public LSpinner(LContainer parent) {
		super(parent);
		setFillLayout(true);
	}

	private static LSpinner test;

	@Override
	protected void createContent(int flags) {
		if (test == null)
			test = this;
		spinner = new JSpinner();
		add(spinner);
		SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 0, 1);
		model.setMaximum(null);
		spinner.setModel(model);
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
		spinner.setEditor(editor);
		JFormattedTextField field = editor.getTextField();
		UndoManager undoManager = new UndoManager();
		field.getDocument().addUndoableEditListener(undoManager);
		undoManager.addChangeListener(e -> {
			Integer oldValue = currentValue;
            currentValue = (Integer) spinner.getValue();
			if (oldValue == null || oldValue.equals(currentValue))
				return;
			newModifyAction(oldValue, currentValue);
        });
		DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
		formatter.setCommitsOnValidEdit(true);
		spinner.setEnabled(true);
		currentValue = 0;
		spinner.setValue(0);
	}

	@Override
	public void setValue(Object obj) {
		if (currentValue == obj)
			return;
		currentValue = null;
		if (obj != null) {
			Integer i = (Integer) obj;
			currentValue = i;
			spinner.setEnabled(true);
			spinner.setValue(i);
		} else {
			spinner.setEnabled(false);
			spinner.setValue(minimum);
		}
	}
	
	public void setMinimum(int i) {
		if (i == Integer.MIN_VALUE)
			minimum = null;
		else
			minimum = i;
		SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
		model.setMinimum(minimum);
	}

	public void setMaximum(int i) {
		if (i == Integer.MAX_VALUE)
			maximum = null;
		else
			maximum = i;
		SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
		model.setMaximum(maximum);
	}
	
	@Override
	protected JComponent getControl() {
		return spinner;
	}

	@Override
	public String encodeData(Integer value) {
		return value + "";
	}
	
	@Override
	public Integer decodeData(String str) {
		return Integer.parseInt(str);
	}

}
