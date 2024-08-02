package lui.gson;

import com.google.gson.JsonParseException;
import gson.GGlobals;
import lui.base.data.LDataCollection;
import lui.base.data.LInitializable;
import lui.container.LContainer;
import lui.base.data.LDataList;
import lui.editor.LFormEditor;

import java.lang.reflect.Type;

public abstract class GFormEditor<T> extends LFormEditor<T, T> {

	public GFormEditor(LContainer parent, int style) {
		super(parent, style);
	}

	@Override
	@SuppressWarnings("unchecked")
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
