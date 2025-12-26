package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Utils {

    private static final Map<String, String> env = new HashMap<>();

    public static void setEnv(String key, String value) {
        env.put(key, value);
    }

    public static String getEnv(String key) {
        return env.get(key);
    }

    public static int generateRandomNumber(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
