package lwt.editor;

import lwt.container.LContainer;
import lwt.container.LImage;
import lbase.data.LPath;
import lwt.datainterface.LGraphical;

public abstract class LDefaultGridEditor<T extends LGraphical> extends LGridEditor<T, T> {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LDefaultGridEditor(LContainer parent) {
		super(parent);
	}

	public T getEditableData(LPath path) {
		return getDataCollection().get(path.index);
	}
	
	public void setEditableData(LPath path, T data) {
		getDataCollection().set(path.index, data);
	}

	protected void setImage(LImage img, int i) {
		img.setImage(getDataCollection().get(i).toImage());
	}

}
