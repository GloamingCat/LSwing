package lwt.dialog;

import java.awt.Component;

import javax.swing.JOptionPane;

public class LConfirmDialog {
	
	public static final int YES_NO_CANCEL = 0;
	public static final int YES_NO = 1;
	public static final int OK_CANCEL = 2;

	public final static int YES = 0;
	public final static int NO = 1;
	public final static int CANCEL = 2;
	public final static int CLOSE = -1;
	
	protected String title;
	protected String message;
	protected Component parent;
	protected int type;

	public LConfirmDialog(LWindow parent, String title, String message, int opts) {
		this.title = title;
		this.message = message;
		this.parent = parent.jdialog != null ? parent.jdialog : parent.jframe;
		if (opts == YES_NO_CANCEL)
			type = JOptionPane.YES_NO_CANCEL_OPTION;
		else if (opts == YES_NO)
			type = JOptionPane.YES_NO_OPTION;
		else
			type = JOptionPane.OK_CANCEL_OPTION;
	}
	
	public int open() {
		int r = JOptionPane.showConfirmDialog(parent, message, title, type, 
				JOptionPane.QUESTION_MESSAGE);
		if (type == JOptionPane.OK_CANCEL_OPTION) {
			return r == 1 ? CANCEL : r;
		} else {
			return r;
		}
	}
	
}
