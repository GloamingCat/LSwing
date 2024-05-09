package lui.widget;

import lui.base.event.LMoveEvent;
import lui.container.LContainer;
import lui.base.data.LDataTree;
import lui.base.data.LPath;

import javax.swing.DropMode;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class LList<T, ST> extends LTree<T, ST> {
	
	public LList(LContainer parent) {
		this(parent, false);
	}

	public LList(LContainer parent, boolean check) {
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
