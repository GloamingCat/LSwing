package lui.dialog;

import lui.base.LVocab;
import lui.widget.LLabel;
import lui.widget.LText;

public class LStringDialog extends LObjectDialog<String> {
	
	private LText txtName;

	public LStringDialog(LWindow parent, String title) {
		super(parent, title);
	}

	@Override
	public void createContent(int style) {
		super.createContent(style);
		content.setGridLayout(2);

		new LLabel(content, LVocab.instance.TEXT).getCellData().setTargetSize(50, 50);
		txtName = new LText(content);
		txtName.getCellData().setExpand(true, false);
		txtName.getCellData().setRequiredSize(100, 50);
	}
	
	public void open(String initial) {
		txtName.setValue(initial);
		super.open(initial);
	}

	@Override
	protected String createResult(String initial) {
		if (txtName.getValue().equals(initial)) {
			return null;
		} else {
			return txtName.getValue();
		}
	}

}
