package lui.container;

import java.awt.*;

import javax.swing.*;

import lui.base.data.LPoint;
import lui.layout.LCellData;
import lui.layout.LLayedCell;

public class LScrollPanel extends JScrollPane implements LContainer, LLayedCell {

	protected LCellData gridData;

	protected JComponent content;

	//////////////////////////////////////////////////
	//region Constructors

	/** Internal, no layout.
	 */
	protected LScrollPanel(JComponent parent) {
		super();
		parent.add(this);
		content = new JPanel();
		content.setLayout(new GridLayout());
		setViewportView(content);
		setAutoscrolls(true);
	}

	public LScrollPanel(LContainer parent) {
		this(parent.getContentComposite());
	}

	//endregion

	//////////////////////////////////////////////////
	//region Scroll
	
	public void setContentSize(LPoint size) {
		setContentSize(size.x, size.y);
	}

	public void setContentSize(int width, int height) {
		content.setPreferredSize(new Dimension(width, height));
	}

	//endregion

	//////////////////////////////////////////////////
	//region Container Methods

	@Override
	public JComponent getTopComposite() {
		return this;
	}

	@Override
	public JComponent getContentComposite() {
		return content;
	}

	@Override
	public LCellData getCellData() {
		if (gridData == null)
			gridData = new LCellData();
		return gridData;
	}

	@Override
	public Dimension getPreferredSize() {
		return content.getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		if (gridData != null)
			gridData.storeMinimumSize(d);
		return d;
	}

	//endregion

}