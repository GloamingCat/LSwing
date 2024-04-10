package lui.widget;

import java.lang.reflect.Type;

import lui.base.LFlags;
import lui.container.LContainer;
import lui.container.LImage;
import lui.dialog.*;
import lui.dialog.LImageDialog;
import lui.dialog.LObjectDialog;

public class LImageButton extends LObjectButton<String> {

	protected LImage image;
	protected String folder = "";

	public LImageButton(LContainer parent, boolean optional) {
		super(parent);
		setShellFactory(new LWindowFactory<>() {
			@Override
			public LObjectDialog<String> createWindow(LWindow parent) {
				LImageDialog w = new LImageDialog(parent, optional ? LFlags.OPTIONAL : 0);
				w.setRootPath(folder);
				return w;
			}
		});
	}

	public void setImage(LImage image) {
		this.image = image;
	}
	
	public void setRootPath(String path) {
		folder = path;
	}

	@Override
	public void setValue(Object value) {
		if (value != null) {
			setEnabled(true);
			String s = (String) value;
			if (image != null) {
				if (s.isEmpty()) {
					image.setImage((String) null);
				} else {
					image.setImage(folder + s);
				}
			}
			currentValue = s;
		} else {
			setEnabled(false);
			if (image != null) {
				image.setImage((String) null);
			}
			currentValue = null;
		}
	}
	
	@Override
	protected Type getType() {
		return String.class;
	}
	
}