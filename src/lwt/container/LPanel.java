package lwt.container;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import lwt.LFlags;
import lbase.event.LMouseEvent;
import lbase.event.listener.LMouseListener;
import lwt.graphics.LPoint;

public class LPanel extends JPanel implements LContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GridBagConstraints gridData;
	private int minWidth, minHeight;
	private int spacingH, spacingV;

	private ArrayList<LMouseListener> mouseListeners = null;
	
	//////////////////////////////////////////////////
	// {{ Constructors
	
	/**
	 * Internal, no layout.
	 */
	LPanel(JComponent parent) {
		super();
		parent.add(this);
	}
	
	/**
	 * Internal, no layout.
	 */
	protected LPanel(JFrame parent) {
		super();
		parent.add(this);
	}
	
	/**
	 * Internal, no layout.
	 */
	protected LPanel(JDialog parent) {
		super();
		parent.add(this);
	}
	
	/** No layout.
	 * @param parent
	 */
	public LPanel(LContainer parent) {
		this(parent.getContentComposite());
	}

	// }}
	
	//////////////////////////////////////////////////
	// {{ Inner Layout

	/**
	 * Fill layout (spacing = 0).
	 */
	public void setFillLayout(boolean horizontal) {
		GridLayout gl = horizontal ? 
				new GridLayout(0, 1) : new GridLayout(1, 0);
		gl.setHgap(0);
		gl.setVgap(0);
		setLayout(gl);
		super.setBorder(new EmptyBorder(0, 0, 0, 0));
	}
	
	/** 
	 * Grid layout (spacing = 5).
	 */
	public void setGridLayout(int columns) {
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[columns];
		spacingH = 5;
		spacingV = 5;
		setLayout(gbl);
	}
	
	/*
	 * Column/row layout (spacing = 5).
	 */
	public void setSequentialLayout(boolean horizontal) {
		if (horizontal) {
			FlowLayout fl = new FlowLayout();
			fl.setHgap(5);
			fl.setVgap(5);
			setLayout(fl);
		} else {
			setGridLayout(1);
		}
	}
	
	public void setMargins(int h, int v) {
		super.setBorder(new EmptyBorder(v, h, v, h));
		doLayout();
	}
	
	public void setSpacing(int h, int v) {
		spacingH = h;
		spacingV = v;
		LayoutManager l = getLayout();
		if (l instanceof GridLayout) {
			GridLayout gl = (GridLayout) l;
			gl.setHgap(h);
			gl.setVgap(v);
		} else if (l instanceof FlowLayout) {
			FlowLayout fl = (FlowLayout) l;
			fl.setHgap(h);
			fl.setVgap(v);
		}
		refreshLayout();
	}
	
	public void setSpacing(int s) {
		setSpacing(s, s);
	}
	
	// }}
	
	//////////////////////////////////////////////////
	// {{ Parent Layout
	
	private void initGridData() {
		if (gridData != null)
			return;
		GridBagLayout gbl = (GridBagLayout) getParent().getLayout();
		int n = getParent().getComponentCount() - 1;
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
		gridData.fill = 0;
		if ((a & LFlags.LEFT) > 0) {
			if ((a & LFlags.TOP) > 0)
				gridData.anchor = GridBagConstraints.NORTHWEST;
			else if ((a & LFlags.BOTTOM) > 0)
				gridData.anchor = GridBagConstraints.SOUTHWEST;
			else {
				gridData.anchor = GridBagConstraints.WEST;
				if ((a & LFlags.CENTER) == 0)
					gridData.fill = GridBagConstraints.VERTICAL;
			}
		} else if ((a & LFlags.RIGHT) > 0) {
			if ((a & LFlags.TOP) > 0)
				gridData.anchor = GridBagConstraints.NORTHEAST;
			else if ((a & LFlags.BOTTOM) > 0)
				gridData.anchor = GridBagConstraints.SOUTHEAST;
			else {
				gridData.anchor = GridBagConstraints.EAST;
				if ((a & LFlags.CENTER) == 0)
					gridData.fill = GridBagConstraints.VERTICAL;
			}
		} else if ((a & LFlags.MIDDLE) > 0) {
			if ((a & LFlags.TOP) > 0)
				gridData.anchor = GridBagConstraints.NORTH;
			else if ((a & LFlags.BOTTOM) > 0)
				gridData.anchor = GridBagConstraints.SOUTH;
			else {
				gridData.anchor = GridBagConstraints.CENTER;
				if ((a & LFlags.CENTER) == 0)
					gridData.fill = GridBagConstraints.VERTICAL;
			}
		} else {
			gridData.fill = GridBagConstraints.HORIZONTAL;
			if ((a & LFlags.TOP) > 0)
				gridData.anchor = GridBagConstraints.NORTH;
			else if ((a & LFlags.BOTTOM) > 0)
				gridData.anchor = GridBagConstraints.SOUTH;
			else {
				gridData.anchor = GridBagConstraints.CENTER;
				if ((a & LFlags.CENTER) == 0)
					gridData.fill = GridBagConstraints.BOTH;
			}
		}
		((GridBagLayout) getParent().getLayout()).setConstraints(this, gridData);
	}

	public void setExpand(boolean h, boolean v) {
		initGridData();
		gridData.weightx = h ? 1 : 0;
		gridData.weighty = v ? 1 : 0;
		if (h) {
			if (gridData.fill == GridBagConstraints.VERTICAL) 
				gridData.fill = GridBagConstraints.BOTH;
			else
				gridData.fill = GridBagConstraints.HORIZONTAL;
		}
		if (v) {
			if (gridData.fill == GridBagConstraints.HORIZONTAL) 
				gridData.fill = GridBagConstraints.BOTH;
			else
				gridData.fill = GridBagConstraints.VERTICAL;
		}
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
	// {{ Listeners
	
	public void addMouseListener(LMouseListener l) {
		if (mouseListeners == null) {
			mouseListeners = new ArrayList<>();
			setMouseListeners();
		}
		mouseListeners.add(l);
	}
	
	public void removeMouseListener(LMouseListener l) {
		mouseListeners.remove(l);
	}
	
	private void setMouseListeners() {
		super.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e0) {
				// Double click
				if (e0.getClickCount() <= 1)
					return;
				LMouseEvent e = createMouseEvent(e0, true, true);
				for (LMouseListener l : mouseListeners)
					l.onMouseChange(e);
			}
			@Override
			public void mousePressed(MouseEvent e0) {
				LMouseEvent e = createMouseEvent(e0, true, false);
				for (LMouseListener l : mouseListeners)
					l.onMouseChange(e);
			}
			@Override
			public void mouseReleased(MouseEvent e0) {
				LMouseEvent e = createMouseEvent(e0, false, false);
				for (LMouseListener l : mouseListeners)
					l.onMouseChange(e);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		super.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
			}
			@Override
			public void mouseMoved(MouseEvent e0) {
				LMouseEvent e = createMouseEvent(e0, false, false);
				for (LMouseListener l : mouseListeners)
					l.onMouseChange(e);
			}
		});
	}
	
	public LMouseEvent createMouseEvent(MouseEvent e, boolean release, boolean repeat) {
		int x = e.getX();
		int y = e.getY();
		int button = 0;
		if (e.getButton() == MouseEvent.BUTTON1)
			button = LFlags.LEFT;
		else if (e.getButton() == MouseEvent.BUTTON2)
			button = LFlags.RIGHT;
		else if (e.getButton() == MouseEvent.BUTTON3)
			button = LFlags.MIDDLE;
		int type;
		if (release) {
			if (repeat)
				type = LFlags.DOUBLEPRESS;
			else
				type = LFlags.RELEASE;
		} else {
			if (repeat)
				type = LFlags.REPEATPRESS;
			else
				type = LFlags.PRESS;
		}
		return new LMouseEvent(button, x, y, type);
	}
		
	// }}
	
	//////////////////////////////////////////////////
	// {{ Size
	
	public LPoint getCurrentSize() {
		Dimension d = getSize();
		return new LPoint(d.width, d.height);
	}
	
	public void setCurrentSize(LPoint size) {
		setPreferredSize(new Dimension(size.x, size.y));
		setSize(size.x, size.y);
	}
	
	public void setCurrentSize(int x, int y) {
		setPreferredSize(new Dimension(x, y));
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
	// {{ User Data

	public Object getData() {
		return getClientProperty("");
	}
	
	public Object getData(String key) {
		return getClientProperty(key);
	}
	
	public void setData(String key, Object data) {
		putClientProperty(key, data);
	}
	
	public void setData(Object data) {
		putClientProperty("", data);
	}
	
	// }}
	
	//////////////////////////////////////////////////
	// {{ Container Methods

	@Override
	public JComponent getContentComposite() {
		return this;
	}

	@Override
	public void refreshLayout() {
		LayoutManager l = getLayout();
		if (l instanceof GridBagLayout) {
			GridBagLayout gbl = (GridBagLayout) l;
			int n = getComponentCount();
			int cols = gbl.columnWeights.length;
			for (var c : getComponents()) {
				GridBagConstraints gbc = gbl.getConstraints(c);
				gbc.insets.bottom = gbc.insets.top = spacingV;
				gbc.insets.left = gbc.insets.right = spacingH;
				gbc.gridx = n * cols;
				gbc.gridy = n / cols;
				gbl.setConstraints(c, gbc);
			}			
		}
		doLayout();
	}
	
	// }}

}
