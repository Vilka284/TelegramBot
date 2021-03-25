import bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static config.ConfigurationMapper.runConfigurationMapping;
import static schedule.Scheduler.executeScheduling;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Logger.class);

    public static void main(String[] args) {
        runConfigurationMapping();
        // TODO make scheduling future task in database
        executeScheduling();
        try {
            TelegramBotsApi telegramBotApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
