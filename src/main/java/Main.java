import bot.Bot;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import config.ConfigurationMapper;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {

    private static final ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final Logger logger = LoggerFactory.getLogger(Logger.class);

    public static void main(String[] args) {
        configurationMapper.run();
        try {
            TelegramBotsApi telegramBotApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            logger.debug(e.getMessage(), e);
        }
    }
}
