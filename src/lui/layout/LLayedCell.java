package lui.layout;

import lui.base.data.LPoint;
import java.awt.*;

public interface LLayedCell extends lui.base.gui.LLayedCell {

    LCellData getCellData();

    Dimension getSize(Dimension d);
    Dimension getPreferredSize();
    Dimension getMinimumSize();

    void setSize(int width, int height);
    void setPreferredSize(Dimension d);
    void setMinimumSize(Dimension d);

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

    @Override
    default LPoint getRequiredSize() {
        Dimension size = getMinimumSize();
        return new LPoint(size.width, size.height);
    }

    @Override
    default void setCurrentSize(int width, int height) {
        setSize(width, height);
    }

    @Override
    default void setTargetSize(int width, int height) {
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    default void setRequiredSize(int width, int height) {
        setMinimumSize(new Dimension(width, height));
    }

}
