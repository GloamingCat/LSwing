package lui.widget;

import lui.container.*;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class LSpinner extends LControlWidget<Integer> {

	private JSpinner spinner;
	private Integer minimum = 0;
	private Integer maximum = 100;
	private final Integer step = 1;

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new LPanel(new lwt.dialog.LShell(400, 200), 2, true)
	 */
	public LSpinner(LContainer parent) {
		this(parent, 1);
	}
	
	public LSpinner(LContainer parent, int columns) {
		super(parent);
		setFillLayout(true);
		getCellData().setExpand(true, false);
		getCellData().setSpread(columns, 1);
	}
	
	@Override
	protected void createContent(int flags) {
		spinner = new JSpinner();
		add(spinner);
		spinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		spinner.addChangeListener(e -> {
            if (currentValue != null && spinner.getValue() == currentValue)
                return;
            newModifyAction(currentValue, (Integer) spinner.getValue());
            currentValue = (Integer) spinner.getValue();
        });
		spinner.setEnabled(true);
		spinner.setEditor(new JSpinner.NumberEditor(spinner));
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
		minimum = i;
		spinner.setModel(new SpinnerNumberModel((Integer) spinner.getValue(), minimum, maximum, step));
	}
	
	public void setMaximum(int i) {
		maximum = i;
		spinner.setModel(new SpinnerNumberModel((Integer) spinner.getValue(), minimum, maximum, step));
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
