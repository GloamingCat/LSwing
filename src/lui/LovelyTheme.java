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

    public static LColor BLACK = new LColor(new Color(29, 15, 42));
    public static LColor DARK = new LColor(new Color(64, 42, 79));
    public static LColor MEDIUM_DARK = new LColor(new Color(159, 83, 162));
    public static LColor MEDIUM = new LColor(new Color(190, 136, 177));
    public static LColor MEDIUM_LIGHT = new LColor(new Color(248, 209, 229));
    public static LColor LIGHT = new LColor(new Color(252, 220, 240));
    public static LColor WHITE = new LColor(new Color(255, 245, 250));

    public static ColorUIResource PRIMARY1 = MEDIUM_DARK.convert(); // Tree Selection border
    public static ColorUIResource PRIMARY2 = MEDIUM_LIGHT.convert(); // Scrollbar BG, button/label selection border
    public static ColorUIResource PRIMARY3 = MEDIUM.convert(); // Tree Selection BG, tree branch lines
    public static ColorUIResource SECONDARY1 = MEDIUM_DARK.convert(); // Frame border, inner border, checkbox border, editable textfield border
    public static ColorUIResource SECONDARY2 = MEDIUM_LIGHT.convert(); // gradient button bottom, scrollbar outer border, menu border, static textfield border
    public static ColorUIResource SECONDARY3 = WHITE.convert(); // default BG, spinner arrow BG, static textfield BG

    public static ColorUIResource CONTROL_TEXT_COLOR = BLACK.convert();
    public static ColorUIResource INACTIVE_CONTROL_TEXT_COLOR = MEDIUM.convert();
    public static ColorUIResource MENU_DISABLED_FOREGROUND = LIGHT.convert();
    public static ColorUIResource DESKTOP_COLOR = WHITE.convert();

    public static ColorUIResource DROP_COLOR = LIGHT.convert();
    public static ColorUIResource DROP_COLOR2 = CONTROL_TEXT_COLOR;

    public static Color BORDER_COLOR = MEDIUM.convert();
    public static Color TAB_LABEL_COLOR = MEDIUM.convert();
    public static Color TAB_BG_COLOR = WHITE.convert();
    public static Color TAB_SELECTED_COLOR = LIGHT.convert();
    public static Color TREE_SELECTION_COLOR = LIGHT.convert();
    public static Color TABLE_CELL_COLOR = LIGHT.convert();
    public static Color SPLIT_DIVIDER_COLOR = MEDIUM.convert();
    public static Color SLIDER_COLOR = MEDIUM.convert();
    public static Color IMAGE_BG_COLOR = LIGHT.convert();

    public static Color GRADIENT1 = MEDIUM_LIGHT.convert();
    public static Color GRADIENT2 = LIGHT.convert();
    public static Color GRADIENT3 = LIGHT.convert();

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

        public void paintIcon(Component c, Graphics g, int x, int y) {
            ButtonModel model = ((AbstractButton)c).getModel();
            if (model.isPressed() && model.isArmed()) {
                pressed.paintIcon(c, g, x, y);
            } else {
                super.paintIcon(c, g, x, y);
            }
        }
    }

    private static class ImageIconUIResource extends ImageIcon implements UIResource, UIDefaults.LazyValue {
        /**
         * Calls the superclass constructor with the same parameter.
         *
         * @param imageData an array of pixels
         * @see javax.swing.ImageIcon#ImageIcon(byte[])
         */
        public ImageIconUIResource(byte[] imageData) {
            super(imageData);
        }

        /**
         * Calls the superclass constructor with the same parameter.
         *
         * @param image an image
         * @see javax.swing.ImageIcon#ImageIcon(Image)
         */
        public ImageIconUIResource(Image image) {
            super(image);
        }

        @Override
        public Object createValue(UIDefaults table) {
            return null;
        }

    }

    public String getName(){ return "Lovely"; }
    @Override
    protected ColorUIResource getPrimary1() { return PRIMARY1; }
    @Override
    protected ColorUIResource getPrimary2() { return PRIMARY2; }
    @Override
    protected ColorUIResource getPrimary3() { return PRIMARY3; }
    @Override
    protected ColorUIResource getSecondary1() { return SECONDARY1; }
    @Override
    protected ColorUIResource getSecondary2() { return SECONDARY2; }
    @Override
    protected ColorUIResource getSecondary3() { return SECONDARY3; }

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
            GRADIENT1, getWhite(), getSecondary2());

        Object directoryIcon = getIconResource(DIRECTORY_ICON);
        Object fileIcon = getIconResource(FILE_ICON);
        java.util.List<?> sliderGradient = Arrays.asList(.3f, .2f,
                GRADIENT3, getWhite(), getSecondary2());

        Object[] defaults = new Object[] {
            "Button.gradient", buttonGradient,
            "Button.rollover", Boolean.TRUE,
            "Button.toolBarBorderBackground", getInactiveControlTextColor(),
            "Button.disabledToolBarBorderBackground", BORDER_COLOR,
            "Button.rolloverIconType", "ocean",
            "Button.foreground", getBlack(),

            "CheckBox.rollover", Boolean.TRUE,
            "CheckBox.gradient", buttonGradient,
            "CheckBox.foreground", getBlack(),

            "CheckBoxMenuItem.gradient", buttonGradient,
            "CheckBoxMenuItem.foreground", getBlack(),

            "ComboBox.foreground", getBlack(),

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

            "Image.background", IMAGE_BG_COLOR,

            "Label.disabledForeground", getInactiveControlTextColor(),

            "Menu.opaque", Boolean.FALSE,

            "MenuBar.gradient", Arrays.asList(1f, 0f,
                getWhite(), GRADIENT2, new ColorUIResource(GRADIENT2)),

            "MenuBar.borderColor", BORDER_COLOR,

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

            "List.focusCellHighlightBorder", focusBorder,

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

            "Slider.altTrackColor", SLIDER_COLOR,
            "Slider.gradient", sliderGradient,
            "Slider.focusGradient", sliderGradient,

            "SplitPane.oneTouchButtonsOpaque", Boolean.FALSE,
            "SplitPane.dividerFocusColor", SPLIT_DIVIDER_COLOR,

            "TabbedPane.foreground", getBlack(),
            "TabbedPane.borderHightlightColor", getPrimary1(),
            "TabbedPane.contentAreaColor", TAB_BG_COLOR,
            "TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3),
            "TabbedPane.selected", TAB_SELECTED_COLOR,
            "TabbedPane.tabAreaBackground", TAB_LABEL_COLOR,
            "TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6),
            "TabbedPane.unselectedBackground", SECONDARY3,

            "Table.focusCellHighlightBorder", focusBorder,
            "Table.gridColor", SECONDARY1,
            "TableHeader.focusCellBackground", TABLE_CELL_COLOR,

            "ToggleButton.gradient", buttonGradient,

            "ToolBar.borderColor", BORDER_COLOR,
            "ToolBar.isRollover", Boolean.TRUE,

            "Tree.closedIcon", directoryIcon,

            "Tree.collapsedIcon", (UIDefaults.LazyValue) table16 -> new COIcon(
                    getHastenedIcon(COLLAPSED_ICON, table16),
                getHastenedIcon(COLLAPSED_RTL_ICON, table16)),

            "Tree.expandedIcon", getIconResource(EXPANDED_ICON),
            "Tree.leafIcon", fileIcon,
            "Tree.openIcon", directoryIcon,
            "Tree.selectionForeground", getBlack(),
            "Tree.selectionBorderColor", getPrimary1(),
            "Tree.selectionBackground", TREE_SELECTION_COLOR,
            "Tree.dropLineColor", getPrimary1(),

            "Table.dropLineColor", getPrimary1(),
            "Table.dropLineShortColor", DROP_COLOR2,
            "ToolTip.background", getPrimary2(),

            "Table.dropCellBackground", DROP_COLOR,
            "Tree.dropCellBackground", DROP_COLOR,
            "List.dropCellBackground", DROP_COLOR,
            "List.dropLineColor", getPrimary1()
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