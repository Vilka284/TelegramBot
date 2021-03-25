package bot;

import config.Configuration;
import config.ConfigurationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

/*
    The message sender class for manual sending messages
    to divide logic from automated bot operations
 */
public class MessageSender extends DefaultAbsSender {

    private final Logger logger = LoggerFactory.getLogger(Logger.class);
    private final Configuration configuration = ConfigurationHolder.getConfiguration();

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

    public void sendFile(long chatId, File file, String fileName) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(chatId));
        InputFile inputFile = new InputFile();
        inputFile.setMedia(file, fileName);
        sendDocument.setDocument(inputFile);
        send(sendDocument);
    }

    private void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void send(SendDocument sendDocument) {
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
