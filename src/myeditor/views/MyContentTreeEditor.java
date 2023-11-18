package myeditor.views;

import lwt.action.LActionStack;
import lwt.container.LContainer;
import lwt.container.LSashPanel;
import lwt.container.LView;
import lwt.dataestructure.LDataTree;
import lwt.editor.LDefaultTreeEditor;
import myeditor.data.MyContent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

public class MyContentTreeEditor extends LView {

	private LDefaultTreeEditor<MyContent> treeEditor;
	private MyContentEditor contentEditor;
	
	public MyContentTreeEditor(LContainer parent) {
		super(parent, true, false);
		
		actionStack = new LActionStack(this);
		
		LSashPanel sashForm = new LSashPanel(this, true);
		
		final LDataTree<MyContent> contentTree = createExampleTree();
		treeEditor = new LDefaultTreeEditor<MyContent>(sashForm) {
			@Override
			public LDataTree<MyContent> getDataCollection() {
				return contentTree;
			}
			@Override
			public MyContent createNewData() {
				return new MyContent("Bla", 0);
			}
			@Override
			public MyContent duplicateData(MyContent original) {
				return new MyContent(original.name, original.value);
			}
		};
		treeEditor.getCollectionWidget().setInsertNewEnabled(true);
		treeEditor.getCollectionWidget().setEditEnabled(false);
		treeEditor.getCollectionWidget().setDuplicateEnabled(true);
		treeEditor.getCollectionWidget().setDragEnabled(true);
		treeEditor.getCollectionWidget().setDeleteEnabled(true);
		addChild(treeEditor);
		
		contentEditor = new MyContentEditor(sashForm);
		contentEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeEditor.addChild(contentEditor);
		
		sashForm.setWeights(new int[] {1, 2});
		
	}
	
	private LDataTree<MyContent> createExampleTree() {
		LDataTree<MyContent> root = new LDataTree<>();
		for (int i = 0; i < 3; i++) {
			String name = "item " + i;
			MyContent data = new MyContent(name, i);
			LDataTree<MyContent> node = new LDataTree<MyContent>(data, root);
			for (int j = 0; j < 3; j++) {
				name = "item " + i + " " + j;
				data = new MyContent(name, j);
				LDataTree<MyContent> subnode = new LDataTree<MyContent>(data, node);
				for (int k = 0; k < 3; k++) {
					name = "item " + i + " " + j + " " + k;
					data = new MyContent(name, k);
					new LDataTree<MyContent>(data, subnode);
				}
			}
	    }
		return root;
	}

}
