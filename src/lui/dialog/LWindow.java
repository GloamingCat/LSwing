package lui.dialog;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import lui.container.LPanel;
import lui.editor.LMenuBar;
import lui.layout.LLayedContainer;

public class LWindow implements LLayedContainer, lui.base.gui.LWindow {

	private final LWindow parent;
	protected final JDialog jdialog;
	protected final JFrame jframe;
	private final LPanel panel;
	
	//////////////////////////////////////////////////
	//region Constructors
	
	/**
	 * Root shell.
	 */
	public LWindow() {
		parent = null;
		jdialog = null;
		jframe = new JFrame();
		panel = new LShellPanel(this, jframe);
	}
	
	/**
	 * Root shell with selection size.
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter width 300
	 * @wbp.eval.method.parameter height 200
	 */
	public LWindow(int width, int height) {
		this();
		setMinimumSize(width, height);
		setSize(width, height);
	}

	/**
	 * Sub-shell.
	 */
	public LWindow(LWindow parent) {
		this.parent = parent;
		jframe = null;
		if (parent.jframe != null)
			jdialog = new JDialog(parent.jframe);
		else
			jdialog = new JDialog(parent.jdialog);
		panel = new LShellPanel(this, jdialog);
	}
	
	//endregion
	
	//////////////////////////////////////////////////
	//region Layout

	@Override
	public void refreshLayout() {
		if (jframe != null) {
			jframe.validate();
		} else {
			jdialog.validate();
		}
	}
	
	//endregion
	
	//////////////////////////////////////////////////
	//region Properties

	@Override
	public void setTitle(String title) {
		if (jframe != null)
			jframe.setTitle(title);
		else
			jdialog.setTitle(title);
	}

	public void setMinimumSize(int width, int height) {
		if (jframe != null)
			jframe.setMinimumSize(new Dimension(width, height));
		else
			jdialog.setMinimumSize(new Dimension(width, height));
	}
	
	public void setSize(int width, int height) {
		if (jframe != null)
			jframe.setSize(width, height);
		else
			jdialog.setSize(width, height);
	}
	
	public void setMenuBar(LMenuBar bar) {
		if (jframe != null)
			jframe.setJMenuBar(bar);
		else
			jdialog.setJMenuBar(bar);
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Container Methods

	public LWindow getParent() {
		return parent;
	}
	
	public void open() {
		refreshLayout();
		if (jframe != null)
			jframe.setVisible(true);
		else
			jdialog.setVisible(true);
	}
	
	public void close() {
		if (jframe != null)
			jframe.setVisible(false);
		else
			jdialog.setVisible(false);
	}

	@Override
	public LPanel getContentComposite() {
		return panel;
	}
	
	@Override
	public JComponent getTopComposite() {
		return panel;
	}

	@Override
	public LWindow getWindow() {
		return this;
	}
	
	@Override
	public Object getChild(int i) {
		if (jframe != null)
			return jframe.getComponent(i);
		else
			return jdialog.getComponent(i);
	}
	
	@Override
	public int getChildCount() {
		if (jframe != null)
			return jframe.getComponentCount();
		else
			return jdialog.getComponentCount();
	}

	@Override
	public void dispose() {
		panel.dispose();
		if (jframe != null)
			jframe.dispose();
		else
			jdialog.dispose();
	}

	//endregion

	protected class LShellPanel extends LPanel {
		
		LWindow shell;
		public LShellPanel(LWindow shell, JDialog jdialog) {
			super(jdialog);
			this.shell = shell;
			setFillLayout(true);
		}
		
		public LShellPanel(LWindow shell, JFrame jdialog) {
			super(jdialog);
			this.shell = shell;
			setFillLayout(true);
		}
		
		@Override
		public LWindow getWindow() {
			return shell;
		}
		
	}

}
