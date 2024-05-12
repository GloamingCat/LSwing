package lui.widget;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import lui.base.LFlags;
import lui.base.LPrefs;
import lui.base.gui.LMenu;
import lui.container.LContainer;

public class LLabel extends LWidget {

	JLabel label;

	//////////////////////////////////////////////////
	//region Basic Constructors

	public LLabel(LContainer parent, int hFill, int vFill) {
		this(parent, LFlags.FILL);
		setEnabled(false);
		getCellData().setSpread(hFill, vFill);
	}

	public LLabel(LContainer parent, int style) {
		super(parent);
		setFillLayout(true);
		setMargins(0, LPrefs.LABELPADDING);
		setAlignment(style);
		getCellData().setExpand((style & LFlags.EXPAND) > 0, false);
		getCellData().setAlignment(LFlags.FILL);
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

	//////////////////////////////////////////////////
	//region Label Text

	public void setAlignment(int a) {
		if ((a & LFlags.BOTTOM) > 0) {
			label.setVerticalAlignment(JLabel.BOTTOM);
		} else if ((a & LFlags.MIDDLE) > 0){
			label.setVerticalAlignment(JLabel.CENTER);
		} else {
			label.setVerticalAlignment(JLabel.TOP);
		}
		if ((a & LFlags.RIGHT) > 0) {
			label.setHorizontalAlignment(JLabel.RIGHT);
		} else if ((a & LFlags.CENTER) > 0) {
			label.setHorizontalAlignment(JLabel.CENTER);
		} else {
			label.setHorizontalAlignment(JLabel.LEFT);
		}
	}

	public void setText(String text) {
		label.setText(text);
		refreshLayout();
	}
	
	@Override
	public void setHoverText(String text) {
		label.setToolTipText("<html>" + text.replace("\n", "<br>") + "</html>");
	}
	
	@Override
	public String toString() {
		return getClass().getName() + " " + label.getText();
	}

	//endregion

	//////////////////////////////////////////////////
	//region Menu

	@Override
	public void setComponentPopupMenu(JPopupMenu menu) {
		super.setComponentPopupMenu(menu);
		label.setComponentPopupMenu(menu);
	}

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
