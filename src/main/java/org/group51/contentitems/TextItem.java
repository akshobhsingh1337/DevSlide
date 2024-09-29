package org.group51.contentitems;

import org.group51.UI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;

/**
 * Represents a text item in a UI presentation, allowing for the display and
 * manipulation of text.
 * This class supports text formatting options such as font size, font type,
 * color, alignment,
 * and styles like bold, italic, and underlined.
 */
public class TextItem implements ContentItem {

    private final transient TextSettings temporarySettings = new TextSettings();
    private final transient ArrayList<JPanel> resizingCorners = new ArrayList<>();
    protected String text;
    protected int x;
    protected int y;
    protected int z;
    protected int fontSize;
    protected String font;
    protected int width;
    protected int height;
    protected Color colour;
    protected int alignment;
    protected boolean isBold;
    protected boolean isUnderlined;
    protected Boolean isItalic;
    private transient JPanel previewPanel = null;
    private transient JLabel previewLabel = null;
    private transient int xPressed;
    private transient int yPressed;

    /**
     * Initializes a new instance of TextItem with default settings.
     */
    public TextItem() {
        text = "";

        x = 100;
        y = 100;

        fontSize = 13;
        width = 100;
        height = 35;
        colour = Color.black;
        font = "Arial";
        alignment = 0;
        isBold = false;
        isUnderlined = false;
        isItalic = false;
    }

