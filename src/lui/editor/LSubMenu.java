package lui.editor;

import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import lui.base.event.LSelectionEvent;
import lui.base.event.listener.LSelectionListener;
import lui.base.gui.LMenu;
import lui.LGlobals;

public class LSubMenu extends JMenu implements LMenu {
	
	public LSubMenu(JComponent c) {
		c.add(this);
	}
	
	public void setMenuButton(boolean value, String buttonName, String buttonKey, 
			Consumer<Object> action, String acc) {
		setMenuButton(this, value, buttonName, buttonKey, action, acc);
	}
	
	public void addSeparator() {
		super.addSeparator();
	}
	
	public void addSelectionListener(LSelectionListener listener) {
		addChangeListener(e -> listener.onSelect(new LSelectionEvent(null, null, -1, false)));
	}
	
	public LSubMenu addSubMenu(String name, String key) {
		return addSubMenu(this, name, key);
	}
	
	public void setButtonEnabled(String key, boolean value) {
		setButtonEnabled(this, key, value);
	}
	
	public void setMenuEnabled(String key, boolean value) {
		setMenuEnabled(this, key, value);
	}
	
	static LSubMenu addSubMenu(JComponent parent, String name, String key) {
		LSubMenu menu = new LSubMenu(parent);
		menu.setText(name);
		parent.putClientProperty(key, menu);
		return menu;
	}
	
	static void setMenuButton(JComponent c, boolean value, String buttonName, String buttonKey, 
			Consumer<Object> action, String acc) {
		if (value) {
			JMenuItem item = (JMenuItem) c.getClientProperty(buttonKey);
			if (item == null) {
				item = new JMenuItem();
				c.add(item);
				item.addActionListener(e -> action.accept(e.paramString()));
				c.putClientProperty(buttonKey, item);
				if (acc != null) {
					String[] keys = acc.split("\\+");
					item.setAccelerator(LGlobals.getAccelerator(keys));
				}
				item.setText(buttonName);
			}
		} else {
			if (c.getClientProperty(buttonKey) != null) {
				JMenuItem item = (JMenuItem) c.getClientProperty(buttonKey);
				c.remove(item);
			}
		}
	}
	
	static void setButtonEnabled(JComponent c, String key, boolean value) {
		if (c.getClientProperty(key) != null) {
			JMenuItem item = (JMenuItem) c.getClientProperty(key);
			item.setEnabled(value);
		}
	}
	
	static void setMenuEnabled(JComponent c, String key, boolean value) {
		if (c.getClientProperty(key) != null) {
			LSubMenu item = (LSubMenu) c.getClientProperty(key);
			item.getParent().setEnabled(value);
		}
	}

}
