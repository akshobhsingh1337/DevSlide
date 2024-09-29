package org.group51;

import javax.swing.*;
import java.awt.*;

public class ResizeHandle extends JComponent {
  private static final int SIZE = 8;
  private static final Color COLOR = new Color(0, 122, 255);

  public ResizeHandle() {
    setPreferredSize(new Dimension(SIZE, SIZE));
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setColor(COLOR);
    g2d.fillOval(0, 0, SIZE, SIZE);
  }
}