package lui.editor;

import lui.base.LMenuInterface;
import lui.base.data.LDataCollection;
import lui.base.data.LDataList;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.base.gui.LControl;
import lui.container.LContainer;
import lui.container.LPanel;
import lui.collection.LForm;

import java.awt.*;

public abstract class LFormEditor<T, ST, W extends LPanel & LControl<T>>
		extends LCollectionEditor<T, ST> {

	protected LDataList<T> values;
	protected LForm<T, ST, W> form;

	//////////////////////////////////////////////////
	//region Constructor

	public LFormEditor(LContainer parent, int flags) {
		super(parent, flags);
		setListeners();
		form.setMenuInterface(getMenuInterface());
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
			protected W createControlWidget(LContainer parent) {
				W widget = LFormEditor.this.createControlWidget(parent);
				widget.setMenuInterface(getMenuInterface());
				return widget;
			}
			@Override
			protected String getLabelText(int i) {
				return LFormEditor.this.getLabelText(i);
			}
			@Override
			public void refreshControl(LFormRow<T, W> control, int i) {
				super.refreshControl(control, i);
				refreshControlWidget(control.widget, i);
			}
			@Override
			public boolean canDecode(String str) {
				return true;
			}
		};
	}

	@Override
	public LForm<T, ST, W> getCollectionWidget() {
		return form;
	}

	@Override
	public void setMenuInterface(LMenuInterface mi) {
		super.setMenuInterface(mi);
		form.setMenuInterface(mi);
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

	public void setFormList(LDataList<?> list) {
		if (list != null) {
			LDataList<T> defaultList = new LDataList<>();
			for (Object obj : list) {
				defaultList.add(createNewElement());
			}
			form.setDefaultList(defaultList);
		} else {
			form.setDefaultList(null);
		}
	}

	protected abstract String getLabelText(final int i);
	protected void refreshControlWidget(LControl<T> widget, final int i) {}
	protected abstract W createControlWidget(LContainer parent);

	//endregion

}
