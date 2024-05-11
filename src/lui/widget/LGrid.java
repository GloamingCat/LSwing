package lui.widget;

import java.awt.*;

import lui.base.LFlags;
import lui.container.LContainer;
import lui.container.LImage;
import lui.editor.LPopupMenu;
import lui.base.data.LDataCollection;
import lui.base.data.LDataList;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LDeleteEvent;
import lui.base.event.LEditEvent;
import lui.base.event.LInsertEvent;
import lui.base.event.LMoveEvent;
import lui.base.event.LSelectionEvent;
import lui.base.gui.LMenu;
import lui.graphics.LColor;
import lui.graphics.LPainter;

public abstract class LGrid<T, ST> extends LSelectableCollection<T, ST> {

	protected int selectedIndex = -1;
	protected T selectedObj = null;

	private boolean editEnabled = false;
	private boolean insertEnabled = false;
	private boolean duplicateEnabled = false;
	private boolean deleteEnabled = false;
	
	public int cellWidth = 24;
	public int cellHeight = 24;
	
	private LayoutManager layout;
	private LayoutManager emptyLayout;
	
	public LGrid(LContainer parent) {
		super(parent);
	}
	
	@Override
	protected void createContent(int flags) {
		setEmptyLayout();
	}

	public void setColumns(int columns) {
		if (columns <= 0) {
			setSequentialLayout(true);
		} else {
			setGridLayout(columns);
		}
		setEqualCells(true, true);
		layout = getLayout();
	}

	public void setDataCollection(LDataCollection<T> collection) {
		LDataList<T> list = (LDataList<T>) collection;
		setList(list);
	}
	
	@SuppressWarnings("unchecked")
	public LDataList<T> getDataCollection() {
		LDataList<T> list = new LDataList<>();
		for (Component c : getComponents()) {
			LImage img = (LImage) c;
			list.add((T) img.getData());
		}
		return list;
	}
	
	public void clear() {
		for (Component c : getComponents()) {
			LImage img = (LImage) c;
			img.dispose();
		}
	}
	
	private void setEmptyLayout() {
		if (emptyLayout == null) {
			setFillLayout(true);
			emptyLayout = getLayout();
		} else {
			setLayout(emptyLayout);
		}
		addLabel(0, null, true);
		refreshLayout();
	}
	
	private int indexOf(LImage label) {
		int i = 0;
		for (Component c : getComponents()) {
			if (c == label)
				return i;
			else
				i++;
		}
		return -1;
	}
	
	public void setList(LDataList<T> list) {
		clear();
		if (list != null) {
			if (!list.isEmpty()) {
				setLayout(layout);
				int i = 0;
				for(T data : list) {
					LImage label = addLabel(i, data, false);
					setImage(label, i);
					i++;
				}
				refreshLayout();
			} else {
				setEmptyLayout();
			}
		}
	}
	
	private LImage addLabel(int i, T data, boolean placeholder) {
		LImage label = new LImage(this);
		label.getCellData().setTargetSize(cellWidth, cellHeight);
		label.getCellData().setRequiredSize(cellWidth, cellHeight);
		label.getCellData().setAlignment(LFlags.FILL);
		LPopupMenu menu = new LPopupMenu(label);
		menu.putClientProperty("label", label);
		if (placeholder) {
			if (insertEnabled)
				setInsertNewEnabled(menu, true);
		} else {
			label.setData(data);
			label.addPainter(new LPainter() {
				@Override
				public void paint() {
					if (indexOf(label) == selectedIndex) {
						setPaintColor(LColor.BLACK);
						drawRect(0, 0, label.getBounds().width - 1, label.getBounds().height - 1);
					}
				}
			});
			label.addMouseListener(e0 -> {
                if (e0.button == LFlags.LEFT && e0.type == LFlags.PRESS) {
                    int i1 = indexOf(label);
                    select(data, i1);
                    LSelectionEvent e = new LSelectionEvent(new LPath(i1), data, i1, true);
                    notifySelectionListeners(e);
                }
            });
			if (!isEditable())
				return label;
			setEditEnabled(menu, editEnabled);
			setInsertNewEnabled(menu, insertEnabled);
			setDuplicateEnabled(menu, duplicateEnabled);
			setDeleteEnabled(menu, deleteEnabled);
		}
		return label;
	}
	
	private boolean isEditable() {
		return editEnabled || insertEnabled || duplicateEnabled || deleteEnabled;
	}

	protected abstract void setImage(LImage label, int i);
	
	public LImage getSelectedCell() {
		if (selectedIndex == -1)
			return null;
		return (LImage) getChild(selectedIndex);
	}
	
