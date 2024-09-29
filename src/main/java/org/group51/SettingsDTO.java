package org.group51;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public record SettingsDTO(String themeName, String defaultCodeLanguage, String defaultCodeBoxTheme) {
    private static final String configFileName = ".devslides.config.json";
    private static SettingsDTO defaultConfig() {
        return new SettingsDTO("com.formdev.flatlaf.intellijthemes.FlatArcIJTheme", "java", "dark");
    }

    public static SettingsDTO load() throws IOException {
        Path homePath = Paths.get(System.getProperty("user.home"));
        File configFile = homePath.resolve(configFileName).toFile();
        Gson gson = new Gson();

        if (!configFile.exists()) {
            return defaultConfig();
        }

        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                return gson.fromJson(reader, SettingsDTO.class);
            }
        } catch (Exception e) {
            return defaultConfig();
        }
    }

    public void save() throws IOException {
        Path homePath = Paths.get(System.getProperty("user.home"));
        File configFile = homePath.resolve(configFileName).toFile();
        Gson gson = new Gson();

        String serialized = gson.toJson(this);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(serialized);
        }
    }
}
