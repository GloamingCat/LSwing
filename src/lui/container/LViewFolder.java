package lui.container;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import lui.LMenuInterface;

import java.awt.*;

public class LViewFolder extends LView {
	
	protected JTabbedPane tabFolder;
	protected int currentTab = 0;
	
	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new lwt.dialog.LShell()
	 */
	public LViewFolder(LContainer parent, boolean doubleBuffered) {
		super(parent, doubleBuffered);
		setLayout(new GridLayout(1, 1));
		tabFolder = new JTabbedPane();
		add(tabFolder);
		tabFolder.addChangeListener(e -> {
            int i = tabFolder.getSelectedIndex();
            if (children != null && currentTab != i && i >= 0 && i < children.size()) {
                currentTab = i;
                children.get(i).onVisible();
            }
        });
	}
	
	public void addTab(String name, LView child) {
		addChild(child);
		tabFolder.addTab(name, child);
	}
	
	public void addTab(String name, LContainer child) {
		tabFolder.addTab(name, child.getContentComposite());
	}

	@Override
	public LMenuInterface getMenuInterface() {
		return children.get(currentTab).getMenuInterface();
	}

	@Override
	public JComponent getContentComposite() {
		return tabFolder;
	}

	@Override
	public JComponent getTopComposite() {
		return this;
	}

}
