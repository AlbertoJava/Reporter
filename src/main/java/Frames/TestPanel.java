package Frames;

import javax.swing.*;
import java.awt.*;

public class TestPanel extends JScrollPane  implements Scrollable{


    public TestPanel(Component test) {
        super (test);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(100,100);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 2;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 2;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return true;
    }
}
