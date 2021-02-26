package bot;

import config.Configuration;
import config.ConfigurationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageSender extends DefaultAbsSender {

    public final Logger logger = LoggerFactory.getLogger(Logger.class);
    private final Configuration configuration = ConfigurationHolder.getConfiguration();

    private static MessageSender instance;

    public static MessageSender getInstance() {
        return instance != null ? instance : new MessageSender();
    }

    public MessageSender() {
        super(new DefaultBotOptions());
    }

    @Override
    public String getBotToken() {
        return configuration.getTelegram().getBot().getToken();
    }

    public void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        send(sendMessage);
    }

    public void sendMessage(long chatId, String text, boolean disableNotification) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        sendMessage.setDisableNotification(disableNotification);
        send(sendMessage);
    }

    private void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
