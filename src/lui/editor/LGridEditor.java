package lui.editor;

import lui.container.LContainer;
import lui.container.LImage;
import lui.base.data.LDataCollection;
import lui.base.data.LDataList;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.widget.LGrid;

public abstract class LGridEditor<T, ST> extends LCollectionEditor<T, ST> {

	protected LGrid<T, ST> grid;
	
	public LGridEditor(LContainer parent) {
		super(parent);
		grid = createGrid();
		setListeners();
	}

	protected LGrid<T, ST> createGrid() {
		return new LGrid<>(this) {
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
			protected void setImage(LImage img, int i) {
				LGridEditor.this.setImage(img, i);
			}
			@Override
			public boolean canDecode(String str) {
				return true;
			}
		};
	}
	
	protected abstract LDataList<T> getDataCollection();
	
	public void onVisible() {
		onChildVisible();
		grid.setDataCollection(getDataCollection());
	}
		
	@Override
	public LGrid<T, ST> getCollectionWidget() {
		return grid;
	}
	
	protected abstract T createNewElement();
	protected abstract T duplicateElement(T original);
	protected abstract String encodeElement(T data);
	protected abstract T decodeElement(String str);
	protected abstract void setImage(LImage label, int i);

	@Override
	public LDataList<T> duplicateData(LDataCollection<T> collection) {
		LDataList<T> list = (LDataList<T>) collection;
		LDataList<T> copy = new LDataList<T>();
		for(T child : list) {
			T childCopy = duplicateElement(child);
			copy.add(childCopy);
		}
		return copy;
	}
	
	@Override
	public String encodeData(LDataCollection<T> collection) {
		LDataList<T> list = (LDataList<T>) collection;
		LDataList<String> text = new LDataList<>();
		for (T obj : list)
			text.add(obj.toString());
        return String.join( " | ", text);
	}
	
	@Override
	public LDataList<T> decodeData(String str) {
		String[] elements = str.split(" \\| ");
		// Get children
		LDataList<T> list = new LDataList<>();
		for (String element : elements) {
			list.add(decodeElement(element));
		}
		return list;
	}

}
