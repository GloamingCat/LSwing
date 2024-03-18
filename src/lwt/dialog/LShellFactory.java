package lwt.dialog;

public abstract class LShellFactory<T> {

	public abstract LObjectShell<T> createShell(LWindow parent);
	public T openShell(LWindow parent, T initial) {
		LObjectShell<T> shell = createShell(parent);
		shell.open(initial);
		T result = shell.getResult();
		return result;
	}
	
}
