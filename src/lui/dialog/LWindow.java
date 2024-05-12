package lui.dialog;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import lui.container.LPanel;
import lui.editor.LMenuBar;
import lui.layout.LCellData;
import lui.layout.LLayedCell;
import lui.layout.LLayedContainer;

public class LWindow implements LLayedContainer, LLayedCell, lui.base.gui.LWindow {

	private final LWindow parent;
	protected final JDialog jdialog;
	protected final JFrame jframe;

	private final LPanel panel;
	private final Component shell;
	
	//////////////////////////////////////////////////
	//region Constructors
	
	/**
	 * Root shell.
	 */
	public LWindow() {
		parent = null;
		jdialog = null;
		jframe = new JFrame();
		jframe.setLayout(new GridLayout(1, 1));
		shell = jframe;
		panel = new LShellPanel(this, jframe);
	}

	/**
	 * Sub-shell.
	 */
	public LWindow(LWindow parent) {
		this.parent = parent;
		jframe = null;
		if (parent.jframe != null)
			jdialog = new JDialog(parent.jframe, Dialog.ModalityType.APPLICATION_MODAL);
		else
			jdialog = new JDialog(parent.jdialog, Dialog.ModalityType.APPLICATION_MODAL);
		jdialog.setLayout(new GridLayout(1, 1));
		jdialog.setLocationRelativeTo(parent.shell);
		shell = jdialog;
		panel = new LShellPanel(this, jdialog);
	}

	/**
	 * Root shell with minimum size.
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter width 300
	 * @wbp.eval.method.parameter height 200
	 */
	public LWindow(int width, int height) {
		this();
		panel.getCellData().setRequiredSize(width, height);
	}

	/**
	 * Sub-shell with minimum size.
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter width 300
	 * @wbp.eval.method.parameter height 200
	 */
	public LWindow(LWindow parent, int width, int height) {
		this(parent);
		panel.getCellData().setRequiredSize(width, height);
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Layout

	public void setContinuousLayout(boolean v) {
		Toolkit.getDefaultToolkit().setDynamicLayout(v);
	}

	@Override
	public void refreshLayout() {
		shell.revalidate();
	}

	@Override
	public void pack() {
		panel.doLayout();
		panel.setPreferredSize(null);
		panel.revalidate();
		shell.setPreferredSize(null);
		shell.revalidate();
		if (jframe != null)
			jframe.pack();
		else
			jdialog.pack();
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

	public void setMenuBar(LMenuBar bar) {
		if (jframe != null)
			jframe.setJMenuBar(bar);
		else
			jdialog.setJMenuBar(bar);
	}

	//////////////////////////////////////////////////
	//region Size

	@Override
	public LCellData getCellData() {
		return null;
	}

	@Override
	public Dimension getSize(Dimension d) {
		return shell.getSize(d);
	}

    public void setCurrentSize(int width, int height) {
		shell.setSize(width, height);
		shell.revalidate();
	}

	@Override
	public Dimension getPreferredSize() {
		return shell.getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return shell.getMinimumSize();
	}

	@Override
	public void revalidate() {
		shell.revalidate();
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Container Methods

	public LWindow getParent() {
		return parent;
	}
	
	public void open() {
		refreshLayout();
		shell.setVisible(true);
	}
	
	public void close() {
		shell.setVisible(false);
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

	protected static class LShellPanel extends LPanel {
		
		LWindow shell;
		public LShellPanel(LWindow shell, JDialog jdialog) {
			super(jdialog);
			this.shell = shell;
			setFillLayout(true);
		}
		
		public LShellPanel(LWindow shell, JFrame jframe) {
			super(jframe);
			this.shell = shell;
			setFillLayout(true);
		}
		
		@Override
		public LWindow getWindow() {
			return shell;
		}
		
	}

}
