package lui.container;

import lui.widget.LControlWidget;

import java.awt.*;

public abstract class LControlView<T> extends LView {

	public LControlView(LContainer parent) {
		super(parent, false);
		setLayout(new GridLayout(1, 1));
	}
	
	public abstract LControlWidget<T> getControl();

}
