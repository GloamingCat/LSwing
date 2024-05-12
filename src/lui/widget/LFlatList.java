package lui.widget;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;

import lui.base.LPrefs;
import lui.container.LContainer;

import java.awt.*;

public class LFlatList extends LControlWidget<Integer> {

	JList<String> list;
	protected boolean optional;

	@SuppressWarnings({"DataFlowIssue"})
	public LFlatList(LContainer parent, boolean optional) {
		super(parent);
		this.optional = optional;
		list.addListSelectionListener(e -> {
            int current = currentValue == null ? -1 : currentValue;
            if (list.getSelectedIndex() == current)
                return;
            newModifyAction(currentValue, list.getSelectedIndex());
            currentValue = list.getSelectedIndex();
        });
		if (optional)
			currentValue = -1;
		else
			currentValue = 0;
	}

	@Override
	protected void createContent(int flags) {
		list = new JList<>();
		list.setModel(new DefaultListModel<>());
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

	//////////////////////////////////////////////////
	//region Properties

	@Override
	public Dimension getMinimumSize() {
		Dimension size = new Dimension(LPrefs.LISTWIDTH, LPrefs.LISTHEIGHT);
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width = Math.max(size.width, LPrefs.LISTWIDTH);
		size.height = Math.max(size.height, LPrefs.LISTHEIGHT);
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

	//endregion

}
