package org.group51;

import java.io.File;
import java.nio.file.Path;

public record PresentationFilePath(String filename) {

    public File getFile(FileManager fileManager) {
        Path basePath = fileManager.getFileDir();
        Path fullPath = basePath.resolve(filename);
        System.out.printf("Resolved path: %s\n", fullPath);
        return fullPath.toFile();
    }
}