	//-------------------------------------------------------------------------------------
	// Button Handler
	//-------------------------------------------------------------------------------------
	
	protected void onEditButton(LMenu menu) {
		LImage label = (LImage) ((LPopupMenu) menu).getClientProperty("label");
		int i = indexOf(label);
		LPath path = new LPath(i);
		newEditAction(path);
	}
	
	protected void onInsertNewButton(LMenu menu) {
		LImage label = (LImage) ((LPopupMenu) menu).getClientProperty("label");
		int i = indexOf(label) + 1;
		LDataTree<T> newNode = emptyNode();
		newInsertAction(null, i, newNode);
	}
	
	protected void onDuplicateButton(LMenu menu) {
		LImage label = (LImage) ((LPopupMenu) menu).getClientProperty("label");
		int i = indexOf(label);
		LDataTree<T> newNode = duplicateNode(new LPath(i));
		newInsertAction(null, i, newNode);
	}
	
	protected void onDeleteButton(LMenu menu) {
		LImage label = (LImage) ((LPopupMenu) menu).getClientProperty("label");
		int i = indexOf(label);
		newDeleteAction(null, i);
	}
	
	public void setEditEnabled(boolean value) {
		editEnabled = value;
	}
	
	public void setInsertNewEnabled(boolean value) {
		insertEnabled = value;
	}
	
	public void setDuplicateEnabled(boolean value) {
		duplicateEnabled = value;
	}
	
	public void setDeleteEnabled(boolean value) {
		deleteEnabled = value;
	}
	
	//-------------------------------------------------------------------------------------
	// Modify
	//-------------------------------------------------------------------------------------
	
	@Override
	public LMoveEvent<T> move(LPath sourceParent, int sourceIndex,
			LPath destParent, int destIndex) { 
		// Not supported.
		return null; 
	}

	@Override
	public LInsertEvent<T> insert(LPath parentPath, int index, LDataTree<T> node) {
		if (getLayout() == emptyLayout) {
			clear();
			setLayout(layout);
		}
		addLabel(index, node.data, false);
		return new LInsertEvent<T>(parentPath, index, node);
	}

	@Override
	public LDeleteEvent<T> delete(LPath parentPath, int index) {
		LImage c = (LImage) getChild(index);
		@SuppressWarnings("unchecked")
		T data = (T) c.getData();
		c.dispose();
		if (getChildCount() == 0) {
			setEmptyLayout();
		}
		return new LDeleteEvent<T>(parentPath, index, new LDataTree<T>(data));
	}
	
	protected abstract LDataTree<T> emptyNode();
	
	protected abstract LDataTree<T> duplicateNode(LPath nodePath);

	//-------------------------------------------------------------------------------------
	// Refresh
	//-------------------------------------------------------------------------------------
	
	@Override
	public void notifyEditListeners(LEditEvent<ST> event) {
		super.notifyEditListeners(event);
		refreshObject(event.path);
		refreshLayout();
	}
	
	@Override
	public void notifyInsertListeners(LInsertEvent<T> event) {
		super.notifyInsertListeners(event);
		refreshAll();
		refreshLayout();
	}
	
	@Override
	public void notifyDeleteListeners(LDeleteEvent<T> event) {
		super.notifyDeleteListeners(event);
		refreshAll();
		refreshLayout();
	}
	
	@Override
	public void refreshObject(LPath path) { 
		LImage label = (LImage) getChild(path.index);
		T data = toObject(path);
		label.setData(data);
		setImage(label, path.index);
	}

	@Override
	public void refreshAll() {
		if (getLayout() == layout) {
			LPath p = new LPath(0);
			for(p.index = 0; p.index < getChildCount(); p.index++) {
				refreshObject(p);
			}
		}
	}
	
	//-------------------------------------------------------------------------------------
	// Selection
	//-------------------------------------------------------------------------------------
	
	public LSelectionEvent select(LPath path) {
		if (path.index >= 0) {
			LImage l = (LImage) getChild(path.index);
			@SuppressWarnings("unchecked")
			T data = (T) l.getData("data");
			select(data, path.index);
			return new LSelectionEvent(path, data, path.index, true);
		} else {
			select((T) null, -1);
			return new LSelectionEvent(path, null, path.index, true);
		}
	}
	
	public T getSelectedObject() {
		return selectedObj;
	}
	
	public LPath getSelectedPath() {
		if (selectedIndex == -1) {
			return null;
		} else {
			return new LPath(selectedIndex);
		}
	}
	
	protected void select(T obj, int i) {
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
			refreshLayout();
		}
	}
	
	@Override
	public void onCopyButton(LMenu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPasteButton(LMenu menu) {
		// TODO Auto-generated method stub
		
	}

}
