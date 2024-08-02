package lui.editor;

import lui.container.LContainer;
import lui.base.data.LDataCollection;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.collection.LEditableTree;

public abstract class LTreeEditor<T, ST> extends LAbstractTreeEditor<T, ST> {
	
	protected LEditableTree<T, ST> tree;
	
	public LTreeEditor(LContainer parent) {
		this(parent, false);
	}
	
	public LTreeEditor(LContainer parent, boolean check) {
		super(parent, check ? 1 : 0);
		setListeners();
		tree.setMenuInterface(getMenuInterface());
	}

	@Override
	protected void createContent(int style) {
		tree = new LEditableTree<>(this, style == 1) {
			@Override
			public LEditEvent<ST> edit(LPath path) {
				return onEditItem(path);
			}
			@Override
			public T toObject(LPath path) {
				LDataTree<T> node = LTreeEditor.this.getDataCollection().getNode(path);
				if (node == null)
					return null;
				return node.data;
			}
			@Override
			public LDataTree<T> toNode(LPath path) {
				return LTreeEditor.this.getDataCollection().getNode(path);
			}
			@Override
			protected LDataTree<T> emptyNode() {
				return new LDataTree<>(createNewElement());
			}
			@Override
			protected LDataTree<T> duplicateNode(LDataTree<T> node) {
				return LTreeEditor.this.duplicateData(node);
			}
			@Override
			protected String encodeNode(LDataTree<T> node) {
				return LTreeEditor.this.encodeData(node);
			}
			@Override
			protected LDataTree<T> decodeNode(String str) {
				return LTreeEditor.this.decodeData(str);
			}
			@Override
			public boolean canDecode(String str) {
				return true;
			}
			@Override
			public boolean isDataChecked(T data) {
				return LTreeEditor.this.isChecked(data);
			}
		};
	}

	public LEditableTree<T, ST> getCollectionWidget() {
		return tree;
	}

	protected boolean isChecked(T data) {
		return true;
	}

	public abstract LDataTree<T> getDataCollection();

	@Override
	public LDataTree<T> decodeData(String str) {
		return LDataTree.decode(str, this::decodeElement);
	}

	@Override
	public String encodeData(LDataCollection<T> collection) {
		LDataTree<T> node = (LDataTree<T>) collection;
		return node.encode(this::encodeElement);
	}

	@Override
	public LDataTree<T> duplicateData(LDataCollection<T> collection) {
		LDataTree<T> node = (LDataTree<T>) collection;
		LDataTree<T> copy = new LDataTree<>(duplicateElement(node.data));
		for(LDataTree<T> child : node.children) {
			LDataTree<T> childCopy = duplicateData(child);
			childCopy.setParent(copy);
		}
		return copy;
	}

}
