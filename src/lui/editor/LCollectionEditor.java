package lui.editor;

import lui.LMenuInterface;
import lui.container.LContainer;
import lui.base.data.LDataCollection;
import lui.base.data.LPath;
import lui.dialog.LWindowFactory;
import lui.base.event.LDeleteEvent;
import lui.base.event.LEditEvent;
import lui.base.event.LInsertEvent;
import lui.base.event.LMoveEvent;
import lui.base.event.listener.LCollectionListener;
import lui.base.gui.LEditableCollection;

import java.awt.*;

/**
 * Edits the items in a list.
 *
 */

public abstract class LCollectionEditor<T, ST> extends LObjectEditor<LDataCollection<T>> {
	
	public String fieldName = "";
	protected LWindowFactory<ST> shellFactory;

	public LCollectionEditor(LContainer parent, int style) {
		super(parent, style, false);
		setLayout(new GridLayout(1, 1));
		addMenu();
	}
	
	protected void setListeners() {
		getCollectionWidget().addInsertListener(new LCollectionListener<>() {
			public void onInsert(LInsertEvent<T> event) {
				getObject().insert(event.parentPath, event.index, event.node);
			}
		});
		getCollectionWidget().addDeleteListener(new LCollectionListener<>() {
			public void onDelete(LDeleteEvent<T> event) {
				getObject().delete(event.parentPath, event.index);
			}
		});
		getCollectionWidget().addMoveListener(new LCollectionListener<>() {
			public void onMove(LMoveEvent<T> event) {
				getObject().move(event.sourceParent, event.sourceIndex, 
						event.destParent, event.destIndex);
			}
		});
		getCollectionWidget().addEditListener(new LCollectionListener<>() {
			public void onEdit(LEditEvent<ST> event) {
				setEditableData(event.path, event.newData);
			}
		});
	}
	
	public void setObject(Object obj) {
		super.setObject(obj);
		@SuppressWarnings("unchecked")
		LDataCollection<T> db = (LDataCollection<T>) obj;
		getCollectionWidget().setDataCollection(db);
	}
	
	public void setShellFactory(LWindowFactory<ST> factory) {
		shellFactory = factory;
	}
	
	public LEditEvent<ST> onEditItem(LPath path) {
		if (shellFactory == null)
			return null;
		ST oldData = getEditableData(path);
		ST newData = shellFactory.openWindow(getWindow(), oldData);
		if (newData != null) {
			return new LEditEvent<>(path, oldData, newData);
		}
		return null;
	}
	
	public void refreshObject(LPath path) {
		getCollectionWidget().refreshObject(path);
	}

	@Override
	public void setMenuInterface(LMenuInterface mi) {
		super.setMenuInterface(mi);
		getCollectionWidget().setMenuInterface(mi);
	}

	@Override
	public void restart() {
		getCollectionWidget().setDataCollection(getObject());
	}

	@Override
	public void saveObjectValues() {
		LDataCollection<T> obj = getObject();
		if (obj != null)
			obj.set(getCollectionWidget().getDataCollection());
	}
	
	// Widget
	public abstract LEditableCollection<T, ST> getCollectionWidget();

	// Editable Data
	protected abstract ST getEditableData(LPath path);
	protected abstract void setEditableData(LPath path, ST newData);

	// Elements
	protected abstract T createNewElement();
	protected T duplicateElement(T original) {
		String json = encodeElement(original);
		return decodeElement(json);
	}
	protected abstract String encodeElement(T data);
	protected abstract T decodeElement(String str);

}
