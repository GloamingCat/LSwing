package lui.graphics;

import java.awt.Rectangle;

public class LRect implements Cloneable {
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public LRect() {}
	
	public LRect(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	public LRect(Rectangle rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public LRect clone() {
        try {
            LRect clone = (LRect) super.clone();
			clone.x = x;
			clone.y = y;
			clone.width = width;
			clone.height = height;
			return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
	}

}
