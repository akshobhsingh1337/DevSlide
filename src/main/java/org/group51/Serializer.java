package org.group51;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.group51.contentitems.ContentItem;
import org.group51.contentitems.ImageItem;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Serializer {
    private final Gson gson;

    public Serializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder
                .registerTypeAdapter(ContentItem.class, new ContentItemSerializer())
                .registerTypeAdapter(Color.class, new ColorSerializer())
                .registerTypeAdapter(PresentationFilePath.class, new PresentationFilePathSerializer())
                .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);

        gson = gsonBuilder.create();
    }

    /**
     * For some reason, some image files sometimes stay around longer than they're actually used in the presentation.
     * This method attempts to only return those files which are actually needed in the presentation
     *
     * @param presentation The presentation that potentially contains images
     * @param fileManager  The file manager with the path where the images are stored
     * @return a list of files which are actually used in the presentation
     */
    private static ArrayList<File> garbageCollectedImages(Presentation presentation, FileManager fileManager) {
        Path basePath = fileManager.getFileDir();

        ArrayList<File> cleanFiles = new ArrayList<>();
        for (Slide slide : presentation.getSlides()) {
            for (ContentItem item : slide.getItems()) {
                if (item instanceof ImageItem imageItem) {
                    String filename = imageItem.getPath().filename();
                    File file = basePath.resolve(filename).toFile();
                    cleanFiles.add(file);
                }
            }
        }

        return cleanFiles;
    }

    public static boolean checkIfEncrypted(File file) {
        try {
            return new ZipFile(file).isEncrypted();
        } catch (ZipException e) {
            return false;
        }
    }

    public Presentation loadPresentation(File file) throws IOException {
        Reader reader = Files.newBufferedReader(file.toPath());
        return deserializeFromJson(reader);
    }

    private Presentation deserializeFromJson(Reader reader) {
        return gson.fromJson(reader, Presentation.class);
    }

    private String serializeToJson(Presentation presentation) {
        return gson.toJson(presentation);
    }

    private ZipFile initZipFile(File file, String password) {
        if (password == null || password.isEmpty()) {
            return new ZipFile(file);
        }
        return new ZipFile(file, password.toCharArray());
    }

    private ZipParameters createZipParams(String password) {
        if (password == null || password.isEmpty()) {
            return new ZipParameters();
        }
        ZipParameters parameters = new ZipParameters();
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        return parameters;
    }

    /**
     * Save a presentation to a .pres file which is essentially a zip file with a .json file and all the images
     *
     * @param presentation    Presentation to save
     * @param fileManager     file manager with the path where the images are stored
     * @param destinationFile file to write to
     * @param password        if null or empty, the zip will not be encrypted
     * @throws IOException in case of an IO error
     */
    public void saveCompressed(Presentation presentation, FileManager fileManager, File destinationFile, String password) throws IOException {
        // remove the old zip, so old images don't end up in the final zip
        if (destinationFile.exists()) {
            Files.delete(destinationFile.toPath());
        }
        try (ZipFile compressed = initZipFile(destinationFile, password)) {
            ArrayList<File> presentationFiles = garbageCollectedImages(presentation, fileManager);

            ZipParameters parameters = createZipParams(password);

            for (File file : presentationFiles) {
                compressed.addFile(file, parameters);
            }

            // Convert string to InputStream
            InputStream inputStream = new ByteArrayInputStream(serializeToJson(presentation).getBytes());
            parameters.setFileNameInZip("presentation.json");
            compressed.addStream(inputStream, parameters);
        }
    }

    public Presentation loadCompressed(File file, String password, Path destinationPath) throws IOException {
        try (ZipFile zip = initZipFile(file, password)) {
            zip.extractAll(destinationPath.toAbsolutePath().toString());
            return loadPresentation(destinationPath.resolve("presentation.json").toFile());
        }
    }
}
