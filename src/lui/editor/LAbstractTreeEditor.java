package lui.editor;

import java.util.ArrayList;

import lui.container.LContainer;
import lui.container.LView;
import lui.base.action.LState;
import lui.base.data.LDataCollection;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LDeleteEvent;
import lui.base.event.LEditEvent;
import lui.base.event.LInsertEvent;
import lui.base.event.LMoveEvent;
import lui.base.event.LSelectionEvent;
import lui.base.event.listener.LCollectionListener;
import lui.widget.LEditableTree;

/**
 * Holds common functionalities for LTreeEditor and LListEditor.
 */
public abstract class LAbstractTreeEditor<T, ST> extends LCollectionEditor<T, ST> {
	
	protected ArrayList<LEditor> contentEditors = new ArrayList<>();

	public LAbstractTreeEditor(LContainer parent, int style) {
		super(parent, style);
		setFillLayout(true);
	}
	
	protected void setListeners() {
		super.setListeners();
		getCollectionWidget().addInsertListener(new LCollectionListener<>() {
			public void onInsert(LInsertEvent<T> event) {
				getCollectionWidget().forceSelection(event.parentPath, event.index);
			}
		});
		getCollectionWidget().addDeleteListener(new LCollectionListener<>() {
			public void onDelete(LDeleteEvent<T> event) {
				try {
					getCollectionWidget().forceSelection(event.parentPath, event.index);
				} catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
					try {
						getCollectionWidget().forceSelection(event.parentPath, event.index - 1);
					} catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e2) {
						forceFirstSelection();
					}
				}
			}
		});
		getCollectionWidget().addMoveListener(new LCollectionListener<>() {
			public void onMove(LMoveEvent<T> event) {
				getCollectionWidget().forceSelection(event.destParent, event.destIndex);
			}
		});
		getCollectionWidget().addEditListener(new LCollectionListener<>() {
			public void onEdit(LEditEvent<ST> event) {
				getCollectionWidget().forceSelection(event.path);
			}
		});
		getCollectionWidget().addCheckListener(e -> {
			@SuppressWarnings("unchecked")
			T data = (T) e.data;
			setChecked(data, e.checked);
		});
	}
	
	public void addChild(LObjectEditor<?> editor) {
		getCollectionWidget().addSelectionListener(event -> {
			if (event == null || event.data == null) {
				editor.setObject(null);
				editor.setSelection(null, false, -1);
			} else {
				editor.setObject(event.data);
				editor.setSelection(event.path, getCollectionWidget().isChecked(event.path), event.id);
			}
        });
		editor.collectionEditor = this;
		contentEditors.add(editor);
		addChild((LView) editor);
	}
	
	public abstract LEditableTree<T, ST> getCollectionWidget();
	protected abstract T createNewElement();
	protected abstract T duplicateElement(T original);
	protected abstract String encodeElement(T data);
	protected abstract T decodeElement(String str);

	protected void setChecked(T data, boolean checked) {}

	@Override
	public LDataTree<T> duplicateData(LDataCollection<T> collection) {
		LDataTree<T> node = (LDataTree<T>) collection;
		LDataTree<T> copy = new LDataTree<>(duplicateElement(node.data));
		for(LDataTree<T> child : node.children) {
			LDataTree<T> childCopy = duplicateData(child);
			childCopy.setParent(copy);
		}
		return copy;
	}
	
	@Override
	public String encodeData(LDataCollection<T> collection) {
		LDataTree<T> node = (LDataTree<T>) collection;
		return node.encode(this::encodeElement);
	}
	
	@Override
	public LDataCollection<T> decodeData(String str) {
		return LDataTree.decode(str, this::decodeElement);
	}
	
	public void setObject(Object obj) {
		LPath selectedPath = getCollectionWidget().getSelectedPath();
		super.setObject(obj);
		if (selectedPath != null) {
			getCollectionWidget().forceSelection(selectedPath);
		} else {
			forceFirstSelection();
		}
	}
	
	public void onVisible() {
		LPath selectedPath = getCollectionWidget().getSelectedPath();
		onChildVisible();
		refreshDataCollection();
		if (selectedPath != null) {
			getCollectionWidget().forceSelection(selectedPath);
		} else {  
			forceFirstSelection();
		}
	}
	
	public void forceFirstSelection() {
		if (getObject() != null) {
			LDataTree<T> tree = getObject().toTree();
			getCollectionWidget().setItems(tree);
			if (!tree.children.isEmpty()) {
				getCollectionWidget().forceSelection(new LPath(0));
			} else {
				getCollectionWidget().forceSelection(null);
			}
		} else {
			getCollectionWidget().setItems(null);
			getCollectionWidget().forceSelection(null);
		}
	}
	
	@Override
	public LState getState() {
		final LPath currentPath = getCollectionWidget().getSelectedPath();
		final ArrayList<LState> states = getChildrenStates();
		return new LState() {
			@Override
			public void reset() {
				LSelectionEvent e = getCollectionWidget().select(currentPath);
				if (e != null) {
					getCollectionWidget().notifySelectionListeners(e);
					resetStates(states);
				}
			}
        };
	}
	
	public void refreshDataCollection() {
		setObject(getDataCollection());
	}
	
	protected abstract LDataCollection<T> getDataCollection();

}
