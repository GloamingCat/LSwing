package lui.widget;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

import lui.container.LContainer;

public class LCombo extends LControlWidget<Integer> {

	private JComboBox<String> combo;
	private boolean includeID = true;
	protected boolean optional;

	public static final int READONLY = 1;
	public static final int OPTIONAL = 2;


	public LCombo(LContainer parent) {
		this(parent, READONLY);
	}

	public LCombo(LContainer parent, int flags) {
		super(parent, flags);
	}
	
	@Override
	protected void createContent(int flags) {
		optional = (flags & OPTIONAL) > 0;
		combo = new JComboBox<>();
		combo.setEditable((flags & READONLY) == 0);
		add(combo);
		combo.addActionListener(e -> {
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
		currentValue = null;
		if (obj != null) {
			Integer i = (Integer) obj;
			combo.setEnabled(true);
			currentValue = i;
			setSelectionIndex(i);
		} else {
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
		Integer oldValue = currentValue;
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
		currentValue = oldValue;
	}
	
	public void setIncludeID(boolean value) {
		includeID = value;
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
