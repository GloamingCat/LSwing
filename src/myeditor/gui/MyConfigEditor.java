package myeditor.gui;

import lwt.container.LContainer;
import lwt.container.LViewFolder;
import lbase.data.LDataList;
import lwt.editor.LDefaultListEditor;
import myeditor.MyVocab;
import myeditor.project.MyProject;

public class MyConfigEditor extends LViewFolder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyConfigEditor(LContainer parent) {
		super(parent, false);
		LDefaultListEditor<String> typesEditor = new LDefaultListEditor<>(this) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
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
				return original + "";
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
			public boolean canDecode(String str) {
				return true;
			}
		};
		typesEditor.setFillLayout(true);
		typesEditor.createMenuInterface();
		addTab(MyVocab.instance.CONTENTTYPES, typesEditor);
	}

}
