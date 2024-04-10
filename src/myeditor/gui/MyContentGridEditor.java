package myeditor.gui;

import lui.base.data.LDataList;
import lui.container.LContainer;
import lui.container.LFlexPanel;
import lui.container.LView;
import lui.editor.LDefaultGridEditor;
import myeditor.data.MyContent;
import myeditor.project.MyProject;

public class MyContentGridEditor extends LView {

    public MyContentGridEditor(LContainer parent) {
		super(parent, false);
		setFillLayout(true);

		createMenuInterface();

		LFlexPanel sashForm = new LFlexPanel(this, true);

        LDefaultGridEditor<MyContent> gridEditor = new MyContentGrid(sashForm);
		gridEditor.getCollectionWidget().cellWidth = 40;
		gridEditor.getCollectionWidget().cellHeight = 40;
		gridEditor.getCollectionWidget().setColumns(4);
		addChild(gridEditor);

        MyContentEditor contentEditor = new MyContentEditor(sashForm);
		contentEditor.setMargins(5, 5);
		gridEditor.addChild(contentEditor);

		sashForm.setWeights(1, 2);

	}

	private static class MyContentGrid extends LDefaultGridEditor<MyContent> {
		public MyContentGrid(LContainer parent) {
			super(parent);
		}
		@Override
		public LDataList<MyContent> getDataCollection() {
			return MyProject.current.contentGrid;
		}
		@Override
		public MyContent createNewElement() {
			return new MyContent("New Element");
		}
		@Override
		public MyContent duplicateElement(MyContent original) {
			return original.clone();
		}
		@Override
		protected String encodeElement(MyContent data) {
			return data.encode();
		}
		@Override
		protected MyContent decodeElement(String str) {
			return MyContent.decode(str);
		}
		@Override
		public boolean canDecode(String str) {
			return MyContent.canDecode(str);
		}
	}
	
}
