package config;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigurationMapper {

    private static final String configFile = "src/main/resources/config/application.yml";
    private final Logger logger = LoggerFactory.getLogger(Logger.class);

    public void run() {
        final Yaml yamlMapper = new Yaml();
        try (InputStream in = Files.newInputStream(Paths.get(configFile))) {
            Configuration config = yamlMapper.loadAs(in, Configuration.class);
            ConfigurationHolder.setConfiguration(config);
            logger.debug(config.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
