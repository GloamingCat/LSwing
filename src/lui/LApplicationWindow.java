package lui;

import lui.base.LPrefs;
import lui.container.LStack;
import lui.container.LView;
import lui.dialog.LErrorDialog;
import lui.dialog.LFileDialog;
import lui.dialog.LConfirmDialog;
import lui.dialog.LWindow;
import lui.editor.LMenuBar;
import lui.editor.LSubMenu;
import lui.graphics.LTexture;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;

import lui.base.LVocab;
import lui.base.action.LActionManager;
import lui.base.gui.LMenu;
import lui.base.serialization.LSerializer;

public abstract class LApplicationWindow extends LWindow implements lui.base.gui.LApplicationWindow {

	protected LSerializer project = null;
	protected ArrayList<LView> views = new ArrayList<>();
	protected LView defaultView = null;
	protected LView currentView;
	protected LStack stack;

	protected LMenuBar menuBar;
	public LSubMenu menuProject;
	public LSubMenu menuEdit;
	public LSubMenu menuView;
	public LSubMenu menuHelp;

	/**
	 * Create the shell.
	 * @wbp.eval.method.parameter initialWidth 800
	 * @wbp.eval.method.parameter initialHeight 600
	 */
	public LApplicationWindow(int minWidth, int minHeight, String icon, String... args) {
		super(minWidth, minHeight);
		LTexture.rootClass = getClass();
		if (icon != null) {
			jframe.setIconImage(LTexture.getBufferedImage(icon));
		}
		stack = new LStack(getContentComposite());
		jframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jframe.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (askSave())
					System.exit(0);
			}
		});
		createMenu();
		createViews();
		String folder = args.length > 0 ? args[0] : null;
		if (folder != null) {
			System.out.println(folder);
		}
		loadDefault(folder);
		setMargins(LPrefs.FRAMEMARGIN, LPrefs.FRAMEMARGIN);
		refreshLayout();
	}

	protected void createMenu() {
		LVocab vocab = LVocab.instance;
		menuBar = new LMenuBar(this);

		menuProject = menuBar.addSubMenu(vocab.PROJECT, "project");
		menuProject.addMenuButton(vocab.NEW, "new", (d) -> onNewProject(), "Ctrl+&N");
		menuProject.addMenuButton(vocab.OPEN, "open", (d) -> onOpenProject(), "Ctrl+&O");
		menuProject.addMenuButton(vocab.SAVE, "save", (d) -> onSaveProject(), "Ctrl+&S");
		menuProject.addSeparator();
		menuProject.addMenuButton(vocab.EXIT, "exit", (d) -> close(), "Alt+F4");

		menuEdit = menuBar.addSubMenu(vocab.EDIT, "edit");
		menuEdit.addMenuButton(vocab.UNDO, "undo", (d) -> currentView.getActionStack().undo(), "Ctrl+&Z");
		menuEdit.setButtonEnabled("undo", false);
		menuEdit.addMenuButton(vocab.REDO, "redo", (d) -> currentView.getActionStack().redo(), "Ctrl+&Y");
		menuEdit.setButtonEnabled("redo", false);
		menuEdit.addSeparator();
		menuEdit.addMenuButton(vocab.COPY, "copy", (d) -> currentView.getMenuInterface().copy(), "Ctrl+&C");
		menuEdit.setButtonEnabled("copy", false);
		menuEdit.addMenuButton(vocab.PASTE, "paste", (d) -> currentView.getMenuInterface().paste(), "Ctrl+&V");
		menuEdit.setButtonEnabled("paste", false);

		menuView = menuBar.addSubMenu(vocab.VIEW, "view");
		menuBar.setMenuEnabled("view", false);
		menuHelp = menuBar.addSubMenu(vocab.HELP, "help");
		menuBar.setMenuEnabled("help", false);
	}

	protected abstract void createViews();

	@Override
	public LMenu getEditMenu() {
		return menuEdit;
	}
	
	public void run() {
		javax.swing.SwingUtilities.invokeLater(() -> {
            open();
            refreshLayout();
        });
	}

	protected void addView(final LView view, String name, String shortcut) {
		menuBar.setMenuEnabled("view", true);
		menuView.addMenuButton(name, name, (d) -> setCurrentView(view), shortcut);
		views.add(view);
	}

	protected void setCurrentView(LView view) {
		currentView = view;
		getContentComposite().setIgnoreRepaint(true);
		stack.setTop(currentView);
		//refreshLayout();
		currentView.onVisible();
		refreshEditButtons();
		getContentComposite().setIgnoreRepaint(false);
	}
	
	@Override
	public void refreshEditButtons() {
		menuProject.setButtonEnabled("save", project != null && LActionManager.getInstance().hasChanges());
		menuEdit.setButtonEnabled("undo", currentView.getActionStack().canUndo());
		menuEdit.setButtonEnabled("redo", currentView.getActionStack().canRedo());
	}
	
	@Override
	public void refreshClipboardButtons() {
		menuEdit.setButtonEnabled("copy", currentView.getMenuInterface().canCopy());
		menuEdit.setButtonEnabled("paste", currentView.getMenuInterface().canPaste());
	}	
	
	//region Load Project
	public void onOpenProject() {
		LSerializer project = openProject();
		if (project != null) {
			this.project = project;
			menuView.setEnabled(true);
			for (LView view : views)
				view.restart();
			if (defaultView != null)
				setCurrentView(defaultView);
		}
	}

	@Override
	public String openLoadProjectDialog() {
		LFileDialog dialog = new LFileDialog(this,
				LVocab.instance.OPENPROJECT,
				getProjectExtension(),
				false);
		return dialog.open();
	}
	
	@Override 
	public void openLoadErrorDialog(String path) {
		LErrorDialog msg = new LErrorDialog(this,
				LVocab.instance.LOADERROR,
				LVocab.instance.LOADERRORMSG + ":" + path);
		msg.open();
	}

	@Override
	public void onLoadSuccess(LSerializer project, String path) {
		this.project = project;
		menuView.setEnabled(true);
		if (defaultView != null)
			setCurrentView(defaultView);
		setTitle(getApplicationName() + " - " + path);
	}
	
	@Override
	public void onLoadFail(String path) {
		LVocab vocab = LVocab.instance;
		LErrorDialog msg = new LErrorDialog(this,
				vocab.LOADERROR,
				vocab.LOADERRORMSG + "\n" + path);
		msg.open();
	}
	//endregion
	
	//region New Project
	public void onNewProject() {
		LSerializer project = newProject();
		if (project != null) {
			this.project = project;
			menuView.setEnabled(true);
			if (defaultView != null)
				setCurrentView(defaultView);
		}
	}
	
	@Override
	public String openNewProjectDialog() {
		LFileDialog dialog = new LFileDialog(this,
				LVocab.instance.NEWPROJECT,
				getProjectExtension(),
				true);
		return dialog.open();
	}
	
	@Override
	public boolean openNewConfirmDialog() {
		LConfirmDialog msg = new LConfirmDialog(this, 
				LVocab.instance.EXISTINGPROJECT,
				LVocab.instance.EXISTINGMSG,
				LConfirmDialog.OK_CANCEL);
		int result = msg.open();
		return result == LConfirmDialog.YES;
	}
	//endregion

	//region Save Project
	public void onSaveProject() {
		if (project == null || !LActionManager.getInstance().hasChanges())
			return;
		if (!project.save()) {
			LVocab vocab = LVocab.instance;
			LErrorDialog msg = new LErrorDialog(this,
					vocab.SAVEERROR,
					vocab.SAVEERRORMSG);
			msg.open();
		} else {
			LActionManager.getInstance().onSave();
		}
	}

	@Override
	public boolean askSave() {
		if (project != null && LActionManager.getInstance().hasChanges()) {
			LVocab vocab = LVocab.instance;
			LConfirmDialog msg = new LConfirmDialog(this, 
					vocab.UNSAVEDPROJECT,
					vocab.UNSAVEDMSG,
					LConfirmDialog.YES_NO_CANCEL);
			int result = msg.open();
			if (result == LConfirmDialog.YES) {
				onSaveProject();
				return true;
			} else if (result == LConfirmDialog.NO) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	//endregion

}
