package lwt.container;

import java.awt.Container;

import javax.swing.JComponent;

import lwt.dialog.LWindow;
import lwt.widget.LWidget;

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
		getContentComposite().doLayout();
	}
	
	default void dispose() {
		for (int i = 0; i < getChildCount(); i++) {
			Object c = getChild(i);
			if (c instanceof LContainer) {
				LContainer lc = (LContainer) c;
				lc.dispose();
			} else if (c instanceof LWidget) {
				LWidget w = (LWidget) c;
				w.dispose();
			}
		}
	}
	
}
