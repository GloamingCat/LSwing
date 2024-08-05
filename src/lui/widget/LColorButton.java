package lui.widget;

import lui.base.LVocab;
import lui.container.LContainer;
import lui.container.LImage;
import lui.graphics.LColor;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Type;

public class LColorButton extends LObjectButton<LColor> {

	protected LImage image = null;

	private final Color nullColor = new Color(0, 0, 0, 0);

	public LColorButton(LContainer parent) {
		super(parent);
		button.onClick = e -> {
			Color initColor = currentValue == null ? Color.white : currentValue.convert();
			Color newColor = JColorChooser.showDialog(this, LVocab.instance.COLORDIALOG, initColor, true);
			if (newColor != null && !initColor.equals(newColor)) {
				LColor oldColor = currentValue;
				setValue(new LColor(newColor));
				newModifyAction(currentValue, oldColor);
			}
		};
	}

	public void setImageWidget(LImage image) {
		this.image = image;
		if (image != null)
			image.setBackground(nullColor);
	}

	@Override
	public void setValue(Object value) {
		if (value != null) {
			setEnabled(true);
			LColor c = (LColor) value;
			if (image != null) {
				image.setBackground(c);
			}
			currentValue = c;
		} else {
			setEnabled(false);
			if (image != null) {
				image.setBackground(nullColor);
				image.setImage("img/pencil.png");
			}
			currentValue = null;
		}
	}
	
	@Override
	protected Type getType() {
		return LColor.class;
	}
	
}