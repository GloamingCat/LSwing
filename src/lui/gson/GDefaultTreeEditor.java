package lui.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonParseException;
import gson.GGlobals;
import lui.base.data.LDataCollection;
import lui.base.data.LDataTree;
import lui.container.LContainer;
import lui.editor.LDefaultTreeEditor;

public abstract class GDefaultTreeEditor<T> extends LDefaultTreeEditor<T> {

	public GDefaultTreeEditor(LContainer parent, boolean check) {
		super(parent, check);
	}

	public GDefaultTreeEditor(LContainer parent) {
		super(parent);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T createNewElement() {
		if (getType() == String.class) {
			return (T) "";
		} else if (getType() == Integer.class) {
			return (T) (Integer) 0;
		} else {
			return GGlobals.gson.fromJson("{}", getType());
		}
	}

	@Override
	public T duplicateElement(T original) {
		if (getType() != original.getClass())
			throw new ClassCastException("Object cannot be cast to " + getType().getTypeName());
		String json = GGlobals.gson.toJson(original, getType());
		return GGlobals.gson.fromJson(json, getType());
	}
	
	@Override
	protected String encodeElement(T data) {
		return GGlobals.gson.toJson(data, getType());
	}

	@Override
	protected T decodeElement(String str) {
		return GGlobals.gson.fromJson(str, getType());
	}

	public abstract Type getType();

	@SuppressWarnings("unchecked")
	@Override
	public LDataTree<T> duplicateData(LDataCollection<T> original) {
		String json = GGlobals.gson.toJson(original, original.getClass());
		return (LDataTree<T>) GGlobals.gson.fromJson(json, original.getClass());
	}

	@Override
	public String encodeData(LDataCollection<T> data) {
		return GGlobals.gson.toJson(data);
	}

	@Override
	public LDataTree<T> decodeData(String str) {
		try {
            return GGlobals.decodeJsonTree(str, e -> GGlobals.gson.fromJson(e, getType()));
		} catch(JsonParseException e) {
			return null;
		}
	}

	@Override
	public boolean canDecode(String str) {
		return true;
	}

}
