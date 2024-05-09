package lui.editor;

import lui.container.LContainer;
import lui.container.LImage;
import lui.base.data.LPath;
import lui.datainterface.LGraphical;

public abstract class LDefaultGridEditor<T extends LGraphical> extends LGridEditor<T, T> {

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
