package config;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Setup {
    public Properties prop;

    @BeforeClass
    public void setup() throws IOException {
        prop = new Properties();
        try (FileInputStream fs = new FileInputStream("./src/test/resources/config.properties")) {
            prop.load(fs);
        }
    }

    @AfterMethod
    public void reload() throws IOException {
        try (FileInputStream fs = new FileInputStream("./src/test/resources/config.properties")) {
            prop.clear();
            prop.load(fs);
        }
    }
}
