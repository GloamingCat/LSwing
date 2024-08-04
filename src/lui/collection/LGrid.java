package lui.collection;

import java.awt.*;

import lui.base.LFlags;
import lui.base.data.*;
import lui.base.event.LControlEvent;
import lui.base.event.listener.LControlListener;
import lui.base.gui.LControl;
import lui.base.gui.LMenu;
import lui.container.LContainer;
import lui.container.LImage;
import lui.graphics.LColor;
import lui.graphics.LPainter;

import javax.swing.*;

public abstract class LGrid<T, ST> extends LControlList<T, ST, LGrid.LCell<T>> {

	protected int cellWidth = 24;
	protected int cellHeight = 24;
	protected Color cellColor;

	private final LColor borderColor;
	private final int borderWidth;

	public static class LCell<T> extends LImage implements LControl<T> {
		private T data;
		private final LGrid<T, ?> grid;
		public LCell(LGrid<T, ?> grid, LColor borderColor, int borderWidth) {
			super(grid.controls);
			this.grid = grid;
			getCellData().setAlignment(LFlags.FILL);
			addPainter(new LPainter() {
				@Override
				public void paint() {
					if (grid.getSelectedControl() == LCell.this) {
						setLineWidth(borderWidth);
						setPaintColor(borderColor);
						drawRect(0, 0, getWidth() - borderWidth, getHeight() - borderWidth);
					}
				}
			});
		}
		@Override
		@SuppressWarnings("unchecked")
		public void setValue(Object value) {
			data = (T) value;
			grid.refreshImage(this, data);
		}
		@Override
		public T getValue() {
			return data;
		}
		@Override
		public void forceModification(T newValue) {}
		@Override
		public void notifyListeners(LControlEvent<T> event) {}
		@Override
		public void addModifyListener(LControlListener<T> listener) {}
		@Override
		public boolean canDecode(String str) { return false; }
		@Override
		public void onPasteButton(LMenu object) {}
		@Override
		public void onCopyButton(LMenu object) {}
	}

	//////////////////////////////////////////////////
	//region Constructor

	public LGrid(LContainer parent) {
		super(parent);
		borderColor = new LColor(UIManager.getColor("Table.gridColor"));
		borderWidth = 1;
		cellColor = UIManager.getColor("desktop");
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

	//endregion

	//////////////////////////////////////////////////
	//region Controls

	@Override
	protected LCell<T> createControl() {
		LCell<T> img = new LCell<>(this, borderColor, borderWidth);
		img.setBackground(cellColor);
		img.getCellData().setTargetSize(cellWidth, cellHeight);
		img.getCellData().setRequiredSize(cellWidth, cellHeight);
		img.addMouseListener(e0 -> {
			if (e0.button == LFlags.LEFT && e0.type == LFlags.PRESS)
				forceSelection(img);
		});
		return img;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Refresh

	@Override
	public void refreshControl(LCell<T> control, int i) {}

	protected abstract void refreshImage(LCell<T> label, T data);

	//endregion

	@Override
	public Dimension getMinimumSize() {
		LPoint margin = controls.getMargins();
		Dimension size = new Dimension(cellWidth + margin.x * 2, cellHeight + margin.y * 2);
			if (gridData != null)
				gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		int cols = columns <= 0 ? Math.max(1, getControlCount()) : columns;
		int rows = Math.ceilDiv(getControlCount(), cols);
		LPoint spacing = controls.getSpacing();
		LPoint margin = controls.getMargins();
		Dimension size = new Dimension(
				cols * cellWidth + (cols - 1) * spacing.x + margin.x * 2,
				rows * cellHeight + (rows - 1) * spacing.y + margin.y * 2);
			if (gridData != null)
				gridData.storePreferredSize(size);
		return size;
	}

}
