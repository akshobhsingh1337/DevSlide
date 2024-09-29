package org.group51;

/**
 * Imports nessesary dependencies
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Map;

/**
 * Class facilitates the drawing canvas
 */
public class TestDrawing extends JPanel implements ActionListener {

    /**
     * Definition of class attributes
     */
    private final JFrame testFrame = new JFrame("Drawing Canvas");
    private final JPanel mainContainer = new JPanel();
    private final ArrayList<DrawableShape> drawableShapes = new ArrayList<DrawableShape>();//holds shapes that have been drawn and displayed on canvas
    private final ArrayList<DrawableShape> undoDrawableShapes = new ArrayList<DrawableShape>();//holds drawn shapes that are temporarily removed from the canvas
    private final ArrayList<DrawableShape> freeDrawShapes = new ArrayList<DrawableShape>();//holds shapes that are from the pencil or eraser tool that are displayed on the canvas
    private final ArrayList<DrawableShape> undoFreeDrawShapes = new ArrayList<DrawableShape>();//holds the free draw shapes that are temporarily removed from the canvas/* equivalent FXMl var */
    private final JRadioButton strokeRB = new JRadioButton("No Fill");
    private final JRadioButton fillRB = new JRadioButton("Fill");
    private final JColorChooser colorChooser = new JColorChooser(Color.BLUE);
    private final JButton rectButton = new JButton("Rectangle");
    private final JButton lineButton = new JButton("Line");
    private final JButton ovlButton = new JButton("Oval");
    private final JButton pencButton = new JButton("Pencil");
    private final JButton eraserButton = new JButton("Erase");
    private final JSlider sizeSlider = new JSlider(1, 50, 10);
    private final JButton saveCanvas = new JButton("Save Canvas");
    JButton undoShape = new JButton("Undo");
    JButton redoShape = new JButton("Redo");

    /**
     * Overriding of imported methods
     */
    private Graphics2D g2D_mainGraphics = new Graphics2D() {
        @Override
        public void draw(Shape shape) {

        }

        @Override
        public boolean drawImage(Image image, AffineTransform affineTransform, ImageObserver imageObserver) {
            return false;
        }

        @Override
        public void drawImage(BufferedImage bufferedImage, BufferedImageOp bufferedImageOp, int i, int i1) {

        }

        @Override
        public void drawRenderedImage(RenderedImage renderedImage, AffineTransform affineTransform) {

        }

        @Override
        public void drawRenderableImage(RenderableImage renderableImage, AffineTransform affineTransform) {

        }

        @Override
        public void drawString(String s, int i, int i1) {

        }

        @Override
        public void drawString(String s, float v, float v1) {

        }

        @Override
        public void drawString(AttributedCharacterIterator attributedCharacterIterator, int i, int i1) {

        }

        @Override
        public boolean drawImage(Image image, int i, int i1, ImageObserver imageObserver) {
            return false;
        }

        @Override
        public boolean drawImage(Image image, int i, int i1, int i2, int i3, ImageObserver imageObserver) {
            return false;
        }

        @Override
        public boolean drawImage(Image image, int i, int i1, Color color, ImageObserver imageObserver) {
            return false;
        }

        @Override
        public boolean drawImage(Image image, int i, int i1, int i2, int i3, Color color, ImageObserver imageObserver) {
            return false;
        }

        @Override
        public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7,
                                 ImageObserver imageObserver) {
            return false;
        }

        @Override
        public boolean drawImage(Image image, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7,
                                 Color color, ImageObserver imageObserver) {
            return false;
        }

        @Override
        public void dispose() {

        }

        @Override
        public void drawString(AttributedCharacterIterator attributedCharacterIterator, float v, float v1) {

        }

        @Override
        public void drawGlyphVector(GlyphVector glyphVector, float v, float v1) {

        }

        @Override
        public void fill(Shape shape) {

        }

