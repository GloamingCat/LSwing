package lui.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonParseException;

import gson.GGlobals;
import lui.container.LContainer;
import lui.editor.LObjectEditor;

import javax.swing.*;

public abstract class GDefaultObjectEditor<T> extends LObjectEditor<T> {

	protected GDefaultObjectEditor(JComponent parent, int style, boolean doubleBuffered) {
		super(parent, style, doubleBuffered);
	}

	public GDefaultObjectEditor(LContainer parent, boolean doubleBuffered) {
		super(parent, doubleBuffered);
	}

	public GDefaultObjectEditor(LContainer parent, int style, boolean doubleBuffered) {
		super(parent, style, doubleBuffered);
	}

	@SuppressWarnings("unchecked")
	public T duplicateData(Object original) {
		T data = (T) original;
		String json = GGlobals.gson.toJson(data, data.getClass());
		return (T) GGlobals.gson.fromJson(json, data.getClass());
	}
	
	@Override
	public String encodeData(T data) {
		return GGlobals.gson.toJson(data, getType());
	}

	@Override
	public T decodeData(String str) {
		try {
			return GGlobals.gson.fromJson(str, getType());
		} catch(JsonParseException e) {
			return null;
		}
	}
	
	@Override
	public boolean canDecode(String str) {
		return true;
	}
	
	public abstract Type getType();

}
