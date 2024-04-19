package myeditor.gui;

import lui.container.LContainer;
import lui.container.LViewFolder;
import lui.base.data.LDataList;
import lui.editor.LDefaultListEditor;
import myeditor.MyVocab;
import myeditor.project.MyProject;

public class MyConfigEditor extends LViewFolder {

	public MyConfigEditor(LContainer parent) {
		super(parent, false);
		LDefaultListEditor<String> typesEditor = new LDefaultListEditor<>(this) {

			@Override
			protected LDataList<String> getDataCollection() {
				return MyProject.current.subContentTypes;
			}
			@Override
			protected String createNewElement() {
				return "New type";
			}
			@Override
			protected String duplicateElement(String original) {
				return original;
			}
			@Override
			protected String encodeElement(String data) {
				return data;
			}
			@Override
			protected String decodeElement(String str) {
				return str;
			}

			@Override
			protected void setChecked(String data, boolean checked) {}

			@Override
			public boolean canDecode(String str) {
				return true;
			}
		};
		typesEditor.setFillLayout(true);
		typesEditor.createMenuInterface();
		addTab(MyVocab.instance.CONTENTTYPES, typesEditor);
	}

}
