package fierydragons;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Class for reading and storing configurations from the config file
public class Config {

    private static final Properties prop = new Properties(); // stores properties (configs)

    // Reads from the config file
    public static void readConfig() {
        String config = "game.properties";

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        try (InputStream fis = classloader.getResourceAsStream(config)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            System.err.println("File not found");
        } catch (IOException ex) {
            System.err.println("IOException");
        }

    }

    // Retrieves a config
    public static String getConfig(String config) {
        return prop.getProperty(config);
    }


}
