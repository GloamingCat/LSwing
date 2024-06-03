package lui.container;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lui.base.LFlags;
import lui.graphics.LColor;
import lui.base.data.LPoint;
import lui.graphics.LRect;
import lui.graphics.LTexture;

import javax.swing.*;

public class LImage extends LCanvas {

	private LTexture original = null;
	private LRect rectangle = null;
	private int align = LFlags.MIDDLE | LFlags.CENTER;
	
	public float r = 1, g = 1, b = 1, a = 1;
	public float h = 0, s = 1, v = 1;

	public float ox = 0, oy = 0;
	public float sx = 1, sy = 1;
	public float rz = 0;
	
	private float dx = 0, dy = 0;

	public LImage(LContainer parent) {
		super(parent);
		Color c = UIManager.getColor("Image.background");
		if (c == null)
			c = UIManager.getColor("Tooltip.background");
		setBackground(c);
	}
	
	@Override
	protected void callPainters(Graphics2D g) {
		currentEvent = g;
		int x = 0;
		int y = 0;
		if (buffer != null) {
			Rectangle bounds = getBounds();
			LRect rect = rectangle != null ? rectangle :
				new LRect(0, 0, buffer.getWidth(), buffer.getHeight());
			LPoint[] limits = new LRect((int)ox, (int)oy, rect.width, rect.height).getLimits(sx, sy, rz);
			if ((align & LFlags.RIGHT) > 0) {
				x = bounds.width - limits[1].x;
			} else if ((align & LFlags.MIDDLE) > 0) {
				x = (bounds.width - limits[1].x - limits[0].x) / 2;
			} else {
				x = -limits[0].x;
			}
			if ((align & LFlags.BOTTOM) > 0) {
				y = bounds.height - limits[1].y;
			} else if ((align & LFlags.CENTER) > 0) {
				y = (bounds.height - limits[1].y - limits[0].y) / 2;
			} else {
				y = -limits[0].y;
			}
			try {
				g.setColor(new Color(1f, 1f, 1f, a));
				AffineTransform at = g.getTransform();
				AffineTransform at2 = (AffineTransform) at.clone();
				at2.translate(x, y);
				at2.rotate(Math.toRadians(rz));
				at2.scale(sx, sy);
				at2.translate(-ox, -oy);
				g.setTransform(at2);
				g.drawImage(buffer, 0, 0, rect.width, rect.height,
						rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);
				g.setTransform(at);
			} catch (IllegalArgumentException ex) { 
				System.err.println("Problem printing quad.");
			}
		}
		dx = x;
		dy = y;
		super.callPainters(g);
	}
	
	public float getImageX() {
		return dx;
	}
	
	public float getImageY() {
		return dy;
	}
	
	public void setBackground(LColor color) {
		setBackground(color.convert());
	}
	
	public void setImage(String path) {
		if (path == null) {
			setImage((LTexture) null, null);
			return;
		}
		LTexture img = new LTexture(path);
		setImage(img);
	}
	
	public void setImage(String path, LRect r) {
		if (path == null) {
			setImage((LTexture) null, null);
			return;
		}
		LTexture img = new LTexture(path);
		setImage(img, r);
	}
	
	public void setImage(LTexture img) {
		if (img == null) {
			setImage((LTexture) null, null);
		} else {
			setImage(img, img.getBounds());
		}
	}
	
	public void setImage(LTexture img, LRect rect) {
		rectangle = rect;
		original = img;
		disposeBuffer();
		refreshBuffer();
		repaint();
	}
	
	public void refreshBuffer() {
		if (original == null || original.convert() == null)
			return;
		disposeBuffer();
		LPoint size = original.getSize();
		buffer = LTexture.deepCopy(original.convert());
		LTexture.colorTransform(buffer, r, g, b, 1, h, s, v);
	}

	public boolean hasImage() {
		return buffer != null;
	}
	
	public LTexture getOriginalImage() {
		return original;
	}
	
	public LRect getRect() {
		return rectangle;
	}	
	
	public void setRect(LRect rect) {
		rectangle = rect;
		repaint();
	}
	
	public void setAlignment(int a) {
		align = a;
		repaint();
	}
	
	public void setOffset(float _ox, float _oy) {
		ox = _ox; oy = _oy;
	}
	
	public void setRGBA(float _r, float _g, float _b, float _a) {
		r = _r; g = _g; b = _b; a = _a;
	}
	
	public void setHSV(float _h, float _s, float _v) {
		h = _h; s = _s; v = _v;
	}
	
	public void setScale(float _sx, float _sy) {
		sx = _sx; sy = _sy;
	}
	
	public void setRotation(float _r) {
		rz = _r;
	}

	public void resetTransform() {
		r = g = b = a = s = v = sx = sy = 1;
		rz = h = ox = oy = 0;
	}

	//////////////////////////////////////////////////
	//region Properties

	private Dimension contentSize() {
		if (rectangle != null)
			return new Dimension(rectangle.width, rectangle.height);
		if (buffer != null)
			return new Dimension(buffer.getWidth(), buffer.getHeight());
		return null;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension size = rectangle == null && buffer == null ?
			super.getMinimumSize() : contentSize();
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = rectangle == null && buffer == null ?
			super.getPreferredSize() : contentSize();
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

	//endregion

}
