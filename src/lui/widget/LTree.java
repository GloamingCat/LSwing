package lui.widget;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Stack;

import javax.swing.*;
import javax.swing.tree.*;

import lui.base.LPrefs;
import lui.container.LContainer;
import lui.base.data.LDataCollection;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LSelectionEvent;

public abstract class LTree<T, ST> extends LSelectableCollection<T, ST> {

	protected JTree tree;
	protected DefaultMutableTreeNode root;
	protected boolean includeID = false;

	//////////////////////////////////////////////////
	//region Node Data

	protected class ItemData {
		public T data = null;
		public int id;
		public String name = "";
		public boolean checked = false;
		public ItemData (int id) {
			this.id = id;
		}
		public String toString() {
			return name;
		}
	}

	//endregion

	//////////////////////////////////////////////////
	//region Constructors
	
	public LTree(LContainer parent) {
		this(parent, false);
	}

	public LTree(LContainer parent, boolean check) {
		super(parent, (check ? 1 : 0));
	}
	
	@Override
	protected void createContent(int flags) {
		root = new DefaultMutableTreeNode();
		root.setUserObject(new ItemData(-1));
		tree = new JTree(root);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		if (flags == 1) { // Check
			tree.setCellRenderer(new CheckBoxNodeRenderer());
			tree.setCellEditor(new CheckBoxNodeEditor());
			tree.setEditable(true);
			tree.addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					int row = tree.getRowForLocation(e.getX(), e.getY());
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					if (row != -1)
						tree.startEditingAtPath(path);
				}
			});
		} else {
			DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
			renderer.setIcon(null);
			renderer.setOpenIcon(null);
			renderer.setClosedIcon(null);
			renderer.setLeafIcon(null);
			renderer.setDisabledIcon(null);
			tree.setCellRenderer(renderer);
		}
		JScrollPane scroll = new JScrollPane(tree);
		add(scroll);
		tree.addTreeSelectionListener(e -> {
			if (tree.getSelectionCount() > 0) {
				DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				LPath path = toPath(item);
				@SuppressWarnings("unchecked")
				ItemData itemData = (ItemData) item.getUserObject();
				LSelectionEvent event = new LSelectionEvent(path, itemData.data, itemData.id, itemData.checked);
				notifySelectionListeners(event);
			}
		});
	}
	
	@Override
	public void setHoverText(String text) {
		tree.setToolTipText("<html>" + text.replace("\n", "<br>") + "</html>");
	}

	public void setIncludeID(boolean value) {
		includeID = value;
	}

	//endregion

	//////////////////////////////////////////////////
	//region Internal Operations

	protected DefaultMutableTreeNode createTreeItem(DefaultMutableTreeNode parent, final int index, LDataTree<T> node) {
		DefaultMutableTreeNode newItem = new DefaultMutableTreeNode();
		ItemData data = new ItemData(node.id);
		refreshItemData(node, data);
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
			return new LSelectionEvent(null, null, -1, false);
		}
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item == null) {
			return null;
		}
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		tree.setSelectionPath(new TreePath(model.getPathToRoot(item)));
		@SuppressWarnings("unchecked")
		ItemData data = (ItemData) item.getUserObject();
		return new LSelectionEvent(path, data.data, data.id, data.checked);
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
			if (path.index < 0 || path.index >= item.getChildCount())
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
		ItemData data = new ItemData(node.id);
		refreshItemData(node, data);
		item.setUserObject(data);
		((DefaultTreeModel) tree.getModel()).nodeChanged(item);
	}

	//endregion

	//////////////////////////////////////////////////
	//region Refresh

	public void refreshObject(LPath path) {
		DefaultMutableTreeNode item = toTreeItem(path);
		if (item != null) {
			LDataTree<T> node = toNode(path);
			@SuppressWarnings("unchecked")
			ItemData itemData = (ItemData) item.getUserObject();
			refreshItemData(node, itemData);
			item.setUserObject(itemData);
			((DefaultTreeModel) tree.getModel()).nodeChanged(item);
		}
	}

	protected void refreshItemData(LDataTree<T> node, ItemData itemData) {
		itemData.id = node.id;
		itemData.data = node.data;
		itemData.name = dataToString(itemData.data);
		if (includeID)
			itemData.name = stringID(node.id) + itemData.name;
		itemData.checked = isDataChecked(itemData.data);
	}

	protected String stringID(int i) {
		return String.format("[%03d] ", i);
	}

	protected String dataToString(T data) {
		return data.toString();
	}

	protected boolean isDataChecked(T data) {
		return true;
	}

	public void refreshAll() {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		model.nodeStructureChanged(this.root);
		refreshItem(this.root);
	}

	private void refreshItem(DefaultMutableTreeNode item) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		model.nodeChanged(item);
		System.out.println(item.getUserObject());
		for (int i = 0; i < item.getChildCount(); i++) {
			refreshItem((DefaultMutableTreeNode) item.getChildAt(i));
		}
	}

	public void refreshAll(LDataTree<T> node) {
		DefaultMutableTreeNode item = toTreeItem(node.toPath());
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged(item);
	}

	public void clear() {
		root.removeAllChildren();
	}

	//endregion

	//////////////////////////////////////////////////
	//region Selection

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

	//endregion

	///////////////////////////////////////////////////
	//region Check

	public boolean isChecked(LPath path) {
		DefaultMutableTreeNode node = toTreeItem(path);
		@SuppressWarnings("unchecked")
		ItemData data = (ItemData) node.getUserObject();
		return data.checked;
	}

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
		Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;
		CheckBoxPanel checkBoxPanel = new CheckBoxPanel();

		public CheckBoxNodeRenderer() {
			Font fontValue = UIManager.getFont("Tree.font");
			if (fontValue != null)
				checkBoxPanel.label.setFont(fontValue);
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
				@SuppressWarnings("unchecked")
				ItemData data = (ItemData) node.getUserObject();
				checkBoxPanel.checkBox.setSelected(data.checked);
				checkBoxPanel.label.setText(data.toString());
			}
			return checkBoxPanel;
		}
	}

	private class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

		CheckBoxNodeRenderer renderer;
		ItemData data;
		DefaultMutableTreeNode node;

		public CheckBoxNodeEditor() {
			renderer = new CheckBoxNodeRenderer();
			renderer.checkBoxPanel.checkBox.addItemListener(itemEvent -> {
				if (data == null || node == null)
					return;
				if (stopCellEditing())
					fireEditingStopped();
			});
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean selected, boolean expanded, boolean leaf, int row)  {
			CheckBoxPanel editor = renderer.getTreeCellRendererComponent(tree, value,
					selected, expanded, leaf, row, true);
			node = (DefaultMutableTreeNode) value;
			@SuppressWarnings("unchecked")
			ItemData itemData = (ItemData) node.getUserObject();
			data = itemData;
			return editor;
		}

		@Override
		public Object getCellEditorValue() {
			if (data.checked != renderer.checkBoxPanel.checkBox.isSelected()) {
				data.checked = renderer.checkBoxPanel.checkBox.isSelected();
				notifyCheckListeners(new LSelectionEvent(toPath(node), data.data, data.id, data.checked));
			}
			return data;
		}

	}

	//endregion

	//////////////////////////////////////////////////
	//region Properties

	@Override
	public Dimension getMinimumSize() {
		Dimension size = new Dimension(LPrefs.LISTWIDTH, LPrefs.LISTHEIGHT);
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width = Math.max(size.width, LPrefs.LISTWIDTH);
		size.height = Math.max(size.height, LPrefs.LISTHEIGHT);
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

	//endregion

}
