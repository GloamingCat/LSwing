package lui.graphics;

import java.awt.Color;
import java.util.HashMap;

public class LColor {
	
	public static final LColor BLACK = new LColor(0, 0, 0, 255);
	public static final LColor WHITE = new LColor(255, 255, 255, 255);
	
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
	
	public Color convert() {
		return new Color(red, green, blue);
	}
	
	//////////////////////////////////////////////////
	//region Map
	
	private static final HashMap<String, LColor> colorMap = new HashMap<>();
	
	public static void setColor(String name, int r, int g, int b, int a) {
		colorMap.put(name, new LColor(r, g, b, a));
	}
	
	public static LColor getColor(String name) {
		return colorMap.get(name);
	}
	
	//endregion
	
}
