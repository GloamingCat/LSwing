package lui.widget;

import lui.container.LContainer;
import lui.base.data.LDataTree;
import lui.base.data.LPath;

import javax.swing.DropMode;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class LList<T, ST> extends LTree<T, ST> {
	private static final long serialVersionUID = 1L;
	
	protected boolean includeID = false;
	
	public LList(LContainer parent) {
		this(parent, false);
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LList(LContainer parent, boolean check) {
		super(parent, check);
	}
	
	@Override
	protected void createContent(int flags) {
		super.createContent(flags);
		tree.setDropMode(DropMode.INSERT);
	}
	
	@Override
	public void setIncludeID(boolean value) {
		includeID = value;
	}
	
	@Override
	public void setItems(LDataTree<T> root) {
		super.setItems(root);
		refreshAll();
	}
	
	@Override
	public void setItemNode(DefaultMutableTreeNode item, LDataTree<T> node) {
		String id = "";
		if (includeID) {
			id = stringID(indexOf(item));
		}
		String name = dataToString(node.data);
		ItemData data = new ItemData(id + name, node.id, node.data);
		item.setUserObject(data);
	}
	
	@Override
	public void refreshObject(DefaultMutableTreeNode item, T data) {

	}
	public void refreshObject(LPath path) {
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item != null) {
			String id = "";
			if (includeID) {
				id = stringID(path.index);
			}
			@SuppressWarnings("unchecked")
			ItemData itemData = (ItemData) item.getUserObject();
			itemData.data = toObject(path);
			itemData.name = id + dataToString(itemData.data);
			item.setUserObject(itemData);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void refreshAll() {
		if (includeID) {
			for (int i = 0; i < root.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
				ItemData data = (LTreeBase<T, ST>.ItemData) node.getUserObject();
				String name = dataToString(data.data);
				data.name = stringID(i) + name;
			}
		} else {
			for (int i = 0; i < root.getChildCount(); i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
				ItemData data = (LTreeBase<T, ST>.ItemData) node.getUserObject();
				data.name = dataToString(data.data);
			}
		}
	}
	
}
