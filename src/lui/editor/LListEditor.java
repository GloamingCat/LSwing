package lui.editor;

import lui.container.LContainer;
import lui.base.data.LDataList;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.widget.LEditableList;

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
				return LListEditor.this.encodeData(node);
			}
			@Override
			protected LDataTree<T> decodeNode(String str) {
				return LListEditor.this.decodeData(str);
			}
			@Override
			public boolean canDecode(String str) {
				return true;
			}
			@Override
			public boolean isDataChecked(T data) {
				return LListEditor.this.isChecked(data);
			}
		};
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
	
}
