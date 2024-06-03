package lui.widget;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import lui.container.LContainer;

public class LCombo extends LControlWidget<Integer> {

	private JComboBox<String> combo;
	private boolean includeID = true;
	private boolean optional = true;

	public LCombo(LContainer parent) {
		this(parent, false);
	}

	public LCombo(LContainer parent, boolean readOnly) {
		super(parent, (readOnly ? 1 : 0));
	}
	
	@Override
	protected void createContent(int flags) {
		combo = new JComboBox<>();
		combo.setEditable(flags == 0);
		add(combo);
		combo.addItemListener(e -> {
			Integer oldValue = currentValue;
            currentValue = getSelectionIndex();
			if (oldValue == null || oldValue.equals(currentValue))
				return;
            newModifyAction(oldValue, currentValue);
		});
	}

	protected int getSelectionIndex() {
		int i = combo.getSelectedItem() == null ? 0 : combo.getSelectedIndex();
		if (optional) {
			return i - 1;
		} else {
			return i;
		}
	}
	
	protected void setSelectionIndex(int i) {
		if (optional) 
			i++;
		if (i >= combo.getItemCount()) {
			combo.setSelectedIndex(0);
		} else {
			combo.setSelectedIndex(i);
		}
	}

	public void setValue(Object obj) {
		if (obj != null) {
			Integer i = (Integer) obj;
			combo.setEnabled(true);
			currentValue = i;
			setSelectionIndex(i);
		} else {
			currentValue = null;
			combo.setEnabled(false);
			combo.setSelectedItem(null);
		}
	}
	
	public void setItems(Object[] items) {
		ArrayList<Object> array = new ArrayList<>();
		if (items != null)
            Collections.addAll(array, items);
		setItems(array);
	}
	
	public void setItems(ArrayList<?> array) {
		if (array == null)
			array = new ArrayList<>();
		currentValue = null;
		combo.removeAllItems();
		if (optional)
			combo.addItem("");
		int id = 0;
		for(Object obj : array) {
			String item = includeID ? String.format("[%03d] ", id) : "";
			combo.addItem(item + obj.toString());
			id++;
		}
	}
	
	public void setIncludeID(boolean value) {
		includeID = value;
	}
	
	public void setOptional(boolean value) {
		optional = value;
	}
	
	@Override
	protected JComponent getControl() {
		return combo;
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
