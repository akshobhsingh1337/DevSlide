package org.group51;

import com.formdev.flatlaf.FlatIntelliJLaf;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

/**
 * Class serves as a home screen when a user boots up the program
 * has its own self contained GUI
 */
public class WelcomeScreen extends JFrame {

    private final JPanel centralPanel;
    private String selectedTheme;// default theme

    public WelcomeScreen() {
        // Set the FlatLaf IntelliJ theme
        try {
            selectedTheme = UI.getSettings().themeName();
            UIManager.setLookAndFeel(selectedTheme);
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf IntelliJ Look and Feel");
        }

        // Adjust the window size based on the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * 0.5), (int) (screenSize.height * 0.6));

        // Title Header
        setTitle("Welcome to DevSlide");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Define custom colors for BG
        Color backgroundColor = new Color(43, 43, 43); // dark gray
        Color menuBackgroundColor = new Color(50, 50, 50); // slightly lighter gray
        Color buttonColor = new Color(60, 63, 65); // IntelliJ IDEA button color
        Color textColor = Color.WHITE;

        // Main panel layout (Complete Panel)
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[grow 0][grow]", "[]"));
        mainPanel.setBackground(backgroundColor);

        // Menu panel (Left panel)
        JPanel menuPanel = new JPanel(new MigLayout("fillx, insets 0, gap 0", "[grow]", "[]20[]"));
        menuPanel.setBackground(menuBackgroundColor);

        // Menu title
        JLabel menuLabel = new JLabel("Menu");
        menuLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
        menuLabel.setForeground(textColor);
        menuPanel.add(menuLabel, "wrap, alignx center, gaptop 15, gapbottom 15");

        // Welcome label panel
        // Text
        JLabel welcomeLabel = new JLabel("Welcome to DevSlide");
        welcomeLabel.setFont(new Font("Helvetica", Font.BOLD, 35));
        welcomeLabel.setForeground(textColor);
        //Tagline
        JLabel tagLine = new JLabel("{ bringing your code to life }");
        tagLine.setFont(new Font("Monospaced", Font.BOLD, 18));
        tagLine.setForeground(new Color(0, 102, 0));
        // Panel
        JPanel welcomeLabelPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[]15[]"));
        welcomeLabelPanel.setOpaque(false);
        welcomeLabelPanel.add(welcomeLabel, "cell 0 0, align center");
        welcomeLabelPanel.add(tagLine, "cell 0 1, align center");

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new MigLayout("fill, insets 20 20 20 20", "[]20[]", "[]20[]"));
        buttonsPanel.setOpaque(false);

        // New Project Panel
        // Icon (Button)
        JButton newProjectButton = createIconButton("New Project", buttonColor, textColor, "new.png");
        // Text
        JLabel newProjectLabel = new JLabel("New Project", SwingConstants.CENTER);
        newProjectLabel.setForeground(textColor);
        newProjectLabel.setFont(new Font("Helvetica", Font.PLAIN, 18));
        newProjectButton.addActionListener(e -> {
            openUIWithTheme(selectedTheme, null); // null because it's a new project
        });
        // Panel
        JPanel newProjectPanel = new JPanel(new MigLayout("fill, insets 0", "[center]", "[]15[]"));
        newProjectPanel.setOpaque(false);
        newProjectPanel.add(newProjectButton, "wrap");
        newProjectPanel.add(newProjectLabel);

        // Open Button Panel
        // Icon (Button)
        JButton openButton = createIconButton("Open", buttonColor, textColor, "open.png");
        // Text
        JLabel openLabel = new JLabel("Open", SwingConstants.CENTER);
        openLabel.setForeground(textColor);
        openLabel.setFont(new Font("Helvetica", Font.PLAIN, 18));
        openButton.addActionListener(e -> {
            Optional<PresentationAndFileManager> presentationAndFileManager = FileManager.openFile();
            if (presentationAndFileManager.isEmpty()) {
                return;
            }
            openUIWithTheme(selectedTheme, presentationAndFileManager.get());

        });
        // Panel
        JPanel openPanel = new JPanel(new MigLayout("fill, insets 0", "[center]", "[]15[]"));
        openPanel.setOpaque(false);
        openPanel.add(openButton, "wrap");
        openPanel.add(openLabel);

        // Add both panels to the buttons panel
        buttonsPanel.add(newProjectPanel, "growx");
        buttonsPanel.add(openPanel, "growx");

        // Central panel (Panel exluding Menu, also updates)
        centralPanel = new JPanel(new MigLayout("insets 0", "[grow]", "[]10[]"));
        centralPanel.setOpaque(false);

        // Default / Starter Screen
        centralPanel.add(welcomeLabelPanel, "dock north, wrap, align center, gaptop 150, gapbottom 50");
        centralPanel.add(buttonsPanel, "align center");

