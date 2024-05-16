package myeditor.gui;

import lui.container.LContainer;
import lui.editor.LObjectEditor;
import lui.widget.LCombo;
import lui.widget.LLabel;
import lui.widget.LTextBox;
import myeditor.MyVocab;
import myeditor.data.MySubContent;
import myeditor.project.MyProject;

public class MySubContentEditor extends LObjectEditor<MySubContent> {

	LCombo cmbType;

	/**
	 * Create the composite.
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new lwt.dialog.LShell()
	 */
	public MySubContentEditor(LContainer parent) {
		super(parent, false);
	}

	@Override
	protected void createContent(int style) {
		setGridLayout(2);

		new LLabel(this, MyVocab.instance.TYPE);

		cmbType = new LCombo(this, true);
		cmbType.setOptional(false);
		cmbType.getCellData().setExpand(true, false);
		addControl(cmbType, "value");

		new LLabel(this, MyVocab.instance.DESCRIPTION);

		LTextBox txtDesc = new LTextBox(this);
		txtDesc.getCellData().setExpand(true, true);
		addControl(txtDesc, "description");

	}

	@Override
	public MySubContent duplicateData(MySubContent original) {
		return original.clone();
	}

	@Override
	public String encodeData(MySubContent obj) {
		return obj.encode();
	}

	@Override
	public MySubContent decodeData(String str) {
		return MySubContent.decode(str);
	}

	@Override
	public boolean canDecode(String str) {
		return MySubContent.canDecode(str);
	}
	
	public void onVisible() {
		cmbType.setItems(MyProject.current.subContentTypes);
		super.onVisible();
	}

}
