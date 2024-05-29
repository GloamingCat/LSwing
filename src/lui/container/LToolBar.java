package lui.container;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.*;

import lui.graphics.LTexture;

public class LToolBar extends LView {

	private ButtonGroup radioGroup = null;

	private final JToolBar toolBar;
	
	public LToolBar(LContainer parent) {
		super(parent, false);
		setLayout(new GridLayout(1, 1));
		toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		toolBar.setRollover(false);
		//toolBar.setBorderPainted(false);
		toolBar.setAlignmentX(JToolBar.LEFT);
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
		toolBar.add(item);
	}
	
	public void addCheckItem(Consumer<Boolean> onSelect, String txt, boolean selected) {
		JCheckBox item = new JCheckBox();
		item.setSelected(selected);
		if (onSelect != null) {
			item.addActionListener(e -> onSelect.accept(item.isSelected()));
		}
		BufferedImage img = LTexture.getBufferedImage(txt);
		if (img == null)
			item.setText(txt);
		else
			item.setIcon(new ImageIcon(img));
		toolBar.add(item);
	}
	
	public <T> void addButtonItem(Consumer<T> onSelect, T data, String txt) {
		JButton item = new JButton();
		if (onSelect != null) {
			item.addActionListener(e -> onSelect.accept(data));
		}
		BufferedImage img = LTexture.getBufferedImage(txt);
		if (img == null)
			item.setText(txt);
		else
			item.setIcon(new ImageIcon(img));
		toolBar.add(item);
	}

	public <T> void addListItem(Consumer<T> onSelect, T[] data, String txt, String[] txtList) {
		JButton button = new JButton();
		BufferedImage img = LTexture.getBufferedImage(txt);
		if (img == null)
			button.setText(txt);
		else
			button.setIcon(new ImageIcon(img));
		toolBar.add(button);
		JPopupMenu menu = new JPopupMenu();
		button.addActionListener(e -> menu.show(button, 0, button.getHeight()));
		for (int i = 0; i < txtList.length; i++) {
			final T value = data[i];
			JMenuItem item = new JMenuItem();
			if (onSelect != null) {
				item.addActionListener(e -> onSelect.accept(value));
			}
			img = LTexture.getBufferedImage(txtList[i]);
			if (img == null)
				item.setText(txtList[i]);
			else
				item.setIcon(new ImageIcon(img));
			menu.add(item);
		}
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
