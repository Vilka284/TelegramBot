package bot;

import config.Configuration;
import config.ConfigurationHolder;
import dao.ParticipantDAO;
import dao.QueueDAO;
import dao.ScheduleDAO;
import dao.SubjectDAO;
import entity.Participant;
import entity.Queue;
import entity.Schedule;
import enumeration.Command;
import enumeration.Day;
import enumeration.Status;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

public class Bot extends TelegramLongPollingBot {

    private final Logger logger = LoggerFactory.getLogger(Logger.class);
    private final Configuration configuration = ConfigurationHolder.getConfiguration();

    private final ParticipantDAO participantDAO = ParticipantDAO.getInstance();
    private final QueueDAO queueDAO = QueueDAO.getInstance();
    private final ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();
    private final SubjectDAO subjectDAO = SubjectDAO.getInstance();

    private final int openTimeInMilliseconds = 30 * 60 * 1000; // 30 minutes

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

            // if bot in group
            if (update.getMessage().getChat().getType().equals("group")
                    || update.getMessage().getChat().getType().equals("supergroup")) {
                if (message.equals(Command.WATCH.getCommand())
                || message.equals(Command.WATCH.getCommand() + "@" + ConfigurationHolder.getConfiguration().getTelegram().getBot().getUsername())) {
                    sendSchedule(chatId, day, "Доступні черги для перегляду\uD83E\uDDD0");
                } else if (message.equals(Command.QUEUE.getCommand())
                        || message.equals(Command.QUEUE.getCommand() + "@" + ConfigurationHolder.getConfiguration().getTelegram().getBot().getUsername())
                        || message.equals(Command.DEQUEUE.getCommand())
                        || message.equals(Command.DEQUEUE.getCommand() + "@" + ConfigurationHolder.getConfiguration().getTelegram().getBot().getUsername())) {
                    sendSimpleMessage(chatId, "Давай обговоримо це в приватних повідомленнях\uD83D\uDE09");
                } else if (message.equals(Command.HELP.getCommand())
                        || message.equals(Command.HELP.getCommand() + "@" + ConfigurationHolder.getConfiguration().getTelegram().getBot().getUsername())) {
                    sendHelp(chatId);
                }
                return;
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
                } else if (message.equals(Command.STOP.getCommand())) {
                    // TODO
                    sendSimpleMessage(chatId, "Ця команда поки що немає змісту, вона запрацює із розширенням функціоналу :)");
                } else if (message.equals(Command.HELP.getCommand())) {
                    sendHelp(chatId);
                } else {
                    sendSimpleMessage(chatId, "Я тебе не розумію, скористайся командою /help");
                }
            } else {
                if (message.equals(Command.START.getCommand())) {
                    Participant newParticipant = new Participant();
                    User from = update.getMessage().getFrom();
                    newParticipant.setTag(from.getUserName());
                    newParticipant.setName(from.getFirstName());
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
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String day = getDayById(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).getName();

            // check operations
            try {
                long operationId = Long.parseLong(data);

                // if bot in group
                if (update.getCallbackQuery().getMessage().getChat().getType().equals("group")
                        || update.getCallbackQuery().getMessage().getChat().getType().equals("supergroup")) {
                    showQueueByScheduleId(chatId, messageId, operationId, day);
                    return;
                }

                Participant participant = participantDAO.getParticipantByChatId(chatId);

                if (participant != null) {
                    String operation = participant.getOperation();
                    if (operation.equals(Command.QUEUE.getCommand())) {
                        addParticipantToQueueByScheduleId(chatId, messageId, operationId, participant, day);
                    } else if (operation.equals(Command.DEQUEUE.getCommand())) {
                        removeParticipantFromQueueByScheduleId(chatId, messageId, operationId, participant, day);
                    } else if (operation.equals(Command.WATCH.getCommand())) {
                        showQueueByScheduleId(chatId, messageId, operationId, day);
                    }
                    participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
                }
            } catch (NumberFormatException | ObjectNotFoundException e) {
                logger.debug(e.getMessage());
            }
        }
    }

    private void showQueueByScheduleId(long chatId, long messageId, long operationId, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(operationId, day);
        if (schedule != null) {
            List<Queue> queueList = queueDAO.getQueueList();
            if (!queueList.isEmpty()) {
                // sort queue by participants enter date
                Comparator<Queue> compareByEnterDate = (q1, q2) ->
                        Long.valueOf(q1.getEnter_date().getTime()).compareTo(q2.getEnter_date().getTime());
                queueList.sort(compareByEnterDate);

                List<String> queueParticipants = queueList.stream()
                        .filter(queue -> queue.getSchedule().getId() == operationId) // filter by required schedule
                        .map(part -> {
                            String tag = part.getParticipant().getTag();
                            return (tag != null ? "@" + tag : part.getParticipant().getName()) + " " + part.getStatus();
                        }) // map participant to string
                        .collect(Collectors.toList());

                if (!queueParticipants.isEmpty()) {
                    StringBuilder queue = new StringBuilder("Черга '" + schedule.getSubject().getName() + " " + schedule.getHour() + "':\n");
                    for (int i = 0; i < queueParticipants.size(); i++) {
                        queue.append(i + 1).append(". ").append(queueParticipants.get(i)).append("\n");
                    }
                    answerCallback(chatId, messageId, queue.toString());
                } else {
                    answerCallback(chatId, messageId, "В цій черзі немає нікого\uD83D\uDC40, будь першим надіславши команду /queue");
                }
            } else {
                answerCallback(chatId, messageId, "В цій черзі немає нікого\uD83D\uDC40, будь першим надіславши команду /queue");
            }
        } else {
            answerCallback(chatId, messageId, "Такої події на сьогодні немає\uD83E\uDD37\u200D♂");
        }
    }

    private void removeParticipantFromQueueByScheduleId(long chatId, long messageId, long operationId, Participant participant, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(operationId, day);
        if (schedule != null) {

            // check if queue are ready to be opened
            if (openToQueue(schedule)) {
                answerCallback(chatId, messageId, "Доступ до цієї черги закритий. Черга відкривається о " + new Time(schedule.getHour().getTime() - openTimeInMilliseconds));
                return;
            }

            // find all queues
            List<Queue> queueList = queueDAO.getQueueList();
            if (queueList != null) {
                // filter queue by schedule
                List<Queue> queueParticipants = queueList.stream()
                        .filter(queue -> queue.getSchedule().getId() == operationId)
                        .collect(Collectors.toList());
                if (!queueParticipants.isEmpty()) {
                    try {
                        // filter participant if participant in queue and not removed from this queue
                        Queue queueToRemove = queueParticipants.stream()
                                .filter(queue -> Objects.equals(queue.getParticipant().getId(), participant.getId())
                                        && !queue.getStatus().equals(Status.DEQUEUE.getStatus()))
                                .collect(Collectors.toList())
                                .get(0);

                        if (queueToRemove.getStatus().equals(Status.DEQUEUE.getStatus() + " " + Status.DEQUEUE.getEmoji())) {
                            answerCallback(chatId, messageId, "Ти не береш участь в черзі '" + schedule.getSubject().getName() + "'");
                            return;
                        }

                        queueDAO.changeParticipantStatus(queueToRemove.getId(), Status.DEQUEUE.getStatus() + " " + Status.DEQUEUE.getEmoji());
                        answerCallback(chatId, messageId, "Я видалив тебе з цієї черги\uD83D\uDC4D \n" +
                                "Щоб записатись у неї знову надішли /queue");
                    } catch (IndexOutOfBoundsException e) {
                        answerCallback(chatId, messageId, "Тебе немає в цій черзі\uD83D\uDE44");
                    }
                } else {
                    answerCallback(chatId, messageId, "Ця черга порожня\uD83D\uDEAB");
                }
            } else {
                answerCallback(chatId, messageId, "Ця черга порожня\uD83D\uDEAB");
            }
        } else {
            answerCallback(chatId, messageId, "Такої події на сьогодні немає\uD83E\uDD37\u200D♂");
        }
    }

    private void addParticipantToQueueByScheduleId(long chatId, long messageId, long operationId, Participant participant, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(operationId, day);
        if (schedule != null) {

            // check if queue are ready to be opened
            if (openToQueue(schedule)) {
                answerCallback(chatId, messageId, "Доступ до цієї черги закритий. Черга відкривається о " + new Time(schedule.getHour().getTime() - openTimeInMilliseconds));
                return;
            }

            List<Queue> queueList = queueDAO.getQueueList().stream()
                    .filter(queue -> queue.getSchedule().getId() == operationId) // filter queue by schedule
                    .filter(queue -> Objects.equals(queue.getParticipant().getId(), participant.getId())) // filter by participant
                    .collect(Collectors.toList());

            // if participant didn't enter queue add to queue
            // else change status
            if (queueList.isEmpty()) {
                Queue queue = new Queue();
                queue.setParticipant(participant);
                queue.setSchedule(schedule);
                queue.setStatus(Status.QUEUE.getStatus() + " " + Status.QUEUE.getEmoji());
                queue.setEnter_date(new Date());
                queueDAO.addToQueue(queue);
                answerCallback(chatId, messageId, "Я успішно додав тебе до черги " + schedule.getSubject().getName() + "\uD83D\uDC4C \n" +
                        "Щоб вийти з неї надішли /dequeue");
            } else {
                Queue queue = queueList.get(0);
                // if participant already in queue
                if (queue.getStatus().equals(Status.QUEUE.getStatus() + " " + Status.QUEUE.getEmoji())) {
                    answerCallback(chatId, messageId, "Ти вже є в черзі '" + schedule.getSubject().getName() + "'");
                    return;
                }
                queueDAO.changeParticipantStatus(queue.getId(), Status.QUEUE.getStatus() + " " + Status.QUEUE.getEmoji());
                answerCallback(chatId, messageId, "Я відновив тебе в черзі '" + schedule.getSubject().getName() + "'");
            }
        } else {
            answerCallback(chatId, messageId, "Такої події на сьогодні немає\uD83E\uDD37\u200D♂");
        }
    }

    private void sendSchedule(long chatId, String day, String message) {
        List<Schedule> schedules = scheduleDAO.getScheduleList();
        Map<Long, String> stringSchedules = filterSchedules(schedules, day);
        if (!stringSchedules.isEmpty()) {
            sendMessageWithInlineButtons(chatId, message, stringSchedules);
        } else {
            sendSimpleMessage(chatId, "Сьогодні немає доступних черг\uD83E\uDD73");
        }
    }

    private void sendSchedule(long chatId, String day, String operation, Participant participant, String message) {
        List<Schedule> schedules = scheduleDAO.getScheduleList();
        Map<Long, String> stringSchedules = filterSchedules(schedules, day);
        if (!stringSchedules.isEmpty()) {
            sendMessageWithInlineButtons(chatId, message, stringSchedules);
            participantDAO.updateParticipantOperationStatus(participant.getId(), operation);
        } else {
            sendSimpleMessage(chatId, "Сьогодні немає доступних черг\uD83E\uDD73");
            participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
        }
    }

    private Map<Long, String> filterSchedules(List<Schedule> schedules, String day) {
        return schedules.stream()
                .filter(schedule -> schedule.getDay().equalsIgnoreCase(day))
                .collect(Collectors.toMap(Schedule::getId, schedule -> {
                    String time = schedule.getHour().toString();
                    String subject = schedule.getSubject().getName();
                    String teacher = schedule.getSubject().getTeacher();
                    return time + " - " + subject + "\n" + teacher;
                }));
    }

    private Day getDayById(int id) {
        return Arrays.stream(Day.values())
                .filter(day -> day.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No such day exists"));
    }

    private boolean openToQueue(Schedule schedule) {
        long scheduleTime = schedule.getHour().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.add(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
        calendar.add(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
        calendar.add(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        long currentTime = calendar.getTime().getTime();
        return currentTime < scheduleTime - openTimeInMilliseconds;
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

    private void sendMessageWithInlineButtons(long chatId, String text, Map<Long, String> stringSchedules) {
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

    private void sendHelp(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        String commands = Arrays.stream(Command.values())
                .filter(Command::isVisible)
                .map(command -> "▪ " + command.getCommand() + " - " + command.getHelp() + "\n")
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
        List<String> commands = Arrays.stream(Command.values())
                .filter(Command::isVisible)
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

    public void answerCallback(long chatId, long messageId, String text) {
        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(String.valueOf(chatId));
        new_message.setMessageId(toIntExact(messageId));
        new_message.setText(text);
        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
