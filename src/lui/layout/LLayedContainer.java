package lui.layout;

import lui.base.LPrefs;
import lui.base.data.LPoint;
import lui.container.LContainer;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public interface LLayedContainer extends LContainer {


	default LayoutManager getLayout() {
		return getContentComposite().getLayout();
	}
	default void setLayout(LayoutManager layout) {
		getContentComposite().setLayout(layout);
	}

	default void setBorder(Border border) {
		getContentComposite().setBorder(border);
	}

	default Border getBorder() {
		return getContentComposite().getBorder();
	}

	/** Fill layout (spacing = 0).
	 */
	default void setFillLayout(boolean horizontal) {
		GridLayout gl = horizontal ?
				new GridLayout(0, 1) : new GridLayout(1, 0);
		gl.setHgap(getHorizontalSpacing());
		gl.setVgap(getVerticalSpacing());
		setLayout(gl);
	}

	/** Grid layout (spacing = 5).
	 */
	default void setGridLayout(int columns) {
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[columns];
		setMargins(-LPrefs.GRIDSPACING, -LPrefs.GRIDSPACING);
		setData("hSpacing", LPrefs.GRIDSPACING);
		setData("vSpacing", LPrefs.GRIDSPACING);
		setLayout(gbl);
	}

	/** Column/row layout (spacing = 5).
	 */
	default void setSequentialLayout(boolean horizontal) {
		if (horizontal) {
			FlowLayout fl = new FlowLayout();
			fl.setHgap(LPrefs.GRIDSPACING);
			fl.setVgap(LPrefs.GRIDSPACING);
			setLayout(fl);
		} else {
			setGridLayout(1);
		}
	}

	default void setMargins(int h, int v) {
		v -= getVerticalSpacing();
		h -= getHorizontalSpacing();
		setBorder(new EmptyBorder(v, h, v, h));
	}

	default LPoint getMargins() {
		EmptyBorder border = (EmptyBorder) getBorder();
		return new LPoint(border.getBorderInsets().left + getHorizontalSpacing(),
						  border.getBorderInsets().top + getVerticalSpacing());
	}

	default void setSpacing(int h, int v) {
		LayoutManager l = getLayout();
		if (l instanceof GridLayout gl) {
            gl.setHgap(h);
			gl.setVgap(v);
		} else if (l instanceof FlowLayout fl) {
            fl.setHgap(h);
			fl.setVgap(v);
		} else {
			LPoint margins = getMargins();
			margins.x += getHorizontalSpacing() - h;
			margins.y += getVerticalSpacing() - v;
			setMargins(margins.x, margins.y);
		}
		setData("hSpacing", h);
		setData("vSpacing", v);
	}

	default void setSpacing(int s) {
		setSpacing(s, s);
	}

	default void setEqualCells(boolean horizontal, boolean vertical) {
		setData("equalCols", horizontal);
		setData("equalRows", vertical);
	}

	default int getHorizontalSpacing() {
		Object obj = getData("hSpacing");
		if (obj == null)
			return 0;
		return (Integer) obj;
	}

	default int getVerticalSpacing() {
		Object obj = getData("vSpacing");
		if (obj == null)
			return 0;
		return (Integer) obj;
	}

	default boolean hasEqualCols() {
		Object v = getData("equalCols");
		if (v == null)
			return false;
		return (Boolean) v;
	}

	default boolean hasEqualRows() {
		Object v = getData("equalRows");
		if (v == null)
			return false;
		return (Boolean) v;
	}

	default void refreshLayoutData() {
		LayoutManager l = getLayout();
		if (l instanceof GridBagLayout gbl) {
			int hSpacing = getHorizontalSpacing();
			int vSpacing = getVerticalSpacing();
			int cols = gbl.columnWeights.length;
			int minWidth = 0;
			int minHeight = 0;
			int sumWidth = 0;
			int sumHeight = 0;
			int i = 0;
			for (Component c : getContentComposite().getComponents()) {
				if (c instanceof LLayedCell lc) {
					LCellData cd = lc.getCellData();
					minWidth = Math.max(minWidth, cd.minWidth);
					minHeight = Math.max(minHeight, cd.minHeight);
					Dimension size = c.getPreferredSize();
					sumWidth += size.width;
					sumHeight += size.height;
					gbl.setConstraints(c, cd.getGridBagConstraints(i, cols, hSpacing, vSpacing));
				}
				i++;
			}
			boolean equalCols = hasEqualCols();
			boolean equalRows = hasEqualRows();
			if (equalCols || equalRows) {
				int rows = Math.ceilDiv(getChildCount(), cols);
				int width = sumWidth / cols;
				int height = sumHeight / rows;
				for (var c : getContentComposite().getComponents()) {
					if (c instanceof LLayedCell lc) {
						LCellData cd = lc.getCellData();
						if (equalCols) {
							cd.minWidth = minWidth;
							cd.width = width;
						}
						if (equalRows) {
							cd.minHeight = minHeight;
							cd.height = height;
						}
					}
				}
			}
		}
	}

}
