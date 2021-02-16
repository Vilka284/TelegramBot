package bot;

import config.Configuration;
import config.ConfigurationHolder;
import enumeration.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Bot extends TelegramLongPollingBot {

    private final Logger logger = LoggerFactory.getLogger(Logger.class);
    private final Configuration configuration = ConfigurationHolder.getConfiguration();

    @Override
    public String getBotUsername() {
        return configuration.getTelegram().getBot().getUsername();
    }

    @Override
    public String getBotToken() {
        return configuration.getTelegram().getBot().getToken();
    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (message.equals(Command.START.getCommand())) {
                // TODO add new participant
            } else if (message.equals(Command.WATCH.getCommand())) {
                // TODO get schedule
            } else if (message.equals(Command.QUEUE.getCommand())) {
                // TODO show schedule to participant
            } else if (message.equals(Command.DEQUEUE.getCommand())) {
                // TODO show schedule to participant
            } else if (message.equals(Command.HELP.getCommand())) {
                sendHelp(chatId);
            } else {
                sendWrongCommand(chatId, "Я тебе не розумію, скористайся командою /help");
            }
        }
    }

    private void sendHelp(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        List<String> commands = Arrays.stream(Command.values())
                .filter(Command::getShow)
                .map(command -> command.getCommand() + " " + command.getHelp())
                .collect(Collectors.toList());
        message.setText(commands.toString());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendWrongCommand(long chatId, String message) {
        //TODO
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            onUpdateReceived(update);
        }
    }

    @Override
    public void onClosing() {
        logger.debug("Session is closed");
    }
}
