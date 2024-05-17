package lui.container;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import lui.graphics.LPainter;
import lui.graphics.LTexture;
import lui.base.data.LPoint;

public class LCanvas extends LView {
	
	protected Graphics2D currentEvent;
	protected ArrayList<LPainter> painters;
	
	protected Graphics2D bufferGC;
	protected BufferedImage buffer;

	/**
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter parent new lwt.dialog.LShell(800, 600)
	 */
	public LCanvas(LContainer parent) {
		super(parent, false);
		painters = new ArrayList<>();
	}
	
	public void addPainter(LPainter painter) {
		painters.add(painter);
	}
	
	public void removePainter(LPainter painter) {
		painters.remove(painter);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		callPainters((Graphics2D) g);
	}

	protected void callPainters(Graphics2D g) {
		currentEvent = g;
		for (LPainter p : painters) {
			p.setGC(currentEvent);
			p.paint();
		}
	}

	//////////////////////////////////////////////////
	//region Draw
	
	public void fillRect() {
		if (bufferGC == null) {
			Rectangle r = getBounds();
			currentEvent.fillRect(r.x, r.y, r.width, r.height);
		} else {
			bufferGC.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
		}
	}
	
	//endregion
	
	//////////////////////////////////////////////////
	//region Buffer
	
	public void setBuffer(LTexture image) {
		buffer = image.convert();
	}
	
	public void drawBuffer(int x, int y, float sx, float sy) {
		int w = buffer.getWidth();
		int h = buffer.getHeight();
		currentEvent.drawImage(buffer,
				x, y, x + Math.round(w * sx), y + Math.round(h * sy),
				0, 0, w, h,
				null);
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
		bufferGC = (Graphics2D) buffer.getGraphics();
	}
	
	public void popBuffer() {
		if (bufferGC != null)
			bufferGC.dispose();
		bufferGC = null;
	}

	public LPoint getImageSize() {
		return new LPoint(buffer.getWidth(), buffer.getHeight());
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
	}
	
	//endregion

	//////////////////////////////////////////////////
	//region Properties

	@Override
	public Dimension getMinimumSize() {
		Dimension size = buffer == null ? super.getMinimumSize()
				: new Dimension(buffer.getWidth(), buffer.getHeight());
		if (gridData != null)
			gridData.storeMinimumSize(size);
		return size;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = buffer == null ? super.getPreferredSize()
				: new Dimension(buffer.getWidth(), buffer.getHeight());
		if (gridData != null)
			gridData.storePreferredSize(size);
		return size;
	}

	//endregion

}
