package lui.editor;

import lui.container.LContainer;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.widget.LTree;

public abstract class LTreeEditor<T, ST> extends LAbstractTreeEditor<T, ST> {
	
	protected LTree<T, ST> tree;
	
	public LTreeEditor(LContainer parent) {
		this(parent, false);
	}
	
	public LTreeEditor(LContainer parent, boolean check) {
		super(parent);
		tree = createTree(check);
		setListeners();
		tree.setMenuInterface(getMenuInterface());
	}

	protected LTree<T, ST> createTree(boolean check) {
		return new LTree<>(this, check) {
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

	public LTree<T, ST> getCollectionWidget() {
		return tree;
	}

	protected boolean isChecked(T data) {
		return true;
	}

	public abstract LDataTree<T> getDataCollection();


}
