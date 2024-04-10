package lui.layout;

import lui.base.LFlags;
import lui.base.gui.LLayoutData;

import java.awt.*;

public class LCellData extends LLayoutData {

    public int minWidth, minHeight;
    public int width = -1, height = -1;
	private int anchor = GridBagConstraints.CENTER;
	private int fill = GridBagConstraints.NONE;

    public GridBagConstraints getGridBagConstraints(int i, int cols, int hSpacing, int vSpacing) {
		return new GridBagConstraints(i % cols, i / cols,
			hSpread, vSpread,
			hExpand ? 1 : 0, vExpand ? 1 : 0,
			anchor, fill,
			new Insets(vSpacing, hSpacing, vSpacing, hSpacing),
			0, 0);
	}

	public void setExpand(boolean h, boolean v) {
		super.setExpand(h, v);
		if (h) {
			if (fill == GridBagConstraints.VERTICAL)
				fill = GridBagConstraints.BOTH;
			else
				fill = GridBagConstraints.HORIZONTAL;
		}
		if (v) {
			if (fill == GridBagConstraints.HORIZONTAL)
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

}
