package lui.widget;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import lui.container.LContainer;

public class LCombo extends LControlWidget<Integer> {

	JComboBox<String> combo;
	private boolean includeID = true;
	private boolean optional = true;
	
	public LCombo(LContainer parent) {
		this(parent, 1, false);
	}
	
	public LCombo(LContainer parent, boolean readOnly) {
		this(parent, 1, readOnly);
	}
	
	public LCombo(LContainer parent, int columns) {
		this(parent, columns, false);
	}
	
	public LCombo(LContainer parent, int columns, boolean readOnly) {
		super(parent, (readOnly ? 1 : 0));
		getCellData().setSpread(columns, 1);
		getCellData().setExpand(true, false);
		combo.addActionListener(arg0 -> {
            int current = currentValue == null ? (optional ? -1 : 0) : currentValue;
            if (getSelectionIndex() == current)
                return;
            newModifyAction(currentValue, getSelectionIndex());
            currentValue = getSelectionIndex();
        });
	}
	
	@Override
	protected void createContent(int flags) {
		combo = new JComboBox<>();
		combo.setEditable(flags == 0);
		add(combo);
	}

	public int getSelectionIndex() {
		int i = combo.getSelectedItem() == null ? 0 : combo.getSelectedIndex();
		if (optional) {
			return i - 1;
		} else {
			return i;
		}
	}
	
	public void setSelectionIndex(int i) {
		if (optional) 
			i++;
		if (i >= combo.getItemCount())
			combo.setSelectedIndex(0);
		else
			combo.setSelectedIndex(i);
	}

	public void setValue(Object obj) {
		if (obj != null) {
			Integer i = (Integer) obj;
			currentValue = i;
			combo.setEnabled(true);
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
