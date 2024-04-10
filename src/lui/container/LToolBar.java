package lui.container;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;

import lui.graphics.LTexture;

public class LToolBar extends LPanel {

	private final JToolBar toolBar;
	
	public LToolBar(LContainer parent) {
		super(parent);
		toolBar = new JToolBar();
		parent.getContentComposite().add(toolBar);
		setFillLayout(true);
	}
	
	public <T> void addItem(Consumer<T> onSelect, T data, String txt, boolean selected) {
		JRadioButton item = new JRadioButton();
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
	}

}
