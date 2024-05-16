package lui.container;

import java.awt.CardLayout;
import java.awt.Component;

import lui.widget.LWidget;

public class LStack extends LPanel {

	private final CardLayout stack;
	
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

}
