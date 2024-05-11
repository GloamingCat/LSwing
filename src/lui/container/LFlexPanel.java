package lui.container;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import lui.base.data.LPoint;
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

	public void setWeights(float first, float second) {
		Dimension minimumSize = new Dimension(0, 0);
		leftComponent.setMinimumSize(minimumSize);
		rightComponent.setMinimumSize(minimumSize);
		leftComponent.setPreferredSize(minimumSize);
		rightComponent.setPreferredSize(minimumSize);
		setResizeWeight(first / (first + second));
		setDividerLocation(first / (first + second));
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
		if (gridData != null)
			gridData.storePreferredSize(d);
		return d;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension d = super.getMinimumSize();
		if (gridData != null)
			gridData.storeMinimumSize(d, super.getPreferredSize());
		return d;
	}

	//endregion

}
