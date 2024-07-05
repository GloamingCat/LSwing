package lui.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonParseException;
import gson.GGlobals;
import lui.base.data.LDataCollection;
import lui.base.data.LDataList;
import lui.base.data.LInitializable;
import lui.container.LContainer;
import lui.editor.LDefaultListEditor;

public abstract class GDefaultListEditor<T> extends LDefaultListEditor<T> {

	public GDefaultListEditor(LContainer parent) {
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
			T data = GGlobals.gson.fromJson("{}", getType());
			if (data instanceof LInitializable i)
				i.initialize();
			return data;
		}
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

	@Override
	public String encodeData(LDataCollection<T> collection) {
		LDataList<T> list = (LDataList<T>) collection;
		return GGlobals.encodeJsonList(list, e -> GGlobals.gson.toJson(e, getType()));
	}

	@Override
	public LDataList<T> decodeData(String str) {
		try {
			return GGlobals.decodeJsonList(str, e -> GGlobals.gson.fromJson(e, getType()));
		} catch(JsonParseException e) {
			return null;
		}
	}

	@Override
	public boolean canDecode(String str) {
		return true;
	}

}
