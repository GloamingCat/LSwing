package myeditor;

import lui.base.serialization.LSerializer;
import lui.LApplicationWindow;
import myeditor.gui.MyConfigEditor;
import myeditor.gui.MyContentGridEditor;
import myeditor.gui.MyContentListEditor;
import myeditor.gui.MyContentTreeEditor;
import myeditor.project.MyProject;

public class MyApplicationWindow extends LApplicationWindow {
	
	/**
	 * Launch the application.
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		try {
			MyApplicationWindow shell = new MyApplicationWindow(args);
			shell.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 */
	public MyApplicationWindow(String... args) {
		super(450, 300, null, args);
	}

	@Override
	protected void createViews() {
		MyContentTreeEditor treeEditor = new MyContentTreeEditor(stack);
		MyContentListEditor listEditor = new MyContentListEditor(stack);
		MyContentGridEditor gridEditor = new MyContentGridEditor(stack);
		MyConfigEditor configEditor = new MyConfigEditor(stack);

		addView(treeEditor, MyVocab.instance.CONTENTTREE, "F2");
		addView(listEditor, MyVocab.instance.CONTENTLIST, "F3");
		addView(gridEditor, MyVocab.instance.CONTENTGRID, "F4");
		addView(configEditor, MyVocab.instance.CONTENTTYPES, "F5");

		defaultView = treeEditor;
	}

	public String getApplicationName() {
		return "LSwing Test";
	}
	public String getProjectExtension() {
		return "txt";
	}
	
	@Override
	public LSerializer createProject(String path) {
		return new MyProject(path);
	}

}
