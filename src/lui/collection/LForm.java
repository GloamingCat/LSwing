package lui.collection;

import lui.base.LFlags;
import lui.base.LMenuInterface;
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
import lui.widget.LActionButton;
import lui.widget.LLabel;

import java.util.ArrayList;

public abstract class LForm<T, ST, W extends LPanel & LControl<T>>
		extends LControlList<T, ST, LForm.LFormRow<T, W>>
		implements LEditableControlList<T, ST, LForm.LFormRow<T, W>> {

	protected LDataList<T> values;
	protected int labelWidth = LPrefs.LABELWIDTH;
	private boolean editEnabled = false;
	private boolean insertEnabled = false;
	private boolean duplicateEnabled = false;
	private boolean deleteEnabled = false;
	private boolean moveEnabled = false;

	protected ArrayList<LCollectionListener<T>> insertListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> moveListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> deleteListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<ST>> editListeners = new ArrayList<>();

	public static class LFormRow<T, W extends LPanel & LControl<T>>
			extends LPanel implements LControl<T> {
		public final W widget;
		public final LLabel label;

		public LFormRow(LForm<T, ?, W> form, int labelWidth) {
			super(form.controls);
			getCellData().setExpand(true, false);
			setGridLayout(2);
			label = new LLabel(this, "");
			label.getCellData().setRequiredSize(labelWidth, LPrefs.WIDGETHEIGHT);
			label.getCellData().setTargetSize(labelWidth, LPrefs.WIDGETHEIGHT);
			widget = form.createControlWidget(this);
			widget.setMenuInterface(form.getMenuInterface());
		}
		@Override
		public void setMenuInterface(LMenuInterface mi) {
			widget.setMenuInterface(mi);
		}
		@Override
		public LMenuInterface getMenuInterface() {
			return widget.getMenuInterface();
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
	private LPanel buttonPanel;

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
			setGridLayout(1);

			buttonPanel = new LPanel(this);
			buttonPanel.setSequentialLayout(true);
			buttonPanel.getCellData().setExpand(true, false);
		}
	}

	//endregion

	//////////////////////////////////////////////////
	//region Controls

	public void setLabelWidth(int w) {
		labelWidth = w;
	}

	@Override
	public LFormRow<T, W> createControl() {
		LFormRow<T, W> row = new LFormRow<>(this, labelWidth);
		if (popupMenu) {
			LPopupMenu menu = new LPopupMenu(row);
			menu.putClientProperty("control", row);
			setEditEnabled(menu, editEnabled);
			setInsertNewEnabled(menu, insertEnabled);
			setDuplicateEnabled(menu, duplicateEnabled);
			setDeleteEnabled(menu, deleteEnabled);
		}
		if (buttonPanel != null) {
			int cols = 2;
			if (editEnabled) {
				row.label.addMouseListener(e -> {
                    if (e.type == LFlags.DOUBLEPRESS)
                        newEditAction(new LPath(indexOf(row)));
                });
			}
			if (duplicateEnabled) {
				LActionButton button = new LActionButton(row, null);
				button.setIcon("Form.duplicateIcon");
				button.addModifyListener(e -> {
					LPath path = new LPath(indexOf(row));
					newInsertAction(path, duplicateNode(path));
				});
				cols++;
			}
			if (deleteEnabled) {
				LActionButton button = new LActionButton(row, null);
				button.getCellData().setTargetSize(LPrefs.WIDGETHEIGHT, LPrefs.WIDGETHEIGHT);
				button.setIcon("Form.deleteIcon");
				button.addModifyListener(e -> newDeleteAction(null, indexOf(row)));
				cols++;
			}
			if (moveEnabled) {
				LActionButton moveup = new LActionButton(row, null);
				moveup.setIcon("Form.upIcon");
				moveup.addModifyListener(e -> {
					int i = indexOf(row);
					newMoveAction(null, i, null, i+1);
				});
				LActionButton movedown = new LActionButton(row, null);
				movedown.setIcon("Form.downIcon");
				movedown.addModifyListener(e -> {
					int i = indexOf(row);
					newMoveAction(null, i, null, i-1);
				});
				cols+=2;
			}
			row.setGridLayout(cols);
		}
		return row;
	}

	protected abstract W createControlWidget(LContainer parent);

	//endregion

	//////////////////////////////////////////////////
	//region Refresh

	@Override
	public void refreshControl(LFormRow<T, W> control, int i) {
		String text = getLabelText(i);
		control.label.setText(text);
		control.label.setHoverText(text);
		control.refreshLayout();
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
		// TODO Copy selected element
	}

	@Override
	public void onPasteButton(LMenu menu) {
		// TODO Paste new element
	}

	public void setEditEnabled(boolean value) {
		editEnabled = value;
	}

	public void setInsertNewEnabled(boolean value) {
		if (insertEnabled != value) {
			insertEnabled = value;
			if (popupMenu) {
				if (value) {
					LPopupMenu menu = new LPopupMenu(filler);
					setInsertNewEnabled(menu, true);
				} else {
					filler.setComponentPopupMenu(null);
				}
			}
			if (buttonPanel != null) {
				LActionButton button = new LActionButton(buttonPanel, null);
				button.setIcon("Form.addIcon");
				button.addModifyListener(e -> newInsertAction(null, emptyNode()));
			}
		}
	}

	public void setDuplicateEnabled(boolean value) {
		duplicateEnabled = value;
	}

	public void setDeleteEnabled(boolean value) {
		deleteEnabled = value;
	}

	public void setMoveEnabled(boolean value) {
		moveEnabled = value;
	}

	@Override
	public LEditEvent<ST> edit(LPath path) { return null; }

	//endregion
}
