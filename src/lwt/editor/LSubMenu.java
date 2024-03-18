package lwt.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lbase.event.LSelectionEvent;
import lbase.event.listener.LSelectionListener;
import lwt.LGlobals;

public class LSubMenu extends JMenu implements lbase.gui.LMenu {
	private static final long serialVersionUID = 1L;
	
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
		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				listener.onSelect(new LSelectionEvent(null, null, -1));
			}
		});
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
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						action.accept(e.paramString());
					}
				});
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
