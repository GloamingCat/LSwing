package lui.graphics;

import lui.base.data.LPoint;

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

	public LPoint[] transform(double sx, double sy, double r) {
		r = Math.toRadians(r);
		double c = Math.cos(r);
		double s = Math.sin(r);
		LPoint[] p = new LPoint[4];
		double x1 = -sx * x;
		double y1 = -sy * y;
		double x2 = sx * (width - x);
		double y2 = sy * (height - y);
		p[0] = new LPoint((int) Math.round(c * x1 - s * y1), (int) Math.round(s * x1 + c * y1));
		p[1] = new LPoint((int) Math.round(c * x2 - s * y1), (int) Math.round(s * x2 + c * y1));
		p[2] = new LPoint((int) Math.round(c * x2 - s * y2), (int) Math.round(s * x2 + c * y2));
		p[3] = new LPoint((int) Math.round(c * x1 - s * y2), (int) Math.round(s * x1 + c * y2));
		return p;
	}

	public LPoint[] getLimits(double sx, double sy, double r) {
		LPoint[] pts = transform(sx, sy, r);
		LPoint max = new LPoint(Integer.MIN_VALUE, Integer.MIN_VALUE);
		LPoint min = new LPoint(Integer.MAX_VALUE, Integer.MAX_VALUE);
		for (LPoint p : pts) {
			max.x = Math.max(max.x, p.x);
			min.x = Math.min(min.x, p.x);
			max.y = Math.max(max.y, p.y);
			min.y = Math.min(min.y, p.y);
		}
		return new LPoint[] {min, max};
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ") " + width + "x" + height;
	}

}
