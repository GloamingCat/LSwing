package lwt.dialog;

import java.awt.Component;

import javax.swing.JOptionPane;

public class LErrorDialog {
	
	protected String title;
	protected String message;
	protected Component parent;

	public LErrorDialog(LWindow parent, String title, String message) {
		this.title = title;
		this.message = message;
		this.parent = parent.jdialog != null ? parent.jdialog : parent.jframe;
	}
	
	public void open() {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
}
