package myeditor.gui;

import lbase.data.LDataTree;
import lbase.data.LPath;
import lbase.event.LEditEvent;
import lbase.event.listener.LCollectionListener;
import lwt.container.LContainer;
import lwt.container.LFlexPanel;
import lwt.container.LView;
import lwt.dialog.LObjectWindow;
import lwt.dialog.LWindow;
import lwt.dialog.LWindowFactory;
import lwt.editor.LTreeEditor;
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
			public LObjectWindow<MySubContent> createWindow(LWindow parent) {
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
