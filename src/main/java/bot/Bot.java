package bot;

import config.Configuration;
import config.ConfigurationHolder;
import dao.ParticipantDAO;
import dao.QueueDAO;
import dao.ScheduleDAO;
import dao.SubjectDAO;
import entity.Participant;
import enumeration.Command;
import org.hibernate.ObjectNotFoundException;
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

    private final ParticipantDAO participantDAO = ParticipantDAO.getInstance();
    private final QueueDAO queueDAO = QueueDAO.getInstance();
    private final ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();
    private final SubjectDAO subjectDAO = SubjectDAO.getInstance();

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

            // check operations
            try {
                long operationId = Long.parseLong(message);
                Participant participant = participantDAO.getParticipantByChatId(chatId);

                String operation = participant.getOperation();
                if (operation.equals(Command.QUEUE.getCommand())) {
                    // TODO queue participant in selected schedule
                } else if (operation.equals(Command.DEQUEUE.getCommand())) {
                    // TODO dequeue participant in selected schedule
                } else if (operation.equals(Command.WATCH.getCommand())) {
                    // TODO show participant selected queue
                } else {
                    sendHelp(chatId);
                }
            } catch (NumberFormatException | ObjectNotFoundException e) {
                logger.debug(e.getMessage());
            }

            try {
                Participant participant = participantDAO.getParticipantByChatId(chatId);

                // check commands
                if (message.equals(Command.START.getCommand())) {
                    sendSimpleMessage(chatId, "Ти вже є учасником.");
                } else if (message.equals(Command.WATCH.getCommand())) {
                    // TODO get schedule
                    participant.setOperation(Command.WATCH.getCommand());
                } else if (message.equals(Command.QUEUE.getCommand())) {
                    // TODO show schedule to participant
                    participant.setOperation(Command.QUEUE.getCommand());
                } else if (message.equals(Command.DEQUEUE.getCommand())) {
                    // TODO show schedule to participant
                    participant.setOperation(Command.DEQUEUE.getCommand());
                } else if (message.equals(Command.HELP.getCommand())) {
                    sendHelp(chatId);
                } else {
                    sendSimpleMessage(chatId, "Я тебе не розумію, скористайся командою /help");
                }
            } catch (ObjectNotFoundException e) {
                logger.debug(e.getMessage());

                if (message.equals(Command.START.getCommand())) {
                    Participant participant = new Participant();
                    participant.setTag(update.getMessage().getFrom().getUserName());
                    participant.setChatId(chatId);
                    participant.setOperation(Command.NONE.getCommand());
                    participantDAO.addParticipant(participant);
                    sendSimpleMessage(chatId, "Вітаю, тепер ти можеш брати участь у чергах. Напиши /help для того, щоб побачити команди.");
                }
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

    private void sendSimpleMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
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
