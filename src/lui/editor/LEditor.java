package lui.editor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import lui.base.gui.LMenu;
import lui.container.LContainer;
import lui.container.LPanel;
import lui.container.LView;
import lui.base.LVocab;
import lui.base.gui.LPastable;
import lui.widget.LButton;
import lui.widget.LWidget;

public abstract class LEditor extends LView implements LPastable {

	//////////////////////////////////////////////////
	//region Constructors

	public LEditor(LContainer parent, boolean doubleBuffered) {
		super(parent, doubleBuffered);
	}

	public LEditor(JComponent parent, boolean doubleBuffered) {
		super(parent, doubleBuffered);
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Object
	
	public abstract void setObject(Object object);
	public abstract void saveObjectValues();

	//endregion
	
	//////////////////////////////////////////////////
	//region Menu
	
	private void addHeaderButtons(LContainer parent) {
		LButton copyButton = new LButton(parent, LVocab.instance.COPY);
		copyButton.onClick = event -> onCopyButton(null);
		LButton pasteButton = new LButton(parent, LVocab.instance.PASTE);
		pasteButton.onClick = event -> onPasteButton(null);
	}
	
	private LPopupMenu addMenu(JComponent parent) {
		LPopupMenu menu = new LPopupMenu(parent);
		setCopyEnabled(menu, true);
		setPasteEnabled(menu, true);
		addFocusOnClick(parent);
		return menu;
	}
	
	private void addFocusOnClick(JComponent c) {
		LEditor editor = this;
		c.setEnabled(true);
		c.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) { // Left button
					if (getMenuInterface() != null)
						getMenuInterface().setFocusEditor(editor);
				}
			}
		});
	}
	
	public void addMenu() {
		addMenu(getContentComposite());
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
	
	public void addHeader(LContainer parent) {
		if (parent == null)
			parent = this;
		LPanel header = new LPanel(parent); 
		header.setSequentialLayout(true);
		addHeaderButtons(header);
	}
	
	public void setCopyEnabled(LMenu menu, boolean value) {
		menu.setMenuButton(value, LVocab.instance.COPY, "copy", (d) -> onCopyButton(menu), "Ctrl+&C");
	}

	public void setPasteEnabled(LMenu menu, boolean value) {
		menu.setMenuButton(value, LVocab.instance.PASTE, "paste", (d) -> onPasteButton(menu), "Ctrl+&V");
	}
	
	//endregion
	
}
