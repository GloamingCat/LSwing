package lwt.dialog;

import java.util.ArrayList;

import lwt.LFlags;
import lwt.container.LPanel;
import lwt.widget.LButton;
import lbase.LVocab;
import lbase.event.LSelectionEvent;
import lbase.event.listener.LSelectionListener;

public abstract class LObjectShell<T> extends LWindow {

	protected LPanel content;
	protected T result = null;
	protected T initial = null;
	
	public LObjectShell(LWindow parent, String title, int style) {
		super(parent);
		getContentComposite().setGridLayout(1);
		content = new LPanel(this);
		content.setFillLayout(true);
		content.setExpand(true, true);
		createContent(style);
		refreshLayout();
	}
	
	public LObjectShell(LWindow parent, String title) {
		this(parent, title, 0);
	}
	
	protected void createContent(int style) {
		LPanel buttons = new LPanel(this);
		buttons.setGridLayout(2);
		buttons.setExpand(true, false);
		buttons.setAlignment(LFlags.RIGHT);
		LButton btnOk = new LButton(buttons, LVocab.instance.OK);
		btnOk.onClick = new LSelectionListener() {
			@Override
			public void onSelect(LSelectionEvent event) {
				result = createResult(initial);
				if (initial.equals(result))
					result = null;
				close();
			}
		};
		LButton btnCancel = new LButton(buttons, LVocab.instance.CANCEL);
		btnCancel.onClick = new LSelectionListener() {
			@Override
			public void onSelect(LSelectionEvent event) {
				result = null;
				close();
			}
		};
	}
	
	public void open(T initial) {
		this.result = null;
		this.initial = initial;
		open();
	}
	
	public T getResult() {
		return result;
	}
	
	protected String[] getItems(ArrayList<?> array) {
		String[] items = new String[array.size()];
		int id = 0;
		for(Object obj : array) {
			String item = String.format("[%03d] ", id);
			items[id] = item + obj.toString();
			id++;
		}
		return items;
	}
	
	protected abstract T createResult(T initial);

}
