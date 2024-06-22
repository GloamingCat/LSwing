package lui;

import lui.graphics.LColor;
import lui.graphics.LTexture;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class LovelyTheme extends OceanTheme {

    public static LColor BLACK = new LColor(new Color(33, 17, 47));
    public static LColor DARK = new LColor(new Color(84, 52, 96));
    public static LColor MEDIUM_DARK = new LColor(new Color(159, 83, 162));
    public static LColor MEDIUM = new LColor(new Color(190, 136, 181));
    public static LColor MEDIUM_LIGHT = new LColor(new Color(225, 184, 220));
    public static LColor LIGHT = new LColor(new Color(252, 225, 240));
    public static LColor WHITE = new LColor(new Color(255, 245, 250));

    protected static final ColorUIResource PRIMARY1 = MEDIUM_DARK.convert(); // Tree Selection border
    protected static final ColorUIResource PRIMARY2 = MEDIUM_LIGHT.convert(); // Scrollbar BG, button/label selection border
    protected static final ColorUIResource PRIMARY3 = MEDIUM.convert(); // Tree Selection BG, tree branch lines, slider/divider
    protected static final ColorUIResource PRIMARY4 = LIGHT.convert(); // Tree selection border, tab selection border
    protected static final ColorUIResource SECONDARY1 = MEDIUM_DARK.convert(); // Frame border, inner border, checkbox border, editable textfield border
    protected static final ColorUIResource SECONDARY2 = MEDIUM_LIGHT.convert(); // gradient button bottom, scrollbar outer border, menu border, static textfield border
    protected static final ColorUIResource SECONDARY3 = WHITE.convert(); // default BG, spinner arrow BG, static textfield BG
    protected static final ColorUIResource SECONDARY4 = DARK.convert(); // Combo BG

    protected static final ColorUIResource CONTROL_TEXT_COLOR = BLACK.convert();
    protected static final ColorUIResource INACTIVE_CONTROL_TEXT_COLOR = MEDIUM.convert();
    protected static final ColorUIResource MENU_DISABLED_FOREGROUND = MEDIUM_LIGHT.convert();
    protected static final ColorUIResource DESKTOP_COLOR = WHITE.convert();

    public String DIRECTORY_ICON = "icons/directory.gif";
    public String FILE_ICON = "icons/file.gif";
    public String HOME_ICON = "icons/homeFolder.gif";
    public String NEW_ICON = "icons/newFolder.gif";
    public String UP_ICON = "icons/upFolder.gif";
    public String COMPUTER_ICON = "icons/computer.gif";
    public String HD_ICON = "icons/hardDrive.gif";
    public String FLOPPY_ICON = "icons/floppy.gif";
    public static String MENU_ICON = "icons/menu.gif";
    public static String CLOSE_ICON = "icons/close.gif";
    public static String CLOSE_PRESSED_ICON = "icons/close-pressed.gif";
    public static String ICONIFY_ICON = "icons/iconify.gif";
    public static String ICONIFY_PRESSED_ICON = "icons/iconify-pressed.gif";
    public static String MAXIMIZE_ICON = "icons/maximize.gif";
    public static String MAXIMIZE_PRESSED_ICON = "icons/maximize-pressed.gif";
    public static String MINIMIZE_ICON = "icons/minimize.gif";
    public static String MINIMIZE_PRESSED_ICON = "icons/minimize-pressed.gif";
    public static String PALETTE_ICON = "icons/paletteClose.gif";
    public static String PALETTE_PRESSED_ICON = "icons/paletteClose-pressed.gif";
    public static String ERROR_ICON = "icons/error.gif";
    public static String INFO_ICON = "icons/info.gif";
    public static String QUESTION_ICON = "icons/question.gif";
    public static String WARNING_ICON = "icons/warning.gif";
    public static String EXPANDED_ICON = "icons/expanded.gif";
    public static String COLLAPSED_ICON = "icons/collapsed.gif";
    public static String COLLAPSED_RTL_ICON = "icons/collapsed-rtl.gif";

    private static class COIcon extends IconUIResource {
        private final Icon rtl;

        public COIcon(Icon ltr, Icon rtl) {
            super(ltr);
            this.rtl = rtl;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (c.getComponentOrientation().isLeftToRight()) {
                super.paintIcon(c, g, x, y);
            } else {
                rtl.paintIcon(c, g, x, y);
            }
        }
    }

    private static class IFIcon extends IconUIResource {
        private final Icon pressed;

        public IFIcon(Icon normal, Icon pressed) {
            super(normal);
            this.pressed = pressed;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            ButtonModel model = ((AbstractButton)c).getModel();
            if (model.isPressed() && model.isArmed()) {
                pressed.paintIcon(c, g, x, y);
            } else {
                super.paintIcon(c, g, x, y);
            }
        }
    }

    private static class ImageIconUIResource extends ImageIcon implements UIResource {
        public ImageIconUIResource(Image image) {
            super(image);
        }
    }

    public String getName(){ return "Lovely"; }
    @Override
    protected ColorUIResource getPrimary1() { return PRIMARY1; }
    @Override
    protected ColorUIResource getPrimary2() { return PRIMARY2; }
    @Override
    protected ColorUIResource getPrimary3() { return PRIMARY3; }
    protected ColorUIResource getPrimary4() { return PRIMARY4; }
    @Override
    protected ColorUIResource getSecondary1() { return SECONDARY1; }
    @Override
    protected ColorUIResource getSecondary2() { return SECONDARY2; }
    @Override
    protected ColorUIResource getSecondary3() { return SECONDARY3; }
    protected ColorUIResource getSecondary4() { return SECONDARY4; }

    @Override
    public ColorUIResource getDesktopColor() { return DESKTOP_COLOR; }
    @Override
    public ColorUIResource getInactiveControlTextColor() { return INACTIVE_CONTROL_TEXT_COLOR; }
    @Override
    public ColorUIResource getControlTextColor() { return CONTROL_TEXT_COLOR; }
    @Override
    public ColorUIResource getMenuDisabledForeground() { return MENU_DISABLED_FOREGROUND; }
    @Override
    protected ColorUIResource getBlack() { return SECONDARY1; }
    @Override
    protected ColorUIResource getWhite() { return SECONDARY3; }

    @Override
    public void addCustomEntriesToTable(UIDefaults table) {
        UIDefaults.LazyValue focusBorder = t -> new BorderUIResource.LineBorderUIResource(getPrimary1());
        java.util.List<?> buttonGradient = Arrays.asList(.3f, 0f,
                getPrimary4(), getWhite(), getPrimary2());
        java.util.List<?> menuGradient = Arrays.asList(1f, 0f,
                getWhite(), getPrimary4(), getSecondary2());
        java.util.List<?> sliderGradient = Arrays.asList(.3f, .2f,
                getPrimary4(), getWhite(), getSecondary2());

        Object directoryIcon = getIconResource(DIRECTORY_ICON);
        Object fileIcon = getIconResource(FILE_ICON);

        Object[] defaults = new Object[] {
            "Button.gradient", buttonGradient,
            "Button.rollover", Boolean.TRUE,
            "Button.toolBarBorderBackground", getInactiveControlTextColor(),
            "Button.disabledToolBarBorderBackground", getPrimary3(),
            "Button.rolloverIconType", "ocean",
            "Button.foreground", getBlack(),

            "CheckBox.rollover", Boolean.TRUE,
            "CheckBox.gradient", buttonGradient,
            "CheckBox.foreground", getBlack(),

            "CheckBoxMenuItem.gradient", buttonGradient,
            "CheckBoxMenuItem.foreground", getBlack(),

            "ComboBox.foreground", getBlack(),
            "ComboBox.background", getPrimary4(),
            "ComboBox.selectionForeground", getBlack(),
            "ComboBox.selectionBackground", getPrimary2(),
            "ComboBox.disabledForeground", getSecondary4(),
            "ComboBox.disabledBackground", getPrimary2(),

            "Spinner.buttonGradient", buttonGradient,

            // home2
            "FileChooser.homeFolderIcon", getIconResource(HOME_ICON),
            // directory2
            "FileChooser.newFolderIcon", getIconResource(NEW_ICON),
            // updir2
            "FileChooser.upFolderIcon", getIconResource(UP_ICON),

            // computer2
            "FileView.computerIcon", getIconResource(COMPUTER_ICON),
            "FileView.directoryIcon", directoryIcon,
            // disk2
            "FileView.hardDriveIcon", getIconResource(HD_ICON),
            "FileView.fileIcon", fileIcon,
            // floppy2
            "FileView.floppyDriveIcon", getIconResource(FLOPPY_ICON),

            "Image.background", getPrimary4(),

            "Label.disabledForeground", getInactiveControlTextColor(),
            "Label.disabledBackground", getPrimary4(),

            "List.focusCellHighlightBorder", focusBorder,
            "List.dropLineColor", getPrimary1(),
            "List.dropCellBackground", getPrimary2(),

            "Menu.opaque", Boolean.FALSE,

            "MenuBar.gradient", menuGradient,

            "MenuBar.borderColor", getPrimary3(),

            "InternalFrame.activeTitleGradient", buttonGradient,
            // close2
            "InternalFrame.closeIcon",
                (UIDefaults.LazyValue) table1 -> new IFIcon(getHastenedIcon(CLOSE_ICON, table1),
                                  getHastenedIcon(CLOSE_PRESSED_ICON, table1)),
            // minimize
            "InternalFrame.iconifyIcon",
                (UIDefaults.LazyValue) table12 -> new IFIcon(getHastenedIcon(ICONIFY_ICON, table12),
                                  getHastenedIcon(ICONIFY_PRESSED_ICON, table12)),
            // restore
            "InternalFrame.minimizeIcon",
                (UIDefaults.LazyValue) table13 -> new IFIcon(getHastenedIcon(MINIMIZE_ICON, table13),
                                  getHastenedIcon(MINIMIZE_PRESSED_ICON, table13)),
            // menubutton3
            "InternalFrame.icon", getIconResource(MENU_ICON),
            // maximize2
            "InternalFrame.maximizeIcon",
                (UIDefaults.LazyValue) table14 -> new IFIcon(getHastenedIcon(MAXIMIZE_ICON, table14),
                                  getHastenedIcon(MAXIMIZE_PRESSED_ICON, table14)),
            // paletteclose
            "InternalFrame.paletteCloseIcon",
                (UIDefaults.LazyValue) table15 -> new IFIcon(getHastenedIcon(PALETTE_ICON, table15),
                                  getHastenedIcon(PALETTE_PRESSED_ICON, table15)),

            "OptionPane.errorIcon", getIconResource(ERROR_ICON),
            "OptionPane.informationIcon", getIconResource(INFO_ICON),
            "OptionPane.questionIcon", getIconResource(QUESTION_ICON),
            "OptionPane.warningIcon", getIconResource(WARNING_ICON),

            "RadioButton.foreground", getBlack(),
            "RadioButton.gradient", buttonGradient,
            "RadioButton.rollover", Boolean.TRUE,

            "RadioButtonMenuItem.foreground", getBlack(),
            "RadioButtonMenuItem.gradient", buttonGradient,

            "ScrollBar.gradient", buttonGradient,

            "Slider.altTrackColor", getPrimary3(),
            "Slider.gradient", sliderGradient,
            "Slider.focusGradient", sliderGradient,

            "SplitPane.oneTouchButtonsOpaque", Boolean.FALSE,
            "SplitPane.dividerFocusColor", getPrimary3(),

            "TabbedPane.foreground", getBlack(),
            "TabbedPane.borderHightlightColor", getPrimary1(),
            "TabbedPane.contentAreaColor", getSecondary3(),
            "TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3),
            "TabbedPane.selected", getPrimary4(),
            "TabbedPane.tabAreaBackground", getPrimary3(),
            "TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6),
            "TabbedPane.unselectedBackground", getSecondary3(),

            "Table.focusCellHighlightBorder", focusBorder,
            "Table.gridColor", getSecondary1(),
            "Table.dropLineColor", getPrimary1(),
            "Table.dropLineShortColor", getSecondary4(),
            "Table.dropCellBackground", getSecondary4(),
            "TableHeader.focusCellBackground", getPrimary4(),

            "ToggleButton.gradient", buttonGradient,

            "ToolBar.borderColor", getPrimary3(),
            "ToolBar.isRollover", Boolean.TRUE,

            "ToolTip.background", getSecondary3(),

            "Tree.closedIcon", directoryIcon,
            "Tree.collapsedIcon", (UIDefaults.LazyValue) table16 -> new COIcon(
                    getHastenedIcon(COLLAPSED_ICON, table16),
                getHastenedIcon(COLLAPSED_RTL_ICON, table16)),

            "Tree.expandedIcon", getIconResource(EXPANDED_ICON),
            "Tree.leafIcon", fileIcon,
            "Tree.openIcon", directoryIcon,
            "Tree.selectionForeground", getBlack(),
            "Tree.selectionBorderColor", getPrimary1(),
            "Tree.selectionBackground", getPrimary4(),
            "Tree.dropLineColor", getPrimary1(),
            "Tree.dropCellBackground", getPrimary2()

        };
        table.putDefaults(defaults);
    }

    private Object getIconResource(String iconID) {
        return (UIDefaults.LazyValue) (table) -> {
            BufferedImage buffer = new LTexture(iconID).convert();
            if (buffer == null) {
                return null;
            }
            return new ImageIconUIResource(buffer);
        };
    }

    private Icon getHastenedIcon(String iconID, UIDefaults table) {
        Object res = getIconResource(iconID);
        return (Icon)((UIDefaults.LazyValue)res).createValue(table);
    }

}