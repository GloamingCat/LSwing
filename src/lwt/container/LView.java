package lwt.container;

import java.util.ArrayList;

import lwt.LMenuInterface;
import lwt.dialog.LWindow;
import lbase.action.LActionStack;
import lbase.action.LState;
import lwt.editor.LEditor;

public class LView extends LPanel implements lbase.gui.LView {
	
	protected LView parent;
	protected LMenuInterface menuInterface;
	boolean doubleBuffered;
	
	protected ArrayList<LView> children = new ArrayList<>();
	protected ArrayList<LEditor> subEditors = new ArrayList<>();
	
	/**
	 * No layout.
	 */
	public LView(LContainer parent, boolean doubleBuffered) {
		super(parent.getContentComposite());
		this.doubleBuffered = doubleBuffered;
	}

	public void addChild(LView child) {
		if (child.parent != null) {
			parent.children.remove(child);
		}
		child.parent = this;
		if (child.getMenuInterface() == null) {
			child.setMenuInterface(menuInterface);
		}
		children.add(child);
	}
	
	public void addChild(LEditor editor) {
		addChild((LView) editor);
		subEditors.add(editor);
	}

	public void removeChild(LView child) {
		if (child.parent != this)
			return;
		child.parent = null;
		children.remove(child);
	}
	
	public void removeChild(LEditor editor) {
		removeChild((LView) editor);
		subEditors.remove(editor);
	}
	
	public void refreshLayout() {
		if (doubleBuffered)
			setIgnoreRepaint(true);
		super.refreshLayout();
		if (doubleBuffered)
			setIgnoreRepaint(false);
	}
	
	public void onVisible() {
		if (doubleBuffered)
			setIgnoreRepaint(true);
		try {
			onChildVisible();
		} catch(Exception e) {
			System.err.println(this.getClass());
			throw e;
		}
		if (doubleBuffered)
			setIgnoreRepaint(false);
	}
	
	public void onChildVisible() {
		for(LView child : children) {
			child.onVisible();
		}
	}
	
	public LState getState() {
		final ArrayList<LState> states = getChildrenStates();
		return () -> resetStates(states);
	}
	
	protected ArrayList<LState> getChildrenStates() {
		ArrayList<LState> list = new ArrayList<>();
		for(LView child : children) {
			list.add(child.getState());
		}
		return list;
	}
	
	protected void resetStates(ArrayList<LState> list) {
		for(LState state : list) {
			state.reset();
		}
	}
	
	public void createMenuInterface() {
		menuInterface = new LMenuInterface(this);
	}
	
	public void setMenuInterface(LMenuInterface mi) {
		this.menuInterface = mi;
		for(LView child : children) {
			child.setMenuInterface(mi);
		}
	}
	
	public LActionStack getActionStack() {
		LMenuInterface mi = getMenuInterface();
		if (mi == null)
			return null;
		return mi.actionStack;
	}
	
	public LMenuInterface getMenuInterface() {
		return menuInterface;
	}

	public void restart() {
		if (getActionStack().getRootView() == this)
			getActionStack().clear();
		restartChildren();
	}
	
	public void restartChildren() {
		for(LView child : children) {
			child.restart();
		}
	}

	@Override
	public LWindow getWindow() {
		return super.getWindow();
	}

}
