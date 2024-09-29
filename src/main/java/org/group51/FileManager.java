package org.group51;

/*
 * Import of relevant dependncies
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

/*
 * Class facilitates the saving of files
 */
public class FileManager {
    private static final String ERRORMSG_SAVING = "Couldn't save to this file";
    private final Path fileDir;
    private Optional<String> presentationPath;

    /**
     * Attempts to place all files involves in the presenation into a temp file
     */
    public FileManager() {
        try {
            fileDir = Files.createTempDirectory("presentationFiles");
        } catch (IOException e) {
            System.err.println("critical error occured while trying to initialize file manager, exiting");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occured while initializing file manager, you won't be able to add content from files to your presentation", "Error Message", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
            throw new RuntimeException("make the java compiler happy");
        }
    }

    /*
     * creates an instance of file manager and prepares for new files to be added
     */
    public static PresentationAndFileManager openNewPresentation() {
        FileManager f = new FileManager();
        f.setEmptyPresentationPath();
        return new PresentationAndFileManager(new Presentation(), f);
    }

    /*
     * allows the user to pick the path they want to store their file in
     */
    private static File pickPath(UI.FileMode mode) {
        JFileChooser presentationPathChooser = new JFileChooser();
        if (mode == UI.FileMode.OPEN) {
            presentationPathChooser.setDialogTitle("Specify the file you want to open");
        } else {
            presentationPathChooser.setDialogTitle("Specify a path to save the presentation to");
        }

        FileNameExtensionFilter jsonFilter = new FileNameExtensionFilter("Presentation files (*.pres)", "pres");
        presentationPathChooser.setFileFilter(jsonFilter);

        int userChoice;
        if (mode == UI.FileMode.OPEN) {
            userChoice = presentationPathChooser.showOpenDialog(null);
        } else {
            userChoice = presentationPathChooser.showSaveDialog(null);
        }

        try {
            switch (userChoice) {
                case JFileChooser.APPROVE_OPTION:
                    return presentationPathChooser.getSelectedFile();
                case JFileChooser.ERROR_OPTION:
                case JFileChooser.CANCEL_OPTION:
                    break;
                default:
                    System.err.println("ERROR: Something went wrong while trying to choose a path. userChoice: " + userChoice);
                    throw new Exception("ERROR: Something went wrong while trying to choose a path");
            }
        } catch (Exception e) {
            System.err.println();
            if (mode == UI.FileMode.OPEN) {
                JOptionPane.showMessageDialog(null, "Couldn't open this file");
            } else {
                JOptionPane.showMessageDialog(null, ERRORMSG_SAVING);
            }
        }
        return null;
    }

    public static Optional<PresentationAndFileManager> openFile() {
        try {
            File toOpen = pickPath(UI.FileMode.OPEN);
            if (toOpen == null) {
                System.err.println("can't open empty file");
                return Optional.empty();
            }

            FileManager files = new FileManager();

            String password = null;
            boolean needPassword = Serializer.checkIfEncrypted(toOpen);
            if (needPassword) {
                String enteredPassword = JOptionPane.showInputDialog("Enter password");
                if (enteredPassword == null || enteredPassword.isBlank()) {
                    JOptionPane.showMessageDialog(null, "Password is required to open this file");
                    return Optional.empty();
                }

                password = enteredPassword;
            }
            // start rendering the new presentation
            Presentation presentation = new Serializer().loadCompressed(toOpen, password, files.getFileDir());
            files.setPresentationPath(toOpen.getPath());

            return Optional.of(new PresentationAndFileManager(presentation, files));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Couldn't open this file");
        }
        return Optional.empty();
    }

    /**
     * Setter for path to presentation
     *
     * @param presentationPath
     */
    public void setPresentationPath(String presentationPath) {
        this.presentationPath = Optional.of(presentationPath);
    }

    /*
     * setter for not currently saved presenation
     */
    public void setEmptyPresentationPath() {
        this.presentationPath = Optional.empty();
    }

    /**
     * getter for file save location
     *
     * @return
     */
    private File getFileToSaveTo() {
        if (presentationPath.isEmpty()) {
            File picked = pickPath(UI.FileMode.SAVE);
            // save the path for future use
            if (picked != null) {
                presentationPath = Optional.of(picked.getPath());
            }
            return picked;
        } else {
            String pathToSaveTo = presentationPath.get();
            return new File(pathToSaveTo);
        }
    }

    /*
     * Method to write the file to the selcted path
     */
    public void savePresentation(Presentation presentation) {
        try {
            File toSaveTo = getFileToSaveTo();
            if (toSaveTo == null) {
                throw new Exception("can't save to this path");
            }
            new Serializer().saveCompressed(presentation, this, toSaveTo, null);

            JOptionPane.showMessageDialog(null, "Successfully saved presentation");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, ERRORMSG_SAVING);
        }
    }

    /**
     * Layer of abstraction
     * allows programmer to easily understand they action they are taking
     *
     * @param presentation
     */
    public void savePresentationTo(Presentation presentation) {
        savePresentationTo(presentation, null);
    }

    /**
     * Allows for the saving of presenations with a password protection
     *
     * @param presentation
     * @param password
     */
    public void savePresentationTo(Presentation presentation, String password) {
        try {
            File toSaveTo = pickPath(UI.FileMode.SAVE);
            if (toSaveTo == null) {
                throw new Exception("can't save to an empty path");
            }
            new Serializer().saveCompressed(presentation, this, toSaveTo, password);
            JOptionPane.showMessageDialog(null, "Successfully saved presentation");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ERRORMSG_SAVING);
        }
    }

    public Path getFileDir() {
        return fileDir;
    }

    private String getFileExtension(File f) {
        if (f == null) {
            throw new RuntimeException("invalid file passed");
        }

        int dotIndex = f.getName().lastIndexOf(".");
        if (dotIndex == -1) {
            return "";
        }

        return f.getName().substring(dotIndex + 1);
    }

    private String generateNewFileName(File f) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + getFileExtension(f);
    }

    /**
     * Adds file to the presenation save area
     *
     * @param file
     * @return
     * @throws IOException
     */
    public PresentationFilePath addFile(File file) throws IOException {
        Path filepath = file.toPath();
        String filename = generateNewFileName(file);
        Path targetPath = fileDir.resolve(filename);
        Files.copy(filepath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        PresentationFilePath presentationPath = new PresentationFilePath(filename);
        System.out.printf("New filepath: %s%n", presentationPath.filename());
        return presentationPath;
    }

    /**
     * creates new file name for the generated diagrams
     *
     * @return
     */
    public File generateNewFile() {
        String filename = generateNewFileName(new File("uml_diagram.png"));
        return fileDir.resolve(filename).toFile();
    }

    /**
     * removes file from the presentation save area
     *
     * @param path
     */
    public void removeFile(PresentationFilePath path) {
        Path filepath = fileDir.resolve(path.filename());
        try {
            Files.delete(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
