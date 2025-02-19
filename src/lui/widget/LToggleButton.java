package lui.widget;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import lui.base.data.LPoint;
import lui.container.LContainer;
import lui.graphics.LTexture;

public class LToggleButton extends LControlWidget<Boolean> {

	JLabel icon;
	LTexture imgTrue;
	LTexture imgFalse;
	private boolean enabled = true;

	public LToggleButton(LContainer parent) {
		super(parent);
	}
	
	public LToggleButton(LContainer parent, String imgTrue, String imgFalse) {
		this(parent);
		this.imgFalse = new LTexture(imgFalse);
		this.imgTrue = new LTexture(imgTrue);
		icon.setIcon(new ImageIcon(this.imgFalse.convert()));
		icon.setHorizontalAlignment(JLabel.CENTER);
		icon.setVerticalAlignment(JLabel.CENTER);
		icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (enabled)
					setValue(!currentValue);
				newModifyAction(!currentValue, currentValue);
			}
		});
	}

	@Override
	protected final void createContent(int flags) {
		icon = new JLabel();
		add(icon);
	}
	
	public void setImages(LTexture imgTrue, LTexture imgFalse) {
		this.imgFalse = imgFalse;
		this.imgTrue = imgTrue;
	}

	public void setValue(Object obj) {
		if (obj != null) {
			enabled = true;
			Boolean i = (Boolean) obj;
			icon.setIcon(new ImageIcon(i ? imgTrue.convert() : imgFalse.convert()));
			currentValue = i;
		} else {
			enabled = false;
			icon.setIcon(new ImageIcon(imgFalse.convert()));
			currentValue = null;
		}
	}
	
	@Override
	protected JComponent getControl() {
		return icon;
	}

	@Override
	public String encodeData(Boolean value) {
		return value + "";
	}
	
	@Override
	public Boolean decodeData(String str) {
		return Boolean.parseBoolean(str);
	}

	//////////////////////////////////////////////////
	//region Properties

	@Override
	public Dimension getMinimumSize() {
		LPoint p = imgFalse.getSize();
		return new Dimension(p.x, p.y);
	}

	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	//endregion

}
