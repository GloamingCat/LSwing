package lui.editor;

import java.util.function.Consumer;

import javax.swing.JMenuBar;
import lui.base.gui.LMenu;
import lui.dialog.LWindow;

public class LMenuBar extends JMenuBar implements LMenu {

	public LMenuBar(LWindow w) {
		w.setMenuBar(this);
	}
	
	public void setMenuButton(boolean value, String buttonName, String buttonKey, 
			Consumer<Object> action, String acc) {
		LSubMenu.setMenuButton(this, value, buttonName, buttonKey, action, acc);
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
