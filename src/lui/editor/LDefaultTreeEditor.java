package lui.editor;

import lui.container.LContainer;
import lui.base.data.LDataTree;
import lui.base.data.LPath;

public abstract class LDefaultTreeEditor<T> extends LTreeEditor<T, T> {

	public LDefaultTreeEditor(LContainer parent) {
		super(parent);
	}

	public LDefaultTreeEditor(LContainer parent, boolean check) {
		super(parent, check);
	}

	public T getEditableData(LPath path) {
		LDataTree<T> node = getDataCollection().getNode(path);
		return node.data;
	}
	
	public void setEditableData(LPath path, T data) {
		LDataTree<T> node = getDataCollection().getNode(path);
		node.data = data;
	}
	
}
