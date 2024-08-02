package lui.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lui.base.action.LControlAction;
import lui.base.data.LPath;
import lui.base.event.LControlEvent;
import lui.base.event.LSelectionEvent;
import lui.base.event.listener.LControlListener;
import lui.base.event.listener.LSelectionListener;
import lui.base.gui.LControl;
import lui.base.gui.LMenu;
import lui.LGlobals;
import lui.LMenuInterface;
import lui.container.LContainer;
import lui.container.LControlView;
import lui.container.LView;
import lui.widget.LControlWidget;

import javax.swing.*;

/**
 * A specific type of Editor that edits a single object.
 * It has a collection of different Controls to edit the
 * object's fields.
 *
 */
public abstract class LObjectEditor<T> extends LEditor implements LControl<T> {

	protected HashMap<LControlWidget<?>, String> controlMap = new HashMap<>();
	protected HashMap<LEditor, String> editorMap = new HashMap<>();
	public LCollectionEditor<?, ?> collectionEditor;
	protected T currentObject;
	protected LPath currentPath;
	protected boolean checked;
	protected int id;
	protected ArrayList<LSelectionListener> selectionListeners = new ArrayList<>();
	protected ArrayList<LControlListener<T>> modifyListeners = new ArrayList<>();
	
	//////////////////////////////////////////////////
	//region Constructors

	protected LObjectEditor(JComponent parent, int style, boolean doubleBuffered) {
		super(parent, doubleBuffered);
		createContent(style);
		addMenu();
	}

	public LObjectEditor(LContainer parent, int style, boolean doubleBuffered) {
		this(parent.getContentComposite(), style, doubleBuffered);
	}
	public LObjectEditor(LContainer parent, boolean doubleBuffered) {
		this(parent.getContentComposite(), 0, doubleBuffered);
	}

	protected abstract void createContent(int style);

	//endregion

	//////////////////////////////////////////////////
	//region Children
	
	public void addChild(LEditor editor, String key) {
		if (key.isEmpty()) {
			addChild(editor);
		} else {
			addChild((LView) editor);
			editorMap.put(editor, key);
		}
	}
	
	public <CT> void addControl(LControlView<CT> view, String key) {
		addControl(view.getControl(), key);
		addChild(view);
	}

