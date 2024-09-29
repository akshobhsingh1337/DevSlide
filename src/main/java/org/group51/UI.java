package org.group51;

import org.group51.contentitems.CodeItem;
import org.group51.contentitems.TextItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Class serves as a controller for UI functions
 */
public class UI {
    private static UI INSTANCE;
    private final FileManager fileManager;
    private TextItem activeText;
    private Slide slide;
    private Presentation presentation;
    private PresentationFrame frame;
    private boolean isExclusiveFullScreen;
    private boolean isPresentationMode;
    private String currentTheme;
    private CodeItem selectedCodeItem;

    public UI(Presentation presentation, FileManager fileManager, String theme) {
        if (INSTANCE != null) {
            this.fileManager = fileManager;
            setPresentation(presentation);
            try {
                UIManager.setLookAndFeel(theme);
                this.currentTheme = theme;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        INSTANCE = this;

        if (!Objects.equals(theme, "")) {
            try {
                UIManager.setLookAndFeel(theme);
                this.currentTheme = theme;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Updating the UI
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.presentation = presentation;
        this.fileManager = fileManager;
        this.frame = new PresentationFrame();
        isExclusiveFullScreen = false;
    }

    /**
     * Detects and applies selected theme
     *
     * @param theme
     * @param pf
     */
    public static void initializeThemeAndUI(String theme, PresentationAndFileManager pf) {
        if (theme == null && getInstance() != null) {
            theme = getInstance().getCurrentTheme();
        }

        if (getInstance() != null) {
            UI.getInstance().reset();
        }
        // Creating new instance of the UI
        String finalTheme = theme;
        Presentation p = pf.presentation();
        FileManager f = pf.fileManager();
        SwingUtilities.invokeLater(() -> new UI(p, f, finalTheme));
    }

    /**
     * getter for instace of UI
     *
     * @return
     */
    public static UI getInstance() {
        return INSTANCE;
    }

    public static SettingsDTO getSettings() {
        try {
            return SettingsDTO.load();
        } catch (IOException ex) {
            throw new RuntimeException("Error reading file", ex);
        }
    }

    public static void updateSettings(String key, String value) {
        SettingsDTO setting = getSettings();
        try {
            switch (key) {
                case "themeName" ->
                        setting = new SettingsDTO(value, setting.defaultCodeLanguage(), setting.defaultCodeBoxTheme());
                case "defaultCodeLanguage" ->
                        setting = new SettingsDTO(setting.themeName(), value, setting.defaultCodeBoxTheme());
                case "defaultCodeBoxTheme" ->
                        setting = new SettingsDTO(setting.themeName(), setting.defaultCodeLanguage(), value);
                default -> throw new IllegalArgumentException("Invalid key");
            }
            setting.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * getter for current theme
     *
     * @return
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(String currentTheme) {
        this.currentTheme = currentTheme;
    }

    /**
     * creates new instance of presentation
     */
    public void openNewPresentation() {
        PresentationAndFileManager p = FileManager.openNewPresentation();
        UI.initializeThemeAndUI(getCurrentTheme(), p);
    }

    /*
     * Opens an existing presentation
     */
    public void openFile() {
        Optional<PresentationAndFileManager> newPresentation = FileManager.openFile();
        if (newPresentation.isEmpty()) {
            return;
        }

        reset();
        UI.initializeThemeAndUI(getCurrentTheme(), newPresentation.get());
    }

    public void savePresentation() {
        fileManager.savePresentation(getPresentation());
    }

    public void savePresentationTo() {
        fileManager.savePresentationTo(getPresentation());
    }

    /**
     * prompts the user to enter password to encrypt the presenation
     */
    public void saveEncryptedTo() {
        String password = JOptionPane.showInputDialog("Enter password");
        if (password == null || password.isBlank()) {
            return;
        }
        fileManager.savePresentationTo(getPresentation(), password);
    }

    public boolean isPresentationMode() {
        return isPresentationMode;
    }

    /**
     * getter for slide object
     *
     * @return
     */
    public Slide getSlide() {
        return this.slide;
    }

    /**
     * setter for slide object
     * based on slide attribute param
     *
     * @param slide
     */
    public void setSlide(Slide slide) {
        this.slide = slide;
    }

    /**
     * getter for currently active text
     *
     * @return
     */
    public TextItem getActiveText() {
        return this.activeText;
    }

    /**
     * setter for currently active text
     *
     * @param textItem
     */
    public void setActiveText(TextItem textItem) {
        this.activeText = textItem;
        frame.setFontColourButton(textItem.getColour());
        frame.setFontSizeSelector(textItem.getFontSize());
        frame.setFontSelector(textItem.getFont());
    }

    public void reset() {
        if (this.frame != null) {
            this.frame.dispose();
            this.frame.renderer = null;
            this.frame = null;
            INSTANCE = null;
        }
    }

    /**
     * returns instance of currently active presenation
     *
     * @return
     */
    public Presentation getPresentation() {
        return presentation;
    }

    /**
     * assigns a presenation as the currenty active instance
     *
     * @param p
     */
    public void setPresentation(Presentation p) {
        this.presentation = p;
        this.frame = new PresentationFrame();
    }

    /**
     * returns the instance of presenation frame
     *
     * @return
     */
    public PresentationFrame getFrame() {
        return frame;
    }

    public void callRefresh() {
        frame.refreshSlide();
    }

    public FileManager getFileManager() {
        return fileManager;
    }


    /**
     * Removes a slide from the presentation
     *
     * @param index index of the slide to remove
     * @return true if the slide was removed, false if a new slide was created (to make sure that there never are 0 slides)
     */
    public boolean removeSlide(int index) {
        boolean flag = true;
        this.presentation.removeSlide(index);

        // make sure that we never have 0 slides to avoid bugs with slide index
        if (this.presentation.getSlides().isEmpty()) {
            this.presentation.addSlide(new Slide());
            flag = false;
        }
        getFrame().renderer.progressToPreviousSlide();
        return flag;
    }

    /**
     * enters presenation mode
     * ensures that comments are hiden by refreshing the page
     */
    public void startPresentation() {
        if (frame == null) return;

        enterFullScreen();
        // Make the frame visible
        frame.setVisible(true);
        isPresentationMode = true;

        frame.hidePreview().hideEditingPane().hideMenuBar();

        callRefresh();
    }

    /**
     * Adds a listener to the frame that listens for the escape key to exit presentation mode
     */
    private void addESCListener() {
        // Create a KeyStroke for the "/" key
        KeyStroke escStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        // Create an Action for the slash key
        Action escAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leavePresentationMode();
            }
        };
        // Create an InputMap and associate the slashKeyStroke with the slashAction
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(escStroke, "escAction");

        // Create an ActionMap and associate the "slashAction" key with the slashAction
        ActionMap actionMap = frame.getRootPane().getActionMap();
        actionMap.put("escAction", escAction);

    }

    /**
     * Attempt to enter fullscreen mode for the presentation mode
     */
    private void enterFullScreen() {
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        // try to enter exclusive fullscreen, if that isn't possible, we'll just make it
        // occupy the full screen
        if (graphicsDevice.isFullScreenSupported()) {
            addESCListener();
            graphicsDevice.setFullScreenWindow(frame);
            isExclusiveFullScreen = true;
        } else {
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            isExclusiveFullScreen = false;
        }
        frame.setVisible(true);
    }

    /**
     * Leaves the presentation mode and returns to the editing mode, showing the preview and the menu bar
     */
    public void leavePresentationMode() {
        frame.showPreview().showEditingPane().showMenuBar();

        isPresentationMode = false;
        leaveFullScreen();
        callRefresh();
    }

    /**
     * Leaves the fullscreen mode to return to normal windowed mode
     */
    private void leaveFullScreen() {
        if (isExclusiveFullScreen) {
            GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            graphicsDevice.setFullScreenWindow(null);
        }
    }

    /**
     * Facilitaies item focus and manipualtion
     *
     * @param codeItem
     */
    public void setSelectedCodeItem(CodeItem codeItem) {
        selectedCodeItem = codeItem;
    }

    /**
     * getter for code item
     *
     * @return
     */
    public CodeItem getCodeItem() {
        return selectedCodeItem;
    }

    enum FileMode {
        SAVE, OPEN
    }
}
