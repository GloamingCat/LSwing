package lwt.editor;

import lwt.container.LContainer;
import lbase.data.LDataTree;
import lbase.data.LPath;

public abstract class LDefaultTreeEditor<T> extends LTreeEditor<T, T> {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LDefaultTreeEditor(LContainer parent) {
		super(parent);
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
