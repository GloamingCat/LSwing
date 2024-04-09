package lwt.widget;

import java.lang.reflect.Type;

import lbase.LFlags;
import lwt.container.LContainer;
import lwt.container.LImage;
import lwt.dialog.LImageWindow;
import lwt.dialog.LObjectWindow;
import lwt.dialog.LWindow;
import lwt.dialog.LWindowFactory;

public class LImageButton extends LObjectButton<String> {

	protected LImage image;
	protected String folder = "";

	public LImageButton(LContainer parent, boolean optional) {
		super(parent);
		setShellFactory(new LWindowFactory<>() {
			@Override
			public LObjectWindow<String> createWindow(LWindow parent) {
				LImageWindow w = new LImageWindow(parent, optional ? LFlags.OPTIONAL : 0);
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