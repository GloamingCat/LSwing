package lui.container;

import java.awt.*;

import javax.swing.*;

import lui.base.data.LPoint;
import lui.layout.LCellData;
import lui.layout.LLayedCell;

public class LScrollPanel extends JScrollPane implements LContainer, LLayedCell {

	protected LCellData gridData;

	protected JComponent content;

	/**
	 * Internal, no layout.
	 */
	LScrollPanel(JComponent parent) {
		super();
		parent.add(this);
		content = new JPanel();
		content.setLayout(new GridLayout());
		setViewportView(content);
	}

	public LScrollPanel(LContainer parent, boolean large) {
		this(parent.getContentComposite());
		if (large) {
			setAutoscrolls(true);
			getCellData().setExpand(true, true);
		}
	}

	public LScrollPanel(LContainer parent) {
		this(parent, false);
	}

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
	public JComponent getContentComposite() {
		return content;
	}

	@Override
	public JComponent getTopComposite() {
		return this;
	}

	@Override
	public LCellData getCellData() {
		if (gridData == null)
			gridData = new LCellData();
		return gridData;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		if (gridData != null) {
			if (gridData.width != -1)
				d.width = Math.max(d.width, gridData.width);
			if (gridData.height != -1)
				d.height = Math.max(d.height, gridData.height);
		}
		return d;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		if (gridData != null) {
			d.width = Math.max(d.width, gridData.minWidth);
			d.height = Math.max(d.height, gridData.minHeight);
		}
		return d;
	}
	//endregion

}