        // Action listener for the Projects button
        JButton projectsButton = createButton("Projects", menuBackgroundColor, textColor);
        projectsButton.addActionListener(e -> {
            // Content to show for the Projects button
            JPanel projectsContent = new JPanel(new MigLayout("insets 0", "[grow]", "push[]10[]push"));
            projectsContent.setOpaque(false);
            projectsContent.add(welcomeLabelPanel, "dock north, wrap, align center, gaptop 150, gapbottom 50");
            projectsContent.add(buttonsPanel, "align center");
            updateCentralPanel(projectsContent);
        });

        // Define the combo box for theme selection
        String[] themes = {"FlatArcIJTheme", "FlatDarkFlatIJTheme", "FlatDraculaIJTheme", "FlatOneDarkIJTheme", "FlatNordIJTheme", "FlatArcDarkOrangeIJTheme", "FlatCarbonIJTheme", "FlatCobalt2IJTheme", "FlatCyanLightIJTheme", "FlatDarkPurpleIJTheme", "FlatHiberbeeDarkIJTheme", "FlatMonocaiIJTheme", "FlatGradiantoNatureGreenIJTheme", "FlatGradiantoMidnightBlueIJTheme", "FlatGradiantoDeepOceanIJTheme", "FlatGradiantoDarkFuchsiaIJTheme", "FlatSolarizedLightIJTheme", "FlatSolarizedDarkIJTheme", "FlatSpacegrayIJTheme", "FlatXcodeDarkIJTheme", "FlatGruvboxDarkSoftIJTheme", "FlatGruvboxDarkMediumIJTheme", "FlatGruvboxDarkHardIJTheme", "FlatHighContrastIJTheme"};
        JComboBox<String> themeComboBox = new JComboBox<>(themes);
        String preselectedTheme = UI.getSettings().themeName();
        themeComboBox.setSelectedItem(preselectedTheme.substring(preselectedTheme.lastIndexOf(".") + 1));
        themeComboBox.addActionListener(e -> {
            String theme = (String) themeComboBox.getSelectedItem();
            selectedTheme = "com.formdev.flatlaf.intellijthemes." + theme;
            UI.updateSettings("themeName", selectedTheme);
            try {
                UIManager.setLookAndFeel(selectedTheme);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Define labels for the combo boxes
        JLabel themeLabel = new JLabel("Choose Theme: ");
        themeLabel.setForeground(textColor);
        themeLabel.setFont(new Font("Helvetica", Font.PLAIN, 19));

        // Action listener for the Customize button
        JButton customizeButton = createButton("Customize", menuBackgroundColor, textColor);
        customizeButton.addActionListener(e -> {
            // Content to show for the "Customize" button
            JLabel customizationLabel = new JLabel("Customization Options", SwingConstants.CENTER);
            customizationLabel.setFont(new Font("Helvetica", Font.BOLD, 35));
            customizationLabel.setForeground(textColor);

            // Panel for the customization options
            JPanel customizationContent = new JPanel(new MigLayout("insets 20", "[right]16[fill]", "[]10[]"));
            customizationContent.setOpaque(false);

            // Adding the components to the Panel
            customizationContent.add(themeLabel, "gaptop 50, gapbottom 20");
            customizationContent.add(themeComboBox, "wrap, gapbottom 20");

            // Add the main customization label
            customizationContent.add(customizationLabel, "dock north, wrap, gaptop 150, gapbottom 50");

            // Update the central panel with the new content
            updateCentralPanel(customizationContent);
        });

        // Create the "Help" button
        JButton learnButton = createButton("Help", menuBackgroundColor, textColor);
        learnButton.addActionListener(e -> {
            // Content to show for the "Help" button
            JPanel learnContent = new JPanel();
            learnContent.setLayout(new MigLayout("insets 20 20 20 20", "[grow, center]", "[]25[]"));
            learnContent.setOpaque(false);

            // Adding the "Help" label
            JLabel learnLabel = new JLabel("Help", SwingConstants.CENTER);
            learnLabel.setFont(new Font("Helvetica", Font.BOLD, 35));
            learnLabel.setForeground(textColor);
            learnContent.add(learnLabel, "dock north, wrap, gaptop 150, gapbottom 50");

            // Adding the "Getting Started" label
            JLabel helpLabel = new JLabel("Getting Started", SwingConstants.CENTER);
            helpLabel.setFont(new Font("Helvetica", Font.PLAIN, 19));
            helpLabel.setForeground(textColor);
            learnContent.add(helpLabel, "wrap");

            // Adding the "KeyboardShortcutsPDF" label
            JLabel keyboardShortcutsPDFLabel = new JLabel("KeyboardShortcutsPDF", SwingConstants.CENTER);
            keyboardShortcutsPDFLabel.setFont(new Font("Helvetica", Font.PLAIN, 19));
            keyboardShortcutsPDFLabel.setForeground(textColor);
            learnContent.add(keyboardShortcutsPDFLabel, "wrap");

            // Adding the "SlashCommandsPDF" label
            JLabel slashCommandsPDFLabel = new JLabel("SlashCommandsPDF", SwingConstants.CENTER);
            slashCommandsPDFLabel.setFont(new Font("Helvetica", Font.PLAIN, 19));
            slashCommandsPDFLabel.setForeground(textColor);
            learnContent.add(slashCommandsPDFLabel, "wrap");

            // Modify the label text to use HTML to look more like a hyperlink
            helpLabel.setText("<html><a href='' style='color:#1E90FF;'>Getting Started</a></html>");
            keyboardShortcutsPDFLabel.setText("<html><a href='' style='color:#1E90FF;'>KeyboardShortcutsPDF</a></html>");
            slashCommandsPDFLabel.setText("<html><a href='' style='color:#1E90FF;'>SlashCommandsPDF</a></html>");

            // Changing cursor hover animation over the links
            helpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            keyboardShortcutsPDFLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            slashCommandsPDFLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Action listeners for the Labels to open linked PDF
            helpLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openHelpPDF("GettingStarted");
                }
            });

            keyboardShortcutsPDFLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openHelpPDF("KeyboardShortcuts");
                }
            });

            slashCommandsPDFLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openHelpPDF("SlashCommands");
                }
            });

            // Update the central panel with the new content
            updateCentralPanel(learnContent);
        });

        // Add buttons to the menuPanel
        menuPanel.add(projectsButton, "wrap, growx, alignx center");
        menuPanel.add(customizeButton, "wrap, growx, alignx center");
        menuPanel.add(learnButton, "wrap, growx, alignx center");

        // Add the menu panel to the main panel
        mainPanel.add(menuPanel, "dock west, width 200::"); // This docks the menu to the left and sets its width

        // Add the central panel to the main panel
        mainPanel.add(centralPanel, "cell 1 0, grow"); // This centers the centralPanel in the remaining space

        // Add main panel to frame
        add(mainPanel);

        setVisible(true);
    }

    // Function to open PDF files
    private static void openPDF(String filePath) {
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
        } else {
            System.err.println("Opening the pdf is not supported on this system.");
        }
    }

    private static File writeInputStreamToTemp(InputStream inputStream, String filename, String fileExtension) throws IOException {
        Path tempFilePath = Files.createTempFile(filename, fileExtension);
        File tempFile = tempFilePath.toFile();
        tempFile.deleteOnExit(); // Ensure the file is deleted when the program exits

        byte[] buffer = inputStream.readAllBytes();
        try (OutputStream out = new FileOutputStream(tempFile)) {
            out.write(buffer);
        }
        return tempFile;
    }

    public static void openHelpPDF(String name) {
        try {
            InputStream resourceAsStream = WelcomeScreen.class.getClassLoader().getResourceAsStream(name + ".pdf");
            if (resourceAsStream == null) {
                return;
            }
            File tempFile = writeInputStreamToTemp(resourceAsStream, name, ".pdf");
            openPDF(tempFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to create menu panel buttons
     *
     * @param text
     * @param bg
     * @param fg
     * @return
     */
    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Helvetica", Font.PLAIN, 16));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Function to create Icon buttons
     *
     * @param text
     * @param bg
     * @param fg
     * @param iconName
     * @return
     */
    private JButton createIconButton(String text, Color bg, Color fg, String iconName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Helvetica", Font.PLAIN, 18));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        button.setFocusPainted(false);

        // Loading the imageIcon from resouces
        URL imageUrl = getClass().getClassLoader().getResource(iconName);
        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image image = originalIcon.getImage();

            // Calculations to maintain the aspect ratio
            int desiredHeight = 140;
            double aspectRatio = (double) originalIcon.getIconWidth() / originalIcon.getIconHeight();
            int newWidth = (int) Math.round(desiredHeight * aspectRatio);
            Image scaledImage = image.getScaledInstance(newWidth, desiredHeight, Image.SCALE_SMOOTH);

            // Create new ImageIcon from the scaled image
            ImageIcon icon = new ImageIcon(scaledImage);
            button.setIcon(icon);
        } else {
            System.err.println("Icon not found: " + iconName);
        }

        // Resetting text to null for the icon
        button.setText("");

        return button;
    }

    /**
     * Function to update the central panel to display different types of content
     *
     * @param newContent
     */
    private void updateCentralPanel(JComponent newContent) {
        centralPanel.removeAll();
        centralPanel.add(newContent, "align center");
        centralPanel.revalidate();
        centralPanel.repaint();
    }

    /**
     * Function to create a new instance of UI and open it with selected theme
     *
     * @param theme
     * @param pf
     */
    private void openUIWithTheme(String theme, PresentationAndFileManager pf) {
        dispose(); // Close the WelcomeScreen
        if (pf == null) {
            pf = FileManager.openNewPresentation();
        }
        UI.initializeThemeAndUI(theme, pf);
    }
}
