package lui.layout;

import lui.base.data.LPoint;
import java.awt.*;

public interface LLayedCell extends lui.base.gui.LLayedCell {

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
