package lui.editor;

import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import lui.base.event.LSelectionEvent;
import lui.base.event.listener.LSelectionListener;
import lui.base.gui.LMenu;

public class LPopupMenu extends JPopupMenu implements LMenu {
	private static final long serialVersionUID = 1L;
	
	public LPopupMenu(JComponent c) {
		c.setComponentPopupMenu(this);
	}

	public void setMenuButton(boolean value, String buttonName, String buttonKey, 
			Consumer<Object> action, String acc) {
		LSubMenu.setMenuButton(this, value, buttonName, buttonKey, action, acc);
	}
	
	public void addSeparator() {
		super.addSeparator();
	}
	
	public void addSelectionListener(LSelectionListener listener) {
		addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				listener.onSelect(new LSelectionEvent(null, null, -1, false));
			}
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});
	}
	
	public LSubMenu addSubMenu(String name, String key) {
		return LSubMenu.addSubMenu(this, name, key);
	}
	
	public void setButtonEnabled(String key, boolean value) {
		LSubMenu.setButtonEnabled(this, key, value);
	}
	
	public void setMenuEnabled(String key, boolean value) {
		LSubMenu.setMenuEnabled(this, key, value);
	}

}
