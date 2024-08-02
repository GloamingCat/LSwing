package lui.collection;

import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LDeleteEvent;
import lui.base.event.LEditEvent;
import lui.base.event.LInsertEvent;
import lui.base.event.LMoveEvent;
import lui.base.gui.LControl;
import lui.base.gui.LEditableCollection;
import lui.base.gui.LMenu;
import lui.container.LPanel;
import lui.editor.LPopupMenu;

public interface LEditableControlList<T, ST, C extends LPanel & LControl<T>> extends LEditableCollection<T, ST> {

	// LControlList
	C getControl(int i);
	int indexOf(C control);
	int getControlCount();
	void refreshLayout();

	// Subclasses
	C createControl();
	void refreshControl(C control, int i);

	//////////////////////////////////////////////////
	//region Edit

	LDataTree<T> emptyNode();

	LDataTree<T> duplicateNode(LPath nodePath);

	default void insertControl(int index, T data) {
		createControl().setValue(data);
		moveData(getControlCount() - 1, index);
	}

	default void deleteControl(int i) {
		getControl(i).dispose();
	}

	default void moveData(int src, int dst) {
		T data = getControl(src).getValue();
		if (dst < src) {
			for (int i = src; i > dst; i--)
				getControl(i).setValue(getControl(i - 1).getValue());
		} else {
			for (int i = src; i < dst; i++)
				getControl(i).setValue(getControl(i + 1).getValue());
		}
		getControl(dst).setValue(data);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Events

	@Override
	default LMoveEvent<T> move(LPath sourceParent, int sourceIndex,
			LPath destParent, int destIndex) {
		LDataTree<T> node = new LDataTree<>(getControl(sourceIndex).getValue());
		moveData(sourceIndex, destIndex);
		return new LMoveEvent<>(sourceParent, sourceIndex, destParent, destIndex, node);
	}

	@Override
	default LInsertEvent<T> insert(LPath parentPath, int index, LDataTree<T> node) {
		insertControl(index, node.data);
		return new LInsertEvent<>(parentPath, index, node);
	}

	@Override
	default LDeleteEvent<T> delete(LPath parentPath, int index) {
		C c = getControl(index);
		T data = c.getValue();
		deleteControl(index);
		return new LDeleteEvent<>(parentPath, index, new LDataTree<>(data));
	}

	//endregion

	///////////////////////////////////////////////////
	//region Listeners
	
	@Override
	default void notifyEditListeners(LEditEvent<ST> event) {
		LEditableCollection.super.notifyEditListeners(event);
		refreshObject(event.path);
		refreshLayout();
	}
	
	@Override
	default void notifyInsertListeners(LInsertEvent<T> event) {
		LEditableCollection.super.notifyInsertListeners(event);
		refreshAll();
		refreshLayout();
	}
	
	@Override
	default void notifyDeleteListeners(LDeleteEvent<T> event) {
		LEditableCollection.super.notifyDeleteListeners(event);
		refreshAll();
		refreshLayout();
	}

	@Override
	default void notifyMoveListeners(LMoveEvent<T> event) {
		LEditableCollection.super.notifyMoveListeners(event);
		refreshAll();
		refreshLayout();
	}

	//endregion

	//////////////////////////////////////////////////
	//region Menu

	@Override
	@SuppressWarnings("unchecked")
	default void onEditButton(LMenu menu) {
		C control = (C) ((LPopupMenu) menu).getClientProperty("control");
		int i = indexOf(control);
		LPath path = new LPath(i);
		newEditAction(path);
	}

	@Override
	@SuppressWarnings("unchecked")
	default void onInsertNewButton(LMenu menu) {
		C control = (C) ((LPopupMenu) menu).getClientProperty("control");
		int i = indexOf(control) + 1;
		LDataTree<T> newNode = emptyNode();
		newInsertAction(null, i, newNode);
	}

	@Override
	@SuppressWarnings("unchecked")
	default void onDuplicateButton(LMenu menu) {
		C control = (C) ((LPopupMenu) menu).getClientProperty("control");
		int i = indexOf(control);
		LDataTree<T> newNode = duplicateNode(new LPath(i));
		newInsertAction(null, i, newNode);
	}
	@Override
	@SuppressWarnings("unchecked")
	default void onDeleteButton(LMenu menu) {
		C control = (C) ((LPopupMenu) menu).getClientProperty("control");
		int i = indexOf(control);
		newDeleteAction(null, i);
	}

	//endregion

}
