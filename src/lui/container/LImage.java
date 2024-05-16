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
	
	private float r = 1, g = 1, b = 1;
	private float h = 0, s = 1, v = 1;
	private int a = 255;
	
	private float ox = 0, oy = 0;
	private float sx = 1, sy = 1;
	private float rz = 0;
	
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
				g.setColor(new Color(255, 255, 255, a));
				if (rz != 0) {
					AffineTransform t = g.getTransform();
					t.translate(ox * sx, oy * sy);
					t.rotate(rz);
					t.translate(-ox * sx, -oy * sy);
					g.setTransform(t);
				}
				g.drawImage(buffer, x, y, x + w, y + h,
						rect.x, rect.y, rect.x + rect.width, rect.y + rect.height,null);
				if (rz != 0)
					g.setTransform(null);
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
		refreshImage();
		rectangle = rect;
		redraw();
	}
	
	public void refreshImage() {
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
		redraw();
	}
	
	public LPoint getImageSize() {
		return new LPoint(buffer.getWidth(), buffer.getHeight());
	}
	
	public void setAlignment(int a) {
		align = a;
		redraw();
	}
	
	public void setOffset(float _ox, float _oy) {
		ox = _ox; oy = _oy;
	}
	
	public void setRGBA(float _r, float _g, float _b, float _a) {
		r = _r; g = _g; b = _b; a = Math.round(_a * 255);
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
