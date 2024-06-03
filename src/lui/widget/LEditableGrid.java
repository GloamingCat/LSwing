package lui.widget;

import lui.base.data.*;
import lui.base.event.*;
import lui.base.event.listener.LCollectionListener;
import lui.base.gui.LEditableCollection;
import lui.base.gui.LMenu;
import lui.container.LContainer;
import lui.container.LImage;
import lui.editor.LPopupMenu;

import java.util.ArrayList;

public abstract class LEditableGrid<T, ST> extends LGrid<T, ST> implements LEditableCollection<T, ST> {

	private boolean editEnabled = false;
	private boolean insertEnabled = false;
	private boolean duplicateEnabled = false;
	private boolean deleteEnabled = false;

	protected ArrayList<LCollectionListener<T>> insertListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> moveListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> deleteListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<ST>> editListeners = new ArrayList<>();


	//////////////////////////////////////////////////
	//region Constructor

	public LEditableGrid(LContainer parent) {
		super(parent);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Data

	@Override
	public LMoveEvent<T> move(LPath sourceParent, int sourceIndex,
			LPath destParent, int destIndex) {
		// Not supported.
		return null;
	}

	@Override
	public LInsertEvent<T> insert(LPath parentPath, int index, LDataTree<T> node) {
		if (isEmpty()) {
			clear();
			setGridLayout();
		}
		addLabel(index, node.data, false);
		return new LInsertEvent<>(parentPath, index, node);
	}

	@Override
	public LDeleteEvent<T> delete(LPath parentPath, int index) {
		LImage c = (LImage) getChild(index);
		@SuppressWarnings("unchecked")
		T data = (T) c.getData();
		c.dispose();
		if (getChildCount() == 0) {
			setEmptyLayout();
		}
		return new LDeleteEvent<>(parentPath, index, new LDataTree<>(data));
	}

	protected abstract LDataTree<T> emptyNode();

	protected abstract LDataTree<T> duplicateNode(LPath nodePath);

	//endregion

	//////////////////////////////////////////////////
	//region Widgets

	@Override
	protected LImage addLabel(int i, T data, boolean placeholder) {
		LImage img = super.addLabel(i, data, placeholder);
		LPopupMenu menu = new LPopupMenu(img);
		menu.putClientProperty("label", img);
		if (placeholder) {
			if (insertEnabled)
				setInsertNewEnabled(menu, true);
		} else {
			setEditEnabled(menu, editEnabled);
			setInsertNewEnabled(menu, insertEnabled);
			setDuplicateEnabled(menu, duplicateEnabled);
			setDeleteEnabled(menu, deleteEnabled);
		}
		return img;
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Menu Handlers

	@Override
	public ArrayList<LCollectionListener<T>> getInsertListeners() {
		return insertListeners;
	}

	@Override
	public ArrayList<LCollectionListener<T>> getDeleteListeners() {
		return deleteListeners;
	}

	@Override
	public ArrayList<LCollectionListener<T>> getMoveListeners() {
		return moveListeners;
	}

	@Override
	public ArrayList<LCollectionListener<ST>> getEditListeners() {
		return editListeners;
	}

	@Override
	public void onEditButton(LMenu menu) {
		LImage label = (LImage) ((LPopupMenu) menu).getClientProperty("label");
		int i = indexOf(label);
		LPath path = new LPath(i);
		newEditAction(path);
	}

	@Override
	public void onInsertNewButton(LMenu menu) {
		LImage label = (LImage) ((LPopupMenu) menu).getClientProperty("label");
		int i = indexOf(label) + 1;
		LDataTree<T> newNode = emptyNode();
		newInsertAction(null, i, newNode);
	}

	@Override
	public void onDuplicateButton(LMenu menu) {
		LImage label = (LImage) ((LPopupMenu) menu).getClientProperty("label");
		int i = indexOf(label);
		LDataTree<T> newNode = duplicateNode(new LPath(i));
		newInsertAction(null, i, newNode);
	}
	@Override
	public void onDeleteButton(LMenu menu) {
		LImage label = (LImage) ((LPopupMenu) menu).getClientProperty("label");
		int i = indexOf(label);
		newDeleteAction(null, i);
	}

	@Override
	public void onCopyButton(LMenu menu) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPasteButton(LMenu menu) {
		// TODO Auto-generated method stub

	}

	public void setEditEnabled(boolean value) {
		editEnabled = value;
	}
	
	public void setInsertNewEnabled(boolean value) {
		insertEnabled = value;
	}
	
	public void setDuplicateEnabled(boolean value) {
		duplicateEnabled = value;
	}
	
	public void setDeleteEnabled(boolean value) {
		deleteEnabled = value;
	}

	//endregion

	///////////////////////////////////////////////////
	//region Listeners
	
	@Override
	public void notifyEditListeners(LEditEvent<ST> event) {
		LEditableCollection.super.notifyEditListeners(event);
		refreshObject(event.path);
		refreshLayout();
	}
	
	@Override
	public void notifyInsertListeners(LInsertEvent<T> event) {
		LEditableCollection.super.notifyInsertListeners(event);
		refreshAll();
		refreshLayout();
	}
	
	@Override
	public void notifyDeleteListeners(LDeleteEvent<T> event) {
		LEditableCollection.super.notifyDeleteListeners(event);
		refreshAll();
		refreshLayout();
	}

	//endregion

}
