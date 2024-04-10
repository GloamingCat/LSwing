package lui.widget;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Stack;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.JTree.DropLocation;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import lui.container.LContainer;
import lui.base.action.collection.LMoveAction;
import lui.base.data.LDataCollection;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LMoveEvent;
import lui.base.event.LSelectionEvent;

public abstract class LTreeBase<T, ST> extends LSelectableCollection<T, ST> {
	private static final long serialVersionUID = 1L;

	protected JTree tree;
	protected DefaultMutableTreeNode root;
	
	protected class ItemData {
		public T data;
		public int id;
		public String name;
		public ItemData(String name, int id, T data) {
			this.data = data;
			this.id = id;
			this.name = name;
		}
		public String toString() {
			return name;
		}
	}

		
	//////////////////////////////////////////////////
	//region Constructors
	
	public LTreeBase(LContainer parent) {
		this(parent, false);
	}

	public LTreeBase(LContainer parent, boolean check) {
		super(parent, (check ? 1 : 0));
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (tree.getSelectionCount() > 0) {
					DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					LPath path = toPath(item);
					LSelectionEvent event = new LSelectionEvent(path, toObject(path), getID(item));
					//event.check = e.detail == SWT.CHECK;
					notifySelectionListeners(event);
				}
			}
		});
	}
	
	@Override
	protected void createContent(int flags) {
		root = new DefaultMutableTreeNode();
		root.setUserObject(new ItemData("", -1, null));
		tree = new JTree(root);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setIcon(null);
		tree.setCellRenderer(renderer);
		setDragEnabled(true);
		tree.setTransferHandler(new TreeTransferHandler());
		tree.setDropMode(DropMode.ON_OR_INSERT);
		add(tree);
	}
	
	@Override
	public void setHoverText(String text) {
		tree.setToolTipText(text);
	}
	
	//endregion
	
	//////////////////////////////////////////////////
	//region Drag
	
	public void setDragEnabled(boolean value) {
		tree.setDragEnabled(value);
	}
	
	protected boolean canDrop(DropLocation dl, int action) {
		DefaultMutableTreeNode source = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		DefaultMutableTreeNode target = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();
		while (target != null) {
			if (target == source)
				return false;
			target = (DefaultMutableTreeNode) target.getParent();
		}
		return true;
	}
	
	public LMoveEvent<T> drop(DefaultMutableTreeNode sourceItem, DropLocation dl) {
		DefaultMutableTreeNode targetItem = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();
		DefaultMutableTreeNode sourceParent = (DefaultMutableTreeNode) sourceItem.getParent();
		int sourceIndex = sourceParent.getIndex(sourceItem);
		LDataTree<T> node = disposeTreeItem(sourceItem);
		DefaultMutableTreeNode destParent = (DefaultMutableTreeNode) targetItem.getParent();
		int destIndex = destParent.getIndex(targetItem);
		LMoveEvent<T> e = moveTreeItem(node, sourceParent, sourceIndex, destParent, destIndex);
		if (e == null) {
			return null;
		}
		if (menuInterface != null) {
			LMoveAction<T> action = new LMoveAction<T>(this, e.sourceParent, e.sourceIndex, e.destParent, e.destIndex);
			menuInterface.actionStack.newAction(action);
		}
		notifyMoveListeners(e);
		return e;
	}
 	
	//endregion
	
	//////////////////////////////////////////////////
	//region Auxiliary
	
	protected int indexOf(DefaultMutableTreeNode item) {
		return item.getParent().getIndex(item);
	}

	protected boolean isOutOfBounds(DefaultMutableTreeNode parent, int i) {
		return i >= parent.getChildCount();
	}

	protected int getID(DefaultMutableTreeNode item) {
		if (item == null)
			return -1;
		@SuppressWarnings("unchecked")
		ItemData data = (ItemData) item.getUserObject();
		return item == null ? -1 : (int) data.id;
	}

	//////////////////////////////////////////////////
	//region Internal operations
	
	protected LSelectionEvent selectTreeItem(DefaultMutableTreeNode item) {
		if (item == null) {
			tree.clearSelection();
			return new LSelectionEvent(null, null, -1);
		} else {
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			tree.setSelectionPath(new TreePath(model.getPathToRoot(item)));
			LPath path = toPath(item);
			return new LSelectionEvent(path, toObject(path), getID(item));
		}
	}

	protected DefaultMutableTreeNode createTreeItem(DefaultMutableTreeNode parent, int index, LDataTree<T> node) {
		ItemData data = new ItemData(dataToString(node.data), node.id, node.data);
		DefaultMutableTreeNode newItem = new DefaultMutableTreeNode();
		newItem.setUserObject(data);
		if (index == -1) {
			parent.add(newItem);
		} else {
			parent.insert(newItem, index);
		}
		createTreeItems(newItem, node);
		return newItem;
	}

	protected void createTreeItems(DefaultMutableTreeNode item, LDataTree<T> node) {
		for(LDataTree<T> child : node.children) {
			ItemData itemData = new ItemData(dataToString(node.data), node.id, node.data);
			DefaultMutableTreeNode newItem = new DefaultMutableTreeNode();
			newItem.setUserObject(itemData);
			item.add(newItem);
			createTreeItems(newItem, child);
		}
	}

	protected LDataTree<T> disposeTreeItem(DefaultMutableTreeNode item) {
		LDataTree<T> data = toNode(item);
		item.removeFromParent();
		return data;
	}

	protected LMoveEvent<T> moveTreeItem(LDataTree<T> node, DefaultMutableTreeNode sourceParent, int sourceIndex, 
			DefaultMutableTreeNode destParent, int destIndex) {
		if (sourceParent == destParent && destIndex == sourceIndex)
			return null;
		LPath sourceParentPath = toPath(sourceParent);
		LPath destParentPath = toPath(destParent);
		createTreeItem(destParent, destIndex, node);
		return new LMoveEvent<T>(sourceParentPath, sourceIndex, destParentPath, destIndex, node);
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Tree Events

	public LSelectionEvent select(LPath path) {
		if (path == null) {
			tree.clearSelection();
			return new LSelectionEvent(null, null, -1);
		}
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item == null) {
			new Exception("Couldn't find tree item: " + path.toString()).printStackTrace();
			return null;
		}
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		tree.setSelectionPath(new TreePath(model.getPathToRoot(item)));
		return new LSelectionEvent(path, toObject(path), getID(item));
	}

	public LMoveEvent<T> move(LPath sourceParent, int sourceIndex, LPath destParent, int destIndex) {
		try {
			DefaultMutableTreeNode sourceItem = toTreeItem(sourceParent, sourceIndex);
			LDataTree<T> node = disposeTreeItem(sourceItem);
			LMoveEvent<T> e = moveTreeItem(node, toTreeItem(sourceParent), sourceIndex, 
					toTreeItem(destParent), destIndex);
			//refreshAll();
			return e;
		} catch(Exception e) {
			String dest = destParent == null ? "" : destParent.toString();
			String src = sourceParent == null ? "" : sourceParent.toString();
			System.out.println("Try move: " + src + sourceIndex + " to " + dest + destIndex);
			throw e;
		}
	}
	
	//endregion
	
	//////////////////////////////////////////////////
	//region Node

	public abstract LDataTree<T> toNode(LPath path);

	public LDataTree<T> toNode(DefaultMutableTreeNode item) {
		LPath path = toPath(item);
		return toNode(path);
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Path

	public DefaultMutableTreeNode toTreeItem(DefaultMutableTreeNode parent, int index) {
		if (index == -1) {
			return (DefaultMutableTreeNode) parent.getLastChild();
		} else {
			return (DefaultMutableTreeNode) parent.getChildAt(index);
		}
	}

	public DefaultMutableTreeNode toTreeItem(LPath parentPath, int index) {
		DefaultMutableTreeNode parent = toTreeItem(parentPath);
		if (index >= parent.getChildCount() || parent.getChildCount() == 0)
			return null;
		if (index == -1)
			index = parent.getChildCount() - 1;
		return (DefaultMutableTreeNode) parent.getChildAt(index);
	}

	public DefaultMutableTreeNode toTreeItem(LPath path) {
		TreeNode item = root;
		while(path != null) {
			if (path.index == -1)
				path.index = item.getChildCount() - 1;
			if (path.index < 0 || path.index > item.getChildCount())
				return null;
			item = item.getChildAt(path.index);
			path = path.child;
		}
		return (DefaultMutableTreeNode) item;
	}

	public LPath toPath(TreeNode item) {
		if (item == null) {
			return null;
		}
		Stack<Integer> indexes = new Stack<>();
		TreeNode parent = item.getParent();
		while(parent != null) {
			indexes.push(parent.getIndex(item));
			item = parent;
			parent = item.getParent();
		}
		if (indexes.isEmpty())
			return null;
		LPath root = new LPath(indexes.pop());
		LPath path = root;
		while(indexes.isEmpty() == false) {
			path.child = new LPath(indexes.pop());
			path = path.child;
		}
		return root;
	}
	
	//endregion

	//////////////////////////////////////////////////
	//region Collection
	//-------------------------------------------------------------------------------------

	public void setDataCollection(LDataCollection<T> collection) {
		if (collection == null) {
			setItems(null);
		} else {
			setItems(collection.toTree());
		}
	}
	
	public LDataTree<T> getDataCollection() {
		return getDataCollection(root);
	}
	
	@SuppressWarnings("unchecked")
	public LDataTree<T> getDataCollection(DefaultMutableTreeNode item) {
		LDataTree<T> node = new LDataTree<T>();
		ItemData itemData = (ItemData) item.getUserObject();
		node.data = itemData.data;
		node.id = itemData.id;
		for (int i = 0; i < item.getChildCount(); i++) {
			node.children.add(getDataCollection((DefaultMutableTreeNode) item.getChildAt(i)));
		}
		return node;
	}

	//-------------------------------------------------------------------------------------
	// String Node
	//-------------------------------------------------------------------------------------

	public void setItems(LDataTree<T> root) {
		clear();
		if (root == null) {
			tree.setEnabled(false);
		} else {
			tree.setEnabled(true);
			for(LDataTree<T> child : root.children) {
				createTreeItem(this.root, -1, child);
			}
		}
	}

	public void setItemNode(DefaultMutableTreeNode item, LDataTree<T> node) {
		item.setUserObject(new ItemData(dataToString(node.data),
				node.id, node.data));
		for(int i = 0; i < node.children.size(); i++) {
			setItemNode((DefaultMutableTreeNode) item.getChildAt(i), node.children.get(i));
		}
	}

	protected String dataToString(T data) {
		return data.toString();
	}

	//-------------------------------------------------------------------------------------
	// Refresh
	//-------------------------------------------------------------------------------------

	public void forceSelection(LPath path) {
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item == null) {
			tree.clearSelection();
			notifySelectionListeners(new LSelectionEvent(null, null, -1));
		} else {
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			tree.setSelectionPath(new TreePath(model.getPathToRoot(item)));
			@SuppressWarnings("unchecked")
			ItemData data = (ItemData) item.getUserObject();
			notifySelectionListeners(new LSelectionEvent(path, data.data, getID(item)));
		}
	}

	public void forceSelection(LPath parent, int index) {
		if (parent == null) {
			parent = new LPath(index);
		} else {
			parent = parent.addLast(index);
		}
		forceSelection(parent);
	}

	public void refreshSelection() {
		if (tree.getSelectionCount() > 0) {
			DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			LPath path = toPath(item);
			notifySelectionListeners(new LSelectionEvent(path, toObject(path), getID(item)));
		} else if (root.getChildCount() > 0) {
			DefaultMutableTreeNode item = (DefaultMutableTreeNode) root.getFirstChild();
			LPath path = toPath(item);
			notifySelectionListeners(new LSelectionEvent(path, toObject(path), getID(item)));		
		} else {
			notifySelectionListeners(new LSelectionEvent(null, null, -1));
		}
	}

	public void refreshObject(LPath path) {
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item != null) {
			refreshObject(item, toObject(path));
		}
	}
	
	public void refreshObject(DefaultMutableTreeNode item, T data) {
		@SuppressWarnings("unchecked")
		ItemData itemData = (ItemData) item.getUserObject();
		itemData.data = data;
		itemData.name = dataToString(itemData.data);
		item.setUserObject(itemData);
	}

	public void refreshAll() {
		refreshNode(toNode((LPath) null), root);
	}

	private void refreshNode(LDataTree<T> node, DefaultMutableTreeNode item) {
		refreshObject(item, node.data);
		int i = 0;
		for (LDataTree<T> child : node.children) {
			refreshNode(child, (DefaultMutableTreeNode) item.getChildAt(i));
			i++;
		}
	}

	public void clear() {
		root.removeAllChildren();
	}

	//-------------------------------------------------------------------------------------
	// Selection
	//-------------------------------------------------------------------------------------	

	public T getSelectedObject() {
		LPath path = getSelectedPath();
		return toObject(path);
	}

	public LPath getSelectedPath() {
		if (tree.getSelectionCount() > 0) {
			DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			return toPath(item);
			//} else if (tree.getItemCount() > 0) {
			//	DefaultMutableTreeNode item = tree.getItems()[0];
			//	return toPath(item);		
		} else {
			return null;
		}
	}

	//-------------------------------------------------------------------------------------
	// Selection
	//-------------------------------------------------------------------------------------	

	public void setChecked(LPath path, boolean value) {
		//DefaultMutableTreeNode item = toTreeItem(path);
		//item.setChecked(value);
	}

	public void checkAll(boolean value) {
		//for (DefaultMutableTreeNode item : tree.getItems())
		//	checkAll(item, value);
	}

	protected void checkAll(DefaultMutableTreeNode item, boolean value) {
		//item.setChecked(value);
		//for(DefaultMutableTreeNode child : item.getItems())
		//	checkAll(child, value);
	}

	//-------------------------------------------------------------------------------------
	// Transfer
	//-------------------------------------------------------------------------------------	

	class TreeTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;
		DataFlavor nodesFlavor;
		DataFlavor[] flavors = new DataFlavor[1];
		DefaultMutableTreeNode[] nodesToRemove;

		public TreeTransferHandler() {
			// Create transfer flavor for the tree nodes
			try {
				String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +
					javax.swing.tree.DefaultMutableTreeNode.class.getName() + "\"";
				nodesFlavor = new DataFlavor(mimeType);
				flavors[0] = nodesFlavor;
			} catch(ClassNotFoundException e) {
				System.out.println("ClassNotFound: " + e.getMessage());
			}
		}

		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDrop())
				return false;
			support.setShowDropLocation(true);
			if (!support.isDataFlavorSupported(nodesFlavor))
				return false;
			if (support.getComponent() != tree)
				return false;
			// Do not allow a drop on the drag source selections.
			JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
			return canDrop(dl, support.getDropAction());
		}

		// Drag start
		@Override
		protected Transferable createTransferable(JComponent c) {
			JTree tree = (JTree)c;
			DefaultMutableTreeNode source = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			return new NodeTransferable(source);
		}

		// Drop
		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
		}

		public int getSourceActions(JComponent c) {
			return MOVE | LINK;
		}

		// Drag finish
		@Override
		public boolean importData(TransferHandler.TransferSupport support) {
			if(!canImport(support)) {
				return false;
			}
			// Extract transfer data.
			DefaultMutableTreeNode node = null;
			try {
				Transferable t = support.getTransferable();
				node = (DefaultMutableTreeNode)t.getTransferData(nodesFlavor);
			} catch(UnsupportedFlavorException ufe) {
				System.out.println("UnsupportedFlavor: " + ufe.getMessage());
			} catch(java.io.IOException ioe) {
				System.out.println("I/O error: " + ioe.getMessage());
			}
			// Get drop location info.
			JTree.DropLocation dl =
					(JTree.DropLocation) support.getDropLocation();
			drop(node, dl);
			return true;
		}

		public String toString() {
			return getClass().getName();
		}

		public class NodeTransferable implements Transferable {
			private DefaultMutableTreeNode node;

			public NodeTransferable(DefaultMutableTreeNode node) {
				this.node = node;
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
				if(!isDataFlavorSupported(flavor))
					throw new UnsupportedFlavorException(flavor);
				return node;
			}

			public DataFlavor[] getTransferDataFlavors() {
				return flavors;
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return nodesFlavor.equals(flavor);
			}
		}
	}
	
	
}
