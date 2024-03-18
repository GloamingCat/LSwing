package lwt.container;

import java.awt.CardLayout;
import java.awt.Component;

import lwt.widget.LWidget;

public class LStack extends LPanel {

	private static final long serialVersionUID = 1L;
	private CardLayout stack;
	
	public LStack(LContainer parent) {
		super(parent);
		stack = new CardLayout();
		setLayout(stack);
	}
	
	public void setTop(LContainer container) {
		stack.first(this);
		for (Component c : getComponents()) {
			if (c == container)
				break;
			stack.next(this);
		}
		refreshLayout();
	}
	
	public void setTop(LWidget widget) {
		stack.first(this);
		for (Component c : getComponents()) {
			if (c == widget)
				break;
			stack.next(this);
		}
		refreshLayout();
	}

}
