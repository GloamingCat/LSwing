package lwt.container;

import lwt.widget.LControlWidget;

public abstract class LControlView<T> extends LView {

	private static final long serialVersionUID = 1L;

	public LControlView(LContainer parent) {
		super(parent, false);
		setFillLayout(true);
	}
	
	public abstract LControlWidget<T> getControl();

}
