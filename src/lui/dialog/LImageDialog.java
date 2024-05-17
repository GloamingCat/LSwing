package lui.dialog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lui.base.LFlags;
import lui.base.LVocab;
import lui.container.LImage;
import lui.container.LFlexPanel;
import lui.container.LScrollPanel;
import lui.widget.LFileSelector;

public class LImageDialog extends LObjectDialog<String> {

	protected LFileSelector selFile;
	protected LImage imgQuad;
	protected LScrollPanel scroll;

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new LShell(600, 400)
	 * @wbp.eval.method.parameter optional true
	 * @wbp.eval.method.parameter rootPath ""
	 */
	public LImageDialog(LWindow parent, int style) {
		super(parent, style, LVocab.instance.IMAGESHELL);
	}

	public void createContent(int style) {
		super.createContent(style);
		content.setFillLayout(true);
		content.getCellData().setRequiredSize(300, 200);

		LFlexPanel sash = new LFlexPanel(content, true);
		sash.getCellData().setExpand(true, true);

		selFile = new LFileSelector(sash, (style & LFlags.OPTIONAL) > 0);
		selFile.addFileRestriction(this::isImage);

		scroll = new LScrollPanel(sash);

		imgQuad = new LImage(scroll);
		imgQuad.setAlignment(LFlags.TOP | LFlags.LEFT);

		selFile.addSelectionListener(event -> resetImage());

		sash.setWeights(1, 1);
	}

	public void setRootPath(String path) {
		selFile.setFolder(path);
	}

	public void open(String initial) {
		selFile.setSelectedFile(initial);
		resetImage();
		super.open(initial);
	}

	@Override
	protected String createResult(String initial) {
		return selFile.getSelectedFile();
	}

	protected boolean isImage(File entry) {
		try {
			BufferedImage image = ImageIO.read(entry);
			if (image != null) {
				image.flush();
			} else {
				return false;
			}
			return true;
		} catch(IOException ex) {
			return false;
		}
	}

	protected void resetImage() {
		String path = selFile.getRootFolder() + selFile.getSelectedFile();
		imgQuad.setImage(path);
		scroll.setContentSize(imgQuad.getCurrentSize());
		imgQuad.repaint();
	}

}
