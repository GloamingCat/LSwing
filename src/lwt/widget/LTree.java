package lwt.widget;

import lwt.dataestructure.LDataTree;
import lwt.dataestructure.LPath;
import lwt.event.LDeleteEvent;
import lwt.event.LEditEvent;
import lwt.event.LInsertEvent;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;

public abstract class LTree<T, ST> extends LTreeBase<T, ST> {
	
	protected Menu menu;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LTree(Composite parent, int style) {
		super(parent, style);
		menu = new Menu(tree);
		tree.setMenu(menu);
	}
	
	//-------------------------------------------------------------------------------------
	// Modify
	//-------------------------------------------------------------------------------------
	
	public LInsertEvent<T> insert(LPath parentPath, int index, LDataTree<T> node) {
		TreeItem parent = toTreeItem(parentPath);
		createTreeItem(parent, index, node);
		refreshAll();
		return new LInsertEvent<T>(parentPath, index, node);
	}
	
	public LDeleteEvent<T> delete(LPath parentPath, int index) {
		TreeItem item = toTreeItem(parentPath, index);
		LDataTree<T> node = disposeTreeItem(item);
		refreshAll();
		return new LDeleteEvent<T>(parentPath, index, node);
	}
	
	public LEditEvent<ST> edit(LPath path) {
		return null;
	}
	
	protected abstract LDataTree<T> emptyNode();
	
	protected abstract LDataTree<T> duplicateNode(LPath nodePath);
	
	//-------------------------------------------------------------------------------------
	// Modify Menu
	//-------------------------------------------------------------------------------------
	
	public void setEditEnabled(boolean value) {
		super.setEditEnabled(menu, value);
	}
	
	public void setInsertNewEnabled(boolean value) {
		super.setInsertNewEnabled(menu, value);
	}
	
	public void setDuplicateEnabled(boolean value) {
		super.setDuplicateEnabled(menu, value);
	}
	
	public void setDeleteEnabled(boolean value) {
		super.setDeleteEnabled(menu, value);
	}
	
	//-------------------------------------------------------------------------------------
	// Menu Handlers
	//-------------------------------------------------------------------------------------
	
	protected void onEditButton(Menu menu) {
		if (tree.getSelectionCount() > 0) {
			TreeItem item = tree.getSelection()[0];
			LPath path = toPath(item);
			LEditEvent<ST> event = newEditAction(path);
			if (event != null) {
    			refreshObject(path);
    			notifySelectionListeners(select(event.path));
			}
		}
	}
	
	protected void onInsertNewButton(Menu menu) {
		LPath parentPath = null;
		int index = -1;
		LDataTree<T> newNode = emptyNode();
		if (tree.getSelectionCount() > 0) {
			TreeItem item = tree.getSelection()[0];
			parentPath = toPath(item.getParentItem());
			index = indexOf(item) + 1;
		}
		LInsertEvent<T> event = newInsertAction(parentPath, index, newNode);
		if (event != null) {
			notifySelectionListeners(select(event.parentPath, event.index));
		}
	}
	
	protected void onDuplicateButton(Menu menu) {
		if (tree.getSelectionCount() > 0) {
			TreeItem item = tree.getSelection()[0];
			LPath itemPath = toPath(item);
			LDataTree<T> node = duplicateNode(itemPath);
			LPath parentPath = toPath(item.getParentItem());
			int i = indexOf(item);
			LInsertEvent<T> event = newInsertAction(parentPath, i, node);
			if (event != null) {
    			notifySelectionListeners(select(event.parentPath, event.index));
			}
		}
	}
	
	protected void onDeleteButton(Menu menu) {
		if (tree.getSelectionCount() > 0) {
			TreeItem item = tree.getSelection()[0];
			LPath parentPath = toPath(item.getParentItem());
			int i = indexOf(item);
			int selectionID = isOutOfBounds(item.getParentItem(), i + 1) ? i - 1 : i;
			System.out.println(selectionID);
			LDeleteEvent<T> event = newDeleteAction(parentPath, i);
			if (event != null) {
				if (selectionID == -1) {
					notifySelectionListeners(select(null));
				} else {
					notifySelectionListeners(select(parentPath, selectionID));
				}
			}
		}
	}
	
}
