package lui.widget;

import lui.LGlobals;
import lui.container.LContainer;
import lui.editor.LPopupMenu;
import lui.base.data.LDataTree;
import lui.base.data.LPath;
import lui.base.event.LDeleteEvent;
import lui.base.event.LEditEvent;
import lui.base.event.LInsertEvent;
import lui.base.gui.LMenu;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public abstract class LTree<T, ST> extends LTreeBase<T, ST> {
	
	protected LPopupMenu menu;
	protected boolean includeID = false;
	protected boolean editEnabled = false;
	
	public LTree(LContainer parent) {
		this(parent, false);
	}

	public LTree(LContainer parent, boolean check) {
		super(parent, check);
		LPopupMenu menu = new LPopupMenu(tree);
		tree.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2)
					onEditButton(menu);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		this.menu = menu;
	}
	
	public void setIncludeID(boolean value) {
		includeID = value;
	}
	
	protected String stringID(int i) {
		return String.format("[%03d] ", i);
	}
	
	//-------------------------------------------------------------------------------------
	// Modify
	//-------------------------------------------------------------------------------------
	
	public LInsertEvent<T> insert(LPath parentPath, int index, LDataTree<T> node) {
		DefaultMutableTreeNode parent = toTreeItem(parentPath);
		createTreeItem(parent, index, node);
		//refreshAll();
		return new LInsertEvent<T>(parentPath, index, node);
	}
	
	public LDeleteEvent<T> delete(LPath parentPath, int index) {
		DefaultMutableTreeNode item = toTreeItem(parentPath, index);
		LDataTree<T> node = disposeTreeItem(item);
		//refreshAll();
		return new LDeleteEvent<T>(parentPath, index, node);
	}
	
	public LEditEvent<ST> edit(LPath path) {
		//refreshAll();
		return null;
	}
	
	protected abstract LDataTree<T> emptyNode();
	protected abstract LDataTree<T> duplicateNode(LDataTree<T> node);
	protected abstract String encodeNode(LDataTree<T> node);
	protected abstract LDataTree<T> decodeNode(String node);
	
	//-------------------------------------------------------------------------------------
	// Modify Menu
	//-------------------------------------------------------------------------------------
	
	public void setCopyEnabled(boolean value) {
		super.setCopyEnabled(menu, value);
	}
	
	public void setPasteEnabled(boolean value) {
		super.setPasteEnabled(menu, value);
	}
	
	public void setEditEnabled(boolean value) {
		editEnabled = value;
		super.setEditEnabled(menu, value);
	}
	
	public void setInsertNewEnabled(boolean value) {
		super.setInsertNewEnabled(menu, value);
	}
	
	public void setDuplicateEnabled(boolean value) {
		super.setDuplicateEnabled(menu, value);
	}
	
	public void setDeleteEnabled(boolean value) {
		super.setDeleteEnabled(menu, value);
	}
	
	//-------------------------------------------------------------------------------------
	// Menu Handlers
	//-------------------------------------------------------------------------------------
	
	@Override
	protected void onEditButton(LMenu menu) {
		if (tree.getSelectionCount() > 0) {
			DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			LPath path = toPath(item);
			LEditEvent<ST> event = newEditAction(path);
			if (event != null) {
				setItemNode(item, toNode(path));
			}
		}
	}
	
	@Override
	protected void onInsertNewButton(LMenu menu) {
		LPath parentPath = null;
		int index = -1;
		LDataTree<T> newNode = emptyNode();
		if (tree.getSelectionCount() > 0) {
			DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			parentPath = toPath(item.getParent());
			index = indexOf(item) + 1;
		}
		newInsertAction(parentPath, index, newNode);
	}
	
	@Override
	protected void onDuplicateButton(LMenu menu) {
		if (tree.getSelectionCount() > 0) {
			DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			LPath itemPath = toPath(item);
			LDataTree<T> node = duplicateNode(toNode(itemPath));
			LPath parentPath = toPath(item.getParent());
			int i = indexOf(item) + 1;
			newInsertAction(parentPath, i, node);
		}
	}
	
	@Override
	protected void onDeleteButton(LMenu menu) {
		if (tree.getSelectionCount() > 0) {
			DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			TreeNode parentItem = item.getParent();
			LPath parentPath = toPath(parentItem);
			int i = indexOf(item);
			newDeleteAction(parentPath, i);
		}
	}
	
	@Override
	public void onCopyButton(LMenu menu) {
		LPath path = getSelectedPath();
		if (path != null) {
			LDataTree<T> node = toNode(path);
			LGlobals.clipboard.setContents(new StringSelection(encodeNode(node)), null);
		}
	}
	
	@Override
	public void onPasteButton(LMenu menu) {
		DataFlavor dataFlavor = DataFlavor.stringFlavor;
		if (!LGlobals.clipboard.isDataFlavorAvailable(dataFlavor))
			return;
		try {
			String str = (String) LGlobals.clipboard.getData(dataFlavor);
			if (str == null)
				return;
			LDataTree<T> newNode = decodeNode(str);
			if (newNode == null)
				return;
			LPath parentPath = null;
			int index = -1;
			if (tree.getSelectionCount() > 0) {
				DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				parentPath = toPath(item.getParent());
				index = indexOf(item) + 1;
			}
			newInsertAction(parentPath, index, newNode);
		} catch (ClassCastException | UnsupportedFlavorException | IOException e) {
			System.err.println(e.getMessage());
			return;
		}

	}
	
}
