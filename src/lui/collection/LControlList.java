package lui.collection;

import lui.base.data.*;
import lui.base.gui.LControl;
import lui.container.LContainer;
import lui.base.event.LSelectionEvent;
import lui.container.LPanel;
import lui.container.LScrollPanel;
import lui.widget.LLabel;

public abstract class LControlList<T, ST, C extends LPanel & LControl<T>> extends LSelectableCollection<T, ST> {

	protected int selectedIndex = -1;
	protected T selectedObj = null;

	protected int columns = 0;

    protected LPanel controls;
	protected LScrollPanel scroll;
	protected LLabel filler;

	protected int fixedControlCount = -1;


	//////////////////////////////////////////////////
	//region Constructor

	public LControlList(LContainer parent) {
		super(parent, 0);
	}

	public LControlList(LContainer parent, int flags) {
		super(parent, flags);
	}

	@Override
	protected void createContent(int flags) {
		setFillLayout(false);
		scroll = new LScrollPanel(this);
		scroll.setBorder(null);
		scroll.getCellData().setExpand(true, true);

        LPanel content = new LPanel(scroll);
		content.setGridLayout(1);
		content.setSpacing(0);

		controls = new LPanel(content);
		controls.getCellData().setExpand(true, true);

		filler = new LLabel(content, 1, 1);
		filler.getCellData().setExpand(true, true);
		filler.getCellData().setTargetSize(0, 0);
		filler.getCellData().setRequiredSize(-1, -1);
		filler.addMouseListener(e -> select((LPath) null));

		setColumns(-1);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Data

	@Override
	public void setDataCollection(LDataCollection<T> collection) {
		LDataList<T> list = (LDataList<T>) collection;
		setList(list);
	}

	@Override
	public LDataList<T> getDataCollection() {
		LDataList<T> list = new LDataList<>();
		for (int i = 0; i < getControlCount(); i++)
			list.add(getControl(i).getValue());
		return list;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Layout

	public void setColumns(int columns) {
		this.columns = columns;
		if (columns <= 0) {
			controls.setSequentialLayout(true);
		} else {
			controls.setGridLayout(columns);
		}
		controls.setEqualCells(true, true);
	}

	@Override
	public void refreshLayout() {
		super.refreshLayout();
	}

	//endregion

	//////////////////////////////////////////////////
	//region Controls

	public void clear() {
		for (int i = 0; i < getControlCount(); i++) {
			getControl(i).dispose();
		}
		controls.removeAll();
	}

	public int indexOf(C control) {
		for (int i = 0; i < getControlCount(); i++) {
			if (getControl(i) == control)
				return i;
		}
		return -1;
	}

	public C getControl(int i) {
		@SuppressWarnings("unchecked")
		C control = (C) controls.getComponent(i);
		return control;
	}

	public int getControlCount() {
		return controls.getChildCount();
	}

	public void setControlCount(int n) {
		fixedControlCount = n;
		// Update children
		int nChildren = getControlCount();
		for (int i = 0; i < nChildren; i++) {
			C control = getControl(i);
			refreshControl(control, i);
		}
		// Add missing controls for exceeding attributes
		for (int i = nChildren; i < n; i ++) {
			C control = createControl();
			refreshControl(control, i);
		}
		// Remove exceeding controls
		while (getControlCount() > n)
			getControl(n).dispose();
		// Fix layout
		if (nChildren != n)
			controls.refreshLayout();
	}

	protected void setList(LDataList<T> list) {
		if (fixedControlCount != -1) {
			if (list != null) {
				for (int i = 0; i < getControlCount(); i++) {
					C control = getControl(i);
					control.setValue(list.get(i));
				}
			} else {
				for (int i = 0; i < getControlCount(); i++) {
					C control = getControl(i);
					control.setValue(null);
					control.setEnabled(false);
				}
			}
		} else {
			clear();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					C control = createControl();
					refreshControl(control, i);
					control.setValue(list.get(i));
				}
			}
			controls.refreshLayout();
		}
	}

	protected abstract C createControl();

	//endregion

	//////////////////////////////////////////////////
	//region Refresh

	protected abstract void refreshControl(C control, int i);

	@Override
	public void refreshObject(LPath path) {
		C control = getControl(path.index);
		T data = toObject(path);
		control.setValue(data);
		refreshControl(control, path.index);
	}

	@Override
	public void refreshAll() {
		LPath p = new LPath(0);
		for (p.index = 0; p.index < getControlCount(); p.index++) {
			refreshObject(p);
		}
	}

	public void refreshControls() {
		for (int i = 0; i < getControlCount(); i++) {
			refreshControl(getControl(i), i);
		}
	}

	//endregion

	///////////////////////////////////////////////////
	//region Selection

	@Override
	public T getSelectedObject() {
		return selectedObj;
	}

	@Override
	public LPath getSelectedPath() {
		if (selectedIndex == -1) {
			return null;
		} else {
			return new LPath(selectedIndex);
		}
	}

	@Override
	public LSelectionEvent select(LPath path) {
		if (path == null) {
			setSelectedData(null, -1);
			return new LSelectionEvent(null, null, -1, true);
		} else if (path.index >= 0 && path.index < getControlCount()) {
			LControl<T> control = getControl(path.index);
			T data = control.getValue();
			setSelectedData(data, path.index);
			return new LSelectionEvent(path, data, path.index, true);
		} else {
			setSelectedData(null, -1);
			return new LSelectionEvent(path, null, path.index, true);
		}
	}

	protected LSelectionEvent select(C control) {
		int i = indexOf(control);
		setSelectedData(control.getValue(), i);
		return new LSelectionEvent(new LPath(i), control.getValue(), i, true);
	}

	private void setSelectedData(T obj, int i) {
		if (i != selectedIndex) {
			if (selectedIndex != -1) {
				LPath path = new LPath(selectedIndex);
				selectedIndex = i;
				refreshObject(path);
			}
			selectedIndex = i;
			selectedObj = obj;
			if (i >= 0) {
				refreshObject(new LPath(i));
			}
			controls.refreshLayout();
		}
	}

	public C getSelectedControl() {
		if (selectedIndex == -1)
			return null;
		return getControl(selectedIndex);
	}

	public void forceSelection(C control) {
		LSelectionEvent e = select(control);
		notifySelectionListeners(e);
	}

	//endregion

}
