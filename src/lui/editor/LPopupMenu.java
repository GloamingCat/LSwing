package lui.editor;

import java.awt.*;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import lui.base.LFlags;
import lui.base.event.LMouseEvent;
import lui.base.event.listener.LMouseListener;
import lui.base.gui.LMenu;

public class LPopupMenu extends JPopupMenu implements LMenu {

	protected final JComponent component;

	public LPopupMenu(JComponent c) {
		c.setComponentPopupMenu(this);
		this.component = c;
	}

	public void setMenuButton(boolean value, String buttonName, String buttonKey, 
			Consumer<Object> action, String acc) {
		LSubMenu.setMenuButton(this, value, buttonName, buttonKey, action, acc);
	}
	
	public void addSeparator() {
		super.addSeparator();
	}
	
	public void addListener(LMouseListener listener) {
		addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				Point p = component.getMousePosition();
				listener.onMouseChange(new LMouseEvent(LFlags.RIGHT, p.x, p.y, LFlags.PRESS));
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
