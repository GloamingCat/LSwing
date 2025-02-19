package myeditor.project;

import java.io.File;
import java.util.Scanner;

import lui.base.data.LDataList;
import lui.base.data.LDataTree;
import lui.base.serialization.LDefaultSerializer;
import lui.base.serialization.LFileManager;
import myeditor.MyVocab;
import myeditor.data.MyContent;

public class MyProject extends LDefaultSerializer {

	public static MyProject current = null;
	
	public LDataList<MyContent> contentList; 
	public LDataList<MyContent> contentGrid; 
	public LDataTree<MyContent> contentTree; 
	public LDataList<String> subContentTypes;
	
	private final String folder;
	
	public MyProject(String path) {
		super(path);
		folder = LFileManager.getDirectory(path);
		current = this;
	}
	
	private LDataList<MyContent> defaultContentList(int n) {
		LDataList<MyContent> list = new LDataList<>();
		for(int i = 0; i < n; i++) {
			list.add(new MyContent(i*10, i));
		}
		return list;
	}
	
	private LDataTree<MyContent> defaultContentTree(int n, int m, int l) {
		LDataTree<MyContent> root = new LDataTree<>();
		for (int i = 0; i < n; i++) {
			MyContent data = new MyContent(i*10, i);
			data.name = "item " + i;
			LDataTree<MyContent> node = new LDataTree<>(data, root);
			for (int j = 0; j < m; j++) {
				data = new MyContent(j*10, i);
				data.name = "item " + i + " " + j;
				LDataTree<MyContent> subNode = new LDataTree<>(data, node);
				for (int k = 0; k < l; k++) {
					data = new MyContent(k*10, j);
					data.name = "item " + i + " " + j + " " + k;
					new LDataTree<>(data, subNode);
				}
			}
		}
		return root;
	}
	
	private LDataList<String> defaultTypes(int n) {
		LDataList<String> types = new LDataList<>(n);
		for (int i = 0; i < n; i++)
			types.add(MyVocab.instance.TYPE + i);
		return types;
	}

	public String imagePath() {
		return folder + "images" + File.separator;
	}

	@Override
	public void initialize() {
		subContentTypes = defaultTypes(10);
		contentList = defaultContentList(9);
		contentGrid = defaultContentList(9);
		contentTree = defaultContentTree(3, 3, 3);
	}
	
	protected byte[] serialize() {
		StringBuilder content = new StringBuilder(subContentTypes.size() + ","
                + contentList.size() + ","
                + contentGrid.size() + "\n");
		for (String t : subContentTypes)
			content.append(t).append("\n");
		for (MyContent c : contentList)
			content.append(c.encode()).append("\n");
		for (MyContent c : contentGrid)
			content.append(c.encode()).append("\n");
		content.append(contentTree.encode((e) -> e == null ? "null" : e.encode()));
		return content.toString().getBytes();
	}
	
	protected void deserialize(byte[] bytes) {
		Scanner scanner = new Scanner(new String(bytes));
		String[] sizes = scanner.nextLine().split(",");
		subContentTypes = new LDataList<>();
		for (int i = Integer.parseInt(sizes[0]); i > 0; i--) {
			subContentTypes.add(scanner.nextLine());
		}
		contentList = new LDataList<>();
		for (int i = Integer.parseInt(sizes[1]); i > 0; i--) {
			contentList.add(MyContent.decode(scanner.nextLine()));
		}
		contentGrid = new LDataList<>();
		for (int i = Integer.parseInt(sizes[2]); i > 0; i--) {
			contentGrid.add(MyContent.decode(scanner.nextLine()));
		}
		scanner.useDelimiter("\\A");
		contentTree = LDataTree.decode(scanner.next(), MyContent::decode);
		scanner.close();
	}
	
}
