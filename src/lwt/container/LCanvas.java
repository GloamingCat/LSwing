package lwt.container;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import lwt.graphics.LPainter;
import lwt.graphics.LTexture;

public class LCanvas extends LView {
	private static final long serialVersionUID = 1L;
	
	protected Graphics currentEvent;
	protected ArrayList<LPainter> painters;
	
	protected Graphics bufferGC;
	protected BufferedImage buffer;

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new lwt.dialog.LShell(800, 600)
	 */
	public LCanvas(LContainer parent) {
		super(parent, false);
		painters = new ArrayList<LPainter>();
	}
	
	public void addPainter(LPainter painter) {
		painters.add(painter);
	}
	
	public void removePainter(LPainter painter) {
		painters.remove(painter);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		currentEvent = g;
		for (LPainter p : painters) {
			p.setGC(g);
			p.paint();
		}
	}

	//////////////////////////////////////////////////
	// {{ Draw
	
	public void fillRect() {
		if (bufferGC == null) {
			Rectangle r = getBounds();
			currentEvent.fillRect(r.x, r.y, r.width, r.height);
		} else {
			bufferGC.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
		}
	}
	
	// }}
	
	//////////////////////////////////////////////////
	// {{ Buffer
	
	public void setBuffer(LTexture image) {
		buffer = image.convert();
	}
	
	public void drawBuffer(int x, int y, float sx, float sy) {
		int w = buffer.getWidth();
		int h = buffer.getHeight();
		currentEvent.drawImage(buffer, 0, 0, w, h,
				x, y, Math.round(w * sx), Math.round(h * sy), null);
	}
	
	public void drawBuffer(int x, int y) {
		currentEvent.drawImage(buffer, x, y, null);
	}
	
	public void pushBuffer() {
		Rectangle r = getBounds();
		pushBuffer(r.width, r.height);
	}
	
	public void pushBuffer(int w, int h) {
		buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bufferGC = buffer.getGraphics();
	}
	
	public void popBuffer() {
		if (bufferGC != null)
			bufferGC.dispose();
		bufferGC = null;
	}
	
	public LPainter getBufferPainter() {
		return new LPainter(bufferGC) {
			@Override
			public void paint() {}
		};
	}
	
	public void disposeBuffer() {
		if (buffer != null)
			buffer.flush();
		buffer = null;
	}
	
	public void dispose() {
		super.dispose();
		disposeBuffer();
	};
	
	// }}
	
	public void redraw() {
		super.repaint();
	}
	
}
