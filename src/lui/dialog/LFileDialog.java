package lui.dialog;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import lui.base.serialization.LFileManager;

public class LFileDialog {
	
	protected JFileChooser dialog;
	protected Component parent;
	protected boolean create;

	public LFileDialog(LWindow parent, String title, String extension, boolean create) {
		dialog = new JFileChooser();
		dialog.setDialogTitle(title);
		dialog.setMultiSelectionEnabled(false);
		if (extension != null) {
			dialog.addChoosableFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return "Project file (*." + extension + ")";
				}
				@Override
				public boolean accept(File f) {
					return f.getName().toLowerCase().endsWith("." + extension);
				}
			});
			if (create)
				dialog.setSelectedFile(new File("New Project." + extension));
		} else {
			dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			dialog.setAcceptAllFileFilterUsed(false);
		}
		dialog.setCurrentDirectory(new File(LFileManager.applicationPath()));
		this.parent = parent.jdialog != null ? parent.jdialog : parent.jframe;
		this.create = create;
	}
	
	public String open(String initialPath) {
		if (initialPath != null) {
			File file = new File(initialPath);
			dialog.setSelectedFile(file);
		}
		if (create)
			dialog.showSaveDialog(parent);
		else
			dialog.showOpenDialog(parent);
		File file = dialog.getSelectedFile();
		return file == null ? null : file.getAbsolutePath();
	}
	
}
