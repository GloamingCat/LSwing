package lui.dialog;

public abstract class LWindowFactory<T> {

	public abstract LObjectDialog<T> createWindow(LWindow parent);
	public T openShell(LWindow parent, T initial) {
		LObjectDialog<T> shell = createWindow(parent);
		shell.open(initial);
		T result = shell.getResult();
		return result;
	}
	
}
