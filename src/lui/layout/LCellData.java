package lui.layout;

import lui.base.LFlags;

import java.awt.*;

public class LCellData extends lui.base.gui.LCellData {

	private int anchor = GridBagConstraints.CENTER;
	private int fill = GridBagConstraints.NONE;

    public GridBagConstraints getGridBagConstraints(int i, int cols, int hSpacing, int vSpacing) {
		if (cols == 0)
			cols = i + 1;
		if (i < cols) // First row
			vSpacing = 0;
		if (i % cols == 0) // First column
			hSpacing = 0;
		return new GridBagConstraints(i % cols, i / cols,
			Math.min(hSpread, cols), vSpread,
			hExpand ? 1 : 0, vExpand ? 1 : 0,
			anchor, fill,
			new Insets(vSpacing, hSpacing, 0, 0),
			0, 0);
	}

	public void setExpand(boolean h, boolean v) {
		super.setExpand(h, v);
		if (h) {
			if (fill == GridBagConstraints.VERTICAL || v)
				fill = GridBagConstraints.BOTH;
			else
				fill = GridBagConstraints.HORIZONTAL;
		}
		if (v) {
			if (fill == GridBagConstraints.HORIZONTAL || h)
				fill = GridBagConstraints.BOTH;
			else
				fill = GridBagConstraints.VERTICAL;
		}
	}

    public void setAlignment(int a) {
		super.setAlignment(a);
		fill = 0;
		if ((a & LFlags.LEFT) > 0) {
			if ((a & LFlags.TOP) > 0)
				anchor = GridBagConstraints.NORTHWEST;
			else if ((a & LFlags.BOTTOM) > 0)
				anchor = GridBagConstraints.SOUTHWEST;
			else {
				anchor = GridBagConstraints.WEST;
				if ((a & LFlags.MIDDLE) == 0)
					fill = GridBagConstraints.VERTICAL;
			}
		} else if ((a & LFlags.RIGHT) > 0) {
			if ((a & LFlags.TOP) > 0)
				anchor = GridBagConstraints.NORTHEAST;
			else if ((a & LFlags.BOTTOM) > 0)
				anchor = GridBagConstraints.SOUTHEAST;
			else {
				anchor = GridBagConstraints.EAST;
				if ((a & LFlags.MIDDLE) == 0)
					fill = GridBagConstraints.VERTICAL;
			}
		} else if ((a & LFlags.CENTER) > 0) {
			if ((a & LFlags.TOP) > 0)
				anchor = GridBagConstraints.NORTH;
			else if ((a & LFlags.BOTTOM) > 0)
				anchor = GridBagConstraints.SOUTH;
			else {
				anchor = GridBagConstraints.CENTER;
				if ((a & LFlags.MIDDLE) == 0)
					fill = GridBagConstraints.VERTICAL;
			}
		} else {
			fill = GridBagConstraints.HORIZONTAL;
			if ((a & LFlags.TOP) > 0)
				anchor = GridBagConstraints.NORTH;
			else if ((a & LFlags.BOTTOM) > 0)
				anchor = GridBagConstraints.SOUTH;
			else {
				anchor = GridBagConstraints.CENTER;
				if ((a & LFlags.MIDDLE) == 0)
					fill = GridBagConstraints.BOTH;
			}
		}
	}

	public void storePreferredSize(Dimension d) {
		if (width != -1)
			d.width = width;
		if (height != -1)
			d.height = height;
		if (minWidth > 0)
			d.width = Math.max(d.width, minWidth);
		if (minHeight > 0)
			d.height = Math.max(d.height, minHeight);
	}

	public void storeMinimumSize(Dimension d) {
		if (minWidth == -1)
			d.width = 0;
		else
			d.width = Math.max(d.width, minWidth);
		if (minHeight == -1)
			d.height = 0;
		else
			d.height = Math.max(d.height, minHeight);
	}

}
