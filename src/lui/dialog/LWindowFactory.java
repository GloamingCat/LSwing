package lui.dialog;

public abstract class LWindowFactory<T> {

	public abstract LObjectDialog<T> createWindow(LWindow parent);
	public T openWindow(LWindow parent, T initial) {
		LObjectDialog<T> shell = createWindow(parent);
		shell.open(initial);
        return shell.getResult();
	}
	
}