        @Override
        public boolean hit(Rectangle rectangle, Shape shape, boolean b) {
            return false;
        }

        @Override
        public GraphicsConfiguration getDeviceConfiguration() {
            return null;
        }

        @Override
        public void setRenderingHint(RenderingHints.Key key, Object o) {

        }

        @Override
        public Object getRenderingHint(RenderingHints.Key key) {
            return null;
        }

        @Override
        public void addRenderingHints(Map<?, ?> map) {

        }

        @Override
        public RenderingHints getRenderingHints() {
            return null;
        }

        @Override
        public void setRenderingHints(Map<?, ?> map) {

        }

        @Override
        public Graphics create() {
            return null;
        }

        @Override
        public void translate(int i, int i1) {

        }

        @Override
        public Color getColor() {
            return null;
        }

        @Override
        public void setColor(Color color) {

        }

        @Override
        public void setPaintMode() {

        }

        @Override
        public void setXORMode(Color color) {

        }

        @Override
        public Font getFont() {
            return null;
        }

        @Override
        public void setFont(Font font) {

        }

        @Override
        public FontMetrics getFontMetrics(Font font) {
            return null;
        }

        @Override
        public Rectangle getClipBounds() {
            return null;
        }

        @Override
        public void clipRect(int i, int i1, int i2, int i3) {

        }

        @Override
        public void setClip(int i, int i1, int i2, int i3) {

        }

        @Override
        public Shape getClip() {
            return null;
        }

        @Override
        public void setClip(Shape shape) {

        }

        @Override
        public void copyArea(int i, int i1, int i2, int i3, int i4, int i5) {

        }

        @Override
        public void drawLine(int i, int i1, int i2, int i3) {

        }

        @Override
        public void fillRect(int i, int i1, int i2, int i3) {

        }

        @Override
        public void clearRect(int i, int i1, int i2, int i3) {

        }

        @Override
        public void drawRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {

        }

        @Override
        public void fillRoundRect(int i, int i1, int i2, int i3, int i4, int i5) {

        }

        @Override
        public void drawOval(int i, int i1, int i2, int i3) {

        }

        @Override
        public void fillOval(int i, int i1, int i2, int i3) {

        }

        @Override
        public void drawArc(int i, int i1, int i2, int i3, int i4, int i5) {

        }

        @Override
        public void fillArc(int i, int i1, int i2, int i3, int i4, int i5) {

        }

        @Override
        public void drawPolyline(int[] ints, int[] ints1, int i) {

        }

        @Override
        public void drawPolygon(int[] ints, int[] ints1, int i) {

        }

        @Override
        public void fillPolygon(int[] ints, int[] ints1, int i) {

        }

        @Override
        public void translate(double v, double v1) {

        }

        @Override
        public void rotate(double v) {

        }

        @Override
        public void rotate(double v, double v1, double v2) {

        }

        @Override
        public void scale(double v, double v1) {

        }

        @Override
        public void shear(double v, double v1) {

        }

        @Override
        public void transform(AffineTransform affineTransform) {

        }

        @Override
        public AffineTransform getTransform() {
            return null;
        }

        @Override
        public void setTransform(AffineTransform affineTransform) {

        }

        @Override
        public Paint getPaint() {
            return null;
        }

        @Override
        public void setPaint(Paint paint) {

        }

        @Override
        public Composite getComposite() {
            return null;
        }

        @Override
        public void setComposite(Composite composite) {

        }

        @Override
        public Color getBackground() {
            return null;
        }

        @Override
        public void setBackground(Color color) {

        }

        @Override
        public Stroke getStroke() {
            return null;
        }

        @Override
        public void setStroke(Stroke stroke) {

        }

        @Override
        public void clip(Shape shape) {

        }

