package lui.gson;

import java.lang.reflect.Type;

import gson.GGlobals;
import lui.container.LContainer;
import lui.editor.LDefaultTreeEditor;

public abstract class GDefaultTreeEditor<T> extends LDefaultTreeEditor<T> {

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

	@Override
	public boolean canDecode(String str) {
		return true;
	}
	
	public abstract Type getType();
	
}
