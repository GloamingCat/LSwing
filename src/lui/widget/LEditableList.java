package lui.widget;

import lui.base.event.LMoveEvent;
import lui.container.LContainer;
import lui.base.data.LDataTree;

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
	public void setItems(LDataTree<T> root) {
		super.setItems(root);
		refreshAll();
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
