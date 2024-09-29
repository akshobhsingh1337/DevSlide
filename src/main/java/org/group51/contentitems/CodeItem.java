package org.group51.contentitems;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.core.DiagramDescription;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.group51.FileManager;
import org.group51.PresentationFilePath;
import org.group51.SettingsDTO;
import org.group51.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a code item that allows displaying and editing code with
 * syntax highlighting and theme support.
 * It supports resizing, moving, and various interactions through a GUI.
 */
public class CodeItem implements ContentItem {
    private final String font;
    private final transient ArrayList<JPanel> resizingCorners = new ArrayList<>();
    private int fontsize;
    private String code;
    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private String syntax;
    private String theme;
    private transient JPanel previewPanel = null;
    private transient JLabel previewLabel = null;
    private transient int xPressed;
    private transient int yPressed;

    /**
     * Constructs a new CodeItem with default settings. It initializes the code
     * item's position,
     * size, font, syntax, and theme based on the screen size and application
     * settings.
     */
    public CodeItem() {
        code = "";
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        x = (int) (screenSize.width * 0.3);
        y = (int) (screenSize.height * 0.3);
        fontsize = 13;
        width = 300;
        height = 500;
        font = "Arial";
        SettingsDTO settings = UI.getSettings();
        syntax = settings.defaultCodeLanguage();
        theme = settings.defaultCodeBoxTheme();
        fontsize = 18;
    }

