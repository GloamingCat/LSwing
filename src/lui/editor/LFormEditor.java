package lui.editor;

import lui.base.data.LDataCollection;
import lui.base.data.LDataList;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.container.LContainer;
import lui.widget.LControlWidget;
import lui.collection.LForm;

import java.awt.*;

public abstract class LFormEditor<T, ST> extends LCollectionEditor<T, ST> {

	protected LDataList<T> values;
	protected LForm<T, ST> form;

	//////////////////////////////////////////////////
	//region Constructor

	public LFormEditor(LContainer parent, int flags) {
		super(parent, flags);
	}

	@Override
	protected void createContent(int flags) {
		setLayout(new GridLayout(1, 1));
		form = new LForm<>(this, flags) {
			@Override
			public LEditEvent<ST> edit(LPath path) {
				return onEditItem(path);
			}
			@Override
			public T toObject(LPath path) {
				if (path == null)
					return null;
				return LFormEditor.this.getDataCollection().get(path.index);
			}
			@Override
			public LDataTree<T> emptyNode() {
				return new LDataTree<>(createNewElement());
			}
			@Override
			public LDataTree<T> duplicateNode(LPath path) {
				return new LDataTree<> (duplicateElement(getDataCollection().get(path.index)));
			}
			@Override
			protected LControlWidget<T> createControlWidget(LContainer parent) {
				return LFormEditor.this.createControlWidget(parent);
			}
			@Override
			protected String getLabelText(int i) {
				return LFormEditor.this.getLabelText(i);
			}
			@Override
			protected void disposeControlWidget(LControlWidget<T> control) {
				LFormEditor.this.disposeControlWidget(control);
			}
			@Override
			public boolean canDecode(String str) {
				return true;
			}
		};
	}

	@Override
	public LForm<T, ST> getCollectionWidget() {
		return form;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Value

	@Override
	@SuppressWarnings("unchecked")
	public void setObject(Object obj) {
		super.setObject(obj);
		form.setDataCollection((LDataCollection<T>) obj);
	}

	@Override
	public void saveObjectValues() {
		super.saveObjectValues();
		LDataList<T> values = form.getDataCollection();
		LDataList<T> list = getDataCollection();
		for (int i = 0; i < values.size(); i++) {
			if (i < list.size()) {
				list.set(i, values.get(i));
			} else {
				T value = createNewElement();
				list.add(value);
			}
		}
	}

	protected abstract LDataList<T> getDataCollection();

	//endregion

	//////////////////////////////////////////////////
	//region Widgets

	@Override
	public void onVisible() {
		LDataList<?> data = getFormList();
		LDataList<T> defaultList = new LDataList<>();
		for (Object obj : data) {
			defaultList.add(createNewElement());
		}
		form.setDefaultList(defaultList);
		super.onVisible();
	}

	protected abstract LDataList<?> getFormList();
	protected abstract String getLabelText(final int i);
	protected abstract LControlWidget<T> createControlWidget(LContainer parent);
	protected abstract void disposeControlWidget(LControlWidget<T> widget);

	//endregion

}
