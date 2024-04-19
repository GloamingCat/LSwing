package lui.container;

import lui.base.LPrefs;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
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
		lineBorder = new LineBorder(Color.GRAY, 1);
		titledBorder = new TitledBorder(lineBorder);
		titledBorder.setTitleColor(Color.GRAY);
		setBorder(titledBorder);
		setMargins(LPrefs.FRAMEMARGIN, LPrefs.FRAMEMARGIN);
	}

	/** No layout.
	 */
	public LFrame(LContainer parent, String title) {
		this(parent.getContentComposite());
		titledBorder.setTitle(title);
	}

	//////////////////////////////////////////////////
	//region Frame

	public void setTitle(String text) {
		titledBorder.setTitle(text);
		//setBorder(titledBorder);
	}

	public void setHoverText(String text) {
		setToolTipText(text);
	}
	
	@Override
	public void setMargins(int h, int v) {
		CompoundBorder border = new CompoundBorder(lineBorder, new EmptyBorder(v, h, v, h));
		titledBorder.setBorder(border);
		setBorder(titledBorder);
	}

	//endregion
	
}
