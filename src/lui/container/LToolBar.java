package lui.container;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.*;

import lui.graphics.LTexture;

public class LToolBar extends LView {

	private ButtonGroup radioGroup = null;

	private final JToolBar toolBar;
	
	public LToolBar(LContainer parent) {
		super(parent, false);
		toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		toolBar.setRollover(false);
		//toolBar.setBorderPainted(false);
		toolBar.setAlignmentX(JToolBar.LEFT);
		setFillLayout(true);
		add(toolBar);
	}
	
	public <T> void addItem(Consumer<T> onSelect, T data, String txt, boolean selected) {
		if (radioGroup == null) {
			radioGroup = new ButtonGroup();
		}
		JRadioButton item = new JRadioButton();
		radioGroup.add(item);
		item.setSelected(selected);
		item.setBorderPainted(selected);
		toolBar.add(item);
		item.addChangeListener(e -> item.setBorderPainted(item.isSelected()));
		if (onSelect != null) {
			item.addActionListener(e -> onSelect.accept(data));
		}
		BufferedImage img = LTexture.getBufferedImage(txt);
		if (img == null)
			item.setText(txt);
		else {
			item.setIcon(new ImageIcon(img));
		}
	}
	
	public void addCheckItem(Consumer<Boolean> onSelect, String txt, boolean selected) {
		JCheckBox item = new JCheckBox();
		toolBar.add(item);
		item.setSelected(selected);
		if (onSelect != null) {
			item.addActionListener(e -> onSelect.accept(item.isSelected()));
		}
		BufferedImage img = LTexture.getBufferedImage(txt);
		if (img == null)
			item.setText(txt);
		else
			item.setIcon(new ImageIcon(img));
	}
	
	public <T> void addButtonItem(Consumer<T> onSelect, T data, String txt) {
		JButton item = new JButton();
		toolBar.add(item);
		if (onSelect != null) {
			item.addActionListener(e -> onSelect.accept(data));
		}
		BufferedImage img = LTexture.getBufferedImage(txt);
		if (img == null)
			item.setText(txt);
		else
			item.setIcon(new ImageIcon(img));
	}
	
	public void addSeparator() {
		toolBar.addSeparator();
		radioGroup = null;
	}

	@Override
	public JComponent getTopComposite() {
		return this;
	}

	@Override
	public JComponent getContentComposite() {
		return toolBar;
	}

}
