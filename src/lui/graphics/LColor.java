package lui.graphics;

import java.awt.Color;

public class LColor implements Cloneable {

	public int red;
	public int green;
	public int blue;
	public int alpha;
	
	public LColor(int r, int g, int b, int a) {
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}
	
	public LColor(int r, int g, int b) {
		this(r, g, b, 255);
	}

	public LColor(double red, double green, double blue, double alpha) {
		this.red = (int) Math.round(red);
		this.green = (int) Math.round(green);
		this.blue = (int) Math.round(blue);
		this.alpha = (int) Math.round(alpha);
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
	
	public Color convert() {
		return new Color(red, green, blue, alpha);
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
