package myeditor.gui;

import lui.base.LFlags;
import lui.container.LContainer;
import lui.container.LFrame;
import lui.container.LImage;
import lui.editor.LObjectEditor;
import lui.widget.LImageButton;
import lui.widget.LLabel;
import lui.widget.LSpinner;
import lui.widget.LText;
import myeditor.MyVocab;
import myeditor.data.MyContent;
import myeditor.project.MyProject;

public class MyContentEditor extends LObjectEditor<MyContent> {

	private final LImageButton btnImage;
	public final MySubContentEditor subEditor;

	/**
	 * Create the composite.
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new lwt.dialog.LShell()
	 */
	public MyContentEditor(LContainer parent) {
		super(parent, true);
		setGridLayout(2);

		LLabel lblName = new LLabel(this, MyVocab.instance.NAME);
		LText txtName = new LText(this);
		txtName.getCellData().setExpand(true, false);
		txtName.addMenu(lblName);
		addControl(txtName, "name");

		LLabel lblValue = new LLabel(this, MyVocab.instance.VALUE);
		LSpinner spnValue = new LSpinner(this);
		spnValue.getCellData().setExpand(true, false);
		spnValue.addMenu(lblValue);
		addControl(spnValue, "value");

		LLabel lblImage = new LLabel(this, MyVocab.instance.IMAGE);
		btnImage = new LImageButton(this, true);
		btnImage.getCellData().setAlignment(LFlags.LEFT);
		btnImage.addMenu(lblImage);
		addControl(btnImage, "img");

		new LLabel(this, 1, 1);
		LImage image = new LImage(this);
		image.getCellData().setExpand(true, true);
		btnImage.setImage(image);

		LFrame frame = new LFrame(this, MyVocab.instance.SUBCONTENT);
		frame.setFillLayout(true);
		frame.getCellData().setSpread(2, 1);
		frame.getCellData().setExpand(true, true);
		subEditor = new MySubContentEditor(frame);
		subEditor.addMenu(frame);
		addChild(subEditor, "subContent");
	}

	@Override
	public MyContent duplicateData(MyContent original) {
		return original.clone();
	}

	@Override
	public String encodeData(MyContent obj) {
		return obj.encode();
	}

	@Override
	public MyContent decodeData(String str) {
		return MyContent.decode(str);
	}

	@Override
	public boolean canDecode(String str) {
		return MyContent.canDecode(str);
	}

	public void onVisible() {
		super.onVisible();
		btnImage.setRootPath(MyProject.current.imagePath());
	}

}
