package bot;

import config.Configuration;
import config.ConfigurationHolder;
import dao.*;
import entity.Queue;
import enumeration.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.StatusService;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

public abstract class AbstractBot extends TelegramLongPollingBot {

    static final Comparator<Queue> compareByEnterDate = Comparator.comparingLong(q -> q.getEnter_date().getTime());
    final Logger logger = LoggerFactory.getLogger(Logger.class);
    final Configuration configuration = ConfigurationHolder.getConfiguration();
    final ParticipantDAO participantDAO = ParticipantDAO.getInstance();
    final QueueDAO queueDAO = QueueDAO.getInstance();
    final ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();
    final SubjectDAO subjectDAO = SubjectDAO.getInstance();
    final WatchCallbackDAO watchCallbackDAO = WatchCallbackDAO.getInstance();
    final StatusService statusService = new StatusService();
    final int openTimeInMilliseconds = 30 * 60 * 1000; // 30 minutes

    public static Comparator<Queue> getQueueComparatorByEnterDate() {
        return compareByEnterDate;
    }

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
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            onUpdateReceived(update);
        }
    }

    @Override
    public void onClosing() {
        logger.debug("Session is closed");
    }

    void sendMessageWithInlineButtons(long chatId, String text, Map<Long, String> stringSchedules) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(setInlineButtons(stringSchedules));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    void sendHelp(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String commands = Arrays.stream(Command.values())
                .filter(Command::isVisible)
                .filter(command -> {
                    boolean isModerator = configuration.getTelegram().getModerators().contains(chatId);
                    if (!isModerator) {
                        return !command.isModerator();
                    }
                    return true;
                })
                .map(command -> "▪ " + command.getCommand() + " - " + command.getHelp() + "\n")
                .collect(Collectors.joining());
        message.setText("Привіт, я QueueBot. Ти можеш використовувати наступні команди:\n" + commands);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    void sendSimpleMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        List<String> commands = Arrays.stream(Command.values())
                .filter(command -> (command.isVisible() && !command.isModerator()))
                .map(Command::getCommand)
                .collect(Collectors.toList());
        sendMessage.setReplyMarkup(setReplyButtons(commands));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private ReplyKeyboardMarkup setReplyButtons(List<String> buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        for (String button : buttons) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(button);
            keyboard.add(keyboardRow);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup setInlineButtons(Map<Long, String> values) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        values.forEach((key, value) -> {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(value);
            button.setCallbackData(String.valueOf(key));
            row.add(button);
            buttons.add(row);
        });
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        return markupKeyboard;
    }

    public void answerCallback(long chatId, long messageId, String message) {
        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(String.valueOf(chatId));
        new_message.setMessageId(toIntExact(messageId));
        new_message.setText(message);
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void answerCallbackWithInlineButtons(long chatId, long messageId, String message, Map<Long, String> queueParticipants) {
        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(String.valueOf(chatId));
        new_message.setMessageId(toIntExact(messageId));
        new_message.setText(message);
        new_message.setReplyMarkup(setInlineButtons(queueParticipants));
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
