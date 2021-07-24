package me.zlataovce.moonlight.config;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * <p>Represents the class responsible for managing the configuration.</p>
 *
 * @author zlataovce <34477304+zlataovce@users.noreply.github.com>
 */
public class MoonlightConfigurationManager {
    @Getter private final Properties prop = new Properties();
    private final File configurationFile = new File(Paths.get(System.getProperty("user.dir")).toString(), "moonlight.config");

    /**
     * <p>Loads the configuration.</p>
     */
    public MoonlightConfigurationManager() {
        this.tryConfig();
        try {
            this.prop.load(new FileInputStream(this.configurationFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Tries to create a configuration file if it doesn't exist.</p>
     */
    public void tryConfig() {
        try {
            if (!this.configurationFile.exists()) {
                new PropertiesBuilder(new BufferedWriter(new FileWriter(this.configurationFile)))
                        .node("jdbcurl", "jdbc:mysql://localhost:3306/moonlight?createDatabaseIfNotExist=true&useUnicode=yes&characterEncoding=UTF-8")
                        .node("dbuser", "moonlight")
                        .node("dbpassword", RandomStringUtils.randomAlphanumeric(15))
                        .build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
