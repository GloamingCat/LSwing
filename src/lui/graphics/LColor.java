package lui.graphics;

import javax.swing.plaf.ColorUIResource;
import java.awt.Color;

public class LColor implements Cloneable {

	public int red;
	public int green;
	public int blue;
	public int alpha;
	
	public LColor(int r, int b, int g, int a) {
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}
	
	public LColor(int r, int b, int g) {
		this(r, g, b, 255);
	}
	
	public LColor() {
		red = 255;
		green = 255;
		blue = 255;
		alpha = 255;
	}

	public LColor(Color c) {
		red = c.getRed();
		green = c.getGreen();
		blue = c.getBlue();
		alpha = c.getAlpha();
	}
	
	public ColorUIResource convert() {
		return new ColorUIResource(red, green, blue);
	}

    @Override
    public LColor clone() {
        try {
            LColor clone = (LColor) super.clone();
			clone.red = red;
			clone.green = green;
			clone.blue = blue;
			clone.alpha = alpha;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
