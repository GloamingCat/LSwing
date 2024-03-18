package lwt.container;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import lwt.LFlags;
import lwt.graphics.LPoint;

public class LScrollPanel extends JScrollPane implements LContainer {

	private static final long serialVersionUID = 1L;
	private GridBagConstraints gridData;
	private int minWidth, minHeight;

	/**
	 * Internal, no layout.
	 */
	LScrollPanel(JComponent parent) {
		super();
		parent.add(this);
	}

	/** Fill layout with no margin.
	 * @param parent
	 * @param horizontal
	 */
	public LScrollPanel(LContainer parent, boolean large) {
		this(parent.getContentComposite());
		if (large) {
			setAutoscrolls(true);
			setExpand(true, true);
		}
	}

	/** No layout.
	 * @param parent
	 */
	public LScrollPanel(LContainer parent) {
		this(parent, false);
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
	
	public void refreshSize(LPoint size) {
		//refreshSize(size.x, size.y);
	}
	
	public void refreshSize(int width, int height) {
		//setMinSize(width, height);
		refreshLayout();
	}
	
	// }}

	//////////////////////////////////////////////////
	// {{ Container Methods

	public Component add(Component c) {
		super.add(c);
		setViewportView(c);
		return c;
	}

	@Override
	public JComponent getContentComposite() {
		return this;
	}

	// }}

}
