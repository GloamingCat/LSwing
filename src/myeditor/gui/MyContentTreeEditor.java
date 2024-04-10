package myeditor.gui;

import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LEditEvent;
import lui.base.event.listener.LCollectionListener;
import lui.container.LContainer;
import lui.container.LFlexPanel;
import lui.container.LView;
import lui.dialog.LObjectDialog;
import lui.dialog.LWindow;
import lui.dialog.LWindowFactory;
import lui.editor.LTreeEditor;
import myeditor.data.MyContent;
import myeditor.data.MySubContent;
import myeditor.project.MyProject;

public class MyContentTreeEditor extends LView {

    public MyContentTreeEditor(LContainer parent) {
		super(parent, false);
		setFillLayout(true);

		createMenuInterface();

		LFlexPanel sashForm = new LFlexPanel(this, true);

        LTreeEditor<MyContent, MySubContent> treeEditor = new MyContentTree(sashForm);
		treeEditor.getCollectionWidget().setInsertNewEnabled(true);
		treeEditor.getCollectionWidget().setEditEnabled(true);
		treeEditor.getCollectionWidget().setDuplicateEnabled(true);
		treeEditor.getCollectionWidget().setDragEnabled(true);
		treeEditor.getCollectionWidget().setDeleteEnabled(true);
		treeEditor.getCollectionWidget().setCopyEnabled(true);
		treeEditor.getCollectionWidget().setPasteEnabled(true);
		treeEditor.setShellFactory(new LWindowFactory<>() {
			@Override
			public LObjectDialog<MySubContent> createWindow(LWindow parent) {
				return new MySubContentWindow(parent);
			}
		});
		addChild(treeEditor);

        MyContentEditor contentEditor = new MyContentEditor(sashForm);
		contentEditor.setMargins(5, 5);
		treeEditor.addChild(contentEditor);
		treeEditor.getCollectionWidget().addEditListener(new LCollectionListener<>() {
			public void onEdit(LEditEvent<MySubContent> event) {
				contentEditor.subEditor.setObject(event.newData);
			}
		});

		sashForm.setWeights(1, 2);

	}

	private static class MyContentTree extends LTreeEditor<MyContent, MySubContent> {
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
		@Override
		public MySubContent getEditableData(LPath path) {
			LDataTree<MyContent> node = getDataCollection().getNode(path);
			return node.data.subContent;
		}
		@Override
		public void setEditableData(LPath path, MySubContent data) {
			LDataTree<MyContent> node = getDataCollection().getNode(path);
			node.data.subContent = data;
		}

	}

}
