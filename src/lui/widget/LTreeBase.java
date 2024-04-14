package lui.widget;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Stack;

import javax.swing.*;
import javax.swing.JTree.DropLocation;
import javax.swing.tree.*;

import lui.container.LContainer;
import lui.base.action.collection.LMoveAction;
import lui.base.data.LDataCollection;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LMoveEvent;
import lui.base.event.LSelectionEvent;

public abstract class LTreeBase<T, ST> extends LSelectableCollection<T, ST> {

	protected JTree tree;
	protected DefaultMutableTreeNode root;

	//////////////////////////////////////////////////
	//region Node Data

	protected class ItemData {
		public T data;
		public int id;
		public String name;
		public boolean checked;
		public ItemData(String name, int id, T data) {
			this.data = data;
			this.id = id;
			this.name = name;
			this.checked = false;
		}
		public String toString() {
			return name;
		}
	}

	//endregion

		
	//////////////////////////////////////////////////
	//region Constructors
	
	public LTreeBase(LContainer parent) {
		this(parent, false);
	}

	public LTreeBase(LContainer parent, boolean check) {
		super(parent, (check ? 1 : 0));
	}
	
	@Override
	protected void createContent(int flags) {
		root = new DefaultMutableTreeNode();
		root.setUserObject(new ItemData("", -1, null));
		tree = new JTree(root);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		if (flags == 1) {
			tree.setCellRenderer(new CheckBoxNodeRenderer());
			tree.setCellEditor(new CheckBoxNodeEditor());
			tree.setEditable(true);
		} else {
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			final Icon icon = renderer.getLeafIcon();
			renderer.setIcon(icon);
			renderer.setOpenIcon(icon);
			renderer.setClosedIcon(icon);
			renderer.setDisabledIcon(icon);
			tree.setCellRenderer(renderer);
		}
		setDragEnabled(true);
		tree.setTransferHandler(new TreeTransferHandler());
		tree.setDropMode(DropMode.ON_OR_INSERT);
		add(tree);
		tree.addTreeSelectionListener(e -> {
			if (tree.getSelectionCount() > 0) {
				DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				LPath path = toPath(item);
				LSelectionEvent event = new LSelectionEvent(path, toObject(path), getID(item));
				event.check = false;
				notifySelectionListeners(event);
			}
		});
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
		LMoveEvent<T> e = reinsert(node, sourceParent, sourceIndex, destParent, destIndex);
		if (e == null) {
			return null;
		}
		if (menuInterface != null) {
			LMoveAction<T> action = new LMoveAction<>(this, e.sourceParent, e.sourceIndex, e.destParent, e.destIndex);
			menuInterface.actionStack.newAction(action);
		}
		notifyMoveListeners(e);
		return e;
	}

	class TreeTransferHandler extends TransferHandler {

		DataFlavor nodesFlavor;
		DataFlavor[] flavors = new DataFlavor[1];
		DefaultMutableTreeNode[] nodesToRemove;

