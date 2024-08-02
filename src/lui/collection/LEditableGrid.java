package lui.collection;

import lui.base.event.listener.LCollectionListener;
import lui.base.gui.LMenu;
import lui.container.LContainer;
import lui.editor.LPopupMenu;

import java.util.ArrayList;

public abstract class LEditableGrid<T, ST> extends LGrid<T, ST> implements LEditableControlList<T, ST, LGrid.LCell<T>> {

	private boolean editEnabled = false;
	private boolean insertEnabled = false;
	private boolean duplicateEnabled = false;
	private boolean deleteEnabled = false;

	protected ArrayList<LCollectionListener<T>> insertListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> moveListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> deleteListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<ST>> editListeners = new ArrayList<>();


	//////////////////////////////////////////////////
	//region Constructor

	public LEditableGrid(LContainer parent) {
		super(parent);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Controls

	@Override
	public LCell<T> createControl() {
		LCell<T> img = super.createControl();
		LPopupMenu menu = new LPopupMenu(img);
		menu.putClientProperty("control", img);
		setEditEnabled(menu, editEnabled);
		setInsertNewEnabled(menu, insertEnabled);
		setDuplicateEnabled(menu, duplicateEnabled);
		setDeleteEnabled(menu, deleteEnabled);
		return img;
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Menu Handlers

	@Override
	public boolean canDecode(String str) {
		return true;
	}

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

	//endregion

}
