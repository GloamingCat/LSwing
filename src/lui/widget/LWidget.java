package lui.widget;

import lui.base.LPrefs;
import lui.base.gui.LMenu;
import lui.container.LContainer;
import lui.container.LPanel;
import lui.editor.LPopupMenu;
import lui.LMenuInterface;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import lui.base.LVocab;
import lui.base.action.LAction;
import lui.base.gui.LPastable;

public abstract class LWidget extends LPanel implements LPastable {

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
	//region Menus
	
	private LPopupMenu addMenu(JComponent parent) {
		LPopupMenu menu = new LPopupMenu(parent);
		setCopyEnabled(menu, true);
		setPasteEnabled(menu, true);
		addFocusOnClick(parent);
		return menu;
	}
	
	private void addFocusOnClick(JComponent c) {
		LWidget widget = this;
		c.setEnabled(true);
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) { // Left button
					menuInterface.setFocusWidget(widget);
				}
			}
		});
	}
	
	public void addMenu() {
		addMenu(this);
	}
	
	public void addMenu(LContainer frame) {
		JComponent c = frame.getContentComposite();
		JPopupMenu menu = getComponentPopupMenu();
		if (menu == null) {
			menu = addMenu(c);
			setComponentPopupMenu(menu);
			addFocusOnClick(this);
		} else if (c.getComponentPopupMenu() == null) {
			c.setComponentPopupMenu(menu);
			addFocusOnClick(c);
		}
	}
	
	public void addMenu(LWidget widget) {
		JPopupMenu menu = getComponentPopupMenu();
		if (menu == null) {
			menu = addMenu((JComponent) widget);
			setComponentPopupMenu(menu);
			addFocusOnClick(this);
		} else if (widget.getComponentPopupMenu() == null) {
			widget.setComponentPopupMenu(menu);
			addFocusOnClick(widget);
		}
	}

	public void setCopyEnabled(LMenu menu, boolean value) {
		menu.setMenuButton(value, LVocab.instance.COPY, "copy", (d) -> onCopyButton(menu), "Ctrl+&C");
	}

	public void setPasteEnabled(LMenu menu, boolean value) {
		menu.setMenuButton(value, LVocab.instance.PASTE, "paste", (d) -> onPasteButton(menu), "Ctrl+&V");
	}
	
	//endregion

	//////////////////////////////////////////////////
	//region Properties

	public void setMenuInterface(LMenuInterface mi) {
		menuInterface = mi;
	}

	public void newAction(LAction action) {
		if (menuInterface != null) {
			menuInterface.actionStack.newAction(action);
		}
	}

	public void setHoverText(String text) {
		setToolTipText("<html>" + text.replace("\n", "<br>") + "</html>");
	}

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
