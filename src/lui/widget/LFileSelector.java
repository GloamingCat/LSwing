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

	// Folder should include "/".
	public void setFolder(String folder) {
		root = new LDataTree<>(folder);
		setFiles(root, 0, folder);
		setCollection(root);
	}
	
	public String getRootFolder() {
		return root.data;
	}

	// Path should include "/".
	protected int setFiles(LDataTree<String> tree, int id, String path) {
		File f = new File(path);
		if (!f.exists())
			return id;
		File[] entries = f.listFiles();
		if (entries == null)
			return id;
		for (File entry : entries) {
			if (entry.isDirectory()) {
				LDataTree<String> subFolder = new LDataTree<>(entry.getName(), tree);
				id = setFiles(subFolder, id, path + entry.getName() + "/");
			} else if (isValidFile(entry)) {
				LDataTree<String> file = new LDataTree<>(entry.getName(), tree);
				file.initID(id++);
			}
		}
		return id;
	}

	public String getSelectedFile() {
		return getFile(getSelectedNode());
	}

	public String getFile(Integer id) {
		if (id == null)
			return "";
		LDataTree<String> node = tree.getDataCollection().findNode((int) id);
		return getFile(node);
	}

	protected String getFile(LDataTree<String> node) {
		if (node == null || node.id == -1)
			return "";
		StringBuilder file = new StringBuilder(node.data);
		node = node.parent;
		while (node != null && node.parent != null) {
			file.insert(0, '/');
			file.insert(0, node.data);
			node = node.parent;
		}
		return file.toString();
	}

	public void setSelectedFile(String file) {
		if (file == null)
			setValue(null);
		else {
			LDataTree<String> node = findNode(file);
			setValue(node == null ? null : node.toPath());
		}
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
