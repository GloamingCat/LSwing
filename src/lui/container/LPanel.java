package lui.container;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lui.base.LFlags;
import lui.base.event.LMouseEvent;
import lui.base.event.listener.LMouseListener;
import lui.layout.LLayedCell;
import lui.layout.LLayedContainer;
import lui.layout.LCellData;

public class LPanel extends JPanel implements LLayedCell, LLayedContainer {

	private ArrayList<LMouseListener> mouseListeners = null;
	protected LCellData gridData;
	
	//////////////////////////////////////////////////
	//region Constructors

	/** No layout.
	 */
	public LPanel(LContainer parent) {
		this(parent.getContentComposite());
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (getLayout() instanceof GridBagLayout gbl)
					revalidate();
			}
		});
	}

	/** Internal, no layout.
	 */
	protected LPanel(JComponent parent) {
		super();
		parent.add(this);
	}

	/** Internal, no layout.
	 */
	protected LPanel(JFrame parent) {
		super();
		parent.add(this);
	}

	/** Internal, no layout.
	 */
	protected LPanel(JDialog parent) {
		super();
		parent.add(this);
	}

	// For ambiguity.
	public LPanel(LPanel parent) {
		this((LContainer) parent);
	}

	// For ambiguity.
	public LPanel(LFrame parent) {
		this((LContainer) parent);
	}

	// For ambiguity.
	public LPanel(LScrollPanel parent) {
		this((LContainer) parent);
	}

	// For ambiguity.
	public LPanel(LFlexPanel parent) {
		this((LContainer) parent);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Listeners
	
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
				LMouseEvent e = createMouseEvent(e0, false, false);
				for (LMouseListener l : mouseListeners)
					l.onMouseChange(e);
			}
			@Override
			public void mouseReleased(MouseEvent e0) {
				LMouseEvent e = createMouseEvent(e0, true, false);
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
			public void mouseDragged(MouseEvent e0) {
				LMouseEvent e = createMouseEvent(e0, false, false);
				for (LMouseListener l : mouseListeners)
					l.onMouseChange(e);
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
		else if (e.getButton() == MouseEvent.BUTTON3)
			button = LFlags.RIGHT;
		else if (e.getButton() == MouseEvent.BUTTON2)
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
		
	//endregion
	
	//////////////////////////////////////////////////
	//region Interfaces

	public void setHoverText(String text) {
		setToolTipText("<html>" + text.replace("\n", "<br>") + "</html>");
	}

	@Override
	public LCellData getCellData() {
		if (gridData == null)
			gridData = new LCellData();
		return gridData;
	}

	@Override
	public JComponent getContentComposite() {
		return this;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		Dimension d2 = getMinimumSize();
		d.width = Math.max(d.width, d2.width);
		d.height = Math.max(d.height, d2.height);
		if (gridData != null)
			gridData.storePreferredSize(d);
		return d;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		if (gridData != null)
			gridData.storeMinimumSize(d);
		return d;
	}

	@Override
	public void doLayout() {
		refreshLayoutData();
		super.doLayout();
	}

	//endregion

}
