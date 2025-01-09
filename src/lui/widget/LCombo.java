package lui.widget;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

import lui.container.LContainer;

public class LCombo extends LControlWidget<Integer> {

	private JComboBox<String> combo;
	private ActionListener listener;

	private boolean includeId;
	protected boolean optional;

	public static final int READONLY = 1;
	public static final int OPTIONAL = 2;
	public static final int INCLUDEID = 4;

	public LCombo(LContainer parent) {
		this(parent, READONLY);
	}

	public LCombo(LContainer parent, int flags) {
		super(parent, flags);
	}
	
	@Override
	protected void createContent(int flags) {
		optional = (flags & OPTIONAL) > 0;
		includeId = (flags & INCLUDEID) > 0;
		combo = new JComboBox<>();
		combo.setEditable((flags & READONLY) == 0);
		add(combo);
		listener = e -> {
			Integer oldValue = currentValue;
			currentValue = getSelectionIndex();
			if (oldValue == null || oldValue.equals(currentValue))
				return;
			newModifyAction(oldValue, currentValue);
		};
		combo.addActionListener(listener);
	}

	public int getMaximumValue() {
		return combo.getItemCount();
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
		combo.removeActionListener(listener);
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
		combo.addActionListener(listener);
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
			String item = includeId ? String.format("[%03d] ", id) : "";
			combo.addItem(item + obj.toString());
			id++;
		}
		setValue(oldValue);
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
