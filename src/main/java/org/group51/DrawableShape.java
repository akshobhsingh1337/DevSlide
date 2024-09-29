package org.group51;

import java.awt.*;

/**
 * Creates object to facilitate the draw canvas feature
 */
public class DrawableShape {
    private final Shape shape;
    private final Color color;
    private final Stroke stroke;

    private final boolean fill;

    /**
     * Constructor for the class
     *
     * @param shape
     * @param color
     * @param stroke
     * @param fill
     */
    public DrawableShape(Shape shape, Color color, Stroke stroke, Boolean fill) {
        this.shape = shape;
        this.color = color;
        this.stroke = stroke;
        this.fill = fill;
    }

    /**
     * Facilitates the mouse drawing
     *
     * @param g2d
     */
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(stroke);
        if (!fill) {
            g2d.draw(shape);
        } else {
            g2d.fill(shape);
        }

    }

    /**
     * getter for the shape object
     *
     * @return
     */
    public Shape getShape() {
        return shape;
    }
}
