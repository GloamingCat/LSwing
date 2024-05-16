package lui.container;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import lui.layout.LCellData;
import lui.layout.LLayedCell;

public class LFlexPanel extends JSplitPane implements LContainer, LLayedCell {

	private LCellData gridData;

	//////////////////////////////////////////////////
	//region Constructors

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

	//endregion

	//////////////////////////////////////////////////
	//region Interfaces

	public void setWeights(float first, float second) {
		Dimension pl = leftComponent.getPreferredSize();
		Dimension ml = leftComponent.getMinimumSize();
		Dimension pr = rightComponent.getPreferredSize();
		Dimension mr = rightComponent.getMinimumSize();
		if (getOrientation() == VERTICAL_SPLIT) {
			pl.height = pr.height = ml.height = mr.height = 0;
		} else {
			pl.width = pr.width = ml.width = mr.width = 0;
		}
		leftComponent.setMinimumSize(ml);
		rightComponent.setMinimumSize(mr);
		leftComponent.setPreferredSize(pl);
		rightComponent.setPreferredSize(pr);
		setResizeWeight(first / (first + second));
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
			gridData.storeMinimumSize(d);
		return d;
	}

	//endregion

}
