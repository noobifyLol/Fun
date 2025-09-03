import java.awt.*;
import javax.swing.*;

public class FadingPanel extends JPanel {
    private float alpha = 1f; // 1 = fully visible, 0 = invisible

    public FadingPanel() {
        super();
        // Leave opacity alone; we want normal opaque behavior.
        // The alpha is applied in paint(...) below to the entire component.
    }

    public FadingPanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    public void paint(Graphics g) {
        // Apply alpha to the WHOLE component (background + children)
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paint(g2);
        g2.dispose();
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0f, Math.min(1f, alpha));
        repaint();
    }

    public float getAlpha() {
        return alpha;
    }
}
