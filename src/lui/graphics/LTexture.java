package lui.graphics;

import lui.base.data.LPoint;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class LTexture {

	public static final boolean onWindows = System.getProperty("os.name").
			toLowerCase().contains("win");
	public static final int libVersion = -1;
	
	public static Class<?> rootClass;
	
	private final BufferedImage image;
	
	public LTexture(String file) {
		image = getBufferedImage(file);
	}
	
	public LTexture(BufferedImage image) {
		this.image = image;
	}
	
	public LTexture(int imgW, int imgH) {
		int[] raster = new int[imgW * imgH];
		Arrays.fill(raster, (byte) 0);
		image = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, imgW, imgH, raster, 0, 4);
	}
	
	public LTexture(ByteBuffer buffer, int width, int height, int channels) {
		byte[] bytes;
		if (buffer.hasArray())
			bytes = buffer.array();
		else {
			bytes = new byte[buffer.capacity()];
			buffer.get(bytes);
		}
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] raster = new int[width * height];
		for (int i = 0; i < width * height; i++) {
			int r = bytes[i*4];
			int g = bytes[i*4+1];
			int b = bytes[i*4+2];
			int a = bytes[i*4+3];
			raster[i] = (a << 24) + (r << 16) + (g << 8) + b;
		}
		image.setRGB(0, 0, width, height, raster, 0, 4);
	}
	
	public boolean isEmpty() {
		return image == null;
	}
	
	//////////////////////////////////////////////////
	//region String Image

	public LTexture(String s, int w, int h, LColor background, boolean borders) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics gc = image.getGraphics();
		if (background != null) {
			gc.setColor(background.convert());
			gc.fillRect(1, 1, w-2, h-2);
		}
		FontMetrics fm = gc.getFontMetrics(gc.getFont());
		Rectangle2D size = fm.getStringBounds(s, gc);
		int x = (w - (int)size.getWidth()) / 2;
		int y = (h - (int)size.getHeight()) / 2;
		gc.setColor(new Color(0, 0, 0));
		gc.drawString(s, x, y);
		if (borders) {
			gc.drawRect(2, 2, w - 5, h - 5);
		}
		gc.dispose();
		this.image = image;
	}

	public LTexture(String s, int w, int h, LColor background) {
		this(s, w, h, background, false);
	}

	//endregion
	
	public BufferedImage convert() {
		return image;
	}
	
	public void dispose() {
		image.flush();
	}
	
	public LPoint getSize() {
		if (image == null)
			return null;
		return new LPoint(image.getWidth(), image.getHeight());
	}
	
	public LRect getBounds() {
		if (image == null)
			return null;
		return new LRect(0, 0, image.getWidth(), image.getHeight());
	}

	//////////////////////////////////////////////////
	//region Serialization

	public ByteBuffer toBuffer() {
		int n = image.getWidth() * image.getHeight();
		ByteBuffer buffer = ByteBuffer.allocateDirect(n * 4);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getRGB(x, y);
				int i = ( x + ( y * image.getHeight() ) ) * 4;
				buffer.put(i*4,   (byte) ((pixel & 0x00ff0000) >> 16));
				buffer.put(i*4+1, (byte) ((pixel & 0x0000ff00) >> 8));
				buffer.put(i*4+2, (byte) (pixel & 0x000000ff));
				buffer.put(i*4+3, (byte) ((pixel & 0xff000000) >> 24));
			}
		}
		return buffer;
	}

	public void storePNG(String fileName) {
		File outputfile = new File(fileName);
        try {
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

	//endregion

	//////////////////////////////////////////////////
	//region Color Transform

	public void colorTransform(
			float r, float g, float b,
			float h, float s, float v) {
		correctTransparency(image);
		colorTransform(image, r, g, b, h, s, v);
	}
	
	public void colorTransform(
			float r, float g, float b, float a,
			float h, float s, float v) {
		correctTransparency(image, a);
		colorTransform(image, r, g, b, h, s, v);
	}
	
	public void correctTransparency() {}

	//endregion
	
	//////////////////////////////////////////////////
	//region ImageData

	public static void correctTransparency(BufferedImage data) {}
	
	public static void correctTransparency(BufferedImage image, float a) {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getRGB(x, y);
				int alpha = (pixel & 0xff000000) >> 24;
				pixel = (pixel & 0x00ffffff) | (Math.min((int)(alpha * a), 255) << 24);
				image.setRGB(x, y, pixel);
			}
		}
	}
	
	/** Applies color transformation on color matrix.
	 * @param _r [0, 1]
	 * @param _g [0, 1]
	 * @param _b [0, 1]
	 * @param _h [0, 360]
	 * @param _s [0, 1]
	 * @param _v [0, 1]
	 */
	public static void colorTransform(BufferedImage image, 
			float _r, float _g, float _b,
			float _h, float _s, float _v) {
		if (_r == 1 && _g == 1 && _b == 1 && 
				_h == 0 && _s == 1 && _v == 1)
			return;
		float[] hsb = new float[3];
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int pixel = image.getRGB(x, y);
				int r = (pixel & 0x00ff0000) >> 16;
				int g = (pixel & 0x0000ff00) >> 8;
				int b = pixel & 0x000000ff;
				int a = (pixel & 0xff000000) >> 24; 
				Color.RGBtoHSB(r, g, b, hsb);
				hsb[0] = (hsb[0] + _h) % 360;
				hsb[1] = Math.max(0, Math.min(1, hsb[1] + _s));
				hsb[2] = Math.max(0, Math.min(1, hsb[2] + _v));
				pixel = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
				r = (byte) _r * ((pixel & 0x00ff0000) >> 16);
				g = (byte) _g * ((pixel & 0x0000ff00) >> 8);
				b = (byte) _b * (pixel & 0x000000ff);
				pixel = (a << 24) | (r << 16) | (g << 8) | b;
				image.setRGB(x, y, pixel);
			}
		}
	}
	
	
	private static final HashMap<String, BufferedImage> loadedImages = new HashMap<String, BufferedImage>();	
	
	public static BufferedImage getBufferedImage(String file) {
		BufferedImage image = loadedImages.get(file);
		if (image == null) {
			try {
				image = ImageIO.read(new File(file));
				loadedImages.put(file, image);
			} catch (IOException e) { }
		}
		return image;
	}

}
