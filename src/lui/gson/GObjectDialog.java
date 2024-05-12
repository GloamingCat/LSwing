package lui.gson;

import lui.container.LContainer;
import lui.container.LControlView;
import lui.dialog.LObjectDialog;
import lui.dialog.LWindow;
import lui.editor.LEditor;
import lui.widget.LControlWidget;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class GObjectDialog<T> extends LObjectDialog<T> {

	public GDefaultObjectEditor<T> contentEditor;
	private static final Gson gson = new Gson();

	//////////////////////////////////////////////////
	//region Constructors

	public GObjectDialog(LWindow parent, int minWidth, int minHeight, int style, String title) {
		super(parent, minWidth, minHeight, style, title);
	}

	public GObjectDialog(LWindow parent, int minWidth, int minHeight, String title) {
		super(parent, minWidth, minHeight, 0, title);
	}

	public GObjectDialog(LWindow parent, int style, String title) {
		super(parent, style, title);
	}

	public GObjectDialog(LWindow parent, String title) {
		this(parent, 0, title);
	}

	@Override
	protected void createContent(int style) {
		contentEditor = new GObjectDialogEditor<>(this);
		contentEditor.createMenuInterface();
		content = contentEditor;
		content.setGridLayout(1);
	}

	@SuppressWarnings("unchecked")
	public void open(T initial) {
		super.open(initial);
		T copy = (T) gson.fromJson(gson.toJson(initial), initial.getClass());
		contentEditor.setObject(copy);
	}

	//endregion

	@Override
	protected T createResult(T initial) {
		JsonElement c = gson.toJsonTree(contentEditor.getObject());
		JsonElement i = gson.toJsonTree(initial);
		if (i.equals(c))
			return null;
		return contentEditor.getObject();
	}

	public void addChild(LEditor editor) {
		contentEditor.addChild(editor);
	}

	public void addChild(LEditor editor, String key) {
		contentEditor.addChild(editor, key);
	}

	public void removeChild(LEditor editor) {
		contentEditor.removeChild(editor);
	}

	protected void addControl(LControlWidget<?> control, String attName) {
		contentEditor.addControl(control, attName);
	}

	protected void addControl(LControlView<?> view, String attName) {
		contentEditor.addControl(view, attName);
	}

	protected void removeControl(LControlWidget<?> control) {
		contentEditor.removeControl(control);
	}

	protected void removeControl(LControlView<?> view) {
		contentEditor.removeControl(view);
	}

    private static class GObjectDialogEditor<T> extends GDefaultObjectEditor<T> {

        public GObjectDialogEditor(LContainer parent) {
            super(parent.getTopComposite(), 0, false);
        }

        @Override
        public Type getType() {
            return null;
        }

        @Override
        protected void createContent(int style) {}

    }
}
