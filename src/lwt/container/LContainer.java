package lwt.container;

import java.awt.Container;

import javax.swing.JComponent;

import lwt.dialog.LWindow;

public interface LContainer {

	JComponent getContentComposite();
	
	default JComponent getTopComposite() {
		return getContentComposite();
	}
	
	default Object getChild(int i) {
		return getContentComposite().getComponent(i);
	}
	
	default int getChildCount() {
		return getContentComposite().getComponentCount();
	}
	
	default LWindow getWindow() {
		Container c = getTopComposite().getParent();
		while (!(c instanceof LContainer))
			c = c.getParent();
		return ((LContainer) c).getWindow();
	}

	default void refreshLayout() {
		getTopComposite().validate();
	}
	
	default void dispose() {
		for (int i = 0; i < getChildCount(); i++) {
			Object c = getChild(i);
			if (c instanceof LContainer lc) {
				lc.dispose();
			}
		}
	}

	default Object getData() {
		return getData("");
	}

	default void setData(Object data) {
		setData("", data);
	}

	default Object getData(String key) {
		return getTopComposite().getClientProperty(key);
	}

	default void setData(String key, Object data) {
		getTopComposite().putClientProperty(key, data);
	}
	
}
