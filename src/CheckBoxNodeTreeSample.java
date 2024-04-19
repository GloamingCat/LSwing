/*
Definitive Guide to Swing for Java 2, Second Edition
By John Zukowski     
ISBN: 1-893115-78-X
Publisher: APress
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemListener;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

public class CheckBoxNodeTreeSample {
  public static void main(String[] args) {

    JFrame frame = new JFrame("CheckBox Tree");

    DefaultMutableTreeNode accessibility = new DefaultMutableTreeNode(new CheckBoxNode("Accessibility", false));
    accessibility.add(new DefaultMutableTreeNode(new CheckBoxNode("Move system caret with focus/selection changes", false)));
    accessibility.add(new DefaultMutableTreeNode(new CheckBoxNode("Always expand alt text for images", true)));
    DefaultMutableTreeNode browsing = new DefaultMutableTreeNode(new CheckBoxNode("Browsing", false));
    browsing.add(new DefaultMutableTreeNode(new CheckBoxNode("Disable script debugging", true)));
    browsing.add(new DefaultMutableTreeNode(new CheckBoxNode("Notify when downloads complete", true)));
    browsing.add(new DefaultMutableTreeNode(new CheckBoxNode("Use AutoComplete", true)));
    browsing.add(new DefaultMutableTreeNode(new CheckBoxNode("Browse in a new process", false)));

    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new CheckBoxNode("Root", false));
    root.add(accessibility);
    root.add(browsing);
    JTree tree = new JTree(root);
    tree.setRootVisible(false);

    CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
    tree.setCellRenderer(renderer);

    tree.setCellEditor(new CheckBoxNodeEditor(tree));
    tree.setEditable(true);
    tree.addTreeSelectionListener(e -> System.out.println(e.getSource()));

    JScrollPane scrollPane = new JScrollPane(tree);
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    frame.setSize(300, 150);
    frame.setVisible(true);
  }
}

class CheckBoxPanel extends JPanel {
  public final JCheckBox checkBox;
  public final JLabel label;
  public CheckBoxPanel() {
    super();
    checkBox = new JCheckBox();
    label = new JLabel();
    setLayout(new BorderLayout());
    add(checkBox, BorderLayout.WEST);
    add(label, BorderLayout.CENTER);
    Font fontValue = UIManager.getFont("Tree.font");
    if (fontValue != null)
      label.setFont(fontValue);
    Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
    checkBox.setFocusPainted((booleanValue != null) && (booleanValue));
  }

}

class CheckBoxNodeRenderer implements TreeCellRenderer {

  private final CheckBoxPanel panel;

  Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;

  protected JCheckBox getCheckBox() {
    return panel.checkBox;
  }

  protected JLabel getLabel() {
    return panel.label;
  }

  public CheckBoxNodeRenderer() {
    panel = new CheckBoxPanel();
    selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
    selectionForeground = UIManager.getColor("Tree.selectionForeground");
    selectionBackground = UIManager.getColor("Tree.selectionBackground");
    textForeground = UIManager.getColor("Tree.textForeground");
    textBackground = UIManager.getColor("Tree.textBackground");
  }

  public CheckBoxPanel getTreeCellRendererComponent(JTree tree, Object value,
      boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
    panel.label.setText(stringValue);
    panel.checkBox.setSelected(false);
    panel.checkBox.setEnabled(tree.isEnabled());
    if (selected) {
      panel.checkBox.setForeground(selectionForeground);
      panel.checkBox.setBackground(selectionBackground);
    } else {
      panel.checkBox.setForeground(textForeground);
      panel.checkBox.setBackground(textBackground);
    }
    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
    CheckBoxNode node = (CheckBoxNode) userObject;
    panel.label.setText(node.text);
    panel.checkBox.setSelected(node.selected);
    return panel;
  }
}

class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

  CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
  CheckBoxNode data;
  JTree tree;

  public CheckBoxNodeEditor(JTree tree) {
    this.tree = tree;
  }

  public Object getCellEditorValue() {
    data.selected = renderer.getCheckBox().isSelected();
    data.text = renderer.getLabel().getText();
    return data;
  }

  public boolean isCellEditable(EventObject event) {
    return true;
  }

  public Component getTreeCellEditorComponent(JTree tree, Object value,
      boolean selected, boolean expanded, boolean leaf, int row) {
    CheckBoxPanel editor = renderer.getTreeCellRendererComponent(tree, value,
        true, expanded, leaf, row, true);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    data = (CheckBoxNode) node.getUserObject();
    // editor always selected / focused
    ItemListener itemListener = itemEvent -> {
      if (stopCellEditing())
        fireEditingStopped();
    };
    editor.checkBox.addItemListener(itemListener);
    return editor;
  }
}

class CheckBoxNode {
  public String text;
  public boolean selected;

  public CheckBoxNode(String text, boolean selected) {
    this.text = text;
    this.selected = selected;
  }

  public String toString() {
    return getClass().getName() + "[" + text + "/" + selected + "]";
  }
}
         