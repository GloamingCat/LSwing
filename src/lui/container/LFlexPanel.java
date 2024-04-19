package lui.container;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import lui.layout.LCellData;
import lui.layout.LLayedCell;

public class LFlexPanel extends JSplitPane implements LContainer, LLayedCell {

	private LCellData gridData;
	
	 LFlexPanel(JComponent parent, int dir) {
		super(dir);
		if (parent != null)
			parent.add(this);
	}

	/** Fill layout.
	 */
	public LFlexPanel(LContainer parent, boolean horizontal) {
		this(parent.getContentComposite(), horizontal ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT);
	}

	/** Fill horizontal layout.
	 */
	public LFlexPanel(LContainer parent) {
		this(parent, true);
	}

	//////////////////////////////////////////////////
	//region Interfaces



	//////////////////////////////////////////////////
	//region Interfaces

	public void setWeights(float first, float second) {
		setResizeWeight(first / (first + second));
		Dimension minimumSize = new Dimension(0, 0);
		leftComponent.setMinimumSize(minimumSize);
		rightComponent.setMinimumSize(minimumSize);
	}
	
	@Override
	public JComponent getContentComposite() {
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
