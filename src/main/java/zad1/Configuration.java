package zad1;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Configuration {
    static public String OPENWEATHER_AUTH_TOKEN;
    static public String FIXER_AUTH_TOKEN;
    static {
        Properties properties = new Properties();
        try {
            URL resource = Configuration.class.getClass().getResource("/app.properties");
            FileInputStream inStream = new FileInputStream(resource.getFile());
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OPENWEATHER_AUTH_TOKEN = properties.getProperty("open-weather-token");
        FIXER_AUTH_TOKEN = properties.getProperty("fixer-auth-token");

    }
}
