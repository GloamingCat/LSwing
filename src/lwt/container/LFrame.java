package lwt.container;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class LFrame extends LPanel implements LContainer {

	private static final long serialVersionUID = 1L;

	/**
	 * Internal, no layout.
	 */
	LFrame(JComponent parent) {
		super(parent);
	}
	
	/** No layout.
	 * @param parent
	 */
	public LFrame(LContainer parent, String title) {
		this(parent.getContentComposite());
		setBorder(new TitledBorder(new EmptyBorder(5, 5, 5, 5), title));
	}
	
	public void setTitle(String text) {
		TitledBorder border = (TitledBorder) getBorder();
		border.setTitle(text);
		setBorder(border);
	}
	
	@Override
	public void setMargins(int h, int v) {
		TitledBorder border = (TitledBorder) getBorder();
		border.setBorder(new EmptyBorder(v, h, v, h));
		setBorder(border);
		doLayout();
	}
	
}
