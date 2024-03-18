package lwt.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public abstract class LPainter {
	
	private Graphics gc;

	public LPainter() {
		gc = null;
	}
	
	public LPainter(Graphics gc) {
		this.gc = gc;
	}
	
	public LPainter(LTexture target) {
		this.gc = target.convert().getGraphics();
	}
	
	public void setGC(Graphics gc) {
		this.gc = gc;
	}

	//////////////////////////////////////////////////
	// {{ Draw
	
	public void drawRect(int x, int y, int w, int h) {
		gc.drawRect(x, y, w, h);
	}
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		gc.drawLine(x1, y1, x2, y2);
	}

	public void drawPolygon(int[] p, boolean close) {
		int[] x = new int[p.length / 2];
		int[] y = new int[p.length / 2];
		for (int i = 0; i < x.length; i++) {
			x[i] = p[i * 2];
			y[i] = p[i * 2 + 1];
		}
		if (close)
			gc.drawPolygon(x, y, x.length);
		else
			gc.drawPolyline(x, y, x.length);
	}
	
	private void drawImage(Image img, int x0, int y0, int w0, int h0, int x, int y, float sx, float sy) {
		gc.drawImage(img, x0, y0, 
				w0, h0, 
				x, y, 
				Math.round(w0 * sx), 
				Math.round(h0 * sy), null);
	}
	
	private void drawImage(Image img, int x, int y, float sx, float sy) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		gc.drawImage(img, 0, 0, w, h, x, y, 
				Math.round(w * sx), Math.round(h * sy), null);
	}
	
	private void drawImage(Image img, int x, int y) {
		gc.drawImage(img, x, y, null);
	}
	
	public void drawImage(LTexture img, int x, int y) {
		drawImage(img.convert(), x, y);
	}
	
	public void drawImage(LTexture img, int x, int y, float sx, float sy) {
		drawImage(img.convert(), x, y, sx, sy);
	}
	
	public void drawImage(LTexture img, int x0, int y0, int w0, int h0, int x, int y, float sx, float sy) {
		drawImage(img.convert(), x0, y0, w0, h0, x, y, sx, sy);
	}
	
	public void drawImage(String path, int x0, int y0, int w0, int h0, int x, int y, float sx, float sy) {
		BufferedImage img = LTexture.getBufferedImage(path);
		drawImage(img, x0, y0, w0, h0, x, y, sx, sy);
	}

	public void drawImageCenter(String path, int x, int y, float sx, float sy) {
		BufferedImage img = LTexture.getBufferedImage(path);
		drawImage(img,
				(int) (x - img.getWidth() * sx / 2),
				(int) (y - img.getHeight() * sy / 2),
				sx, sy);
	}
	
	public void fillPolygon(int[] p) {
		int[] x = new int[p.length / 2];
		int[] y = new int[p.length / 2];
		for (int i = 0; i < x.length; i++) {
			x[i] = p[i * 2];
			y[i] = p[i * 2 + 1];
		}
		gc.fillPolygon(x, y, x.length);
	}
	
	public void fillRect(int x, int y, int w, int h) {
		gc.fillRect(x, y, w, h);
	}
	
	public void setTransparency(int alpha) {
		Color color = gc.getColor();
		gc.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
	}
	
	public void setPaintColor(LColor color) {
		gc.setColor(color.convert());
	}
	
	public void setFillColor(LColor color) {
		gc.setColor(color.convert());
	}
	
	public void dispose() {
		gc.dispose();
	}
	
	// }}
	
	public abstract void paint();

}