package lwt.dialog;

public abstract class LWindowFactory<T> {

	public abstract LObjectWindow<T> createWindow(LWindow parent);
	public T openShell(LWindow parent, T initial) {
		LObjectWindow<T> shell = createWindow(parent);
		shell.open(initial);
		T result = shell.getResult();
		return result;
	}
	
}
