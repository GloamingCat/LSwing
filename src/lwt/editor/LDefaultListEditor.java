package lwt.editor;

import lwt.container.LContainer;
import lbase.data.LDataCollection;
import lbase.data.LDataTree;
import lbase.data.LPath;

public abstract class LDefaultListEditor<T> extends LListEditor<T, T> {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LDefaultListEditor(LContainer parent) {
		super(parent);
	}

	public T getEditableData(LPath path) {
		return getDataCollection().get(path.index);
	}
	
	public void setEditableData(LPath path, T data) {
		getDataCollection().set(path.index, data);
	}
	
	@Override
	public String encodeData(LDataCollection<T> collection) {
		return super.encodeData(collection.toTree());
	}
	
	@Override
	public LDataTree<T> decodeData(String str) {
		LDataTree<T> node = super.decodeData(str);
		if (node == null)
			return null;
		for (var child : node.children) {
			child.children.clear();
		}
		return node;
	}

}
