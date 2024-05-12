package lui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.KeyStroke;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

public class LGlobals {
	
	public static final Clipboard clipboard = initClipboard();
	
	private static final HashMap<String, Integer> accelerators = initAccelerators();
	private static HashMap<String, Integer> initAccelerators() {
		HashMap<String, Integer> map = new HashMap<>();
		map.put("f1", KeyEvent.VK_F1);
		map.put("f2", KeyEvent.VK_F2);
		map.put("f3", KeyEvent.VK_F3);
		map.put("f4", KeyEvent.VK_F4);
		map.put("f5", KeyEvent.VK_F5);
		map.put("v", KeyEvent.VK_V);
		map.put("c", KeyEvent.VK_C);
		map.put("z", KeyEvent.VK_Z);
		map.put("y", KeyEvent.VK_Y);
		map.put("d", KeyEvent.VK_D);
		map.put("n", KeyEvent.VK_N);
		map.put("s", KeyEvent.VK_S);
		map.put("o", KeyEvent.VK_O);
		map.put("del", KeyEvent.VK_DELETE);
		map.put("space", KeyEvent.VK_SPACE);
		map.put("enter", KeyEvent.VK_ENTER);
		map.put("ctrl", ActionEvent.CTRL_MASK);
		map.put("alt", ActionEvent.ALT_MASK);
		return map;
	}
	
	public static KeyStroke getAccelerator(String[] keys) {
		int mask = 0;
		for (int i = 0; i < keys.length - 1; i++)
			mask = mask | accelerators.get(keys[i].trim().toLowerCase());
		String key = keys[keys.length - 1].trim().toLowerCase();
		if (key.charAt(0) == '&')
			key = "" + key.charAt(1);
		int code = accelerators.get(key);
		//noinspection MagicConstant
		return KeyStroke.getKeyStroke(code, mask);
	}
	
	private static Clipboard initClipboard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
}
