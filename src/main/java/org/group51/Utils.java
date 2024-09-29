package org.group51;

import org.group51.contentitems.ImageItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Utils {
    /**
     * Adds an image item to the application using the provided file path and
     * dimensions.
     *
     * @param filePath The file path of the image.
     * @param width    The width of the image.
     * @param height   The height of the image.
     */
    public static void addImageItemFromFile(PresentationFilePath filePath, int width, int height) {
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
     * Saves the provided image to a file and adds it to the current slide.
     *
     * @param img
     */
    public static void saveImageAndAddToSlide(BufferedImage img) {
        try {
            FileManager currentFileManager = UI.getInstance().getFileManager();
            File outFile = currentFileManager.generateNewFile();
            PresentationFilePath path = new PresentationFilePath(outFile.getName());
            ImageIO.write(img, "png", outFile);

            // Display confirmation dialog with the saved image
            ImageIcon icon = new ImageIcon(img);
            JLabel label = new JLabel(icon);
            JScrollPane imageScrollPane = new JScrollPane(label);
            imageScrollPane.setPreferredSize(new Dimension(800, 600)); // Adjust size as necessary

            Utils.addImageItemFromFile(path, img.getWidth(), img.getHeight());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error saving Canvas image", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
