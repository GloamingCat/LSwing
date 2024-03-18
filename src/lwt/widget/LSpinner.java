package lwt.widget;

import lwt.container.*;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LSpinner extends LControlWidget<Integer> {
	private static final long serialVersionUID = 1L;

	private JSpinner spinner;
	private int minimum = 0;
	private int maximum = 100;

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
		setExpand(true, false);
		setSpread(columns, 1);
	}
	
	@Override
	protected void createContent(int flags) {
		spinner = new JSpinner();
		add(spinner);
		spinner.setModel(new SpinnerNumberModel(minimum, minimum, maximum, 1));
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (currentValue != null && spinner.getValue() == currentValue)
					return;
				newModifyAction(currentValue, (Integer) spinner.getValue());
				currentValue = (Integer) spinner.getValue();
			}
		});
	}
	
	@Override
	public void setValue(Object obj) {
		if (obj != null) {
			Integer i = (Integer) obj;
			spinner.setEnabled(true);
			spinner.setValue(i);
			currentValue = i;
		} else {
			spinner.setEnabled(false);
			spinner.setValue((int) 0);
			currentValue = null;
		}
	}
	
	public void setMinimum(int i) {
		minimum = i;
		spinner.setModel(new SpinnerNumberModel((int) spinner.getValue(), minimum, maximum, 1));
	}
	
	public void setMaximum(int i) {
		maximum = i;
		spinner.setModel(new SpinnerNumberModel((int) spinner.getValue(), minimum, maximum, 1));
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
