package lwt.layout;

import lbase.data.LPoint;
import java.awt.*;

public interface LLayedCell extends lbase.gui.LLayedCell {

    LCellData getCellData();

    Dimension getSize(Dimension d);
    Dimension getPreferredSize();

    @Override
    default LPoint getCurrentSize() {
        Dimension size = getSize(null);
        return new LPoint(size.width, size.height);
    }

    @Override
    default LPoint getTargetSize() {
        Dimension size = getPreferredSize();
        return new LPoint(size.width, size.height);
    }
}
