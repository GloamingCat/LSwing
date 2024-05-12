package lui.layout;

import lui.base.LPrefs;
import lui.base.data.LPoint;
import lui.container.LContainer;
import thirdparty.WrapLayout;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

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
				new GridLayout(1, 0) : new GridLayout(0, 1);
		gl.setHgap(getHorizontalSpacing());
		gl.setVgap(getVerticalSpacing());
		setLayout(gl);
		LPoint margin = getMargins();
		setMargins(margin.x, margin.y);
	}

	/** Grid layout (spacing = 5).
	 */
	default void setGridLayout(int columns) {
		GridBagLayout gbl = new GridBagLayout();
		setData("columns", columns);
		setData("hSpacing", LPrefs.GRIDSPACING);
		setData("vSpacing", LPrefs.GRIDSPACING);
		setLayout(gbl);
	}

	/** Column/row layout (spacing = 5).
	 */
	default void setSequentialLayout(boolean horizontal) {
		if (horizontal) {
			WrapLayout wl = new WrapLayout();
			wl.setHgap(LPrefs.GRIDSPACING);
			wl.setVgap(LPrefs.GRIDSPACING);
			setLayout(wl);
		} else {
			setGridLayout(1);
		}
	}

	default void setMargins(int h, int v) {
		setData("hMargin", h);
		setData("vMargin", v);
		setBorder(new EmptyBorder(v, h, v, h));
	}

	default LPoint getMargins() {
		Integer h = (Integer) getData("hMargin");
		Integer v = (Integer) getData("vMargin");
		return new LPoint(h == null ? 0 : h, v == null ? 0 : v);
	}

	default void setSpacing(int h, int v) {
		setData("hSpacing", h);
		setData("vSpacing", v);
		LayoutManager l = getLayout();
		if (l instanceof GridLayout gl) {
            gl.setHgap(h);
			gl.setVgap(v);
		} else if (l instanceof WrapLayout fl) {
            fl.setHgap(h);
			fl.setVgap(v);
		}
	}

	default void setSpacing(int s) {
		setSpacing(s, s);
	}

	default LPoint getSpacing() {
		Integer h = (Integer) getData("hSpacing");
		Integer v = (Integer) getData("vSpacing");
		return new LPoint(h == null ? 0 : h, v == null ? 0 : v);
	}

	default void setEqualCells(boolean horizontal, boolean vertical) {
		setData("equalCols", horizontal);
		setData("equalRows", vertical);
	}

	default void setEqualCells(boolean horizontal) {
		setEqualCells(horizontal, horizontal);
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
			boolean equalCols = hasEqualCols();
			boolean equalRows = hasEqualRows();
			int hSpacing = getHorizontalSpacing();
			int vSpacing = getVerticalSpacing();
			int cols = (int) getData("columns");
			int i = 0;
			int minWidth = 0, minHeight = 0, prefWidth = 0, prefHeight = 0;
			HashMap<String, Integer> skip = new HashMap<>();
			for (Component c : getContentComposite().getComponents()) {
				if (c instanceof LLayedCell lc) {
					LCellData cd = lc.getCellData();
					GridBagConstraints gbc = cd.getGridBagConstraints(i, cols, hSpacing, vSpacing);
					for (String pos = gbc.gridx+","+gbc.gridy; skip.containsKey(pos); pos = gbc.gridx+","+gbc.gridy) {
						gbc.gridx += skip.get(pos);
						if (gbc.gridx + gbc.gridwidth > cols) {
							gbc.gridy++;
							gbc.gridx = 0;
							i = gbc.gridy * cols;
						}
					}
					if (gbc.gridheight > 1) {
						for (int h = 1; h < gbc.gridheight; h++) {
							String pos = gbc.gridx+","+(gbc.gridy + h);
							skip.put(pos, gbc.gridwidth);
						}
					}
					c.setPreferredSize(null);
					c.setMinimumSize(null);
					Dimension p = c.getPreferredSize();
					Dimension m = c.getMinimumSize();
					if (equalCols) {
						prefWidth = Math.max(prefWidth, p.width);
						minWidth = Math.max(minWidth, m.width);
						gbc.weightx = 1;
					}
					if (equalRows) {
						prefHeight = Math.max(prefHeight, p.height);
						minHeight = Math.max(minHeight, m.height);
						gbc.weighty = 1;
					}
					gbl.setConstraints(c, gbc);
					i += gbc.gridwidth;
				}
			}
			if (equalRows || equalCols) {
				for (Component c : getContentComposite().getComponents()) {
					Dimension p = c.getPreferredSize();
					Dimension m = c.getMinimumSize();
					c.setPreferredSize(new Dimension(equalCols ? prefWidth : p.width, equalRows ? prefHeight : p.height));
					c.setMinimumSize(new Dimension(equalCols ? minWidth : m.width, equalRows ? minHeight : m.height));
				}
			}
		}
	}

}