		public TreeTransferHandler() {
			// Create transfer flavor for the tree nodes
			try {
				String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +
					DefaultMutableTreeNode.class.getName() + "\"";
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
			private final DefaultMutableTreeNode node;

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
		return data == null ? -1 : data.id;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Internal Operations

	protected DefaultMutableTreeNode createTreeItem(DefaultMutableTreeNode parent, final int index, LDataTree<T> node) {
		DefaultMutableTreeNode newItem = new DefaultMutableTreeNode();
		ItemData data = new ItemData(dataToString(node.data), node.id, node.data);
		newItem.setUserObject(data);
		((DefaultTreeModel) tree.getModel()).insertNodeInto(newItem, parent, index >= 0 ? index : parent.getChildCount());
		int childIndex = 0;
		for (LDataTree<T> child : node.children) {
			createTreeItem(newItem, childIndex, child);
			childIndex++;
		}
		return newItem;
	}

	protected LDataTree<T> disposeTreeItem(DefaultMutableTreeNode item) {
		LDataTree<T> data = toNode(item);
		((DefaultTreeModel) tree.getModel()).removeNodeFromParent(item);
		return data;
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
			new Exception("Couldn't find tree item: " + path).printStackTrace();
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
			return reinsert(node, toTreeItem(sourceParent), sourceIndex,
					toTreeItem(destParent), destIndex);
		} catch(Exception e) {
			String dest = destParent == null ? "" : destParent.toString();
			String src = sourceParent == null ? "" : sourceParent.toString();
			System.out.println("Try move: " + src + sourceIndex + " to " + dest + destIndex);
			throw e;
		}
	}

	protected LMoveEvent<T> reinsert(LDataTree<T> node, DefaultMutableTreeNode sourceParent, int sourceIndex,
			DefaultMutableTreeNode destParent, int destIndex) {
		if (sourceParent == destParent && destIndex == sourceIndex)
			return null;
		LPath sourceParentPath = toPath(sourceParent);
		LPath destParentPath = toPath(destParent);
		createTreeItem(destParent, destIndex, node);
		return new LMoveEvent<>(sourceParentPath, sourceIndex, destParentPath, destIndex, node);
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
		while (parent != null) {
			indexes.push(parent.getIndex(item));
			item = parent;
			parent = item.getParent();
		}
		if (indexes.isEmpty())
			return null;
		LPath root = new LPath(indexes.pop());
		LPath path = root;
		while (!indexes.isEmpty()) {
			path.child = new LPath(indexes.pop());
			path = path.child;
		}
		return root;
	}
	
	//endregion

	//////////////////////////////////////////////////
	//region Collection

	public void setDataCollection(LDataCollection<T> collection) {
		if (collection == null) {
			setItems(null);
		} else {
			LDataTree<T> tree = collection.toTree();
			tree.restoreParents();
			setItems(tree);
		}
	}
	
	public LDataTree<T> getDataCollection() {
		return getDataCollection(root);
	}
	
	@SuppressWarnings("unchecked")
	public LDataTree<T> getDataCollection(DefaultMutableTreeNode item) {
		ItemData itemData = (ItemData) item.getUserObject();
		LDataTree<T> node = new LDataTree<>(itemData.id, itemData.data);
		for (int i = 0; i < item.getChildCount(); i++) {
			LDataTree<T> child = getDataCollection((DefaultMutableTreeNode) item.getChildAt(i));
			child.setParent(node);
		}
		return node;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Node

	public void setItems(LDataTree<T> root) {
		clear();
		if (root == null) {
			tree.setEnabled(false);
			((DefaultTreeModel) tree.getModel()).reload(this.root);
		} else {
			tree.setEnabled(true);
			for (LDataTree<T> child : root.children) {
				createTreeItem(this.root, -1, child);
			}
		}
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged(this.root);
	}

	public void setItemNode(DefaultMutableTreeNode item, LDataTree<T> node) {
		item.setUserObject(new ItemData(dataToString(node.data),
				node.id, node.data));
		((DefaultTreeModel) tree.getModel()).nodeChanged(item);
	}

	protected String dataToString(T data) {
		return data.toString();
	}

	//endregion

	//////////////////////////////////////////////////
	//region Refresh

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

	public void refreshObject(LPath path) {
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item != null) {
			T data = toObject(path);
			@SuppressWarnings("unchecked")
			ItemData itemData = (ItemData) item.getUserObject();
			itemData.data = data;
			itemData.name = dataToString(itemData.data);
			item.setUserObject(itemData);
			((DefaultTreeModel) tree.getModel()).nodeChanged(item);
		}
	}

	public void refreshAll() {
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged(this.root);
	}

	public void clear() {
		root.removeAllChildren();
	}

	//endregion

	//////////////////////////////////////////////////
	//region Selection

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

	public boolean isChecked(LPath path) {
		DefaultMutableTreeNode node = toTreeItem(path);
		ItemData data = (ItemData) node.getUserObject();
		return data.checked;
	}

	//endregion

	///////////////////////////////////////////////////
	//region Check


	class CheckBoxPanel extends JPanel {
		public JCheckBox checkBox;
		public JLabel label;
		public CheckBoxPanel() {
			super();
			setLayout(new BorderLayout());
			checkBox = new JCheckBox();
			label = new JLabel();
			add(checkBox, BorderLayout.WEST);
			add(label, BorderLayout.CENTER);
		}
	}

	class CheckBoxNodeRenderer implements TreeCellRenderer  {
		private final CheckBoxPanel checkBoxPanel = new CheckBoxPanel();
		Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;

		public CheckBoxNodeRenderer() {
			Font fontValue;
			fontValue = UIManager.getFont("Tree.font");
			if (fontValue != null) {
				checkBoxPanel.checkBox.setFont(fontValue);
				checkBoxPanel.label.setFont(fontValue);
			}
			Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
			checkBoxPanel.checkBox.setFocusPainted((booleanValue != null) && booleanValue);
			selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
			selectionForeground = UIManager.getColor("Tree.selectionForeground");
			selectionBackground = UIManager.getColor("Tree.selectionBackground");
			textForeground = UIManager.getColor("Tree.textForeground");
			textBackground = UIManager.getColor("Tree.textBackground");
		}

		public CheckBoxPanel getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			checkBoxPanel.setEnabled(tree.isEnabled());
			if (selected) {
				checkBoxPanel.setForeground(selectionForeground);
				checkBoxPanel.setBackground(selectionBackground);
			} else {
				checkBoxPanel.setForeground(textForeground);
				checkBoxPanel.setBackground(textBackground);
			}
			if (value instanceof DefaultMutableTreeNode node) {
				ItemData data = (ItemData) node.getUserObject();
				checkBoxPanel.checkBox.setSelected(data.checked);
				checkBoxPanel.label.setText(data.toString());
			}
			return checkBoxPanel;
		}
	}

	private class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		ItemData data;

		public boolean isCellEditable(EventObject event) {
			return true;
		}

		public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean selected, boolean expanded, boolean leaf, int row)  {
			CheckBoxPanel editor = renderer.getTreeCellRendererComponent(tree, value,
					true, expanded, leaf, row, true);
			editor.checkBox.addItemListener(itemEvent -> {
				if (stopCellEditing())
					fireEditingStopped();
			});
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			data = (ItemData) node.getUserObject();
			return editor;
		}

		public Object getCellEditorValue() {
			data.checked = renderer.checkBoxPanel.checkBox.isSelected();
			return data;
		}

	}

	//endregion
	
}
