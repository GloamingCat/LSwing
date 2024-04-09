package lwt.container;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class LFrame extends LPanel {

	/**
	 * Internal, no layout.
	 */
	LFrame(JComponent parent) {
		super(parent);
	}
	
	/** No layout.
	 */
	public LFrame(LContainer parent, String title) {
		this(parent.getContentComposite());
		setBorder(new TitledBorder(new EmptyBorder(5, 5, 5, 5), title));
	}

	//////////////////////////////////////////////////
	//region Frame

	public void setTitle(String text) {
		TitledBorder border = (TitledBorder) getBorder();
		border.setTitle(text);
		setBorder(border);
	}

	public void setHoverText(String text) {
		setToolTipText(text);
	}
	
	@Override
	public void setMargins(int h, int v) {
		TitledBorder border = (TitledBorder) getBorder();
		border.setBorder(new EmptyBorder(v, h, v, h));
		setBorder(border);
	}

	//endregion
	
}
