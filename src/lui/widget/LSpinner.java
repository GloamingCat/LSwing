package lui.widget;

import lui.container.*;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;

public class LSpinner extends LControlWidget<Integer> {

	JSpinner spinner;
	private Integer minimum = 0;
	private Integer maximum = null;
	private final int step = 1;

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new LPanel(new lwt.dialog.LShell(400, 200), 2, true)
	 */
	public LSpinner(LContainer parent) {
		this(parent, 1);
		currentValue = 0;
	}
	
	public LSpinner(LContainer parent, int columns) {
		super(parent);
		setFillLayout(true);
		getCellData().setExpand(true, false);
		getCellData().setSpread(columns, 1);
		spinner.addChangeListener(e -> {
            if (currentValue == null || spinner.getValue() == currentValue)
                return;
			newModifyAction(currentValue, (Integer) spinner.getValue());
            currentValue = (Integer) spinner.getValue();
        });
		JFormattedTextField field = ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField();
		DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
		formatter.setCommitsOnValidEdit(true);
	}
	
	@Override
	protected void createContent(int flags) {
		spinner = new JSpinner();
		add(spinner);
		SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 0, 1);
		model.setMaximum(null);
		spinner.setModel(model);
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
		spinner.setEditor(editor);
		spinner.setEnabled(true);
	}

	@Override
	public void setValue(Object obj) {
		if (obj != null) {
			Integer i = (Integer) obj;
			currentValue = i;
			spinner.setEnabled(true);
			spinner.setValue(i);
		} else {
			currentValue = null;
			spinner.setEnabled(false);
			spinner.setValue(0);
		}
	}
	
	public void setMinimum(int i) {
		if (i == Integer.MIN_VALUE)
			minimum = null;
		else
			minimum = i;
		SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
		model.setMinimum(minimum);
		int value = (int) spinner.getValue();
		if (maximum != null && value > maximum)
			value = maximum;
		if (minimum != null && value < minimum)
			value = minimum;
		model.setValue(value);
	}

	public void setMaximum(int i) {
		if (i == Integer.MAX_VALUE)
			maximum = null;
		else
			maximum = i;
		SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
		model.setMaximum(maximum);
		int value = (int) spinner.getValue();
		if (maximum != null && value > maximum)
			value = maximum;
		if (minimum != null && value < minimum)
			value = minimum;
		model.setValue(value);
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
