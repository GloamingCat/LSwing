package lui.widget;

import lui.LGlobals;
import lui.base.action.collection.LMoveAction;
import lui.base.event.LMoveEvent;
import lui.base.event.listener.LCollectionListener;
import lui.base.gui.LEditableCollection;
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
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public abstract class LEditableTree<T, ST> extends LTree<T, ST> implements LEditableCollection<T, ST> {
	
	protected LPopupMenu menu;
	protected boolean editEnabled = false;

	protected ArrayList<LCollectionListener<T>> insertListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> moveListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<T>> deleteListeners = new ArrayList<>();
	protected ArrayList<LCollectionListener<ST>> editListeners = new ArrayList<>();
	
	public LEditableTree(LContainer parent) {
		this(parent, false);
	}

	public LEditableTree(LContainer parent, boolean check) {
		super(parent, check);
		setDragEnabled(true);
		tree.setTransferHandler(new TreeTransferHandler());
		tree.setDropMode(DropMode.ON_OR_INSERT);
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
		tree.setShowsRootHandles(true);
		this.menu = new LPopupMenu(tree);
		this.menu.addListener(e -> {
				TreePath path = tree.getPathForLocation(e.x, e.y);
				tree.setSelectionPath(path);
			}
		);
	}

	protected int indexOf(DefaultMutableTreeNode item) {
		if (item.getParent() == null)
			return -1;
		return item.getParent().getIndex(item);
	}

	//////////////////////////////////////////////////
	//region Modify
	
	public LInsertEvent<T> insert(LPath parentPath, int index, LDataTree<T> node) {
		DefaultMutableTreeNode parent = toTreeItem(parentPath);
		getDataCollection().initIDs(node);
		createTreeItem(parent, index, node);
		return new LInsertEvent<>(parentPath, index, node);
	}
	
	public LDeleteEvent<T> delete(LPath parentPath, int index) {
		DefaultMutableTreeNode item = toTreeItem(parentPath, index);
		LDataTree<T> node = disposeTreeItem(item);
		return new LDeleteEvent<>(parentPath, index, node);
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
	public LEditEvent<ST> edit(LPath path) {
		return null;
	}
	
	protected abstract LDataTree<T> emptyNode();
	protected abstract LDataTree<T> duplicateNode(LDataTree<T> node);
	protected abstract String encodeNode(LDataTree<T> node);
	protected abstract LDataTree<T> decodeNode(String node);

	//endregion
	
	//////////////////////////////////////////////////
	//region Menu Items
	
	public void setCopyEnabled(boolean value) {
		LEditableCollection.super.setCopyEnabled(menu, value);
	}
	
	public void setPasteEnabled(boolean value) {
		LEditableCollection.super.setPasteEnabled(menu, value);
	}
	
	public void setEditEnabled(boolean value) {
		editEnabled = value;
		LEditableCollection.super.setEditEnabled(menu, value);
	}
	
	public void setInsertNewEnabled(boolean value) {
		LEditableCollection.super.setInsertNewEnabled(menu, value);
	}
	
	public void setDuplicateEnabled(boolean value) {
		LEditableCollection.super.setDuplicateEnabled(menu, value);
	}
	
	public void setDeleteEnabled(boolean value) {
		LEditableCollection.super.setDeleteEnabled(menu, value);
	}

	//endregion
	
	//////////////////////////////////////////////////
	//region Menu Handlers

	@Override
	public ArrayList<LCollectionListener<T>> getInsertListeners() {
		return insertListeners;
	}

	@Override
	public ArrayList<LCollectionListener<T>> getDeleteListeners() {
		return deleteListeners;
	}

	@Override
	public ArrayList<LCollectionListener<T>> getMoveListeners() {
		return moveListeners;
	}

	@Override
	public ArrayList<LCollectionListener<ST>> getEditListeners() {
		return editListeners;
	}

	@Override
	public void onEditButton(LMenu menu) {
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
	public void onInsertNewButton(LMenu menu) {
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
	public void onDuplicateButton(LMenu menu) {
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
	public void onDeleteButton(LMenu menu) {
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
			String str = encodeNode(node);
			LGlobals.clipboard.setContents(new StringSelection(str), null);
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
			if (newNode != null)
				newInsertAction(null, newNode);
		} catch (ClassCastException | UnsupportedFlavorException | IOException e) {
			System.err.println(e.getMessage());
		}
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
		if (path == null)
			return false;
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

	private class TreeTransferHandler extends TransferHandler {

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
				return canDrop(node, dl.getPath());
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
				return false;
            }
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport support) {
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

}
