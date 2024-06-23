package lui.editor;

import lui.base.data.LDataList;
import lui.container.LContainer;
import lui.base.data.LDataCollection;
import lui.base.data.LDataTree;
import lui.base.data.LPath;

public abstract class LDefaultListEditor<T> extends LListEditor<T, T> {

	public LDefaultListEditor(LContainer parent) {
		super(parent);
	}

	public T getEditableData(LPath path) {
		return getDataCollection().get(path.index);
	}
	
	public void setEditableData(LPath path, T data) {
		getDataCollection().set(path.index, data);
	}

}
