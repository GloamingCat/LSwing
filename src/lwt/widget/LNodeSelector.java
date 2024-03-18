package lwt.widget;

import lwt.container.LContainer;
import lbase.LVocab;
import lbase.data.LDataTree;
import lbase.data.LPath;
import lbase.event.LSelectionEvent;
import lbase.event.listener.LSelectionListener;

import javax.swing.JComponent;

public class LNodeSelector<T> extends LControlWidget<Integer> {
	private static final long serialVersionUID = 1L;
	
	protected LDataTree<T> collection;
	protected LTree<T, T> tree;
	protected LButton btnNull;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LNodeSelector(LContainer parent, boolean optional) {
		super(parent);
		setGridLayout(1);
		tree.setExpand(true, true);
		tree.addSelectionListener(new LSelectionListener() {
			@Override
			public void onSelect(LSelectionEvent event) {
				LPath path = tree.getSelectedPath();
				int id = path == null ? -1 : collection.getNode(path).id;
				if ((Integer)id == currentValue)
					return;
				currentValue = id;
				newModifyAction(currentValue, id);
			}
		});
		tree.setDragEnabled(false);
		if (!optional)
			return;
		btnNull = new LButton(this, LVocab.instance.DESELECT);
		btnNull.setExpand(true, false);
		btnNull.onClick = new LSelectionListener() {
			@Override
			public void onSelect(LSelectionEvent event) {
				selectNone();
			}
		};
	}

	@SuppressWarnings("serial")
	@Override
	protected void createContent(int flags) {
		tree = new LTree<T, T>(this) {
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
	
	public void addSelectionListener(LSelectionListener l) {
		tree.addSelectionListener(l);
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
		if (collection.children.size() > 0)
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
