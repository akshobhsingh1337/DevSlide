package org.group51.contentitems;

import org.group51.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Represents a video item within a UI presentation, allowing for the display
 * and interaction with a video thumbnail.
 * This class supports functionalities such as loading a video thumbnail,
 * setting the video path, resizing,
 * and moving the video item within a UI panel.
 */
public class VideoItem implements ContentItem {
    private final transient ArrayList<JPanel> resizingCorners = new ArrayList<>();
    private transient Optional<Image> loadedImage;
    private String path;
    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private transient JPanel previewPanel = null;
    private transient JLabel previewLabel = null;
    private transient int xPressed;
    private transient int yPressed;

    /**
     * Initializes a new instance of VideoItem with default settings and dimensions.
     */
    public VideoItem() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        loadedImage = Optional.empty();
        path = "";

        // Set up the initial position somewhere in the middle of the screen
        x = (int) (screenSize.width * 0.3);
        y = (int) (screenSize.height * 0.3);
        width = 100;
        height = 100;
    }

    /**
     * Applies semi-transparency to a given image, typically used for creating a
     * semi-transparent thumbnail
     * of the video.
     *
     * @param img   The image to be made semi-transparent.
     * @param alpha The degree of transparency to apply, ranging from 0.0 to 1.0.
     * @return A new {@link Image} instance with the applied transparency.
     */
    public static Image makeImageTransparent(Image img, float alpha) {
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }

    /**
     * Sets the file path for the video.
     *
     * @param path The filesystem path to the video file.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Loads the video thumbnail image into memory, scaling it to fit the specified
     * width and height.
     */
    private void loadImg() {
        if (path.isEmpty()) {
            throw new RuntimeException("ERROR: path for the video has not been set");
        }
        try {
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream("videoIcon.png");
            ImageIcon icon = new ImageIcon(ImageIO.read(imageStream));
            // Scale the image
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
            loadedImage = Optional.of(scaledImage);
            // Scale the image
        } catch (IOException e) {
            loadedImage = Optional.empty();
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates and returns a JLabel containing the scaled video thumbnail image.
     *
     * @return A {@link JLabel} displaying the video thumbnail.
     */
    private JLabel createImageLabel() {
        if (loadedImage.isEmpty()) {
            loadImg();
        }

        // Create a JLabel with the scaled image and set its bounds
        if (loadedImage.isPresent()) {
            JLabel imageLabel = new JLabel(new ImageIcon(loadedImage.get()));
            imageLabel.setBounds(x, y, width, height);
            return imageLabel;
        }
        throw new RuntimeException("No image available. Check if the video path is correct.");
    }

    /**
     * Sets up mouse event listeners for the video item, enabling functionality such
     * as resizing, moving,
     * and context menu actions.
     *
     * @param imageLabel The JLabel to which mouse event listeners will be added.
     * @param panel      The JPanel within which the video item is displayed.
     */
    private void setUpImageEventListeners(JLabel imageLabel, JPanel panel) {
        if (!UI.getInstance().isPresentationMode()) {
            imageLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    xPressed = e.getXOnScreen() - imageLabel.getLocationOnScreen().x;
                    yPressed = e.getYOnScreen() - imageLabel.getLocationOnScreen().y;
                    if (previewLabel != null) {
                        panel.remove(previewLabel);
                        previewLabel = null;
                        panel.repaint();
                    }
                    showResizingCorners(panel);
                    UI.getInstance().getFrame().enableConvertToUMLButton(false);
                    UI.getInstance().getFrame().enableImportFileButton(false);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem deleteItem = new JMenuItem("Delete");
                        JMenuItem bringFoward = new JMenuItem("Bring Forward");
                        JMenuItem sendBackward = new JMenuItem("Send Backward");
                        JMenuItem formatBox = new JMenuItem("Format Item");

                        popupMenu.add(deleteItem);
                        popupMenu.add(bringFoward);
                        popupMenu.add(sendBackward);
                        popupMenu.add(formatBox);

                        popupMenu.show(imageLabel, e.getX(), e.getY());
                        deleteItem.addActionListener(event -> {
                            // Handle delete action
                            UI.getInstance().getSlide().deleteItem(VideoItem.this);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for increasing Z index of Video item by 1
                        bringFoward.addActionListener(event -> {
                            int currentIndex = getZIndex();
                            setZIndex(currentIndex + 1);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for decreasing Z index of Video item by 1
                        sendBackward.addActionListener(event -> {
                            int currentIndex = getZIndex();
                            setZIndex(currentIndex - 1);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for using the format box
                        formatBox.addActionListener(event -> {
                            formatBox(VideoItem.this, panel);
                        });
                    } else if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
                        try {
                            Desktop.getDesktop().browse(new URI(path));
                            System.out.println(path);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    int xReleased = e.getX();
                    int yReleased = e.getY();
                    int movedX = xReleased - xPressed;
                    int movedY = yReleased - yPressed;
                    if (movedY != 0 || movedX != 0) {
                        x = x + movedX;
                        y = y + movedY;
                        UI.getInstance().callRefresh();
                    }
                }
            });
            imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (previewLabel == null) {
                        createPreview(imageLabel, panel); // Create the preview
                    }
                    int deltaX = e.getX() - xPressed;
                    int deltaY = e.getY() - yPressed;
                    updatePreviewPosition(deltaX, deltaY);
                }
            });
        }
    }

    /**
     * Creates a semi-transparent preview of the video item that is displayed during
     * resizing or moving operations.
     *
     * @param imageLabel The JLabel of the video item from which the preview is
     *                   created.
     * @param panel      The panel where the preview will be displayed.
     */
    private void createPreview(JLabel imageLabel, JPanel panel) {
        // Clone the original image label and make it semi-transparent
        ImageIcon icon = (ImageIcon) imageLabel.getIcon();
        Image image = icon.getImage();
        Image newimg = image.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(),
                java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(newimg);

        previewLabel = new JLabel(icon);
        previewLabel.setBounds(imageLabel.getBounds());
        previewLabel.setOpaque(false);
        previewLabel.setBorder(BorderFactory.createDashedBorder(null));
        previewLabel.setCursor(new Cursor(Cursor.MOVE_CURSOR));

        // Make the preview semi-transparent
        previewLabel.setIcon(new ImageIcon(makeImageTransparent(icon.getImage(), 0.5f)));

        // Add the preview label to the panel for visualization
        panel.add(previewLabel);
        panel.setComponentZOrder(previewLabel, 0); // Ensure the preview is always on top
        panel.repaint();
    }

    /**
     * Updates the position of the video item preview based on user drag actions.
     *
     * @param deltaX The change in X-coordinate.
     * @param deltaY The change in Y-coordinate.
     */
    private void updatePreviewPosition(int deltaX, int deltaY) {
        // Update the position of the preview label based on drag
        int newX = x + deltaX;
        int newY = y + deltaY;
        previewLabel.setLocation(newX, newY);
    }

    /**
     * Displays resizing corners around the video item, enabling the user to adjust
     * its size.
     *
     * @param panel The JPanel where the resizing corners will be added.
     */
    private void showResizingCorners(JPanel panel) {
        clearResizingCorners(panel);
        int cornerSize = 20;

        // Corner positions
        int[][] cornerPositions = {
                {x - cornerSize / 2, y - cornerSize / 2},
                {x + width - cornerSize / 2, y - cornerSize / 2},
                {x + width - cornerSize / 2, y + height - cornerSize / 2},
                {x - cornerSize / 2, y + height - cornerSize / 2}
        };

        for (int[] pos : cornerPositions) {
            JPanel corner = new JPanel();
            corner.setBounds(pos[0], pos[1], cornerSize, cornerSize);
            corner.setBackground(Color.WHITE);
            Border border = BorderFactory.createLineBorder(Color.BLACK);
            corner.setBorder(border);
            panel.add(corner);
            resizingCorners.add(corner);

            // Add mouse listener for resizing
            corner.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int newX = e.getXOnScreen() - corner.getLocationOnScreen().x;
                    int newY = e.getYOnScreen() - corner.getLocationOnScreen().y;
                    updatePreviewRectangle(newX, newY, corner, panel);
                }
            });

            corner.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    finalizeResize(corner, panel);
                }
            });
        }
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Updates the dimensions and position of the video item preview rectangle
     * during resizing operations.
     *
     * @param newX   The new X-coordinate relative to the corner's starting
     *               position.
     * @param newY   The new Y-coordinate relative to the corner's starting
     *               position.
     * @param corner The corner being dragged.
     * @param panel  The panel containing the video item.
     */
    private void updatePreviewRectangle(int newX, int newY, JPanel corner, JPanel panel) {
        if (previewPanel == null) {
            previewPanel = new JPanel();
            previewPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent background
            // previewPanel.setBorder(BorderFactory.createDashedBorder(Color.BLACK)); //
            // Dashed border
            previewPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            panel.add(previewPanel);
        } else {
            // Remove and re-add the previewPanel to ensure it's on top of the image
            panel.remove(previewPanel);
        }
        panel.add(previewPanel);
        panel.setComponentZOrder(previewPanel, 0); // Bring to front

        // Calculate the changes based on the corner being dragged
        Rectangle originalBounds = new Rectangle(x, y, width, height);
        Rectangle newBounds = new Rectangle(originalBounds);

        // Identify the corner being dragged
        Point cornerPoint = corner.getLocation();
        if (cornerPoint.equals(new Point(x - 10, y - 10))) {
            // Top-left corner
            newBounds.x += newX;
            newBounds.y += newY;
            newBounds.width -= newX;
            newBounds.height -= newY;
        } else if (cornerPoint.equals(new Point(x + width - 10, y - 10))) {
            // Top-right corner
            newBounds.width += newX;
            newBounds.y += newY;
            newBounds.height -= newY;
        } else if (cornerPoint.equals(new Point(x - 10, y + height - 10))) {
            // Bottom-left corner
            newBounds.x += newX;
            newBounds.width -= newX;
            newBounds.height += newY;
        } else if (cornerPoint.equals(new Point(x + width - 10, y + height - 10))) {
            // Bottom-right corner
            newBounds.width += newX;
            newBounds.height += newY;
        }

        // Ensure the new width and height are not less than a minimum size
        newBounds.width = Math.max(newBounds.width, 1);
        newBounds.height = Math.max(newBounds.height, 1);

        // Apply the new bounds to the previewPanel
        previewPanel.setBounds(newBounds.x, newBounds.y, newBounds.width, newBounds.height);

        panel.repaint();
    }

    /**
     * Finalizes the resize operation, applying the preview dimensions to the actual
     * video item.
     *
     * @param corner The corner panel used for resizing.
     * @param panel  The main panel containing the video item.
     */
    private void finalizeResize(JPanel corner, JPanel panel) {
        if (previewPanel != null) {
            // Apply the dimensions from the previewPanel to the actual image
            this.width = previewPanel.getWidth();
            this.height = previewPanel.getHeight();

            // Update the component's position based on the previewPanel's position
            this.x = previewPanel.getX();
            this.y = previewPanel.getY();

            // Remove the preview panel from the parent panel
            panel.remove(previewPanel);
            previewPanel = null;

            panel.repaint();

            // Update the image
            loadImg();

            // Refresh the UI
            UI.getInstance().callRefresh();
        }
    }

    /**
     * Clears the resizing corners from the display panel.
     *
     * @param panel The panel from which the resizing corners will be removed.
     */
    private void clearResizingCorners(JPanel panel) {
        for (JPanel corner : resizingCorners) {
            panel.remove(corner);
        }
        resizingCorners.clear();
    }

    /**
     * Renders the video item within a specified JPanel.
     *
     * @param panel The JPanel on which the video item will be drawn.
     */
    @Override
    public void paint(JPanel panel) {
        try {
            JLabel imageLabel = createImageLabel();
            setUpImageEventListeners(imageLabel, panel);
            panel.add(imageLabel);
        } catch (RuntimeException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Displays a format box for editing properties of a VideoItem.
     * Allows changing position, size, and z-index of the item.
     *
     * @param item  The VideoItem to be edited.
     * @param panel The JPanel on which the VideoItem is displayed.
     */
    private void formatBox(VideoItem item, JPanel panel) {
        // Create and set up the format box frame
        JFrame formatBox = new JFrame("Format");
        formatBox.setLayout(new BorderLayout(10, 10)); // Set layout with gaps

        // Create a content panel with grid layout and padding
        JPanel contentPanel = new JPanel(new GridLayout(6, 2, 10, 10)); // 6 rows, 2 columns, with gaps
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        formatBox.add(contentPanel, BorderLayout.CENTER);

        // Get the maximum X and Y values from the panel's dimensions
        int maxX = panel.getWidth();
        int maxY = panel.getHeight();

        // Create and add components for X index
        JLabel xLabel = new JLabel("X index (0 - " + maxX + "):");
        JTextField xField = new JTextField(String.valueOf(item.x));
        contentPanel.add(xLabel);
        contentPanel.add(xField);

        // Create and add components for Y index
        JLabel yLabel = new JLabel("Y index (0 - " + maxY + "):");
        JTextField yField = new JTextField(String.valueOf(item.y));
        contentPanel.add(yLabel);
        contentPanel.add(yField);

        // Add label and field for Z index
        contentPanel.add(new JLabel("Z Index:"));
        JTextField zField = new JTextField(String.valueOf(getZIndex()));
        contentPanel.add(zField);

        // Add label and field for item width
        contentPanel.add(new JLabel("Width:"));
        JTextField widthField = new JTextField(String.valueOf(item.width));
        contentPanel.add(widthField);

        // Add label and field for item height
        contentPanel.add(new JLabel("Height:"));
        JTextField heightField = new JTextField(String.valueOf(item.height));
        contentPanel.add(heightField);

        // Create and add 'Apply' button with action listener
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            try {
                // Get all the values from fields
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                int z = Integer.parseInt(zField.getText());
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());

                // Validate the inputs
                if (width < 0 || height < 0) {
                    throw new IllegalArgumentException("Width and height must be non-negative.");
                }
                if (x < 0 || x > maxX) {
                    throw new IllegalArgumentException("X must be between 0 and " + maxX + ".");
                }
                if (y < 0 || y > maxY) {
                    throw new IllegalArgumentException("Y must be between 0 and " + maxY + ".");
                }
                if (z < 0) {
                    throw new IllegalArgumentException("Z-index must be non-negative.");
                }

                // Apply changes if inputs are valid
                item.setX(x);
                item.setY(y);
                item.setZIndex(z);
                item.setWidth(width);
                item.setHeight(height);

                item.loadImg(); // Reload the image with new dimensions
                panel.repaint();
                panel.revalidate();
                UI.getInstance().callRefresh();

                formatBox.dispose();

                // Error cases
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(formatBox, "Please enter valid numbers.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(formatBox, ex.getMessage());
            }
        });

        buttonPanel.add(applyButton);
        formatBox.add(buttonPanel, BorderLayout.SOUTH); // Position button at bottom

        formatBox.pack(); // Adjusts the frame size to fit its contents
        formatBox.setLocationRelativeTo(null); // Center the formatBox on the screen
        formatBox.setVisible(true); // Make the format box visible
    }

    /**
     * Returns the z-index of this item.
     *
     * @return The z-index value.
     */
    @Override
    public int getZIndex() {
        return z;
    }

    /**
     * Sets the z-index of this item.
     *
     * @param z The new z-index value to be set.
     */
    @Override
    public void setZIndex(int z) {
        this.z = z;
    }

    /**
     * Sets the x-coordinate of this item.
     *
     * @param x The new x-coordinate value to be set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of this item.
     *
     * @param y The new y-coordinate value to be set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the width of this item.
     *
     * @param width The new width value to be set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the height of this item.
     *
     * @param height The new height value to be set.
     */
    public void setHeight(int height) {
        this.height = height;
    }
}