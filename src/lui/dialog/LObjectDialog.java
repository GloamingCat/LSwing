package lui.dialog;

import lui.base.LPrefs;
import lui.container.LPanel;
import lui.widget.LButton;

import lui.base.LFlags;
import lui.base.LVocab;

public abstract class LObjectDialog<T> extends LWindow {

	protected LPanel content;
	protected LPanel buttons;
	protected T result = null;
	protected T initial = null;

	public LObjectDialog(LWindow parent, int minWidth, int minHeight, int style) {
		super(parent, minWidth, minHeight);
		createContent(style);
		createButtons();
		setGridLayout(1);
		setMargins(LPrefs.FRAMEMARGIN, LPrefs.FRAMEMARGIN);
		content.getCellData().setExpand(true, true);
		content.setPreferredSize(null);
		content.refreshLayoutData();
		content.revalidate();
		buttons.setPreferredSize(null);
		buttons.refreshLayoutData();
		buttons.revalidate();
		pack();
	}

	public LObjectDialog(LWindow parent, int minWidth, int minHeight, int style, String title) {
		this(parent, minWidth, minHeight, style);
		setTitle(title);
	}

	public LObjectDialog(LWindow parent, int minWidth, int minHeight, String title) {
		this(parent, minWidth, minHeight, 0);
		setTitle(title);
	}

	public LObjectDialog(LWindow parent, int style, String title) {
		this(parent, 0, 0, style);
		setTitle(title);
	}

	public LObjectDialog(LWindow parent, String title) {
		this(parent, 0, title);
	}

	protected void createContent(int style) {
		content = new LPanel(this);
		content.setGridLayout(1);
	}

	protected void createButtons() {
		buttons = new LPanel(this);
		buttons.setGridLayout(2);
		buttons.getCellData().setExpand(true, false);
		buttons.getCellData().setAlignment(LFlags.RIGHT);
		LButton btnOk = new LButton(buttons, LVocab.instance.OK);
		btnOk.onClick = event -> {
            result = createResult(initial);
            if (initial.equals(result))
                result = null;
            close();
        };
		LButton btnCancel = new LButton(buttons, LVocab.instance.CANCEL);
		btnCancel.onClick = event -> {
            result = null;
            close();
        };
		btnOk.getCellData().setRequiredSize(LPrefs.BUTTONWIDTH, 0);
		btnOk.getCellData().setAlignment(0);
		btnCancel.getCellData().setRequiredSize(LPrefs.BUTTONWIDTH, 0);
	}

	public void open(T initial) {
		this.result = null;
		this.initial = initial;
		open();
	}

	public T getResult() {
		return result;
	}
	
	protected abstract T createResult(T initial);

}
