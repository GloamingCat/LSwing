package lui.widget;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import lui.base.LPrefs;
import lui.base.gui.LMenu;
import lui.LFlags;
import lui.container.LContainer;

public class LLabel extends LWidget {

	private JLabel label;

	//////////////////////////////////////////////////
	//region Basic Constructors

	public LLabel(LContainer parent, int hfill, int vfill) {
		this(parent, 0);
		getCellData().setAlignment(0);
		setEnabled(false);
		getCellData().setSpread(hfill, vfill);
	}

	public LLabel(LContainer parent, int style) {
		super(parent);
		setMargins(0, 3);
		getCellData().setExpand((style & LFlags.EXPAND) > 0, false);
		getCellData().setMinimumSize(LPrefs.LABELWIDTH, LPrefs.WIDGETHEIGHT);
		int alignment = 0;
		if ((style & LFlags.TOP) > 0) {
			alignment = LFlags.LEFT | LFlags.TOP;
		} else if ((style & LFlags.BOTTOM) > 0) {
			alignment = LFlags.LEFT | LFlags.BOTTOM;
		} else if ((style & LFlags.RIGHT) > 0) {
			alignment = LFlags.RIGHT | LFlags.CENTER;
		} else if ((style & LFlags.CENTER) > 0) {
			label.setHorizontalTextPosition(JLabel.CENTER);
		}
		getCellData().setAlignment(alignment);
	}

	@Override
	protected void createContent(int flags) {
		if (flags == 1)
			return;
		label = new JLabel();
		add(label);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Text Constructors

	public LLabel(LContainer parent, String text) {
		this(parent, LFlags.LEFT | LFlags.TOP, text);
	}

	public LLabel(LContainer parent, int style, String text) {
		this(parent, style);
		label.setText(text);
	}

	public LLabel(LContainer parent, String text, String tooltip) {
		this(parent, LFlags.LEFT | LFlags.TOP, text);
		setHoverText(tooltip);
	}

	public LLabel(LContainer parent, int style, String text, String tooltip) {
		this(parent, style, text);
		setHoverText(tooltip);
	}

	//endregion

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
	//region Menu

	@Override
	public void onCopyButton(LMenu menu) {}

	@Override
	public void onPasteButton(LMenu menu) {}

	@Override
	public boolean canDecode(String str) {
		return false;
	}

	//endregion

}
