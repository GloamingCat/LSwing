package lui.container;

import lui.base.LPrefs;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class LFrame extends LPanel {

	private final LineBorder lineBorder;
	private final TitledBorder titledBorder;

	/**
	 * Internal, no layout.
	 */
	LFrame(JComponent parent) {
		super(parent);
		Color color = UIManager.getColor("textText");
		lineBorder = new LineBorder(color, 1);
		titledBorder = new TitledBorder("");
		titledBorder.setTitleColor(color);
		titledBorder.setTitlePosition(TitledBorder.TOP);
		titledBorder.setTitleJustification(TitledBorder.CENTER);
		setMargins(LPrefs.FRAMEMARGIN, LPrefs.FRAMEMARGIN);
	}

	public LFrame(LContainer parent, String title) {
		this(parent.getContentComposite());
		titledBorder.setTitle(title);
	}

	public LFrame(LContainer parent, String title, String tooltip) {
		this(parent, title);
		setHoverText(tooltip);
	}

	//////////////////////////////////////////////////
	//region Frame

	public void setTitle(String text) {
		titledBorder.setTitle(text);
	}
	
	@Override
	public void setMargins(int h, int v) {
		super.setMargins(h, v);
		CompoundBorder border = new CompoundBorder(lineBorder, getBorder());
		titledBorder.setBorder(border);
		setBorder(titledBorder);
	}

	//endregion
	
}
