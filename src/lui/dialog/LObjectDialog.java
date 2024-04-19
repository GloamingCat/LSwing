package lui.dialog;

import lui.base.LPrefs;
import lui.container.LPanel;
import lui.widget.LButton;

import lui.base.LFlags;
import lui.base.LVocab;

public abstract class LObjectDialog<T> extends LWindow {

	protected LPanel content;
	protected T result = null;
	protected T initial = null;

	public LObjectDialog(LWindow parent, int style) {
		super(parent);
		setGridLayout(1);
		setMargins(LPrefs.FRAMEMARGIN, LPrefs.FRAMEMARGIN);
		content = new LPanel(this);
		content.setGridLayout(1);
		content.getCellData().setExpand(true, true);
		createContent(style);
	}

	public LObjectDialog(LWindow parent, int style, String title) {
		this(parent, style);
		setTitle(title);
	}

	public LObjectDialog(LWindow parent, String title) {
		this(parent, 0, title);
	}

	protected void createContent(int style) {
		LPanel buttons = new LPanel(this);
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
		btnOk.getCellData().setMinimumSize(80, 0);
		btnOk.getCellData().setAlignment(0);
		btnCancel.getCellData().setMinimumSize(80, 0);
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
