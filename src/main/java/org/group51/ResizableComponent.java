package org.group51;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResizableComponent extends JComponent {
  private static final int HANDLE_SIZE = 8;
  private final ResizeHandle[] handles = new ResizeHandle[4];
  private boolean dragging = false;
  private int dragIndex = -1;
  private Point dragStartPoint;

  public ResizableComponent() {
    setLayout(null);
    for (int i = 0; i < handles.length; i++) {
      handles[i] = new ResizeHandle();
      add(handles[i]);
    }
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        for (int i = 0; i < handles.length; i++) {
          if (handles[i].getBounds().contains(e.getPoint())) {
            dragging = true;
            dragIndex = i;
            dragStartPoint = e.getPoint();
            return;
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        dragging = false;
        dragIndex = -1;
      }
    });
    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        if (dragging && dragIndex != -1) {
          Point dragEndPoint = e.getPoint();
          int dx = dragEndPoint.x - dragStartPoint.x;
          int dy = dragEndPoint.y - dragStartPoint.y;
          resizeComponent(dx, dy);
          dragStartPoint = dragEndPoint;
        }
      }
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    updateHandles();
  }

  private void updateHandles() {
    int w = getWidth();
    int h = getHeight();
    handles[0].setLocation(0, 0); // Top-left
    handles[1].setLocation(w - HANDLE_SIZE, 0); // Top-right
    handles[2].setLocation(0, h - HANDLE_SIZE); // Bottom-left
    handles[3].setLocation(w - HANDLE_SIZE, h - HANDLE_SIZE); // Bottom-right
  }

  private void resizeComponent(int dx, int dy) {
    int newWidth = getWidth();
    int newHeight = getHeight();
    int newX = getX();
    int newY = getY();

    switch (dragIndex) {
      case 0: // Top-left
        newWidth -= dx;
        newHeight -= dy;
        newX += dx;
        newY += dy;
        break;
      case 1: // Top-right
        newWidth += dx;
        newHeight -= dy;
        newY += dy;
        break;
      case 2: // Bottom-left
        newWidth -= dx;
        newHeight += dy;
        newX += dx;
        break;
      case 3: // Bottom-right
        newWidth += dx;
        newHeight += dy;
        break;
    }

    setBounds(newX, newY, newWidth, newHeight);
    revalidate();
    repaint();
  }
}