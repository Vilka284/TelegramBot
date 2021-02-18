package bot;

import config.Configuration;
import config.ConfigurationHolder;
import dao.ParticipantDAO;
import dao.QueueDAO;
import dao.ScheduleDAO;
import dao.SubjectDAO;
import entity.Participant;
import entity.Schedule;
import enumeration.Command;
import enumeration.Day;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
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
            String day = getDayById(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).getName();
            boolean numberOperationComplete = false;

            // check operations
            try {
                long operationId = Long.parseLong(message);
                Participant participant = participantDAO.getParticipantByChatId(chatId);

                if (participant != null) {
                    String operation = participant.getOperation();
                    if (operation.equals(Command.QUEUE.getCommand())) {
                        // TODO queue participant in selected schedule
                        addParticipantToQueueByScheduleId(chatId, participant, operationId);
                        numberOperationComplete = true;
                    } else if (operation.equals(Command.DEQUEUE.getCommand())) {
                        // TODO dequeue participant in selected schedule
                        removeParticipantFromQueueByScheduleId(chatId, participant, operationId);
                        numberOperationComplete = true;
                    } else if (operation.equals(Command.WATCH.getCommand())) {
                        // TODO show participant selected queue
                        showQueueByScheduleId(chatId, participant, operationId);
                        numberOperationComplete = true;
                    }
                    participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
                }
            } catch (NumberFormatException | ObjectNotFoundException e) {
                logger.debug(e.getMessage());
            }

            // check commands
            Participant participant = participantDAO.getParticipantByChatId(chatId);
            if (participant != null) {
                if (message.equals(Command.START.getCommand())) {
                    sendSimpleMessage(chatId, "Ти вже є учасником.");
                } else if (message.equals(Command.WATCH.getCommand())) {
                    sendSchedule(chatId, day, Command.WATCH.getCommand(), participant, "Доступні черги для перегляду\uD83E\uDDD0");
                } else if (message.equals(Command.QUEUE.getCommand())) {
                    sendSchedule(chatId, day, Command.QUEUE.getCommand(), participant, "Обери чергу в яку хочеш записатись✍");
                } else if (message.equals(Command.DEQUEUE.getCommand())) {
                    sendSchedule(chatId, day, Command.DEQUEUE.getCommand(), participant, "Обери чергу з якої хочеш вийти✖");
                } else if (message.equals(Command.HELP.getCommand())) {
                    sendHelp(chatId);
                } else if (!numberOperationComplete) {
                    sendSimpleMessage(chatId, "Я тебе не розумію, скористайся командою /help");
                }
            } else {
                if (message.equals(Command.START.getCommand())) {
                    Participant newParticipant = new Participant();
                    newParticipant.setTag(update.getMessage().getFrom().getUserName());
                    newParticipant.setChatId(chatId);
                    newParticipant.setOperation(Command.NONE.getCommand());
                    participantDAO.addParticipant(newParticipant);
                    sendSimpleMessage(chatId, "Вітаю \uD83D\uDC4B, тепер ти можеш брати участь у чергах.\n" +
                            "Правила прості:\n" +
                            "▪️Реєстрація в чергу відбувається в день здачі, для цього надішли команду /queue\n" +
                            "▪️Черга активна впродовж дня здачі\n" +
                            "▪️З черги можна вийти за допомогою команди /dequeue\n" +
                            "\uD83C\uDD98 Напиши /help для того, щоб побачити команди.");
                } else {
                    sendSimpleMessage(chatId, "Надішли /start щоб почати, інакше буду тебе ігнорувати\uD83D\uDE48");
                }
            }
        }
    }

    private void showQueueByScheduleId(long chatId, Participant participant, long operationId) {
        // TODO
        sendSimpleMessage(chatId, "queue");
    }

    private void removeParticipantFromQueueByScheduleId(long chatId, Participant participant, long operationId) {
        // TODO
        sendSimpleMessage(chatId, "removed from queue");
    }

    private void addParticipantToQueueByScheduleId(long chatId, Participant participant, long operationId) {
        // TODO
        sendSimpleMessage(chatId, "added to queue");
    }

    private void sendSchedule(long chatId, String day, String operation, Participant participant, String message) {
        List<Schedule> schedules = scheduleDAO.getScheduleList();
        Map<Long, String> stringSchedules = filterSchedules(schedules, day);
        if (stringSchedules.isEmpty()) {
            sendSimpleMessage(chatId, "Сьогодні немає доступних черг\uD83E\uDD73");
            participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
        } else {
            // TODO send buttons
            sendSimpleMessage(chatId, message + "\n" + stringSchedules.toString());
            participantDAO.updateParticipantOperationStatus(participant.getId(), operation);
        }
    }

    private Map<Long, String> filterSchedules(List<Schedule> schedules, String day) {
        return schedules.stream()
                .filter(schedule -> schedule.getDay().equalsIgnoreCase(day))
                .collect(Collectors.toMap(Schedule::getId, schedule -> {
                    String time = schedule.getHour().toString();
                    String subject = schedule.getSubject().getName();
                    return time + " - " + subject;
                }));
    }

    private void sendHelp(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String commands = Arrays.stream(Command.values())
                .filter(Command::isVisible)
                .map(command -> command.getCommand() + " - " + command.getHelp() + "\n")
                .collect(Collectors.joining());
        message.setText(commands);
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

    public Day getDayById(int id) {
        return Arrays.stream(Day.values())
                .filter(day -> day.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No such day exists"));
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
