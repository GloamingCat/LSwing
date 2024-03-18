package lwt;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import lwt.container.LView;

public class LMenuInterface extends lbase.LMenuInterface {
	
	public LMenuInterface(LView root) {
		super(root);
	}
	
	public boolean canPaste() {
		if (!LGlobals.clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor))
			return false;
		try {
			Object obj = LGlobals.clipboard.getData(DataFlavor.stringFlavor);
			if (obj == null)
				return false;
			return super.canPaste(obj);
		} catch(UnsupportedFlavorException | IOException e) {
			return false;
		}
	}

}
