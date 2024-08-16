package lui.widget;

import lui.base.LPrefs;
import lui.container.LContainer;
import lui.container.LPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LSeparator extends LPanel {

    JLabel label = null;

    public LSeparator(LContainer parent, boolean horizontal) {
        super(parent);
        getCellData().setExpand(horizontal, !horizontal);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        addLine(horizontal);
    }

    public LSeparator(LContainer parent, String title) {
        this(parent, true);
        initLabel();
        label.setText(title);
        addLine(true);
    }

    public LSeparator(LContainer parent, String title, String tooltip) {
        this(parent, title);
        label.setToolTipText(tooltip);
    }

    public void setTitle(String text) {
        initLabel();
        label.setText(text);
    }

    @Override
    public void setHoverText(String text) {
        initLabel();
        label.setToolTipText(text);
    }

    private void initLabel() {
        if (label != null)
            return;
        label = new JLabel();
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        add(label);
    }

    private void addLine(boolean horizontal) {
        JSeparator s = new JSeparator(horizontal ? JSeparator.HORIZONTAL : JSeparator.VERTICAL);
        if (horizontal) {
            s.setBorder(new EmptyBorder(0, LPrefs.GRIDSPACING, 0, LPrefs.GRIDSPACING));
            s.setAlignmentY(JSeparator.CENTER);
        } else {
            s.setBorder(new EmptyBorder(LPrefs.GRIDSPACING, 0, LPrefs.GRIDSPACING, 0));
            s.setAlignmentX(JSeparator.CENTER);
        }
		add(s);
    }

}
