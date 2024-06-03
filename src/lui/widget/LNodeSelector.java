package lui.widget;

import lui.base.LPrefs;
import lui.container.LContainer;
import lui.base.LVocab;
import lui.base.data.LDataTree;
import lui.base.data.LPath;

import javax.swing.JComponent;

public class LNodeSelector<T> extends LControlWidget<Integer> {
	
	protected LDataTree<T> collection;
	protected LTree<T, T> tree;
	protected LButton btnNull;

	public LNodeSelector(LContainer parent, boolean optional) {
		super(parent, optional ? 1 : 0);
		setGridLayout(1);
	}

	@Override
	protected void createContent(int flags) {
		tree = createTree(flags);
		tree.addSelectionListener(event -> {
            LPath path = tree.getSelectedPath();
            Integer id = path == null ? -1 : collection.getNode(path).id;
            if (id.equals(currentValue))
                return;
            newModifyAction(currentValue, id);
            currentValue = id;
        });
		tree.setDragEnabled(false);
		tree.getCellData().setExpand(true, true);
		tree.getCellData().setRequiredSize(LPrefs.LISTWIDTH, LPrefs.LISTHEIGHT);
		if (flags == 0)
			return;
		btnNull = new LButton(this, LVocab.instance.DESELECT);
		btnNull.onClick = event -> selectNone();
		btnNull.getCellData().setExpand(true, false);
	}

	protected LTree<T, T> createTree(int flags) {
		return new LTree<>(this) {
			@Override
			public T toObject(LPath path) {
				LDataTree<T> node = collection.getNode(path);
				if (node == null)
					return null;
				return node.data;
			}
			@Override
			public LDataTree<T> emptyNode() {
				return null;
			}
			@Override
			public LDataTree<T> duplicateNode(LDataTree<T> node) {
				return null;
			}
			@Override
			public LDataTree<T> toNode(LPath path) {
				return collection.getNode(path);
			}
			@Override
			protected String encodeNode(LDataTree<T> node) {
				return null;
			}
			@Override
			protected LDataTree<T> decodeNode(String node) {
				return null;
			}
			@Override
			public boolean canDecode(String str) {
				return false;
			}
		};
	}

	public void selectNone() {
		tree.notifySelectionListeners(tree.select(null));
	}

	public void setValue(LPath path) {
		tree.select(null);
		if (path != null) {
			tree.select(path);
			currentValue = collection.getNode(path).id;
		} else {
			currentValue = null;
		}
	}
	
	public void setValue(Object obj) {
		if (obj != null) {
			Integer i = (Integer) obj;
			currentValue = i;
			if (i >= 0) {
				LDataTree<?> node = collection.findNode((int) i);
				if (node != null) {
					tree.select(node.toPath());
					return;
				}
			}
		} else {
			currentValue = null;
		}
		tree.select(null);
	}
	
	public void setCollection(LDataTree<T> collection) {
		this.collection = collection;
		tree.setDataCollection(collection);
	}

	public LDataTree<T> getCollection() {
		return collection;
	}
	
	public T getSelectedObject() {
		if (currentValue == null)
			return null;
		return collection.get(currentValue);
	}
	
	public LPath getSelectedPath() {
		return tree.getSelectedPath();
	}
	
	public LDataTree<T> getSelectedNode() {
		LPath path = getSelectedPath();
		if (path == null)
			return null;
		return collection.getNode(path);
	}
	
	public void forceFirstSelection() {
		if (!collection.children.isEmpty())
			tree.forceSelection(new LPath(0));
		else 
			tree.select(null);
	}

	@Override
	public String encodeData(Integer value) {
		return value + "";
	}
	
	@Override
	public Integer decodeData(String str) {
		return Integer.parseInt(str);
	}

	@Override
	protected JComponent getControl() {
		return tree;
	}

}
