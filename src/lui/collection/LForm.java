package lui.collection;

import lui.base.LPrefs;
import lui.base.data.LDataList;
import lui.base.data.LPath;
import lui.base.event.LControlEvent;
import lui.base.event.LEditEvent;
import lui.base.event.listener.LCollectionListener;
import lui.base.event.listener.LControlListener;
import lui.base.gui.LControl;
import lui.base.gui.LMenu;
import lui.container.LContainer;
import lui.container.LPanel;
import lui.editor.LPopupMenu;
import lui.widget.LControlWidget;
import lui.widget.LLabel;

import java.util.ArrayList;

public abstract class LForm<T, ST> extends LControlList<T, ST, LForm.LFormRow<T>> implements LEditableControlList<T, ST, LForm.LFormRow<T>> {

	protected LDataList<T> values;
	protected int labelWidth = LPrefs.LABELWIDTH;
	protected int controlWidth = LPrefs.BUTTONWIDTH;

	private boolean editEnabled = false;
	private boolean insertEnabled = false;
	private boolean duplicateEnabled = false;
	private boolean deleteEnabled = false;

	protected ArrayList<LCollectionListener<T>> insertListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> moveListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> deleteListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<ST>> editListeners = new ArrayList<>();

	public static class LFormRow<T> extends LPanel implements LControl<T> {
		private final LControlWidget<T> widget;
		private final LLabel label;

		public LFormRow(LForm<T, ?> form, int labelWidth, int controlWidth) {
			super(form.controls);
			getCellData().setExpand(true, false);
			setGridLayout(2);
			label = new LLabel(this, "");
			label.getCellData().setRequiredSize(labelWidth, LPrefs.WIDGETHEIGHT);
			label.getCellData().setTargetSize(labelWidth, LPrefs.WIDGETHEIGHT);
			widget = form.createControlWidget(this);
			widget.getCellData().setRequiredSize(controlWidth, LPrefs.WIDGETHEIGHT);
			widget.getCellData().setTargetSize(controlWidth, LPrefs.WIDGETHEIGHT);
			widget.getCellData().setExpand(true, false);
			widget.setMenuInterface(form.getMenuInterface());
			widget.addModifyListener(e -> LFormRow.this.setValue(e.newValue));
		}
		@Override
		public void forceModification(T newValue) {
			widget.forceModification(newValue);
		}
		@Override
		public void setValue(Object value) {
			widget.setValue(value);
		}
		@Override
		public T getValue() {
			return widget.getValue();
		}
		public void addModifyListener(LControlListener<T> l) {
			widget.addModifyListener(l);
		}
		@Override
		public void notifyListeners(LControlEvent<T> event) {
			widget.notifyListeners(event);
		}
		@Override
		public boolean canDecode(String str) {
			return widget.canDecode(str);
		}
		@Override
		public void onPasteButton(LMenu menu) {
			widget.onPasteButton(menu);
		}
		@Override
		public void onCopyButton(LMenu menu) {
			widget.onCopyButton(menu);
		}
		@Override
		public void dispose() {
			widget.dispose();
			super.dispose();
		}
	}

	//////////////////////////////////////////////////
	//region Constructor

	public static final int MENU = 1;
	public static final int BUTTONS = 2;

	private final boolean popupMenu;

	public LForm(LContainer parent) {
		this(parent, 0);
	}

	public LForm(LContainer parent, int flags) {
		super(parent, flags);
		popupMenu = (flags & MENU) > 0;
	}

	@Override
	protected void createContent(int flags) {
		super.createContent(flags);
		if ((flags & BUTTONS) > 0) {

		}
	}

	//endregion

	//////////////////////////////////////////////////
	//region Layout

	public void setLabelWidth(int w) {
		labelWidth = w;
	}

	public void setControlWidth(int w) {
		controlWidth = w;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Controls

	@Override
	public LFormRow<T> createControl() {
		LFormRow<T> row = new LFormRow<>(this, labelWidth, controlWidth);
		row.addModifyListener(e -> {
			LEditEvent<T> event = new LEditEvent<>(new LPath(indexOf(row)), e.oldValue, e.newValue);

		});
		if (popupMenu) {
			LPopupMenu menu = new LPopupMenu(row);
			menu.putClientProperty("control", row);
			setEditEnabled(menu, editEnabled);
			setInsertNewEnabled(menu, insertEnabled);
			setDuplicateEnabled(menu, duplicateEnabled);
			setDeleteEnabled(menu, deleteEnabled);
		}
		return row;
	}

	protected abstract LControlWidget<T> createControlWidget(LContainer parent);
	protected abstract void disposeControlWidget(LControlWidget<T> widget);

	//endregion

	//////////////////////////////////////////////////
	//region Refresh

	@Override
	public void refreshControl(LFormRow<T> control, int i) {
		control.label.setText(getLabelText(i));
	}

	protected abstract String getLabelText(final int i);

	//endregion

	//////////////////////////////////////////////////
	//region Menu Handlers

	@Override
	public ArrayList<LCollectionListener<T>> getInsertListeners() {
		return insertListeners;
	}

	@Override
	public ArrayList<LCollectionListener<T>> getDeleteListeners() {
		return deleteListeners;
	}

	@Override
	public ArrayList<LCollectionListener<T>> getMoveListeners() {
		return moveListeners;
	}

	@Override
	public ArrayList<LCollectionListener<ST>> getEditListeners() {
		return editListeners;
	}


	@Override
	public void onCopyButton(LMenu menu) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPasteButton(LMenu menu) {
		// TODO Auto-generated method stub

	}

	public void setEditEnabled(boolean value) {
		editEnabled = value;
	}

	public void setInsertNewEnabled(boolean value) {
		if (insertEnabled != value) {
			insertEnabled = value;
			if (value) {
				LPopupMenu menu = new LPopupMenu(filler);
				setInsertNewEnabled(menu, true);
			} else {
				filler.setComponentPopupMenu(null);
			}
		}
	}

	public void setDuplicateEnabled(boolean value) {
		duplicateEnabled = value;
	}

	public void setDeleteEnabled(boolean value) {
		deleteEnabled = value;
	}

	@Override
	public LEditEvent<ST> edit(LPath path) { return null; }

	//endregion
}
