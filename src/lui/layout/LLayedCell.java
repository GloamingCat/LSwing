package lui.layout;

import lui.base.data.LPoint;
import java.awt.*;

public interface LLayedCell extends lui.base.gui.LLayedCell {

    @Override
    LCellData getCellData();

    Dimension getSize(Dimension d);
    Dimension getPreferredSize();
    Dimension getMinimumSize();
    void revalidate();
    void setSize(int width, int height);

    @Override
    default LPoint getCurrentSize() {
        Dimension size = getSize(null);
        return new LPoint(size.width, size.height);
    }

    @Override
    default LPoint getTargetSize() {
        //revalidate();
        Dimension size = getPreferredSize();
        return new LPoint(size.width, size.height);
    }

    @Override
    default LPoint getRequiredSize() {
        Dimension size = getMinimumSize();
        return new LPoint(size.width, size.height);
    }

    @Override
    default void setCurrentSize(int width, int height) {
        setSize(width, height);
    }

}
