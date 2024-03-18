package myeditor.gui;

import lwt.container.LContainer;
import lwt.container.LFlexPanel;
import lwt.container.LView;
import lbase.data.LDataTree;
import lwt.editor.LDefaultTreeEditor;
import myeditor.data.MyContent;
import myeditor.project.MyProject;

public class MyContentTreeEditor extends LView {
	private static final long serialVersionUID = 1L;

	private LDefaultTreeEditor<MyContent> treeEditor;
	private MyContentEditor contentEditor;
	
	public MyContentTreeEditor(LContainer parent) {
		super(parent, false);
		setFillLayout(true);
		
		createMenuInterface();
		
		LFlexPanel sashForm = new LFlexPanel(this, true);
		
		treeEditor = new MyContentTree(sashForm);
		treeEditor.getCollectionWidget().setInsertNewEnabled(true);
		treeEditor.getCollectionWidget().setEditEnabled(false);
		treeEditor.getCollectionWidget().setDuplicateEnabled(true);
		treeEditor.getCollectionWidget().setDragEnabled(true);
		treeEditor.getCollectionWidget().setDeleteEnabled(true);
		treeEditor.getCollectionWidget().setCopyEnabled(true);
		treeEditor.getCollectionWidget().setPasteEnabled(true);
		addChild(treeEditor);
		
		contentEditor = new MyContentEditor(sashForm);
		contentEditor.setMargins(5, 5);
		treeEditor.addChild(contentEditor);
		
		sashForm.setWeights(1, 2);
		
	}
	
	@SuppressWarnings("serial")
	private class MyContentTree extends LDefaultTreeEditor<MyContent> {
		public MyContentTree(LContainer parent) {
			super(parent);
		}
		@Override
		public LDataTree<MyContent> getDataCollection() {
			return MyProject.current.contentTree;
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
