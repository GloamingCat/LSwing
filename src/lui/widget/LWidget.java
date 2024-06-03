package lui.widget;

import lui.base.LMenuInterface;
import lui.base.LPrefs;
import lui.container.LContainer;
import lui.container.LPanel;

import java.awt.*;
import lui.base.action.LAction;

public abstract class LWidget extends LPanel {

	protected LMenuInterface menuInterface;

	//////////////////////////////////////////////////
	//region Constructors

	public LWidget(LContainer parent, int style) {
		super(parent);
		setLayout(new GridLayout(1, 1));
		createContent(style);
	}

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new lwt.container.LPanel(new LShell(800, 600), true)
	 */
	public LWidget(LContainer parent) {
		this(parent, 0);
	}
	
	protected abstract void createContent(int flags);

	//endregion

	//////////////////////////////////////////////////
	//region Action

	public LMenuInterface getMenuInterface() {
		return menuInterface;
	}

	public void setMenuInterface(LMenuInterface mi) {
		menuInterface = mi;
	}

	public void newAction(LAction action) {
		if (menuInterface != null) {
			menuInterface.actionStack.newAction(action);
		}
	}

	//endregion

	//////////////////////////////////////////////////
	//region Properties

	@Override
	public Dimension getMinimumSize() {
		// Default height for label/spinner/combo/text field/check box
		// For tree/list/text box, override to expand
		// For grid/image/toggle, override to consider images
		// For button, override to change width
		Dimension size = super.getMinimumSize();
		size.height = LPrefs.WIDGETHEIGHT;
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		// Default height for label/spinner/combo/text field/check box
		// For tree/list/text box, override to expand
		// For grid/image/toggle, override to consider images
		// For button, override to change width
		Dimension size = super.getPreferredSize();
		size.height = LPrefs.WIDGETHEIGHT;
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

	//endregion

}
