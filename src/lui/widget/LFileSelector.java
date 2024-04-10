package lui.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Function;

import lui.container.LContainer;
import lui.base.data.LDataTree;

public class LFileSelector extends LNodeSelector<String> {
	
	protected LDataTree<String> root;
	protected ArrayList<Function<File, Boolean>> fileRestrictions = new ArrayList<>();
	
	public LFileSelector(LContainer parent, boolean optional) {
		super(parent, optional);
	}
	
	public void setFolder(String folder) {
		root = new LDataTree<>(folder);
		setFiles(root, folder);
		setCollection(root);
	}
	
	public String getRootFolder() {
		return root.data;
	}
	
	protected void setFiles(LDataTree<String> tree, String path) {
		File f = new File(path);
		if (!f.exists())
			return;
		for (File entry : f.listFiles()) {
			if (entry.isDirectory()) {
				LDataTree<String> subFolder = new LDataTree<>(entry.getName(), tree);
				setFiles(subFolder, path + entry.getName() + "/");
			} else if (isValidFile(entry)) {
				LDataTree<String> file = new LDataTree<>(entry.getName(), tree);
				file.id = 0;
			}
		}
	}
	
	public String getFile(int id) {
		LDataTree<String> node = root.findNode(id);
		if (node == null || node.id == -1)
			return null;
		return node.data;
	}

	public String getSelectedFile() {
		LDataTree<String> node = getSelectedNode();
		if (node == null || node.id == -1)
			return "";
		StringBuilder file = new StringBuilder(node.data);
		node = node.parent;
		while (node != root) {
			file.insert(0, node.data);
			file.insert(0, '/');
			node = node.parent;
		}
		return file.toString();
	}
	
	public void setSelectedFile(String file) {
		LDataTree<String> node = findNode(file);
		setValue(node == null ? null : node.toPath());
	}
	
	public LDataTree<String> findNode(String file) {
		String[] names = file.split("/");
		LDataTree<String> node = root;
        for (String name : names) {
            LDataTree<String> child = findChild(name, node);
            if (child == null)
                return null;
            node = child;
        }
		return node;
	}
	
	private LDataTree<String> findChild(String name, LDataTree<String> parent) {
		for (LDataTree<String> child : parent.children) {
			if (name.equals(child.data)) {
				return child;
			}
		}
		return null;
	}

	private boolean isValidFile(File entry) {
		for (var r : fileRestrictions) {
			if (!r.apply(entry)) {
				return false;
			}
		}
		return true;
	}
	
	public void addFileRestriction(Function<File, Boolean> r) {
		fileRestrictions.add(r);
	}
	
	public void removeFileRestriction(Function<File, Boolean> r) {
		fileRestrictions.remove(r);
	}

}
