package lui.editor;

import lui.container.LContainer;
import lui.container.LImage;
import lui.base.data.LDataCollection;
import lui.base.data.LDataList;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.collection.LEditableGrid;

public abstract class LGridEditor<T, ST> extends LCollectionEditor<T, ST> {

	protected LEditableGrid<T, ST> grid;
	
	public LGridEditor(LContainer parent) {
		super(parent, 0);
		setListeners();
		grid.setMenuInterface(getMenuInterface());
	}

	protected void createContent(int style) {
		grid = new LEditableGrid<>(this) {
			@Override
			public LEditEvent<ST> edit(LPath path) {
				return onEditItem(path);
			}
			@Override
			public T toObject(LPath path) {
				if (path == null)
					return null;
				return LGridEditor.this.getDataCollection().get(path.index);
			}
			@Override
			public LDataTree<T> emptyNode() {
				return new LDataTree<>(createNewElement());
			}
			@Override
			public LDataTree<T> duplicateNode(LPath path) {
				return new LDataTree<> (duplicateElement(getDataCollection().get(path.index)));
			}
			@Override
			protected void refreshImage(LCell<T> img, T data) {
				LGridEditor.this.setImage(img, data);
			}
		};
	}

	protected abstract LDataList<T> getDataCollection();
	
	public void onVisible() {
		grid.setDataCollection(getDataCollection());
		onChildVisible();
	}
		
	@Override
	public LEditableGrid<T, ST> getCollectionWidget() {
		return grid;
	}

	protected abstract void setImage(LImage label, T data);

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
