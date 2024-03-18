package lwt.widget;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lwt.container.LContainer;

public class LFlatList extends LControlWidget<Integer> {
	private static final long serialVersionUID = 1L;

	protected JList<String> list;
	protected boolean optional;
	
	public LFlatList(LContainer parent, boolean optional) {
		super(parent);
		this.optional = optional;
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int current = currentValue == null ? -1 : currentValue;
				if (list.getSelectedIndex() == current)
					return;
				newModifyAction(currentValue, list.getSelectedIndex());
				currentValue = list.getSelectedIndex();
			}
		});
		if (optional)
			currentValue = -1;
		else
			currentValue = 0;
	}

	@Override
	protected void createContent(int flags) {
		list = new JList<String>();
		list.setModel(new DefaultListModel<String>());
		add(list);
	}
	
	public String getSelectedText() {
		return list.getSelectedValue();
	}
	
	public void setValue(Object obj) {
		if (obj != null) {
			currentValue = (Integer) obj;
			list.setEnabled(true);
			list.setSelectedIndex(currentValue);
		} else {
			list.setEnabled(false);
			list.clearSelection();
			currentValue = null;
		}
	}
	
	public void setItems(Object[] items) {
		if (items == null) {
			list.setListData(new String[] {});
			return;
		}
		int off = optional ? 1 : 0;
		String[] strs = new String[items.length + off];
		for (int i = 0; i < items.length; i++) {
			strs[i + off] = items[i] == null ? "NULL" : items[i].toString();
		}
		list.setListData(strs);
	}
	
	public void setItems(String[] items) {
		list.setListData(items);
	}
	
	public int indexOf(String item) {
		int i = 0;
		if (item == null) {
			System.out.println("Null item");
			return -1;
		}
		for(Object s : ((DefaultListModel<String>) list.getModel()).toArray()) {
			if (item.equals(s)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	@Override
	public String encodeData(Integer value) {
		return value + "";
	}
	
	@Override
	public Integer decodeData(String str) {
		return Integer.parseInt(str);
	}
	
	@Override
	protected JComponent getControl() {
		return list;
	}

}
