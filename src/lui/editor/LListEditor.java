package lui.editor;

import lui.base.LMenuInterface;
import lui.base.data.LDataCollection;
import lui.container.LContainer;
import lui.base.data.LDataList;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.collection.LEditableList;

public abstract class LListEditor<T, ST> extends LAbstractTreeEditor<T, ST> {
	
	protected LEditableList<T, ST> list;
	
	public LListEditor(LContainer parent) {
		this(parent, false);
	}
	
	public LListEditor(LContainer parent, boolean check) {
		super(parent, check ? 1 : 0);
		setListeners();
		list.setMenuInterface(getMenuInterface());
	}

	@Override
	protected void createContent(int style) {
		list = new LEditableList<>(this, style == 1) {
			@Override
			public LEditEvent<ST> edit(LPath path) {
				return onEditItem(path);
			}
			@Override
			public T toObject(LPath path) {
				if (path == null || path.index == -1)
					return null;
				return LListEditor.this.getDataCollection().get(path.index);
			}
			@Override
			public LDataTree<T> emptyNode() {
				return new LDataTree<>(createNewElement());
			}
			@Override
			public LDataTree<T> duplicateNode(LDataTree<T> node) {
				T data = duplicateElement(node.data);
				return new LDataTree<> (data);
			}
			@Override
			public LDataTree<T> toNode(LPath path) {
				return LListEditor.this.getDataCollection().toTree().getNode(path);
			}
			@Override
			protected String encodeNode(LDataTree<T> node) {
				return LListEditor.this.encodeElement(node.data);
			}
			@Override
			protected LDataTree<T> decodeNode(String str) {
				return new LDataTree<>(LListEditor.this.decodeElement(str));
			}
			@Override
			public boolean isDataChecked(T data) {
				return LListEditor.this.isChecked(data);
			}
		};
	}

	@Override
	public void setMenuInterface(LMenuInterface mi) {
		super.setMenuInterface(mi);
		list.setMenuInterface(mi);
	}

	public LEditableList<T, ST> getCollectionWidget() {
		return list;
	}

	public void setIncludeID(boolean value) {
		list.setIncludeID(value);
	}

	protected boolean isChecked(T data) {
		return true;
	}

	protected abstract LDataList<T> getDataCollection();

	@Override
	public LDataList<T> decodeData(String str) {
		LDataTree<T> tree = LDataTree.decode(str, this::decodeElement);
		return tree == null ? null : tree.toList();
	}

	@Override
	public String encodeData(LDataCollection<T> collection) {
		LDataList<T> list = (LDataList<T>) collection;
		return list.toTree().encode(this::encodeElement);
	}

}
