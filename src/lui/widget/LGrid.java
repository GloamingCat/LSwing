package lui.widget;

import java.awt.*;

import lui.base.LFlags;
import lui.base.data.*;
import lui.container.LContainer;
import lui.container.LImage;
import lui.base.event.LSelectionEvent;
import lui.graphics.LColor;
import lui.graphics.LPainter;

import javax.swing.*;

public abstract class LGrid<T, ST> extends LSelectableCollection<T, ST> {

	protected int selectedIndex = -1;
	protected T selectedObj = null;

	protected int cellWidth = 24;
	protected int cellHeight = 24;
	protected Color cellColor = UIManager.getColor("desktop");
	
	private LayoutManager layout;
	private LayoutManager emptyLayout;

	private int columns = 0;

	private LColor borderColor;
	private final int borderWidth = 1;

	//////////////////////////////////////////////////
	//region Constructor

	public LGrid(LContainer parent) {
		super(parent);
	}
	
	@Override
	protected void createContent(int flags) {
		borderColor = new LColor(UIManager.getColor("Table.gridColor"));
		setEmptyLayout();
	}

	//endregion

	//////////////////////////////////////////////////
	//region Data

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

	//endregion

	//////////////////////////////////////////////////
	//region Layout

	public void setCellSize(int w, int h) {
		cellWidth = w;
		cellHeight = h;
		setMinimumSize(null);
		setPreferredSize(null);
	}

	public void setCellColor(LColor color) {
		cellColor = color.convert();
	}

	public void setColumns(int columns) {
		this.columns = columns;
		if (columns <= 0) {
			setSequentialLayout(true);
		} else {
			setGridLayout(columns);
		}
		setEqualCells(true, true);
		layout = getLayout();
	}

	protected void setGridLayout() {
		setLayout(layout);
	}

	protected void setEmptyLayout() {
		if (emptyLayout == null) {
			setFillLayout(true);
			emptyLayout = getLayout();
		} else {
			setLayout(emptyLayout);
		}
		addLabel(0, null, true);
		refreshLayout();
	}

	protected boolean isEmpty() {
		return getLayout() == emptyLayout;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Widgets

	public void clear() {
		for (Component c : getComponents()) {
			LImage img = (LImage) c;
			img.dispose();
		}
	}

	protected int indexOf(LImage label) {
		int i = 0;
		for (Component c : getComponents()) {
			if (c == label)
				return i;
			else
				i++;
		}
		return -1;
	}
	
	protected void setList(LDataList<T> list) {
		clear();
		if (list != null) {
			if (!list.isEmpty()) {
				setLayout(layout);
				int i = 0;
				for (T data : list) {
					LImage label = addLabel(i, data, false);
					setImage(label, i);
					i++;
				}
			} else {
				setEmptyLayout();
			}
		}
		refreshLayout();
	}
	
	protected LImage addLabel(int i, T data, boolean placeholder) {
		LImage img = new LImage(this);
		img.setBackground(cellColor);
		img.getCellData().setTargetSize(cellWidth, cellHeight);
		img.getCellData().setRequiredSize(cellWidth, cellHeight);
		img.getCellData().setAlignment(LFlags.FILL);
		if (!placeholder) {
			img.setData(data);
			img.addPainter(new LPainter() {
				@Override
				public void paint() {
					if (indexOf(img) == selectedIndex) {
						setLineWidth(borderWidth);
						setPaintColor(borderColor);
						drawRect(0, 0, cellWidth - borderWidth, cellHeight - borderWidth);
					}
				}
			});
			img.addMouseListener(e0 -> {
                if (e0.button == LFlags.LEFT && e0.type == LFlags.PRESS) {
                    int i1 = indexOf(img);
                    select(data, i1);
                    LSelectionEvent e = new LSelectionEvent(new LPath(i1), data, i1, true);
                    notifySelectionListeners(e);
                }
            });
		}
		return img;
	}

	protected abstract void setImage(LImage label, int i);
	
	public LImage getSelectedCell() {
		if (selectedIndex == -1)
			return null;
		return (LImage) getChild(selectedIndex);
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

	public LImage getImage(int i) {
		return (LImage) getComponent(i);
	}

	//endregion

	///////////////////////////////////////////////////
	//region Selection

	@Override
	public LSelectionEvent select(LPath path) {
		if (path.index >= 0 && path.index < getChildCount()) {
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

	//endregion

	@Override
	public Dimension getMinimumSize() {
		LPoint margin = getMargins();
		Dimension size = new Dimension(cellWidth + margin.x * 2, cellHeight + margin.y * 2);
			if (gridData != null)
				gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		int cols = columns == 0 ? getChildCount() : columns;
		int rows = Math.ceilDiv(getChildCount(), cols);
		LPoint spacing = getSpacing();
		LPoint margin = getMargins();
		Dimension size = new Dimension(
				cols * cellWidth + (cols - 1) * spacing.x + margin.x * 2,
				rows * cellHeight + (rows - 1) * spacing.y + margin.y * 2);
			if (gridData != null)
				gridData.storePreferredSize(size);
		return size;
	}

}
