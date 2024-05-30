package lui.container;

import java.awt.*;
import java.awt.geom.AffineTransform;

import lui.base.LFlags;
import lui.graphics.LColor;
import lui.base.data.LPoint;
import lui.graphics.LRect;
import lui.graphics.LTexture;

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
		setBackground(new LColor(224, 224, 224));
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
			int w = Math.round(rect.width * sx);
			int h = Math.round(rect.height * sy);
			if ((align & LFlags.RIGHT) > 0) {
				x = bounds.width - w;
			} else if ((align & LFlags.MIDDLE) > 0) {
				x = (bounds.width - w) / 2;
			}
			if ((align & LFlags.BOTTOM) > 0) {
				y = bounds.height - h;
			} else if ((align & LFlags.CENTER) > 0) {
				y = (bounds.height - h) / 2;
			}
			try {
				g.setColor(new Color(1f, 1f, 1f, a));
				AffineTransform at = null;
				if (rz != 0) {
					at = g.getTransform();
					AffineTransform t = AffineTransform.getTranslateInstance(ox * sx - x, oy * sy - y);
					t.rotate(Math.toRadians(rz));
					t.translate(-ox * sx + x, -oy * sy + y);
					g.transform(t);
				}
				g.drawImage(buffer, x, y, x + w, y + h,
						rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,null);
				if (at != null)
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