	public <CT> void addControl(LControlWidget<CT> control, String key) {
		controlMap.put(control, key);
		control.setMenuInterface(getMenuInterface());
		control.addModifyListener(new LControlListener<>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onModify(LControlEvent<CT> event) {
				if (!controlMap.containsKey(control)) {
					control.removeModifyListener(this);
					return;
				}
				if (key.isEmpty()) {
					currentObject = (T) event.newValue;
				} else if (currentObject != null) {
					if (modifyListeners.isEmpty())
						setFieldValue(currentObject, key, event.newValue);
					else {
						T oldValue = duplicateData(currentObject);
						setFieldValue(currentObject, key, event.newValue);
						LObjectEditor.this.notifyListeners(new LControlEvent<>(oldValue, currentObject));
					}
					if (collectionEditor != null && currentPath != null && event.detail >= 0)
						collectionEditor.refreshObject(currentPath);
				}
				refresh();
			}
		});
	}
	
	public <CT> void removeControl(LControlView<CT> view) {
		removeControl(view.getControl());
		removeChild(view);
	}
	
	public <CT> void removeControl(LControlWidget<CT> control) {
		controlMap.remove(control);
	}
	
	//endregion
	
	public void refresh() {}
	
	public void setMenuInterface(LMenuInterface mi) {
		super.setMenuInterface(mi);
		for(LControlWidget<?> control : controlMap.keySet()) {
			control.setMenuInterface(mi);
		}
	}
	
	//////////////////////////////////////////////////
	//region Object
	
	@SuppressWarnings("unchecked")
	public void setObject(Object obj) {
		try {
			if (currentObject != null)
				saveObjectValues();
			currentObject = (T) obj;
			for(LEditor subEditor : subEditors) {
				subEditor.setObject(obj);
			}
			if (obj != null) {
				for(Map.Entry<LEditor, String> entry : editorMap.entrySet()) {
					Object value = getFieldValue(obj, entry.getValue());
					entry.getKey().setObject(value);
				}
				for(Map.Entry<LControlWidget<?>, String> entry : controlMap.entrySet()) {
					if (entry.getValue().isEmpty()) {
						entry.getKey().setValue(obj);
					} else {
						Object value = getFieldValue(obj, entry.getValue());
						try {
							entry.getKey().setValue(value);
						} catch (Exception e) {
							System.err.println(this.getClass() + ": " + entry.getValue());
							throw e;
						}
					}
				}
			} else {
				for(Map.Entry<LEditor, String> entry : editorMap.entrySet()) {
					entry.getKey().setObject(null);
				}
				for(Map.Entry<LControlWidget<?>, String> entry : controlMap.entrySet()) {
					entry.getKey().setValue(null);
				}
			}
			for(LSelectionListener listener : selectionListeners) {
				listener.onSelect(new LSelectionEvent(currentPath, obj, id, checked));
			}
		} catch (Exception e) {
			System.err.println(this.getClass());
			throw e;
		}
	}
	
	public T getObject() {
		return currentObject;
	}
	
	public void setSelection(LPath path, boolean checked, int id) {
		currentPath = path;
		this.checked = checked;
		this.id = id;
	}
	
	public void saveObjectValues() {
		if (getObject() == null)
			return;
		for(Map.Entry<LControlWidget<?>, String> entry : controlMap.entrySet()) {
			LControlWidget<?> control = entry.getKey();
			control.notifyEmpty();
		}
	}
	
	public void addSelectionListener(LSelectionListener listener) {
		selectionListeners.add(listener);
	}

	@Override
	public T getValue() {
		return currentObject;
	}

	@Override
	public void setValue(Object value) {
		T oldValue = currentObject;
		setObject(value);
		currentObject = oldValue;
		saveObjectValues();
		for(Map.Entry<LEditor, String> entry : editorMap.entrySet()) {
			LEditor editor = entry.getKey();
			editor.saveObjectValues();
		}
		currentObject = null;
		setObject(oldValue);
	}

	public Map<String, Object> getFieldValues() {
		return getFieldValues(currentObject);
	}

	public Object getFieldValue(String name) {
		return getFieldValue(currentObject, name);
	}

	public void setFieldValue(String name, Object value) {
		setFieldValue(currentObject, name, value);
	}

	public static Map<String, Object> getFieldValues(Object object) {
		Map<String, Object> map = new HashMap<>();
		if (object == null)
			return map;
		for (Field field : object.getClass().getFields()) {
            try {
                map.put(field.getName(), field.get(object));
            } catch (IllegalAccessException ignored) { }
        }
		return map;
	}

	public static Object getFieldValue(Object object, String name) {
		try {
			Field field = object.getClass().getField(name);
			return field.get(object);
		} catch (NoSuchFieldException e) {
			System.err.println(name + " not found in " + object.getClass());
			e.printStackTrace();
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setFieldValue(Object object, String name, Object value) {
		try {
			Field field = object.getClass().getField(name);
			field.set(object, value);
		} catch (NoSuchFieldException e) {
			System.err.println(name + " not found in " + object.getClass());
			e.printStackTrace();
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	//endregion
	
	//////////////////////////////////////////////////
	//region Events
	
	protected void newModifyAction(T oldValue, T newValue) {
		LControlEvent<T> event = new LControlEvent<>(oldValue, newValue);
		if (getActionStack() != null) {
			getActionStack().newAction(new LControlAction<>(this, event));
		}
		notifyListeners(event);
	}
	
	public void notifyListeners(LControlEvent<T> event) {
		for (LControlListener<T> listener : modifyListeners) {
			listener.onModify(event);
		}
	}
	
	public void addModifyListener(LControlListener<T> listener) {
		modifyListeners.add(listener);
	}

	public void forceModification(T newValue) {
		T oldValue = duplicateData(currentObject);
		setValue(newValue);
		newModifyAction(oldValue, newValue);
	}
	
	//endregion
	
	//////////////////////////////////////////////////
	//region Clipboard
	
	public void onCopyButton(LMenu menu) {
		String str = encodeObject();
		LGlobals.clipboard.setContents(new StringSelection(str), null);
	}
	
	public void onPasteButton(LMenu menu) {
		DataFlavor dataFlavor = DataFlavor.stringFlavor;
		if (!LGlobals.clipboard.isDataFlavorAvailable(dataFlavor))
			return;
		try {
			String str = (String) LGlobals.clipboard.getData(dataFlavor);
			if (str == null)
				return;
			T newValue = decodeData(str);
			if (newValue != null && !newValue.equals(currentObject))
				forceModification(newValue);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public abstract T duplicateData(T obj);
	public abstract T decodeData(String str);
	public abstract String encodeData(T obj);
	public String encodeObject() {
		if (currentObject == null)
			return null;
		return encodeData(currentObject);
	}

	//endregion

}
