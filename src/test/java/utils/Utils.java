package utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Utils {

    private static PropertiesConfiguration config;

    static {
        try {
            config = new PropertiesConfiguration("src/test/resources/config.properties");
        } catch (ConfigurationException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    // Save environment variable
    public static void setEnv(String key, String value) throws ConfigurationException {
        config.setProperty(key, value);
        config.save();
    }

    // Read environment variable
    public static String getEnv(String key) {
        return config.getString(key);
    }

    // Generate random number (inclusive)
    public static int generateRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
