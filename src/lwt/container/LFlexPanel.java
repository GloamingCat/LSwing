package lwt.container;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import lwt.LFlags;
import lwt.graphics.LPoint;

public class LFlexPanel extends JSplitPane implements LContainer {

	private static final long serialVersionUID = 1L;
	private GridBagConstraints gridData;
	private int minWidth, minHeight;
	
	 LFlexPanel(JComponent parent, int dir) {
		super(dir);
		if (parent != null)
			parent.add(this);
	}

	/**
	 * Fill layout.
	 */
	public LFlexPanel(LContainer parent, boolean horizontal) {
		this(parent.getContentComposite(), horizontal ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
	}

	/**
	 * Fill horizontal layout.
	 */
	public LFlexPanel(LContainer parent) {
		this(parent, true);
	}

	//////////////////////////////////////////////////
	// {{ Parent Layout
	
	private void initGridData() {
		GridBagLayout gbl = (GridBagLayout) getParent().getLayout();
		int n = getParent().getComponentCount();
		int cols = gbl.columnWeights.length;
		gridData = new GridBagConstraints(
				n % cols, n / cols, 
				1, 1, 
				0, 0, 
				GridBagConstraints.CENTER, 
				GridBagConstraints.NONE, 
				new Insets(0, 0, 0, 0),
				0, 0);
		gbl.setConstraints(this, gridData);
	}
	
	public void setSpread(int cols, int rows) {
		initGridData();
		gridData.gridwidth = cols;
		gridData.gridheight = rows;
		((GridBagLayout) getParent().getLayout()).setConstraints(this, gridData);
	}
	
	public void setAlignment(int a) {
		initGridData();
		if ((a & LFlags.LEFT) > 0) {
			if ((a & LFlags.TOP) > 0)
				gridData.anchor = GridBagConstraints.NORTHWEST;
			else if ((a & LFlags.BOTTOM) > 0)
				gridData.anchor = GridBagConstraints.SOUTHWEST;
			else if ((a & LFlags.CENTER) > 0)
				gridData.anchor = GridBagConstraints.WEST;
		} else if ((a & LFlags.RIGHT) > 0) {
			if ((a & LFlags.TOP) > 0)
				gridData.anchor = GridBagConstraints.NORTHEAST;
			else if ((a & LFlags.BOTTOM) > 0)
				gridData.anchor = GridBagConstraints.SOUTHEAST;
			else if ((a & LFlags.CENTER) > 0)
				gridData.anchor = GridBagConstraints.EAST;
		} else if ((a & LFlags.MIDDLE) > 0) {
			if ((a & LFlags.TOP) > 0)
				gridData.anchor = GridBagConstraints.NORTH;
			else if ((a & LFlags.BOTTOM) > 0)
				gridData.anchor = GridBagConstraints.SOUTH;
			else if ((a & LFlags.CENTER) > 0)
				gridData.anchor = GridBagConstraints.CENTER;
		}	
		((GridBagLayout) getParent().getLayout()).setConstraints(this, gridData);
	}

	public void setExpand(boolean h, boolean v) {
		initGridData();
		gridData.weightx = h ? 1 : 0;
		gridData.weighty = v ? 1 : 0;
		((GridBagLayout) getParent().getLayout()).setConstraints(this, gridData);
	}
	
	public void setMinimumWidth(int w) {
		minWidth = w;
		((LPanel) getParent()).refreshLayout();
	}
	
	public void setMinimumHeight(int h) {
		minHeight = h;
		((LPanel) getParent()).refreshLayout();
	}
	
	// }}
	
	//////////////////////////////////////////////////
	// {{ Size
	
	public LPoint getCurrentSize() {
		Dimension d = getSize();
		return new LPoint(d.width, d.height);
	}
	
	public void setCurrentSize(LPoint size) {
		setSize(size.x, size.y);
	}
	
	public void setCurrentSize(int x, int y) {
		setSize(x, y);
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		d.width = Math.max(d.width, minWidth);
		d.height = Math.max(d.height, minHeight);
		return d;
	}
	
	// }}

	//////////////////////////////////////////////////
	// {{ Container Methods
	
	public void setWeights(float first, float second) {
		setResizeWeight(second / (first + second));
		Dimension minimumSize = new Dimension(0, 0);
		leftComponent.setMinimumSize(minimumSize);
		rightComponent.setMinimumSize(minimumSize);
	}
	
	@Override
	public JComponent getContentComposite() {
		return this;
	}
	
	// }}

}