        @Override
        public FontRenderContext getFontRenderContext() {
            return null;
        }
    };
    private boolean selectLine = false;// for activating the line tool
    private boolean selectOval = false;// for activating the oval tool
    private boolean selectRectangle = false;// for activating the rectangle tool
    private boolean selectEraser = false;// for activating the erasing tool
    private boolean selectPencil = true;
    private int startX, startY, lastX, lastY, oldX, oldY, width, height;
    private boolean dragged = false;// variable checks if user has actively made an effort to drag shape
    private boolean freeDrawUsed = false;

    /**
     * Constructor for the class
     */
    public TestDrawing() {
        super();
        testFrame.setPreferredSize(new Dimension(1000, 900));
        testFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        testFrame.setLocationRelativeTo(null);
        testFrame.setContentPane(mainContainer);

        setOpaque(true);
        setBackground(Color.decode("#FFFFFF"));
        TestDrawing.this.setPreferredSize(new Dimension(700, 700));

        //setting features of slider
        sizeSlider.setPaintTicks(true);
        sizeSlider.setMinorTickSpacing(5);
        sizeSlider.setMajorTickSpacing(10);

        mainContainer.setLayout(new BorderLayout());
        JPanel shapesPanel = new JPanel();
        shapesPanel.setLayout(new FlowLayout());
        shapesPanel.add(rectButton);
        shapesPanel.add(lineButton);
        shapesPanel.add(ovlButton);
        shapesPanel.add(pencButton);
        shapesPanel.add(eraserButton);
        shapesPanel.add(fillRB);
        shapesPanel.add(strokeRB);
        shapesPanel.add(undoShape);
        shapesPanel.add(redoShape);
        shapesPanel.add(saveCanvas);
        shapesPanel.add(colorChooser);
        shapesPanel.add(sizeSlider);

        saveCanvas.addActionListener(this);
        undoShape.addActionListener(this);
        redoShape.addActionListener(this);

        ButtonGroup radioButtons = new ButtonGroup();
        radioButtons.add(fillRB);
        radioButtons.add(strokeRB);
        strokeRB.setSelected(true);//starts shape being drawn not being filled

        mainContainer.add(shapesPanel, BorderLayout.NORTH);//adding panel holding drawing tools
        mainContainer.add(TestDrawing.this, BorderLayout.CENTER);//adding panel wth canvas screen

        MyMouseListener myMouse = new MyMouseListener();
        addMouseListener(myMouse);
        addMouseMotionListener(myMouse);

        assignActionListener();
        testFrame.setVisible(true);
        testFrame.pack();

        testFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to close this Canvas?",
                        "Back to slides?", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                } else {
                    testFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

    }

    /**
     * Allows the user to draw shapes using their mouse
     */
    @Override
    protected void paintComponent(Graphics currentGraphics) {
        super.paintComponent(currentGraphics);
        // downcasting
        g2D_mainGraphics = (Graphics2D) currentGraphics;

        if (dragged) {
            if (selectRectangle) {
                drawRectAnimation();
            }
            if (selectOval) {
                drawOvalAnimation();
            }
            if (selectLine) {
                drawLineAnimation();
            }
            if (selectPencil) {
                pencilTool();
            }
            if (selectEraser) {
                eraser();
            }
        }

        for (DrawableShape ds : drawableShapes) {
            if (ds == null) {
                continue;
            } else {
                ds.draw(g2D_mainGraphics);
            }

        }
        for (DrawableShape ds : freeDrawShapes) {
            if (ds == null) {
                continue;
            } else {
                ds.draw(g2D_mainGraphics);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == saveCanvas) {
            BufferedImage img = new BufferedImage(TestDrawing.this.getWidth(), TestDrawing.this.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            TestDrawing.this.printAll(g2d);
            g2d.dispose();

            Utils.saveImageAndAddToSlide(img);
        }

        if (e.getSource() == undoShape) {
            try {

                DrawableShape ds = drawableShapes.get(drawableShapes.size() - 1);

                if (ds == null) {//the free draw tool was last used
                    drawableShapes.remove(ds);//remove null
                    undoDrawableShapes.add(ds);//adding null as identifier

                    DrawableShape fs = freeDrawShapes.get(freeDrawShapes.size() - 1);//getting the null item
                    if (fs == null) {
                        undoFreeDrawShapes.add(fs);//adding null
                        freeDrawShapes.remove((freeDrawShapes.size() - 1));//removing null
                    }

                    DrawableShape line = freeDrawShapes.get(freeDrawShapes.size() - 1);
                    while (line != null && (!freeDrawShapes.isEmpty())) {
                        undoFreeDrawShapes.add(line);
                        freeDrawShapes.remove(line);
                        repaint();
                        line = freeDrawShapes.get(freeDrawShapes.size() - 1);
                    }
                    repaint();
                } else {
                    drawableShapes.remove(ds);
                    undoDrawableShapes.add(ds);
                    repaint();
                }
                repaint();

            } catch (Exception e1) {

            }

        }

        if (e.getSource() == redoShape) {
            try {

                DrawableShape ds = undoDrawableShapes.get(undoDrawableShapes.size() - 1);

                if (ds == null) {//the free draw tool was last used
                    undoDrawableShapes.remove(ds);//remove null
                    drawableShapes.add(ds);//adding null as indicator for free draw

                    //getting item at end of array list
                    DrawableShape ufs = undoFreeDrawShapes.get(undoFreeDrawShapes.size() - 1);

                    while (ufs != null) {
                        undoFreeDrawShapes.remove(ufs);
                        freeDrawShapes.add(ufs);
                        repaint();
                        ufs = undoFreeDrawShapes.get(undoFreeDrawShapes.size() - 1);

                    }
                    if (ufs == null) {
                        undoFreeDrawShapes.remove(undoFreeDrawShapes.size() - 1);
                        freeDrawShapes.add(ufs);
                        repaint();
                    }

                    repaint();
                } else {
                    drawableShapes.add(ds);
                    undoDrawableShapes.remove(ds);
                    repaint();
                }
                repaint();

            } catch (Exception e2) {
            }
        }
    }

    public void assignActionListener() {
        rectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRectangleAsCurrentShape();
            }

        });

        ovlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setOvalAsCurrentShape();
            }

        });

        lineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLineAsCurrentShape();
            }

        });

        pencButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPencilAsCurrentTool();
            }

        });

        eraserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEraserAsCurrentTool();
            }

        });

    }

    /*Draw methods*/

    /**
     * draw oval shape
     */
    private void drawOval() {

        // get width and height of shape
        width = lastX - startX;
        height = lastY - startY;
        g2D_mainGraphics.setStroke(new BasicStroke(sizeSlider.getValue()));// width of line taken from the slider

        // user selects if shape is filled or not using radio button
        if (fillRB.isSelected()) {
            g2D_mainGraphics.setColor(colorChooser.getColor());
            g2D_mainGraphics.fillOval(startX, startY, width, height);// output filled oval
            drawableShapes.add(new DrawableShape(new Ellipse2D.Double(startX, startY, width, height),
                    colorChooser.getColor(), new BasicStroke(sizeSlider.getValue()), true));

        } else {
            g2D_mainGraphics.setColor(colorChooser.getColor());
            g2D_mainGraphics.drawOval(startX, startY, width, height);// output outline stroke of oval
            drawableShapes.add(new DrawableShape(new Ellipse2D.Double(startX, startY, width, height),
                    colorChooser.getColor(), new BasicStroke(sizeSlider.getValue()), false));
        }
        repaint();
    }

    /**
     * draw rectangle
     */
    private void drawRect() {

        // get width and height of shape
        width = lastX - startX;
        height = lastY - startY;
        g2D_mainGraphics.setStroke(new BasicStroke(sizeSlider.getValue()));// width of line taken from the slider

        //user selects if shape is filled or not using radio button
        if (fillRB.isSelected()) {
            drawableShapes.add(new DrawableShape(new Rectangle2D.Double(startX, startY, width, height),
                    colorChooser.getColor(), new BasicStroke(sizeSlider.getValue()), true));
        } else {
            drawableShapes.add(new DrawableShape(new Rectangle2D.Double(startX, startY, width, height),
                    colorChooser.getColor(), new BasicStroke(sizeSlider.getValue()), false));
        }

        repaint();
    }

    /**
     * draw line stroke
     */
    private void drawLine() {
        g2D_mainGraphics.setStroke(new BasicStroke(sizeSlider.getValue()));// width of line taken from the slider
        g2D_mainGraphics.setColor(colorChooser.getColor());
        g2D_mainGraphics.drawLine(startX, startY, lastX, lastY);// output outline stroke of shape
        drawableShapes.add(new DrawableShape(new Line2D.Double(startX, startY, lastX, lastY),
                colorChooser.getColor(), new BasicStroke(sizeSlider.getValue()), false));
        repaint();
    }

    /**
     * enable free drawing/pencil tool
     */
    private void pencilTool() {
        g2D_mainGraphics.setStroke(new BasicStroke(sizeSlider.getValue()));
        g2D_mainGraphics.setColor(colorChooser.getColor());

        //adding the shapes to a specifically dedicated free draw array list - which uses null as a separator
        freeDrawShapes.add(new DrawableShape(new Line2D.Double(oldX, oldY, lastX, lastY),
                colorChooser.getColor(), new BasicStroke(sizeSlider.getValue()), false));

        repaint();
        oldX = lastX;
        oldY = lastY;
    }

    /**
     * enable user to erase drawing
     */
    private void eraser() {//// CAN COMBINE WITH FREEDRAWING() method-> just change colour
        Color c = Color.decode("#FFFFFF");
        g2D_mainGraphics.setStroke(new BasicStroke(sizeSlider.getValue()));
        g2D_mainGraphics.setColor(c);

        //adding the shapes to a specifically dedicated free draw array list - which uses null as a separator
        freeDrawShapes.add(new DrawableShape(new Line2D.Double(oldX, oldY, lastX, lastY),
                c, new BasicStroke(sizeSlider.getValue()), false));

        repaint();
        oldX = lastX;
        oldY = lastY;
    }

    /**
     * drawing effect for oval
     */
    private void drawOvalAnimation() {
        width = lastX - startX;
        height = lastY - startY;
        g2D_mainGraphics.setStroke(new BasicStroke(sizeSlider.getValue()));

        //user selects if shape is filled or not using radio button
        if (fillRB.isSelected()) {
            //fillRect: displaying the background of the JPanel as white since the drawing animation can clear the JPanel colour
            g2D_mainGraphics.setColor(Color.WHITE);
            g2D_mainGraphics.fillRect(0, 0, testFrame.getWidth(), testFrame.getHeight());

            g2D_mainGraphics.setColor(colorChooser.getColor());
            g2D_mainGraphics.fillOval(startX, startY, width, height);//output filled oval
        } else {
            g2D_mainGraphics.setColor(Color.WHITE);
            g2D_mainGraphics.fillRect(0, 0, testFrame.getWidth(), testFrame.getHeight());

            g2D_mainGraphics.setColor(colorChooser.getColor());
            g2D_mainGraphics.drawOval(startX, startY, width, height);// output outline stroke of oval
        }
        repaint();
    }

    /* Drawing effect/animation methods */

    /**
     * drawing effect of rectangle
     */
    private void drawRectAnimation() {
        // get width and height of shape
        width = lastX - startX;
        height = lastY - startY;
        g2D_mainGraphics.setStroke(new BasicStroke(sizeSlider.getValue()));

        //user selects if shape is filled or not using radio button
        if (fillRB.isSelected()) {
            //fillRect: displaying the background of the JPanel as white since the drawing animation can clear the JPanel colour
            g2D_mainGraphics.setColor(Color.WHITE);
            g2D_mainGraphics.fillRect(0, 0, testFrame.getWidth(), testFrame.getHeight());

            g2D_mainGraphics.setColor(colorChooser.getColor());
            g2D_mainGraphics.fillRect(startX, startY, width, height);//output filled rect
        } else {
            g2D_mainGraphics.setColor(Color.WHITE);
            g2D_mainGraphics.fillRect(0, 0, testFrame.getWidth(), testFrame.getHeight());

            g2D_mainGraphics.setColor(colorChooser.getColor());
            g2D_mainGraphics.drawRect(startX, startY, width, height);//output outline stroke of rect
        }
        repaint();
    }

    /**
     * Drawing effect for line
     */
    private void drawLineAnimation() {
        g2D_mainGraphics.setStroke(new BasicStroke(sizeSlider.getValue()));
        g2D_mainGraphics.setColor(colorChooser.getColor());

        g2D_mainGraphics.setColor(Color.WHITE);
        g2D_mainGraphics.fillRect(0, 0, testFrame.getWidth(), testFrame.getHeight());
        g2D_mainGraphics.drawLine(startX, startY, lastX, lastY);//output outline line stroke
        repaint();
    }

    /*Buttons - setting which ones are selected by default*/

    /**
     * setting oval as the currently selected button
     */
    private void setOvalAsCurrentShape() {
        selectOval = true;
        selectRectangle = false;
        selectLine = false;
        selectPencil = false;
        selectEraser = false;
    }

    /**
     * setting rectangle as the currently selected button
     */
    private void setRectangleAsCurrentShape() {
        selectOval = false;
        selectRectangle = true;
        selectLine = false;
        selectPencil = false;
        selectEraser = false;
    }

    /**
     * setting line as the currently selected button
     */
    private void setLineAsCurrentShape() {
        selectOval = false;
        selectRectangle = false;
        selectLine = true;
        selectPencil = false;
        selectEraser = false;
    }

    /**
     * setting pencil as the currently selected button
     */
    private void setPencilAsCurrentTool() {
        selectOval = false;
        selectRectangle = false;
        selectLine = false;
        selectPencil = true;
        selectEraser = false;
    }

    /**
     * setting eraser as the currently selected button
     */
    private void setEraserAsCurrentTool() {
        selectOval = false;
        selectRectangle = false;
        selectLine = false;
        selectPencil = false;
        selectEraser = true;
    }

    public ArrayList getDrawableshapes() {
        return drawableShapes;
    }

    /**
     * class for the mouse events that happen on the drawing canvas
     */
    private class MyMouseListener extends MouseAdapter {
        /**
         * Retrieves position of wher mouse is clicked on the canvas
         *
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            oldX = startX;
            oldY = startY;
        }

        /**
         * Retrieves position of where the mouse id dragged on the canvas
         *
         * @param e
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();

            dragged = true;
            // repaint();

            /* outputting selected shape/stroke */
            if (selectRectangle) {
                drawRectAnimation();
                freeDrawUsed = false;
            }
            if (selectOval) {
                drawOvalAnimation();
                freeDrawUsed = false;
            }
            if (selectLine) {
                drawLineAnimation();
                freeDrawUsed = false;
            }
            if (selectPencil) {
                pencilTool();
                freeDrawUsed = true;
            }
            if (selectEraser) {
                eraser();
                freeDrawUsed = true;
            }
        }

        /**
         * completes the drawing of the shape selected
         *
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragged) {
                if (freeDrawUsed) {
                    drawableShapes.add(null);// indicator freedrawshape is used
                    freeDrawShapes.add(null);// separator
                }
                if (selectRectangle) {
                    drawRect();
                }
                if (selectOval) {
                    drawOval();
                }
                if (selectLine) {
                    drawLine();
                }
            }
            dragged = false;

        }
    }

}
