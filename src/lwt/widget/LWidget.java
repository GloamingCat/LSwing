package lwt.widget;

import lwt.container.LContainer;
import lwt.container.LPanel;
import lwt.editor.LPopupMenu;
import lwt.LMenuInterface;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import lbase.LVocab;
import lbase.action.LAction;
import lbase.gui.LPastable;

public abstract class LWidget extends LPanel implements LPastable {

	protected LMenuInterface menuInterface;

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

	public void setMenuInterface(LMenuInterface mi) {
		menuInterface = mi;
	}

	public void newAction(LAction action) {
		if (menuInterface != null) {
			menuInterface.actionStack.newAction(action);
		}
	}

	public void setHoverText(String text) {
		setToolTipText(text);
	}
	
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

	public void setCopyEnabled(lbase.gui.LMenu menu, boolean value) {
		menu.setMenuButton(value, LVocab.instance.COPY, "copy", (d) -> onCopyButton(menu), "Ctrl+&C");
	}

	public void setPasteEnabled(lbase.gui.LMenu menu, boolean value) {
		menu.setMenuButton(value, LVocab.instance.PASTE, "paste", (d) -> onPasteButton(menu), "Ctrl+&V");
	}
	
	//endregion

}