    /**
     * Adds mouse listeners to the surrounding panel of the text area for handling
     * actions like moving,
     * showing a context menu, and resizing.
     *
     * @param TextAreaSurrounding The JPanel surrounding the text area.
     * @param panel               The main panel where the code item is displayed.
     */
    public void AddActionListeners(JPanel TextAreaSurrounding, JPanel panel) {
        if (!UI.getInstance().isPresentationMode()) {
            TextAreaSurrounding.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    xPressed = e.getXOnScreen() - TextAreaSurrounding.getLocationOnScreen().x;
                    yPressed = e.getYOnScreen() - TextAreaSurrounding.getLocationOnScreen().y;
                    if (previewLabel != null) {
                        panel.remove(previewLabel);
                        previewLabel = null;
                        panel.repaint();
                    }
                    showResizingCorners(panel);
                    UI.getInstance().setSelectedCodeItem(CodeItem.this);
                    UI.getInstance().getFrame().enableConvertToUMLButton(true);
                    UI.getInstance().getFrame().enableImportFileButton(true);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {

                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem deleteItem = new JMenuItem("Delete");
                        JMenuItem bringFoward = new JMenuItem("Bring Forward");
                        JMenuItem sendBackward = new JMenuItem("Send Backward");
                        JMenuItem formatBox = new JMenuItem("Format Item");
                        JMenuItem convertToUMLItem = new JMenuItem("Convert to UML Diagram");
                        JMenuItem importFromFileItem = new JMenuItem("Import From File");

                        popupMenu.add(deleteItem);
                        popupMenu.add(bringFoward);
                        popupMenu.add(sendBackward);
                        popupMenu.add(convertToUMLItem);
                        popupMenu.add(formatBox);
                        popupMenu.add(importFromFileItem);

                        popupMenu.show(TextAreaSurrounding, e.getX(), e.getY());
                        deleteItem.addActionListener(event -> {
                            // Handle delete action
                            UI.getInstance().getSlide().deleteItem(CodeItem.this);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for increasing Z index of Code item by 1
                        bringFoward.addActionListener(event -> {
                            int currentIndex = getZIndex();
                            setZIndex(currentIndex + 1);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for decreasing Z index of Code item by 1
                        sendBackward.addActionListener(event -> {
                            int currentIndex = getZIndex();
                            setZIndex(currentIndex - 1);
                            UI.getInstance().callRefresh();
                        });
                        // Action listener for converting code to UML diagram
                        convertToUMLItem.addActionListener(event -> {
                            convertPlantUMLToImage(CodeItem.this.code);
                        });
                        importFromFileItem.addActionListener(e1 -> importCodeFromFile(CodeItem.this));
                        // Action listener for using the format box
                        formatBox.addActionListener(event -> {
                            formatBox(CodeItem.this, panel);
                        });
                    }
                    Border border = BorderFactory.createLineBorder(Color.blue);
                    TextAreaSurrounding.setBorder(border);
                    // UI.getInstance().SetActiveText(TextItem.this);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (previewLabel != null) {
                        // Update the code box's position based on the preview's position
                        x = previewLabel.getX();
                        y = previewLabel.getY();
                        panel.remove(previewLabel);
                        previewLabel = null; // Clear the preview after drop
                        panel.repaint();
                        UI.getInstance().callRefresh();
                    }
                }
            });
            TextAreaSurrounding.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (previewLabel == null) {
                        createPreview(TextAreaSurrounding, panel);
                    }
                    // The following line gets the mouse's location on the screen
                    Point location = e.getLocationOnScreen();
                    // Calculate the new position for the preview relative to the panel
                    int newX = location.x - panel.getLocationOnScreen().x - xPressed;
                    int newY = location.y - panel.getLocationOnScreen().y - yPressed;
                    updatePreviewPosition(newX, newY);
                }
            });

        }
    }

    /**
     * Creates a semi-transparent preview label for the code item being manipulated.
     * This preview appears
     * during the drag operation to show where the code item will be placed if
     * dropped.
     *
     * @param TextAreaSurrounding The JPanel that surrounds the text area, used to
     *                            determine the initial size and location of the
     *                            preview.
     * @param panel               The main panel where the code item is displayed,
     *                            and where the preview label will be added.
     */
    private void createPreview(JPanel TextAreaSurrounding, JPanel panel) {
        previewLabel = new JLabel();
        previewLabel.setSize(TextAreaSurrounding.getSize());
        previewLabel.setFont(new Font(font, Font.PLAIN, fontsize));
        previewLabel.setForeground(new Color(0, 0, 0, 128)); // Semi-transparent
        previewLabel.setBorder(BorderFactory.createDashedBorder(null));
        previewLabel.setLocation(TextAreaSurrounding.getLocation());

        panel.add(previewLabel);
        panel.setComponentZOrder(previewLabel, 0);
        panel.repaint();
    }

    /**
     * Updates the position of the preview label to reflect the new location as it
     * is being dragged.
     *
     * @param newX The new X-coordinate of the preview label within the main panel.
     * @param newY The new Y-coordinate of the preview label within the main panel.
     */
    private void updatePreviewPosition(int newX, int newY) {
        if (previewLabel != null) {
            previewLabel.setLocation(newX, newY);
            previewLabel.repaint();
        }
    }

    /**
     * Displays resizing corners around the code item, allowing the user to resize
     * it by dragging these corners.
     *
     * @param panel The main panel where the code item is displayed, and where the
     *              resizing corners will be added.
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
     * Updates the dimensions of the preview rectangle based on the user's drag
     * action on one of the resizing corners.
     * This method calculates the new size and position of the preview rectangle
     * during the resize operation.
     *
     * @param newX   The new X-coordinate relative to the corner's starting
     *               position.
     * @param newY   The new Y-coordinate relative to the corner's starting
     *               position.
     * @param corner The JPanel representing the corner being dragged.
     * @param panel  The main panel where the preview rectangle is displayed.
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
     * Finalizes the resizing operation by applying the dimensions from the preview
     * rectangle to the actual code item.
     * It updates the code item's width, height, and position based on the preview
     * rectangle's dimensions and location.
     *
     * @param corner The JPanel representing the corner that was used for resizing.
     * @param panel  The main panel where the code item and preview rectangle are
     *               displayed.
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
     * Clears the resizing corners from the main panel.
     *
     * @param panel The main panel from which the resizing corners will be removed.
     */
    private void clearResizingCorners(JPanel panel) {
        for (JPanel corner : resizingCorners) {
            panel.remove(corner);
        }
        resizingCorners.clear();
    }

    /**
     * Renders the code item on the provided panel, including the text area for
     * code,
     * syntax and theme selection dropdowns, and resizing corners.
     *
     * @param panel The JPanel where the code item should be rendered.
     */
    public void paint(JPanel panel) {
        JPanel CodeAreaSurrounding = new JPanel();
        CodeAreaSurrounding.setBounds(x, y, width, height);

        Border border = BorderFactory.createLineBorder(Color.black, 1);
        CodeAreaSurrounding.setBorder(border);
        CodeAreaSurrounding.setLayout(null);

        RSyntaxTextArea codeArea = new RSyntaxTextArea();
        codeArea.setSyntaxEditingStyle("text/" + syntax);
        codeArea.setCodeFoldingEnabled(true);
        codeArea.setText(code);
        loadTheme(codeArea);
        codeArea.setBounds(10, 20, width - 15, height - 20);

        // Add codeArea to the panel
        CodeAreaSurrounding.add(codeArea);
        codeArea.setBorder(border);
        codeArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (e.getComponent() == codeArea) {
                    code = codeArea.getText();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(codeArea);
        scrollPane.setBounds(10, 20, width - 15, height - 20);
        CodeAreaSurrounding.add(scrollPane);

        String[] options = getSyntaxConstants();
        for (int i = 0; i < options.length; i++) {
            options[i] = options[i].replaceFirst("^text/", "");
        }
        JComboBox<String> dropdown = new JComboBox<>(options);
        dropdown.setSelectedItem(syntax);
        dropdown.setBounds(width - 90, 0, 90, 20);
        dropdown.setBorder(border);
        dropdown.addActionListener(e -> {
            // Get the selected item from the dropdown
            String selectedOption = (String) dropdown.getSelectedItem();

            // Set the syntax style based on the selected item
            if (selectedOption != null) {
                syntax = selectedOption;
                codeArea.setSyntaxEditingStyle("text/" + syntax);
                UI.getInstance().callRefresh();
            }
        });
        JLabel ThemeLbl = new JLabel("Theme:");
        Font fontObj = new Font(font, Font.BOLD, 10);
        ThemeLbl.setFont(fontObj);
        ThemeLbl.setBounds(width - 190, 0, 40, 20);
        CodeAreaSurrounding.add(ThemeLbl);
        CodeAreaSurrounding.add(dropdown);
        String[] themes = {"dark", "default-alt", "default", "druid", "eclipse", "idea", "monokai", "vs"};
        JComboBox<String> Theme_dropdown = new JComboBox<>(themes);
        Theme_dropdown.setBounds(width - 150, 0, 60, 20);
        CodeAreaSurrounding.add(Theme_dropdown);
        Theme_dropdown.setSelectedItem(theme);
        Theme_dropdown.addActionListener(e -> {
            // Get the selected item from the dropdown
            String selectetheme = (String) Theme_dropdown.getSelectedItem();

            // Set the syntax style based on the selected item
            if (selectetheme != null) {
                theme = selectetheme;
                loadTheme(codeArea);
                UI.getInstance().callRefresh();
            }
        });
        JLabel lblFontsize = new JLabel("Font size:");
        lblFontsize.setBounds(5, 0, 50, 20);
        lblFontsize.setFont(fontObj);
        CodeAreaSurrounding.add(lblFontsize);
        int[] fontsizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 28, 32, 36, 40, 48, 56, 64, 72};
        JComboBox<Integer> fontSizeDropdown = new JComboBox<>();
        for (int size : fontsizes) {
            fontSizeDropdown.addItem(size);
        }
        fontSizeDropdown.setSelectedItem(fontsize);
        fontSizeDropdown.setBounds(55, 0, 50, 20);
        fontSizeDropdown.addActionListener(e -> {
            this.fontsize = (int) fontSizeDropdown.getSelectedItem();
            UI.getInstance().callRefresh();
        });
        CodeAreaSurrounding.add(fontSizeDropdown);
        // Add action listeners

        Font codeFont = new Font("Arial", codeArea.getFont().getStyle(), fontsize);
        codeArea.setFont(codeFont);
        codeArea.setText(code);
        AddActionListeners(CodeAreaSurrounding, panel);

        // Add CodeAreaSurrounding to the main panel
        panel.add(CodeAreaSurrounding);
    }

    /**
     * Applies the selected theme to the RSyntaxTextArea component.
     *
     * @param textArea The RSyntaxTextArea to which the theme will be applied.
     */
    private void loadTheme(RSyntaxTextArea textArea) {
        String themeFilePath = "org/fife/ui/rsyntaxtextarea/themes/" + theme + ".xml";
        try (InputStream themeStream = getClass().getClassLoader().getResourceAsStream(themeFilePath)) {
            Theme theme = Theme.load(themeStream);
            theme.apply(textArea);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up event listeners for the RSyntaxTextArea component to handle focus
     * lost events.
     *
     * @param codeArea The RSyntaxTextArea on which the event listeners are to be
     *                 set up.
     */
    public void setUpEventListeners(RSyntaxTextArea codeArea) {
        codeArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (e.getComponent() == codeArea) {
                    code = codeArea.getText();
                }
            }
        });
    }

    /**
     * Retrieves an array of syntax constants available for syntax highlighting.
     *
     * @return An array of syntax constants as Strings.
     */
    private String[] getSyntaxConstants() {
        java.lang.reflect.Field[] fields = SyntaxConstants.class.getFields();
        String[] options = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            try {
                options[i] = (String) fields[i].get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return options;
    }

    /**
     * Displays a format box for editing properties of a CodeItem.
     * Allows changing position, size, and z-index of the item.
     *
     * @param item  The CodeItem to be edited.
     * @param panel The JPanel on which the CodeItem is displayed.
     */
    private void formatBox(CodeItem item, JPanel panel) {
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
     * Converts a given PlantUML code to an image and prompts the user for
     * confirmation before adding it to the application as well as the working
     * directory.
     *
     * @param plantUMLCode The PlantUML code to be converted into an image.
     */
    public void convertPlantUMLToImage(String plantUMLCode) {
        SourceStringReader reader = new SourceStringReader(plantUMLCode);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            // Generate the PNG image from PlantUML code
            DiagramDescription description = reader.outputImage(os, new FileFormatOption(FileFormat.PNG));
            byte[] byteArray = os.toByteArray();

            // Create a BufferedImage for dimension extraction and for display
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(byteArray));

            // Prepare and display a confirmation dialog with the generated image
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel(icon);
            JScrollPane imageScrollPane = new JScrollPane(label);
            imageScrollPane.setPreferredSize(new Dimension(800, 600)); // Adjust size as necessary

            int result = JOptionPane.showConfirmDialog(null, imageScrollPane,
                    "Are you sure you want to add this diagram?", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                // Proceed with file saving and adding the diagram if the user confirms
                FileManager currentFileManager = UI.getInstance().getFileManager();
                File umlDiagramFile = currentFileManager.generateNewFile();
                PresentationFilePath path = new PresentationFilePath(umlDiagramFile.getName());

                // Save the generated image to the file
                try (FileOutputStream fos = new FileOutputStream(umlDiagramFile)) {
                    fos.write(byteArray);
                }

                // Creating the imageItem instance from the file
                addImageItemFromFile(path, image.getWidth(), image.getHeight());

                // Show confirmation message to the user
                JOptionPane.showMessageDialog(null, "New image file added to " + umlDiagramFile.getName(),
                        "Image Added", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // User chose NO, do nothing
                System.out.println("Diagram addition cancelled by user.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to generate UML diagram", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds an image item to the application using the provided file path and
     * dimensions.
     *
     * @param filePath The file path of the image.
     * @param width    The width of the image.
     * @param height   The height of the image.
     */
    private void addImageItemFromFile(PresentationFilePath filePath, int width, int height) {
        ImageItem imageInstance = new ImageItem();

        // Configure the new ImageItem instance with the provided file path and
        // dimensions
        imageInstance.setPath(filePath);
        imageInstance.setWidth(width);
        imageInstance.setHeight(height);

        // Add the ImageItem to the application and refresh the UI
        UI.getInstance().getSlide().addItem(imageInstance);
        UI.getInstance().callRefresh();
    }

    /**
     * Determines the appropriate syntax highlighting style for a given file
     * extension
     * It checks against a map of special cases where the syntax style name does not
     * match the file extension directly
     * If the extension is not a special case, it defaults to using the extension
     * itself as the syntax style
     *
     * @param extension The file extension for which to determine the syntax
     *                  highlighting style
     * @return The syntax highlighting style string
     */
    private String determineSyntaxStyle(String extension) {
        Map<String, String> specialCases = new HashMap<>();
        // Define mappings for file extensions with syntax style names that differ from
        // the extension itself
        specialCases.put("py", "python");
        specialCases.put("js", "javascript");
        specialCases.put("cs", "csharp");
        // Additional mappings for exceptions can be added here

        // Convert the extension to lowercase to ensure case-insensitive comparison
        String lowerCaseExtension = extension.toLowerCase();
        // Return the mapped syntax style for special cases, or default to the extension
        // itself
        return specialCases.getOrDefault(lowerCaseExtension, lowerCaseExtension);
    }

    /**
     * Opens a file chooser dialog for the user to select a text or code file
     * Once a file is selected, its content is loaded into the code area of the
     * current CodeItem instance,
     * and the syntax highlighting style is updated based on the file's extension
     *
     * @param codeItem The CodeItem instance into which the file's content will be
     *                 loaded
     */
    public void importCodeFromFile(CodeItem codeItem) {
        JFileChooser fileChooser = new JFileChooser();
        // Configure the file chooser to only accept files, not directories
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Limit the files shown to those matching specific extensions
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text and Code Files", "txt", "java", "py", "js", "cpp", "c", "cs", "html", "css", "json", "xml");
        fileChooser.addChoosableFileFilter(filter);

        // Show the file chooser dialog and process the user's selection
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Read the content of the selected file
                String fileContent = Files.readString(selectedFile.toPath());
                // Update the code content of the current CodeItem
                codeItem.code = fileContent;

                // Determine and set the appropriate syntax highlighting based on the file
                // extension
                String fileName = selectedFile.getName();
                String fileExtension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
                codeItem.syntax = determineSyntaxStyle(fileExtension);

                // Debugging statement
                System.out.println(fileExtension + " -> Syntax style: " + codeItem.syntax);

                // Refresh the UI
                UI.getInstance().callRefresh();
            } catch (IOException e) {
                // Handle file reading errors
                JOptionPane.showMessageDialog(null, "Failed to read file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Retrieves the current code text of the code item.
     *
     * @return The current code text as a {@code String}.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code text of the code item.
     *
     * @param code The new code text to be set for the code item.
     */
    public void setCode(String code) {
        this.code = code;
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
