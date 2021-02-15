package config;

public class ConfigurationHolder {
    private static Configuration configuration;

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(Configuration configuration) {
        ConfigurationHolder.configuration = configuration;
    }
}
