import bot.Bot;
import config.ConfigurationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {

    private static final ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final Logger logger = LoggerFactory.getLogger(Logger.class);

    // TODO schedule queue clearing
    public static void main(String[] args) {
        configurationMapper.run();
        try {
            TelegramBotsApi telegramBotApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
