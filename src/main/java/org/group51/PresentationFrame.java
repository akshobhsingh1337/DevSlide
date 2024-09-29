package org.group51;

import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.group51.contentitems.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class acts as the basis of the UI
 */
public class PresentationFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int[] fontsizes = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 28, 32, 36, 40, 48, 56, 64, 72};
    private final String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    private final Object themeObject = new FlatArcIJTheme();
    SlideManager renderer;
    JComboBox<Integer> fontSizeSelector;
    JButton fontColourButton;
    JComboBox<String> fontSelector;
    private JTabbedPane editingPane;
    private JMenuBar topMenuBar;
    private JList<String> slidePreviewList;
    private DefaultListModel<String> slideListModel;
    private JScrollPane previewPane;
    private JButton convertToUMLButton;
    private JButton importFileButton;
    private JPanel mainSlidePanel;
    private int separatorCount = 0;

    public PresentationFrame() {
        // Initialize render manager
        this.renderer = new SlideManager(this);

        setTitle("DevSlide");

        // Get the screen size of the user
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Set up the frame with a percentage of the screen size
        int screenWidth = (int) (screenSize.width * 0.8); // 80% of the screen width
        int screenHeight = (int) (screenSize.height * 0.8); // 80% of the screen height

        setSize(screenWidth, screenHeight);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Create a menu bar
        setUpMenuBar();
        // B - Feature Tabs (TEXT, INSERT, DRAW, DESIGN)
        setUpEditingPane();

        // Adding the tabbedPane to the container, replacing the existing one
        Container contentPane = getContentPane();
        contentPane.add(editingPane, BorderLayout.NORTH); // Place it at the top

        // Slides preview
        setUpPreviewRenderer();
        contentPane.add(previewPane, BorderLayout.WEST); // Add to the west region of the frame

        // Main Board
        JPanel mainBoardPanel = createPanel("Main Board");

        // create new keyboard shortcut thing
        new KeyboardShortcuts(mainBoardPanel);

        getContentPane().add(mainBoardPanel, BorderLayout.CENTER);

        // do the initial render
        refreshSlide();
        // Making the frame visible
        setVisible(true);
    }

    /**
     * Parses the slide index from the slide label string
     *
     * @param inputString
     * @return the slide number, if parsing was successful
     */
    public static Optional<Integer> parseSlideNumber(String inputString) {
        String regex = "Slide (\\d+)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);

        if (matcher.find()) {
            String numberStr = matcher.group(1);
            int number = Integer.parseInt(numberStr);
            return Optional.of(number);
        } else {
            return Optional.empty();
        }
    }

    public JPanel getMainSlidePanel() {
        return mainSlidePanel;
    }

    /**
     * Add various keybindings to the frame
     */
    private void setUpKeybindings() {
        this.setFocusable(true);

        KeyStroke rightButton = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        KeyStroke leftButton = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        KeyStroke slashKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0);
        KeyStroke f5Button = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);

        // Create an Action for the slash key
        Action nextSlideAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderer.progressToNextSlide();
            }
        };
        Action previousSlideAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderer.progressToPreviousSlide();
            }
        };
        Action startPresentationAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UI.getInstance().startPresentation();
            }
        };

        // Create an Action for the slash key
        Action slashAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                // focused on a text component, doesnt trigger listener
                if (focusOwner instanceof JTextComponent) {
                    return;
                }
                String slashCommand = JOptionPane.showInputDialog(PresentationFrame.this,
                        "Enter your desired slashCommand");
                new SlashCommandHandler(slashCommand);
            }
        };
        // Create an InputMap and associate the slashKeyStroke with the slashAction
        InputMap inputMap = mainSlidePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(rightButton, "nextSlideAction");
        inputMap.put(f5Button, "startPresentation");
        inputMap.put(leftButton, "previousSlideAction");
        inputMap.put(slashKeyStroke, "slashAction");

        // Create an ActionMap and associate the "slashAction" key with the slashAction
        ActionMap actionMap = mainSlidePanel.getActionMap();
        actionMap.put("nextSlideAction", nextSlideAction);
        actionMap.put("previousSlideAction", previousSlideAction);
        actionMap.put("slashAction", slashAction);
        actionMap.put("startPresentation", startPresentationAction);
    }

    /**
     * Sets up the menu bar, with all the different menu items and their event listeners
     */
    private void setUpMenuBar() {
        topMenuBar = new JMenuBar();

        // Create a menu items
        JMenu fileMenuItem = new JMenu("File");
        JMenu settingsMenuItem = new JMenu("Settings");
        JMenu helpMenu = new JMenu("Help");

        // Add menu items to the menu bar
        topMenuBar.add(fileMenuItem);
        topMenuBar.add(settingsMenuItem);
        topMenuBar.add(helpMenu);

        // Add dropdowns to the file menu
        JMenuItem newPresentation = createMenuItem("New");
        newPresentation.addActionListener(e -> UI.getInstance().openNewPresentation());
        fileMenuItem.add(newPresentation);

        JMenuItem open = createMenuItem("Open");
        open.addActionListener(e -> UI.getInstance().openFile());
        fileMenuItem.add(open);

        JMenuItem save = createMenuItem("Save");
        fileMenuItem.add(save);
        save.addActionListener(e -> UI.getInstance().savePresentation());

        JMenuItem saveEncryptedAs = createMenuItem("Save encrypted as");
        saveEncryptedAs.addActionListener(e -> UI.getInstance().saveEncryptedTo());
        fileMenuItem.add(saveEncryptedAs);

        JMenuItem saveAs = createMenuItem("Save as");
        saveAs.addActionListener(e -> UI.getInstance().savePresentationTo());
        fileMenuItem.add(saveAs);

        // Create dropdown items for the 'Help' menu
        JMenuItem gettingStarted = createMenuItem("GettingStarted-PDF");
        gettingStarted.addActionListener(e -> WelcomeScreen.openHelpPDF("GettingStarted"));
        helpMenu.add(gettingStarted);

        JMenuItem keyboardShortcuts = createMenuItem("KeyboardShortcuts-PDF");
        keyboardShortcuts.addActionListener(e -> WelcomeScreen.openHelpPDF("KeyboardShortcuts"));
        helpMenu.add(keyboardShortcuts);

        JMenuItem slashCommands = createMenuItem("SlashCommands-PDF");
        slashCommands.addActionListener(e -> WelcomeScreen.openHelpPDF("SlashCommands"));
        helpMenu.add(slashCommands);

        JMenu changeTheme = new JMenu("Change Theme");// for changing the appearance of the gui

        // changeTheme.setMnemonic(KeyEvent.VK_T);//alt+T shortcut
        ArrayList<JMenuItem> themesDropDowns = new ArrayList<JMenuItem>();

        String[] themeList = {"FlatArcIJTheme", "FlatDarkFlatIJTheme", "FlatDraculaIJTheme", "FlatOneDarkIJTheme",
                "FlatNordIJTheme", "FlatArcDarkOrangeIJTheme", "FlatCarbonIJTheme", "FlatCobalt2IJTheme",
                "FlatCyanLightIJTheme", "FlatDarkPurpleIJTheme", "FlatHiberbeeDarkIJTheme", "FlatMonocaiIJTheme",
                "FlatGradiantoNatureGreenIJTheme", "FlatGradiantoMidnightBlueIJTheme", "FlatGradiantoDeepOceanIJTheme",
                "FlatGradiantoDarkFuchsiaIJTheme", "FlatSolarizedLightIJTheme", "FlatSolarizedDarkIJTheme",
                "FlatSpacegrayIJTheme", "FlatXcodeDarkIJTheme", "FlatGruvboxDarkSoftIJTheme",
                "FlatGruvboxDarkMediumIJTheme", "FlatGruvboxDarkHardIJTheme", "FlatHighContrastIJTheme"};

        for (String theme : themeList) {
            themesDropDowns.add(createThemeJMenuItem(theme));
        }

        JMenu DefaultLang = new JMenu("Default Coding Language");
        java.lang.reflect.Field[] fields = SyntaxConstants.class.getFields();
        String[] options = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            try {
                options[i] = (String) fields[i].get(null);
                options[i] = options[i].replaceFirst("^text/", "");
                JMenuItem menuItem = new JMenuItem(options[i]);
                DefaultLang.add(menuItem);
                menuItem.addActionListener(e -> {
                    JMenuItem selectedItem = (JMenuItem) e.getSource();
                    System.out.println("Selected item: " + selectedItem.getText());
                    UI.updateSettings("defaultCodeLanguage", selectedItem.getText());
                });
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String[] Codethemes = {"dark", "default-alt", "default", "druid", "eclipse", "idea", "monokai", "vs"};
        JMenu DefaultCodeTheme = new JMenu("Default Coding Theme");
        for (String Codetheme : Codethemes) {
            JMenuItem menuItem = new JMenuItem(Codetheme);
            DefaultCodeTheme.add(menuItem);
            menuItem.addActionListener(e -> {
                JMenuItem selectedItem = (JMenuItem) e.getSource();
                UI.updateSettings("defaultCodeBoxTheme", selectedItem.getText());
            });
        }

        // Add dropdowns to the settings menu
        settingsMenuItem.add(changeTheme);
        settingsMenuItem.add(DefaultLang);
        settingsMenuItem.add(DefaultCodeTheme);

        // adds the different theme types to the menu item changeTheme, whilst including
        // separartors
        for (int i = 0; i < themesDropDowns.size(); i++) {
            changeTheme.add(themesDropDowns.get(i));
            separatorCount++;

            if (separatorCount % 4 == 0) {
                changeTheme.addSeparator();
            }
        }

        // Set the menu bar for the frame
        showMenuBar();
    }

    /*
     * Function to open PDF files
     */
    private void openPDF(String filePath) {
        if (Desktop.isDesktopSupported()) {
            try {
                File pdfFile = new File(filePath);
                if (!pdfFile.exists()) {
                    System.err.println("File not found: " + filePath);
                    return;
                }
                Desktop.getDesktop().open(pdfFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds buttons and their action listeners to the menu panes
     */
    public void setUpEditingPane() {
        editingPane = new JTabbedPane();

        // Create custom panels for each tab
        JPanel textPanel = createPanel("Text Functions");
        textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create JButton for Font Color
        fontColourButton = new JButton(" ");
        fontColourButton.setToolTipText("Changes the colour of the text in the currently selected text box");
        fontColourButton.setBackground(Color.black);
        fontColourButton.setSize(50, 50);

        // Add ActionListener to the JButton
        fontColourButton.addActionListener((ActionEvent e) -> {
            Color initialColor = fontColourButton.getBackground();
            JColorChooser FontColour = new JColorChooser(initialColor);
            FontColour.setPreviewPanel(new JPanel());
            Color result = JColorChooser.showDialog(null, "Choose Font Color", initialColor);
            if (result != null) {
                fontColourButton.setBackground(result);
                // Set the selected color as the font color for the text component
                TextItem textItem = UI.getInstance().getActiveText();
                textItem.setColour(result);
                UI.getInstance().callRefresh();
            }
        });

        fontSizeSelector = createFontSizeSelector();
        fontSizeSelector.setToolTipText("Changes the size of the text in the currently selected text box");

        fontSelector = new JComboBox<>();
        fontSelector.setToolTipText("Changes the size of the text in the currently selected text box");
        for (String font : fonts) {
            fontSelector.addItem(font);
        }
        fontSelector.addActionListener(e -> {
            String selectedItem = (String) fontSelector.getSelectedItem(); // Cast to string
            TextItem textItem = UI.getInstance().getActiveText();
            textItem.setFont(selectedItem);
            UI.getInstance().callRefresh();
        });
        JButton btnBold = new JButton("B");
        btnBold.setToolTipText("toggles the font in the currently selected text or comment box to be bold");
        btnBold.addActionListener(e -> {
            TextItem textItem = UI.getInstance().getActiveText();
            textItem.toggleBold();
            UI.getInstance().callRefresh();
        });
        JButton italicsButton = new JButton("I");
        italicsButton.setToolTipText("toggles the font in the currently selected text or comment box to be italicized");
        italicsButton.addActionListener(e -> {
            TextItem textItem = UI.getInstance().getActiveText();
            textItem.toggleItalic();
            UI.getInstance().callRefresh();
        });
        JButton btnUnderline = new JButton("U");
        btnUnderline.setToolTipText("toggles the font in the currently selected text or comment box to be underlined");
        btnUnderline.addActionListener(e -> {
            TextItem textItem = UI.getInstance().getActiveText();
            textItem.toggleUnderlined();
            UI.getInstance().callRefresh();
        });

        // The first row panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.add(createAddTextButton("Add Text box"));

        JPanel testPanel = createPanel("");
        testPanel.setLayout(new BoxLayout(testPanel, BoxLayout.Y_AXIS));
        // Panel for font settings
        JPanel fontSettingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontSettingsPanel.add(new JLabel("Fonts:"));
        fontSettingsPanel.add(fontSelector);
        fontSettingsPanel.add(new JLabel("Font size:"));
        fontSettingsPanel.add(fontSizeSelector);
        fontSettingsPanel.add(new JLabel("Font color:"));
        fontSettingsPanel.add(fontColourButton);

        // Panel for the text formatting buttons
        JPanel textFormattingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textFormattingPanel.add(btnBold);
        textFormattingPanel.add(italicsButton);
        textFormattingPanel.add(btnUnderline);
        initFormatButtons(textFormattingPanel);

        testPanel.add(fontSettingsPanel);
        testPanel.add(textFormattingPanel);

        textPanel.add(buttonPanel);
        textPanel.add(testPanel);

        // Insert Panel and its buttons
        JPanel insertPanel = createPanel("Insert Functions");
        insertPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        insertPanel.add(createAddTextButton("Insert text"));
        insertPanel.add(createAddCommentButton("Insert comment"));
        insertPanel.add(createAddSlideButton("Insert slide"));
        insertPanel.add(createRemoveSlideButton("Remove slide"));
        insertPanel.add(createAddImageButton("Insert Image"));
        insertPanel.add(createAddVideoButton("Insert Video"));
        insertPanel.add(createAddCodeButton("Insert CodeBox"));

        // Declaration and initialization of the Convert to UML Diagram button
        convertToUMLButton = new JButton("Convert to UML Diagram");
        convertToUMLButton.setToolTipText("When a CodeBox is selected, click this to convert your PlantUML code into a diagram");
        ImageIcon scaledUMLIcon = createScaledIcon("add-uml.png", 50, 50);
        convertToUMLButton.setIcon(scaledUMLIcon);
        // Set the text to appear below the icon
        convertToUMLButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        convertToUMLButton.setHorizontalTextPosition(SwingConstants.CENTER);
        convertToUMLButton.setEnabled(false); // Initially disabled
        convertToUMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CodeItem selectedCodeItem = UI.getInstance().getCodeItem();
                // Check if there's a selected CodeItem and get its PlantUML code
                if (selectedCodeItem != null) {
                    String plantUMLCode = selectedCodeItem.getCode();
                    selectedCodeItem.convertPlantUMLToImage(plantUMLCode);
                } else {
                    JOptionPane.showMessageDialog(null, "No CodeItem selected.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        insertPanel.add(convertToUMLButton);

        // Declaration and initialization of the Import Code button
        importFileButton = new JButton("Import File to CodeBox");
        importFileButton.setToolTipText("When a CodeBox is selected, click this to select a file to import");
        ImageIcon scaledIcon = createScaledIcon("file-import.png", 50, 50);
        importFileButton.setIcon(scaledIcon);
        // Set the text to appear below the icon
        importFileButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        importFileButton.setHorizontalTextPosition(SwingConstants.CENTER);
        importFileButton.setEnabled(false); // Initially disabled
        importFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CodeItem selectedCodeItem = UI.getInstance().getCodeItem();
                if (selectedCodeItem != null) {
                    selectedCodeItem.importCodeFromFile(selectedCodeItem);
                } else {
                    JOptionPane.showMessageDialog(null, "No CodeItem selected.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        insertPanel.add(importFileButton);

        JPanel drawPanel = createPanel("Draw Functions");
        drawPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        drawPanel.add(createBarChart("Draw Bar Chart"));
        drawPanel.add(createPieChart("Draw Pie Chart"));
        drawPanel.add(createDrawingCanvas("Drawing Canvas"));

        // Design Panel and its buttons
        JPanel designPanel = createPanel("Design Functions");
        designPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        designPanel.add(createChangeBackgroundButton("Change BG"));

        // Presentation Panel
        JPanel presentPanel = createPanel("Present");
        presentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        presentPanel.add(createStartPresentationButton("Present from Beginning"));
        presentPanel.add(createMidPresentationButton("Present from Current Slide"));

        // Add tabs
        editingPane.addTab("TEXT", textPanel);
        editingPane.addTab("INSERT", insertPanel);
        editingPane.addTab("DRAW", drawPanel);
        editingPane.addTab("DESIGN", designPanel);
        editingPane.addTab("PRESENT", presentPanel);

        // Customize tab components
        for (int i = 0; i < editingPane.getTabCount(); i++) {
            JLabel lbl = new JLabel(editingPane.getTitleAt(i));
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            editingPane.setTabComponentAt(i, lbl);
        }

        // Listen for tab changes to update the color
        editingPane.addChangeListener(e -> {
            for (int i = 0; i < editingPane.getTabCount(); i++) {
                editingPane.getTabComponentAt(i);
            }
            editingPane.getTabComponentAt(editingPane.getSelectedIndex());
        });
    }

    public void enableConvertToUMLButton(boolean enabled) {
        convertToUMLButton.setEnabled(enabled);
    }

    public void enableImportFileButton(boolean enabled) {
        importFileButton.setEnabled(enabled);
    }

    public void addCodebox() {
        int index = renderer.getCurrentSlideIndex();
        CodeItem codeItem = new CodeItem();
        codeItem.setCode("System.out.print('Hello World');");
        UI.getInstance().getPresentation().getSlide(index).addItem(codeItem);
        refreshSlide();
    }

    private JComboBox<Integer> createFontSizeSelector() {
        JComboBox<Integer> fontSizeSelector = new JComboBox<>();
        for (int size : fontsizes) {
            fontSizeSelector.addItem(size);
        }

        fontSizeSelector.addActionListener(e -> {
            try {
                int selectedItem = (int) fontSizeSelector.getSelectedItem(); // Cast to int
                TextItem textItem = UI.getInstance().getActiveText();
                textItem.setFontSize(selectedItem);
                UI.getInstance().callRefresh();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        });
        return fontSizeSelector;
    }

    /**
     * Function called by action listener to allow image to be added to the presenation
     */
    public void addImage() {
        JFileChooser imageChooser = new JFileChooser();
        imageChooser.showDialog(null, "Add image");
        File image = imageChooser.getSelectedFile();
        // creates the new image as an instance of ImageInstance
        ImageItem imageInstance = new ImageItem();
        // assigns a path to said image
        try {
            FileManager currentFileManager = UI.getInstance().getFileManager();
            PresentationFilePath path = currentFileManager.addFile(image);
            imageInstance.setImagePathAndDimensions(path);
            // adds the image you defined before
            UI.getInstance().getSlide().addItem(imageInstance);
            refreshSlide();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Something went wrong while adding the image", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Part of the main loop, this function calls all the
     * different paint functions on the items in the current slide
     * and then forces swing to refresh
     */
    public void refreshSlide() {
        // Create the new mainBoardPanel

        // Remove the old mainBoardPanel from the content pane
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component.equals(mainSlidePanel)) {
                getContentPane().remove(component);
            }
        }

        // render the new slide content
        mainSlidePanel = renderer.paint();
        setUpKeybindings();

        // Add the new mainBoardPanel to the content pane
        getContentPane().add(mainSlidePanel);

        // Revalidate and repaint the content pane
        revalidate();
        repaint();
    }

    private ImageIcon createScaledIcon(String imageName, int width, int height) {
        // Load the icon image
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(imageName));
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private JButton createAddTextButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Adds a text box to the currently selected slide");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("text-box.png", 50, 50);
        button.setIcon(scaledIcon);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> addText());
        return button;
    }

    /**
     * Called by action listener to add text to a slide
     */
    public void addText() {
        System.out.println("Text added");
        int index = renderer.getCurrentSlideIndex();
        TextItem textItem = new TextItem();
        textItem.setText("Add text here");
        UI.getInstance().getPresentation().getSlide(index).addItem(textItem);
        refreshSlide();
    }

    private JButton createAddCommentButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Adds a comment to the currently selected slide");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("comment.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> addComment());
        return button;
    }

    /**
     * Called by action listener to add a comment to the slide
     */
    public void addComment() {
        System.out.println("Comment added");
        int index = renderer.getCurrentSlideIndex();
        CommentItem commentItem = new CommentItem();
        commentItem.setText("This is a comment");
        UI.getInstance().getPresentation().getSlide(index).addItem(commentItem);
        refreshSlide();
    }

    private JButton createAddSlideButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Adds a new slide at the end of the presentation");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("add-slide.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> addSlide());
        return button;
    }

    /**
     * Sets the font colour in a selected text box to the specified colour
     *
     * @param color
     */
    public void setFontColourButton(Color color) {
        if (fontColourButton.getBackground() != color) {
            fontColourButton.setBackground(color);
        }
    }

    /**
     * sets the font size to the spcified size
     *
     * @param size
     */
    public void setFontSizeSelector(int size) {
        if (fontSizeSelector.getSelectedItem() != null) {
            int selectedFontSize = (int) fontSizeSelector.getSelectedItem();
            if (size != selectedFontSize) {
                fontSizeSelector.setSelectedItem(size);
            }
        }
    }

    /**
     * Allows user to chose between local or web hosted when adding videos to the slide
     *
     * @return
     */
    private String getUserInput() {
        String userInput = null;
        String[] options = {"Local File", "URL"};
        int choice = JOptionPane.showOptionDialog(this, "Select input type:", "Input Type", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) { // Local File
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Convert the file's absolute path to a URI
                    String absolutePath = selectedFile.getPath();
                    URI uri = new File(absolutePath).toURI();

                    // Convert the URI to a string, which will be properly encoded
                    String encodedPath = uri.toString();
                    userInput = encodedPath;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (choice == 1) { // URL
            String urlInput = JOptionPane.showInputDialog(this, "Enter URL:");
            if (urlInput != null && !urlInput.isEmpty()) {
                try {
                    new URL(urlInput); // Check if the URL is valid
                    userInput = urlInput;
                } catch (MalformedURLException e) {
                    JOptionPane.showMessageDialog(this, "Invalid URL", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        return userInput;
    }

    /**
     * Sets the font of a given box based on user input
     *
     * @param font
     */
    public void setFontSelector(String font) {
        if (fontSelector.getSelectedItem() != null) {
            String selectedFont = (String) fontSelector.getSelectedItem();
            if (!Objects.equals(font, selectedFont)) {
                fontSelector.setSelectedItem(font);
            }
        }
    }

    /**
     * adds a slide to the list of slides
     * updates the GUI and relevant data structures
     */
    public void addSlide() {
        if (slideListModel == null) {
            throw new RuntimeException("Something went wrong and slidelistmodel is null");
        }

        int newIndex = (UI.getInstance().getPresentation().addSlide() - 1);

        // switch to the new slide
        renderer.setCurrentSlideIndex(newIndex);
        slideListModel.add(renderer.getCurrentSlideIndex(), "Slide " + (slideListModel.getSize()));
    }

    private JButton createRemoveSlideButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Removes the currently selected slide");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("remove-slide.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> removeSlide());
        return button;
    }

    /**
     * removes slide from the slide list
     * Updates GUI and relevant data structures
     */
    public void removeSlide() {
        try {
            if (slideListModel == null) {
                throw new RuntimeException("this shouldn't be the case");
            }
            int indexOfSlideToRemove = renderer.getCurrentSlideIndex();
            slideListModel.remove(indexOfSlideToRemove);
            UI.getInstance().removeSlide(indexOfSlideToRemove);
            System.out.println("Removed slide " + indexOfSlideToRemove);

        } catch (Exception d) {
            JOptionPane.showMessageDialog(PresentationFrame.this, "Error deleting slide");
        }
    }

    private JButton createAddImageButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Inserts an Image into the currently selected slide");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("add-image.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> addImage());
        return button;
    }

    private JButton createAddVideoButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Add a video of your choice to the slide");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("add-video.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> {
            String userInput = getUserInput();
            if (userInput != null) {
                VideoItem videoInstance = new VideoItem();
                videoInstance.setPath(userInput);
                int index = renderer.getCurrentSlideIndex();
                UI.getInstance().getPresentation().getSlide(index).addItem(videoInstance);
                refreshSlide();
            }
        });
        return button;
    }

    private JButton createAddCodeButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Adds a CodeBox to the screen");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("add-code.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> addCodebox());
        return button;
    }

    private JButton createChangeBackgroundButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Change the background of the currently selected slide");

        ImageIcon scaledIcon = createScaledIcon("add-background.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> changeBackground());
        return button;
    }

    /**
     * Creates UI element to allow the user to enter presenation mode from the start of the presenation
     * Also specifies action listener and action that it takes
     *
     * @param text
     * @return
     */
    private JButton createStartPresentationButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Enter Presenation mode from the first slide");

        ImageIcon scaledIcon = createScaledIcon("start-presentation.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> {
            // Set the current slide to the first slide
            renderer.setCurrentSlideIndex(0);
            UI.getInstance().setSlide(UI.getInstance().getPresentation().getSlide(0));

            // Select the first item in the slide preview list
            slidePreviewList.setSelectedIndex(0);

            UI.getInstance().startPresentation();
        });
        return button;
    }

    /**
     * Creates UI element to allow the user to enter presenation mode from current slide
     * Also specifies action listener and action that it takes
     *
     * @param text
     * @return
     */
    private JButton createMidPresentationButton(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Enter Presenation mode from the current slide");

        ImageIcon scaledIcon = createScaledIcon("mid-presentation.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(e -> {
            UI.getInstance().startPresentation();
        });
        return button;
    }

    /**
     * updates the colour of the background of a given slide
     */
    public void changeBackground() {
        int index = renderer.getCurrentSlideIndex();
        Color initialColor = new Color(255, 255, 255);
        JColorChooser fontColour = new JColorChooser(initialColor);
        fontColour.setPreviewPanel(new JPanel());
        Color result = JColorChooser.showDialog(null, "Choose Font Color", initialColor);
        UI.getInstance().getPresentation().getSlide(index).setBackground(result);
        refreshSlide();
    }

    /**
     * getter for the renderer object
     */
    public SlideManager getRenderer() {
        return renderer;
    }

    /**
     * factory for JMenuItem object
     *
     * @param text
     * @return
     */
    private JMenuItem createMenuItem(String text) {
        return new JMenuItem(text);
    }

    /**
     * gets list of Themes ad presents them to the user
     * also defines action listener and action to apply theme
     *
     * @param text
     * @return
     */
    public JMenuItem createThemeJMenuItem(String text) {
        JMenuItem themeItem = new JMenuItem(text);

        // end of actionPerformed
        themeItem.addActionListener(actionEvent -> {
            String themeClassName = "com.formdev.flatlaf.intellijthemes." + text;

            // applies theme to the JFrame
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(themeClassName);
                    UI.getInstance().setCurrentTheme(themeClassName);

                    UI.updateSettings("themeName", themeClassName);
                    SwingUtilities.updateComponentTreeUI(PresentationFrame.this);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
        return themeItem;
    }

    /**
     * When called, launches the BarChart edit window
     *
     * @param text
     * @return
     */
    private JButton createBarChart(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Add Bar Chart to the slide");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("add-barchart.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(actionEvent -> new BarChartMain(themeObject));
        return button;
    }

    /**
     * Creates the pie chart edit window
     *
     * @param text
     * @return
     */
    private JButton createPieChart(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Add Pie Chart to the slide");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("add-piechart.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(actionEvent -> new PieChartMain(themeObject));
        return button;
    }

    /**
     * create the draw canvas edit window
     *
     * @param text
     * @return
     */
    private JButton createDrawingCanvas(String text) {
        JButton button = new JButton(text);
        button.setToolTipText("Open the Drawing canvas");

        // Load the icon image and scale
        ImageIcon scaledIcon = createScaledIcon("add-canvas.png", 50, 50);
        button.setIcon(scaledIcon);
        // Set the text to appear below the icon
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);

        button.addActionListener(actionEvent -> new TestDrawing());
        return button;
    }

    /**
     * factory for panels
     *
     * @param text
     * @return
     */
    public JPanel createPanel(String text) {
        JPanel panel = new JPanel();
        if (!UI.getInstance().isPresentationMode()) {
            panel.setBorder(BorderFactory.createTitledBorder(text));
        }
        return panel;
    }

    /**
     * hides the menu bar
     * utilised by presenation mode
     *
     * @return
     */
    public PresentationFrame hideMenuBar() {
        setJMenuBar(null);
        return this;
    }

    /**
     * shows the menu bar
     * utilised by presenation mode
     *
     * @return
     */
    public PresentationFrame showMenuBar() {
        setJMenuBar(topMenuBar);
        return this;
    }

    /**
     * hides the slide preview
     * utilised by presenation mode
     *
     * @return
     */
    public PresentationFrame hidePreview() {
        previewPane.setVisible(false);
        return this;
    }

    /**
     * shows the slide preview
     * utilised by presenation mode
     *
     * @return
     */
    public PresentationFrame showPreview() {
        previewPane.setVisible(true);
        return this;
    }

    /**
     * hides the editing pane
     * utilised by presenation mode
     *
     * @return
     */
    public PresentationFrame hideEditingPane() {
        editingPane.setVisible(false);
        return this;
    }

    /**
     * shows the editing pane
     * utilised by presenation mode
     *
     * @return
     */
    public PresentationFrame showEditingPane() {
        editingPane.setVisible(true);
        return this;
    }

    /**
     * creates the UI element for the slide preview bar
     */
    private void setUpPreviewRenderer() {
        slideListModel = new DefaultListModel<>();
        slidePreviewList = new JList<>(slideListModel);
        slidePreviewList.setFixedCellHeight(100); // Set the height of each cell in the list
        slidePreviewList.setFixedCellWidth(150); // Set the width of each cell in the list
        UI.getInstance().setSlide(UI.getInstance().getPresentation().getSlide(0));

        ArrayList<Slide> slides = UI.getInstance().getPresentation().getSlides();

        for (int i = 0; i < slides.size(); i++) {
            slideListModel.addElement("Slide " + i);
        }

        // Set a custom cell renderer to display the slide preview
        slidePreviewList.setCellRenderer(new SlidePreviewRenderer());
        slidePreviewList.setDragEnabled(true);
        slidePreviewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        slidePreviewList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem deleteSlide = new JMenuItem("Delete slide");
                    deleteSlide.addActionListener(actionEvent -> {
                        int index = slidePreviewList.locationToIndex(e.getPoint());
                        boolean wasRemoved = UI.getInstance().removeSlide(index);
                        if (wasRemoved) {
                            slideListModel.remove(index);
                        }
                        UI.getInstance().callRefresh();
                    });
                    menu.add(deleteSlide);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        // Set up drag and drop
        slidePreviewList.setDropMode(DropMode.INSERT);
        slidePreviewList.setTransferHandler(new ListTransferHandler());

        // Add the list to a scroll pane (in case of many slides)
        previewPane = new JScrollPane(slidePreviewList);
        previewPane.setPreferredSize(new Dimension(200, 0)); // Adjust the width as necessary
    }

    /**
     * Load icon for the alignment function
     *
     * @param iconName
     * @return
     */
    private Optional<JButton> loadIcon(String iconName) {
        try {
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream(iconName);
            if (imageStream != null) {
                ImageIcon icon = new ImageIcon(ImageIO.read(imageStream));
                Image scaledImage = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                return Optional.of(new JButton(new ImageIcon(scaledImage)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * updates the look of the formt buttons
     * also creates action listeners
     *
     * @param panel
     */
    private void initFormatButtons(JPanel panel) {
        Optional<JButton> alignLeftButton = loadIcon("align-left.png");
        alignLeftButton
                .ifPresent(button -> button.setToolTipText("Format text in currently selected text box to the left"));
        alignLeftButton.ifPresent(b -> b.addActionListener(e -> formatText(FlowLayout.LEFT)));
        alignLeftButton.ifPresent(panel::add);

        Optional<JButton> alignCenterButton = loadIcon("align-center.png");
        alignCenterButton
                .ifPresent(button -> button.setToolTipText("Format text in currently selected text box to the centre"));
        alignCenterButton.ifPresent(b -> b.addActionListener(e -> formatText(FlowLayout.CENTER)));
        alignCenterButton.ifPresent(panel::add);

        Optional<JButton> alignRightButton = loadIcon("align-right.png");
        alignRightButton
                .ifPresent(button -> button.setToolTipText("Format text in currently selected text box to the right"));
        alignRightButton.ifPresent(b -> b.addActionListener(e -> formatText(FlowLayout.RIGHT)));
        alignRightButton.ifPresent(panel::add);
    }

    /*
     * Action performed when action listener for format buttons is called
     */
    public void formatText(int layout) {
        UI.getInstance().getActiveText().setAlignment(layout);
        UI.getInstance().callRefresh();
    }

    /**
     * Class that handles the drag and drop functionality for the slide preview list to reorder slides
     */
    private class ListTransferHandler extends TransferHandler {
        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection(slidePreviewList.getSelectedValue());
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
            int targetIndex = dl.getIndex();
            if (targetIndex == -1) {
                return false;
            }

            try {
                String slideName = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                // HACK: we have to rely on the Slide name containing the slide index in order
                // to know where a slide came from
                // if we mess with the naming scheme, thats gonna mess the drag and drop up...
                Optional<Integer> idx = parseSlideNumber(slideName);
                if (idx.isEmpty()) {
                    System.err.println(
                            "it seems that it wasn't possible to parse the slide index from the label itself, maybe you changed the way the slides are labelled? if so, change it back");
                    return false;
                }
                int originalSlideIndex = idx.get();

                UI.getInstance().getPresentation().reorderSlides(originalSlideIndex, targetIndex - 1);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    /**
     * Class that renders the slide preview list
     */
    private class SlidePreviewRenderer extends JLabel implements ListCellRenderer<String> {
        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value);
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            // If someone clicked on it
            if (isSelected) {
                setBackground(Color.DARK_GRAY);
                setForeground(Color.WHITE);
                renderer.setCurrentSlideIndex(index);
                UI.getInstance().setSlide(UI.getInstance().getPresentation().getSlide(index));
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.black);
            }

            return this;
        }
    }
}
