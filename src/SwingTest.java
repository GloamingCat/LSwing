import myeditor.data.MyContent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class SwingTest {

    protected static class ItemData {
		public MyContent data;
		public int id;
		public String name;
		public ItemData(String name, int id, MyContent data) {
			this.data = data;
			this.id = id;
			this.name = name;
		}
        public ItemData(int id, MyContent data) {
			this.data = data;
			this.id = id;
			this.name = data.toString();
		}
		public String toString() {
			return name;
		}
	}

    public SwingTest() {
        DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("c");
        child1.setUserObject(new ItemData(0, new MyContent(0, 1)));
        DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("b");
        child2.setUserObject(new ItemData(1, new MyContent(1, 2)));
        DefaultMutableTreeNode child3 = new DefaultMutableTreeNode("a");
        child3.setUserObject(new ItemData(2, new MyContent(2, 3)));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        JTree tree = new JTree(root);

		root.setUserObject(new ItemData("", -1, null));
        root.add(child1);
        root.add(child2);
        root.add(child3);

		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		tree.setCellRenderer(renderer);
		tree.setDropMode(DropMode.ON_OR_INSERT);
		tree.addTreeSelectionListener(e -> {
            if (tree.getSelectionCount() > 0) {
                DefaultMutableTreeNode item = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                System.out.println(item.getUserObject());
            }
        });
		((DefaultTreeModel) tree.getModel()).nodeStructureChanged(root);

		((ItemData) child2.getUserObject()).name = "fudvjdflk";
		((DefaultTreeModel) tree.getModel()).nodeChanged(child2);

        JFrame frame = new JFrame();
		frame.add(tree);
		frame.setSize(400, 300);
		frame.revalidate();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
		SwingUtilities.invokeLater(SwingTest::new);
	}

}
