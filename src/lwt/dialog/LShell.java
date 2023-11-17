package lwt.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import lwt.LContainer;

public class LShell extends Shell implements LContainer {

	public LShell() {
		super(Display.getDefault(), SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
	}
	
	public LShell(LShell parent) {
		super(parent, SWT.APPLICATION_MODAL);
	}

	public LShell getParent() {
		return (LShell) super.getParent();
	}

	@Override
	public Composite getComposite() {
		return this;
	}

}