    /**
     * Sets up event listeners for the text area, enabling text updates and UI
     * interactions.
     *
     * @param text The JTextArea to which focus listeners will be added.
     */
    public void setUpEventListeners(JTextArea text) {
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (e.getComponent() == text) {
                    setText(text.getText());
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                UI.getInstance().setActiveText(TextItem.this);
            }
        });
    }

    /**
     * Adds mouse listeners to the text area for interaction like dragging and
     * context menu.
     *
     * @param textAreaSurrounding The JPanel surrounding the text area.
     * @param panel               The main panel where the text item is displayed.
     */
    public void addActionListeners(JPanel textAreaSurrounding, JPanel panel) {
        if (!UI.getInstance().isPresentationMode()) {
            textAreaSurrounding.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    Point locationOnScreen = textAreaSurrounding.getLocationOnScreen();
                    xPressed = e.getXOnScreen() - locationOnScreen.x;
                    yPressed = e.getYOnScreen() - locationOnScreen.y;
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

                        popupMenu.show(textAreaSurrounding, e.getX(), e.getY());
                        deleteItem.addActionListener(event -> {
                            // Handle delete action
                            UI.getInstance().getSlide().deleteItem(TextItem.this);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for increasing Z index of text item by 1
                        bringFoward.addActionListener(event -> {
                            int currentIndex = getZIndex();
                            setZIndex(currentIndex + 1);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for decreasing Z index of text item by 1
                        sendBackward.addActionListener(event -> {
                            int currentIndex = getZIndex();
                            setZIndex(currentIndex - 1);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for using the format box
                        formatBox.addActionListener(event -> {
                            formatBox(TextItem.this, panel);
                        });
                    }
                    Border border = BorderFactory.createLineBorder(Color.blue);
                    textAreaSurrounding.setBorder(border);
                    UI.getInstance().setActiveText(TextItem.this);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (previewLabel != null) {
                        // Update the text area's position based on the preview's position
                        x = previewLabel.getX();
                        y = previewLabel.getY();
                        panel.remove(previewLabel);
                        previewLabel = null; // Clear the preview after drop
                        panel.repaint();
                        UI.getInstance().callRefresh();
                    }
                }
            });
            textAreaSurrounding.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (previewLabel == null) {
                        createPreview(textAreaSurrounding, panel); // Create the preview on the first drag event
                    }
                    Point currentScreenLocation = e.getLocationOnScreen();
                    int newX = currentScreenLocation.x - panel.getLocationOnScreen().x - xPressed;
                    int newY = currentScreenLocation.y - panel.getLocationOnScreen().y - yPressed;
                    updatePreviewPosition(newX, newY);
                }
            });
        }
    }

    /**
     * Creates a semi-transparent preview of the text item for visual feedback
     * during dragging or resizing.
     *
     * @param textAreaSurrounding The JPanel surrounding the text area.
     * @param panel               The panel where the preview will be displayed.
     */
    private void createPreview(JPanel textAreaSurrounding, JPanel panel) {
        previewLabel = new JLabel();
        previewLabel.setSize(textAreaSurrounding.getSize());
        previewLabel.setForeground(new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 128)); // Semi-transparent
        previewLabel.setBorder(BorderFactory.createDashedBorder(null));
        previewLabel.setLocation(textAreaSurrounding.getLocation());

        panel.add(previewLabel);
        panel.setComponentZOrder(previewLabel, 0); // Ensure the preview is always on top
        panel.repaint();
    }

    /**
     * Updates the position of the text item preview based on user drag actions.
     *
     * @param newX The new X-coordinate for the preview.
     * @param newY The new Y-coordinate for the preview.
     */
    private void updatePreviewPosition(int newX, int newY) {
        if (previewLabel != null) {
            previewLabel.setLocation(newX, newY);
            previewLabel.repaint();
        }
    }

    /**
     * Displays resizing corners around the text item, enabling the user to adjust
     * its size.
     *
     * @param panel The JPanel where the resizing corners will be added.
     */
    private void showResizingCorners(JPanel panel) {
        clearResizingCorners(panel);
        int cornerSize = 20;

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
     * Updates the dimensions and position of the text item preview rectangle during
     * resizing operations.
     *
     * @param newX   The new X-coordinate relative to the corner's starting
     *               position.
     * @param newY   The new Y-coordinate relative to the corner's starting
     *               position.
     * @param corner The corner being dragged.
     * @param panel  The panel containing the text item.
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
     * text item.
     *
     * @param corner The corner panel used for resizing.
     * @param panel  The main panel containing the text item.
     */
    private void finalizeResize(JPanel corner, JPanel panel) {
        if (previewPanel != null) {
            // Apply the dimensions from the previewPanel to the actual panel
            this.width = previewPanel.getWidth();
            this.height = previewPanel.getHeight();

            // Update the component's position based on the previewPanel's position
            this.x = previewPanel.getX();
            this.y = previewPanel.getY();

            // Remove the preview panel from the parent panel
            panel.remove(previewPanel);
            previewPanel = null;

            panel.repaint();

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
     * Renders the text item within a specified JPanel.
     *
     * @param panel The JPanel on which the text item will be drawn.
     */
    public void paint(JPanel panel) {
        JPanel textAreaSurrounding = new JPanel();
        textAreaSurrounding.setBounds(x, y, width, height);

        textAreaSurrounding.setLayout(new FlowLayout(FlowLayout.LEFT));

        JTextArea textArea = new JTextArea();

        // make the background of the boxes transparent such that we dont end up with
        // weird artifacts around the text
        textArea.setOpaque(false);
        textAreaSurrounding.setOpaque(false);

        textAreaSurrounding.setLayout(new FlowLayout(alignment));
        textArea.setText(text);

        if (!UI.getInstance().isPresentationMode()) {
            Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
            textAreaSurrounding.setBorder(border);
        } else {
            textArea.setEditable(false);
            textArea.setFocusable(false);
            textAreaSurrounding.setFocusable(false);
        }

        setUpEventListeners(textArea);
        textAreaSurrounding.add(textArea);

        textArea.setForeground(colour);
        Font textFont;
        if (isBold && !isItalic)
            textFont = new Font(font, Font.BOLD, fontSize);
        else if (isItalic && !isBold)
            textFont = new Font(font, Font.ITALIC, fontSize);
        else if (isBold && isItalic)
            textFont = new Font(font, Font.ITALIC + Font.BOLD, fontSize);
        else {
            textFont = new Font(font, textArea.getFont().getStyle(), fontSize);
        }
        if (isUnderlined) {
            Map attributes = textFont.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            textFont = textFont.deriveFont(attributes);
        }
        textArea.setFont(textFont);
        addActionListeners(textAreaSurrounding, panel);
        panel.add(textAreaSurrounding);
    }

    /**
     * Applies text settings from temporary settings to the specified TextItem.
     *
     * @param item The TextItem to which the settings should be applied.
     */
    private void applyTextSettings(TextItem item) {
        // Apply the changes from temporary settings to the actual TextItem
        if (temporarySettings.color != null) {
            item.setColour(temporarySettings.color);
        }
        if (temporarySettings.textSize != null) {
            item.setFontSize(temporarySettings.textSize);
        }
        if (temporarySettings.textFont != null) {
            item.setFont(temporarySettings.textFont);
        }
    }

    /**
     * Constructs and displays a format box for editing text item properties.
     *
     * @param item  The TextItem to format.
     * @param panel The panel where the TextItem is displayed.
     */
    private void formatBox(TextItem item, JPanel panel) {
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

        // 'Text Options' button panel with FlowLayout for center alignment
        JPanel textOptionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton textOptionsButton = new JButton("Text Options");
        textOptionsButton.addActionListener(e -> showTextOptions(item));
        textOptionsPanel.add(textOptionsButton);
        contentPanel.add(textOptionsPanel);

        // Create and add 'Apply' button with action listener
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            // Apply text settings and attempt to update item properties
            applyTextSettings(item);
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

                // item.loadImg(); // Reload the image with new dimensions
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
     * Displays a dialog with options for modifying the properties of a text item.
     * Users can change the font color, size, and family of the text.
     *
     * @param item The text item to be modified.
     */
    private void showTextOptions(TextItem item) {
        // Setting all possible values for font sizes and families
        final int[] fontsizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 28, 32, 36, 40, 48, 56, 64, 72};
        final String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        // Create a new dialog to show text options
        JDialog textOptionsDialog = new JDialog();
        textOptionsDialog.setTitle("Text Options");
        textOptionsDialog.setLayout(new BorderLayout(10, 10));
        textOptionsDialog.setSize(350, 200);

        // Set up the panel that contains options
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textOptionsDialog.add(optionsPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4); // Padding
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make the combo boxes fill their cells horizontally
        gbc.weightx = 1;

        // Font Color
        JLabel colorLabel = new JLabel("Font Colour:");
        colorLabel.setFont(new Font("Arial", Font.BOLD, 12)); // Increase label text size
        gbc.gridx = 0; // First column
        gbc.gridy = 0; // First row
        optionsPanel.add(colorLabel, gbc);

        JButton fontColourButton = new JButton();
        fontColourButton.setBackground(item.getColour());
        fontColourButton.setPreferredSize(new Dimension(20, 20)); // Set a size for color button
        fontColourButton.addActionListener(e -> {
            Color initialColor = fontColourButton.getBackground();
            Color newColor = JColorChooser.showDialog(null, "Choose Font Color", initialColor);
            if (newColor != null) {
                fontColourButton.setBackground(newColor);
                temporarySettings.color = newColor; // Store temporarily
            }
        });
        gbc.gridx = 1; // Second column
        gbc.gridwidth = GridBagConstraints.REMAINDER; // End of row
        optionsPanel.add(fontColourButton, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;

        // Font Size
        JLabel sizeLabel = new JLabel("Font Size:");
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 12)); // Increase label text size
        gbc.gridx = 0; // First column
        gbc.gridy = 1; // Second row
        optionsPanel.add(sizeLabel, gbc);

        JComboBox<Integer> fontSizeSelector = new JComboBox<>();
        for (int size : fontsizes) {
            fontSizeSelector.addItem(size);
        }
        fontSizeSelector.setSelectedItem(item.getFontSize());
        fontSizeSelector.addActionListener(e -> {
            Integer newSize = (Integer) fontSizeSelector.getSelectedItem();
            temporarySettings.textSize = newSize; // Store temporarily
        });
        gbc.gridx = 1; // Second column
        gbc.gridwidth = GridBagConstraints.REMAINDER; // End of row
        optionsPanel.add(fontSizeSelector, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;

        // Font Family
        JLabel fontLabel = new JLabel("Font:");
        fontLabel.setFont(new Font("Arial", Font.BOLD, 12)); // Increase label text size
        gbc.gridx = 0; // First column
        gbc.gridy = 2; // Third row
        optionsPanel.add(fontLabel, gbc);

        JComboBox<String> fontSelector = new JComboBox<>(fonts);
        fontSelector.setSelectedItem(item.getFont());
        fontSelector.addActionListener(e -> {
            String newFont = (String) fontSelector.getSelectedItem();
            temporarySettings.textFont = newFont; // Store temporarily
        });
        gbc.gridx = 1; // Second column
        gbc.gridwidth = GridBagConstraints.REMAINDER; // End of row
        optionsPanel.add(fontSelector, gbc);

        textOptionsDialog.setLocationRelativeTo(null);
        textOptionsDialog.setVisible(true);
    }

    /**
     * Returns the color of the text.
     *
     * @return The current text color.
     */
    public Color getColour() {
        return colour;
    }

    /**
     * Sets the color of the text.
     *
     * @param colour The new color to apply to the text.
     */
    public void setColour(Color colour) {
        this.colour = colour;
    }

    /**
     * Toggles the bold style of the text on or off.
     */
    public void toggleBold() {
        this.isBold = !isBold;
    }

    /**
     * Toggles the underlined style of the text on or off.
     */
    public void toggleUnderlined() {
        this.isUnderlined = !isUnderlined;
    }

    /**
     * Toggles the italic style of the text on or off.
     */
    public void toggleItalic() {
        this.isItalic = !isItalic;
    }

    /**
     * Gets the font size of the text.
     *
     * @return The current font size.
     */
    public Integer getFontSize() {
        return fontSize;
    }

    /**
     * Sets the font size of the text.
     *
     * @param fontSize The new font size to apply.
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Gets the font family of the text.
     *
     * @return The current font family.
     */
    public String getFont() {
        return font;
    }

    /**
     * Sets the font family of the text.
     *
     * @param font The new font family to apply.
     */
    public void setFont(String font) {
        this.font = font;
    }

    /**
     * Sets the text content.
     *
     * @param text The new text content.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the alignment of the text within its container.
     *
     * @param alignment The alignment setting (e.g., left, center, right).
     */
    public void setAlignment(int alignment) {
        this.alignment = alignment;
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

    /**
     * Class for holding text settings such as color, size, and font.
     */
    private class TextSettings {
        Color color; // Color of the text
        Integer textSize; // Size of the text font
        String textFont; // Font type of the text
    }

}
