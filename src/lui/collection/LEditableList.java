package lui.collection;

import lui.base.data.LDataTree;
import lui.base.event.LDeleteEvent;
import lui.base.event.LInsertEvent;
import lui.base.event.LMoveEvent;
import lui.container.LContainer;

import javax.swing.DropMode;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class LEditableList<T, ST> extends LEditableTree<T, ST> {
	
	public LEditableList(LContainer parent) {
		this(parent, false);
	}

	public LEditableList(LContainer parent, boolean check) {
		super(parent, check);
		tree.setShowsRootHandles(false);
	}
	
	@Override
	protected void createContent(int flags) {
		super.createContent(flags);
		tree.setDropMode(DropMode.INSERT);
	}

	@Override
	public void notifyInsertListeners(LInsertEvent<T> event) {
		super.notifyInsertListeners(event);
		refreshItemIds();
	}

	@Override
	public void notifyDeleteListeners(LDeleteEvent<T> event) {
		super.notifyDeleteListeners(event);
		refreshItemIds();
	}

	@Override
	public void notifyMoveListeners(LMoveEvent<T> event) {
		super.notifyMoveListeners(event);
		refreshItemIds();
	}

	@SuppressWarnings("unchecked")
	public void refreshItemIds() {
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
			ItemData itemData = (LTree<T, ST>.ItemData) child.getUserObject();
			refreshItemData(new LDataTree<>(i, itemData.data), itemData);
		}
	}

	@Override
	public LMoveEvent<T> drop(DefaultMutableTreeNode target, int targetIndex) {
		if (targetIndex == -1) {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) target.getParent();
			return super.drop(parent, parent.getIndex(target) + 1);
		} else {
			return super.drop(target, targetIndex);
		}
	}
	
}
