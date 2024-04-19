package lui.widget;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Stack;

import javax.swing.*;
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
		public ItemData(String name, int id, T data, boolean checked) {
			this.data = data;
			this.id = id;
			this.name = name;
			this.checked = checked;
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
		root.setUserObject(new ItemData("", -1, null, false));
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
				ItemData itemData = (ItemData) item.getUserObject();
				LSelectionEvent event = new LSelectionEvent(path, itemData.data, itemData.id, itemData.checked);
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

	private LDataTree<T> dragData;
	private DefaultMutableTreeNode dragParent;
	private int dragIndex;

	public void setDragEnabled(boolean value) {
		tree.setDragEnabled(value);
	}
	
	protected boolean canDrop(DefaultMutableTreeNode source, TreePath path) {
		DefaultMutableTreeNode target = (DefaultMutableTreeNode) path.getLastPathComponent();
		while (target != null) {
			if (target == source) {
				new Exception("Source = target?").printStackTrace();
				return false;
			}
			target = (DefaultMutableTreeNode) target.getParent();
		}
		return true;
	}

	protected void drag(DefaultMutableTreeNode dragNode) {
		dragParent = (DefaultMutableTreeNode) dragNode.getParent();
		dragIndex = dragParent.getIndex(dragNode);
		dragData = toNode(dragNode);
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.removeNodeFromParent(dragNode);
	}

	protected void dragFinish(DefaultMutableTreeNode dragNode, boolean moved) {
		if (!moved) {
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			System.out.println("reinstert");
			model.insertNodeInto(dragNode, dragParent, dragIndex);
		}
	}
	
	public LMoveEvent<T> drop(DefaultMutableTreeNode targetParent, int targetIndex) {
		if (targetIndex == -1)
			targetIndex = targetParent.getChildCount();
		LMoveEvent<T> e = reinsert(dragData, dragParent, dragIndex, targetParent, targetIndex);
		if (e == null)
			return null;
		if (menuInterface != null) {
			LMoveAction<T> action = new LMoveAction<>(this, e.sourceParent, e.sourceIndex, e.destParent, e.destIndex);
			menuInterface.actionStack.newAction(action);
		}
		notifyMoveListeners(e);
		return e;
	}

	class TreeTransferHandler extends TransferHandler {

		private DataFlavor[] flavors;

		public TreeTransferHandler() {
			try {
				String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +
					ItemData.class.getName() + "\"";
				flavors = new DataFlavor[] { new DataFlavor(mimeType) };
			} catch (ClassNotFoundException e) {
				System.out.println("ClassNotFound: " + e.getMessage());
			}
		}

		// Drag start
		@Override
		protected Transferable createTransferable(JComponent c) {
			JTree tree = (JTree) c;
			DefaultMutableTreeNode movedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			System.out.println("drag start");
			drag(movedNode);
			return new NodeTransferable(movedNode);
		}

		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDrop())
				return false;
			support.setShowDropLocation(true);
			if (!support.isDataFlavorSupported(flavors[0]))
				return false;
			if (support.getComponent() != tree)
				return false;
			JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            try {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) support.getTransferable().getTransferData(flavors[0]);
				tree.expandPath(dl.getPath());
				System.out.println("can drop:" + canDrop(node, dl.getPath()));
				return canDrop(node, dl.getPath());
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
				return false;
            }
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport support) {
			System.out.println("import data");
			if (!canImport(support)) {
				return false;
			}
			try {
				JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();
				tree.expandPath(new TreePath(parent.getPath()));
				return drop(parent, dl.getChildIndex()) != null;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
        }

		// Drop
		@Override
		protected void exportDone(JComponent source, Transferable transferable, int action) {
            try {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) transferable.getTransferData(flavors[0]);
				System.out.println(action);
				dragFinish(node, action != NONE);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }

		public int getSourceActions(JComponent c) {
			return COPY;
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
				return flavor.equals(flavors[0]);
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
		ItemData data = new ItemData(dataToString(node.data), node.id, node.data, isDataChecked(node.data));
		newItem.setUserObject(data);
		((DefaultTreeModel) tree.getModel()).insertNodeInto(newItem, parent, index >= 0 ? index : parent.getChildCount());;
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
			return new LSelectionEvent(null, null, -1, false);
		}
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item == null) {
			new Exception("Couldn't find tree item: " + path).printStackTrace();
			return null;
		}
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		tree.setSelectionPath(new TreePath(model.getPathToRoot(item)));
		ItemData data = (ItemData) item.getUserObject();
		return new LSelectionEvent(path, data.data, data.id, data.checked);
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
		ItemData data = new ItemData(dataToString(node.data), node.id, node.data, isDataChecked(node.data));
		item.setUserObject(data);
		((DefaultTreeModel) tree.getModel()).nodeChanged(item);
	}

	protected String dataToString(T data) {
		return data.toString();
	}

	protected boolean isDataChecked(T data) {
		return true;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Refresh

	public void forceSelection(LPath path) {
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item == null) {
			tree.clearSelection();
			notifySelectionListeners(new LSelectionEvent(null, null, -1, false));
		} else {
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			tree.setSelectionPath(new TreePath(model.getPathToRoot(item)));
			@SuppressWarnings("unchecked")
			ItemData data = (ItemData) item.getUserObject();
			notifySelectionListeners(new LSelectionEvent(path, data.data, data.id, data.checked));
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
			itemData.checked = isDataChecked(itemData.data);
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

	private static class CheckBoxPanel extends JPanel {
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

	private class CheckBoxNodeRenderer implements TreeCellRenderer  {
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
		DefaultMutableTreeNode node;

		public CheckBoxNodeEditor() {
			renderer.checkBoxPanel.checkBox.addItemListener(itemEvent -> {
				if (data == null || node == null)
					return;
				if (data.checked != renderer.checkBoxPanel.checkBox.isSelected()) {
					data.checked = renderer.checkBoxPanel.checkBox.isSelected();
					notifyCheckListeners(new LSelectionEvent(toPath(node), data.data, data.id, data.checked));
				}
				if (stopCellEditing())
					fireEditingStopped();
			});
		}

		public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean selected, boolean expanded, boolean leaf, int row)  {
			CheckBoxPanel editor = renderer.getTreeCellRendererComponent(tree, value,
					true, expanded, leaf, row, true);
			node = (DefaultMutableTreeNode) value;
			data = (ItemData) node.getUserObject();
			return editor;
		}

		public Object getCellEditorValue() {
			return data;
		}

	}

	//endregion
	
}
