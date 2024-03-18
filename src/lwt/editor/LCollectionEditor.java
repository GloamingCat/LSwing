package lwt.editor;

import lwt.LGlobals;
import lwt.LMenuInterface;
import lwt.container.LContainer;
import lbase.data.LDataCollection;
import lbase.data.LPath;
import lwt.dialog.LShellFactory;
import lbase.event.LDeleteEvent;
import lbase.event.LEditEvent;
import lbase.event.LInsertEvent;
import lbase.event.LMoveEvent;
import lbase.event.listener.LCollectionListener;
import lbase.gui.LMenu;
import lwt.widget.LCollection;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Edits the items in a list.
 *
 */

public abstract class LCollectionEditor<T, ST> extends LObjectEditor<LDataCollection<T>> {
	private static final long serialVersionUID = 1L;
	
	public String fieldName = "";
	protected LShellFactory<ST> shellFactory;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LCollectionEditor(LContainer parent) {
		super(parent, false);
		setFillLayout(true);
	}
	
	protected void setListeners() {
		getCollectionWidget().addInsertListener(new LCollectionListener<T>() {
			public void onInsert(LInsertEvent<T> event) {
				getObject().insert(event.parentPath, event.index, event.node);
			}
		});
		getCollectionWidget().addDeleteListener(new LCollectionListener<T>() {
			public void onDelete(LDeleteEvent<T> event) {
				getObject().delete(event.parentPath, event.index);
			}
		});
		getCollectionWidget().addMoveListener(new LCollectionListener<T>() {
			public void onMove(LMoveEvent<T> event) {
				getObject().move(event.sourceParent, event.sourceIndex, 
						event.destParent, event.destIndex);
			}
		});
		getCollectionWidget().addEditListener(new LCollectionListener<ST>() {
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
	
	public void setShellFactory(LShellFactory<ST> factory) {
		shellFactory = factory;
	}
	
	public LEditEvent<ST> onEditItem(LPath path) {
		if (shellFactory == null)
			return null;
		ST oldData = getEditableData(path);
		ST newData = shellFactory.openShell(getWindow(), oldData);
		if (newData != null) {
			return new LEditEvent<ST>(path, oldData, newData);
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
	
	@Override
	public void onCopyButton(LMenu menu) {
		String str = encodeData(getObject());
		LGlobals.clipboard.setContents(new StringSelection(str), null);
	}
	
	@Override
	public void onPasteButton(LMenu menu) {
		DataFlavor dataFlavor = DataFlavor.stringFlavor;
		if (!LGlobals.clipboard.isDataFlavorAvailable(dataFlavor))
			return;
		try {
			String str = (String) LGlobals.clipboard.getData(dataFlavor);
			if (str == null)
				return;
			LDataCollection<T> newValue = decodeData(str);
			LDataCollection<T> oldValue = getObject();
			if (newValue != null && !newValue.equals(oldValue)) {
				oldValue = oldValue.clone();
				getCollectionWidget().setDataCollection(newValue);
				newModifyAction(oldValue, newValue);
			}
		} catch (ClassCastException | UnsupportedFlavorException | IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	// Widget
	public abstract LCollection<T, ST> getCollectionWidget();

	// Editable Data
	protected abstract ST getEditableData(LPath path);
	protected abstract void setEditableData(LPath path, ST newData);
	
}
