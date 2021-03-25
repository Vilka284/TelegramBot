package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigurationMapper {

    private static final String configFile = "src/main/resources/config/application.yml";
    private static final Logger logger = LoggerFactory.getLogger(Logger.class);

    public static void runConfigurationMapping() {
        final Yaml yamlMapper = new Yaml();
        try (InputStream in = Files.newInputStream(Paths.get(configFile))) {
            Configuration config = yamlMapper.loadAs(in, Configuration.class);
            ConfigurationHolder.setConfiguration(config);
            //logger.info(config.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
