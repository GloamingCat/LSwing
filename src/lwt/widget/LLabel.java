package lwt.widget;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import lbase.gui.LMenu;
import lwt.LFlags;
import lwt.container.LContainer;

public class LLabel extends LWidget {
	private static final long serialVersionUID = 1L;

	private JLabel label;
	
	LLabel (LContainer parent, int style) {
		super(parent);
	}
	
	LLabel(LContainer parent, int style, int hfill, int vfill) {
		super(parent, 1);
		setAlignment(0);
		setEnabled(false);
	}

	public LLabel(LContainer parent, int style, String text, int columns) {
		this(parent, 0);
		label.setText(text);
		setMargins(0, 3);
		setSpread(columns, 1);
		setExpand((style & LFlags.EXPAND) > 0, false);
		int alingment = 0;
		if ((style & LFlags.TOP) > 0) {
			alingment = LFlags.LEFT & LFlags.TOP;
		} else if ((style & LFlags.BOTTOM) > 0) {
			alingment = LFlags.LEFT & LFlags.BOTTOM;
		} else if ((style & LFlags.RIGHT) > 0) {
			alingment = LFlags.RIGHT & LFlags.CENTER;		
		} else if ((style & LFlags.CENTER) > 0) {
			label.setHorizontalTextPosition(JLabel.CENTER);
		}
		setAlignment(alingment);
	}

	public LLabel(LContainer parent, int style, String text) {
		this(parent, style, text, 1);
	}
	
	public LLabel(LContainer parent, String text, int columns) {
		this(parent, 0, text, columns);
	}
	
	public LLabel(LContainer parent, String text) {
		this(parent, 0, text, 1);
	}
	
	public LLabel(LContainer parent, String text, String tooltip) {
		this(parent, 0, text, 1);
		setHoverText(tooltip);
	}

	public LLabel(LContainer parent, int hfill, int vfill) {
		this(parent, 1, hfill, vfill);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public LLabel(LContainer parent) {
		this(parent, "Text");
	}
	
	public LLabel(LContainer parent, int style, String text, String tooltip) {
		this(parent, style, text);
		setHoverText(tooltip);
	}

	@Override
	protected void createContent(int flags) {
		if (flags == 1)
			return;
		label = new JLabel();
		add(label);
	}

	public void setText(String text) {
		label.setText(text);
		refreshLayout();
	}
	
	@Override
	public void setHoverText(String text) {
		label.setToolTipText(text);
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " " + label.getText();
	}
	
	@Override
	public void setComponentPopupMenu(JPopupMenu menu) {
		super.setComponentPopupMenu(menu);
		label.setComponentPopupMenu(menu);
	}

	//////////////////////////////////////////////////
	// {{ Menu

	@Override
	public void onCopyButton(LMenu menu) {}

	@Override
	public void onPasteButton(LMenu menu) {}

	@Override
	public boolean canDecode(String str) {
		return false;
	}

	// }}

}
