package bot;

import entity.Participant;
import entity.Queue;
import entity.Schedule;
import enumeration.Command;
import enumeration.Day;
import enumeration.Status;
import org.hibernate.ObjectNotFoundException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

public class Bot extends AbstractBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String day = Day.getDayById(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).getName();

            // if bot in group
            if (update.getMessage().getChat().getType().equals("group")
                    || update.getMessage().getChat().getType().equals("supergroup")) {
                if (message.equals(Command.WATCH.getCommand())
                        || message.equals(Command.WATCH.getCommand() + "@" + getBotUsername())) {
                    sendSchedule(chatId, day, "Доступні черги для перегляду\uD83E\uDDD0");
                } else if (message.equals(Command.QUEUE.getCommand())
                        || message.equals(Command.QUEUE.getCommand() + "@" + getBotUsername())
                        || message.equals(Command.DEQUEUE.getCommand())
                        || message.equals(Command.DEQUEUE.getCommand() + "@" + getBotUsername())) {
                    sendSimpleMessage(chatId, "Давай обговоримо це в приватних повідомленнях\uD83D\uDE09");
                } else if (message.equals(Command.HELP.getCommand())
                        || message.equals(Command.HELP.getCommand() + "@" + getBotUsername())) {
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
                    String[] operation = participant.getOperation().split(" ");
                    // if participant selected watch as previous command
                    // next command will be executed automatically without additional call
                    if (operation.length > 1
                            && operation[0].equals(Command.WATCH.getCommand())) {
                        addParticipantToQueueByScheduleId(chatId, Long.parseLong(operation[1]), participant, day);
                        participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
                        return;
                    }

                    // standard command call
                    sendSchedule(chatId, day, Command.QUEUE.getCommand(), participant, "✍Обери чергу в яку хочеш записатись✍");
                } else if (message.equals(Command.DEQUEUE.getCommand())) {
                    String[] operation = participant.getOperation().split(" ");
                    // if participant selected watch as previous command
                    // next command will be executed automatically without additional call
                    if (operation.length > 1
                            && operation[0].equals(Command.WATCH.getCommand())) {
                        removeParticipantFromQueueByScheduleId(chatId, Long.parseLong(operation[1]), participant, day);
                        participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
                        return;
                    }

                    // standard command call
                    sendSchedule(chatId, day, Command.DEQUEUE.getCommand(), participant, "✖Обери чергу з якої хочеш вийти✖");
                } else if (message.equals(Command.STOP.getCommand())) {
                    // TODO
                    sendSimpleMessage(chatId, "Ця команда поки що немає змісту, вона запрацює із розширенням функціоналу :)");
                    participantDAO.updateParticipantOperationStatus(participant.getId(), Command.STOP.getCommand());
                } else if (message.equals(Command.HELP.getCommand())) {
                    sendHelp(chatId);
                    participantDAO.updateParticipantOperationStatus(participant.getId(), Command.HELP.getCommand());
                } else if (message.equals(Command.REMOVE.getCommand()) && isModerator(chatId)) {
                    sendSchedule(chatId, day, Command.REMOVE.getCommand(), participant, "\uD83D\uDEABОбери чергу з якої ти хочеш видалити учасника\uD83D\uDEAB");
                } else if (message.equals(Command.BAN.getCommand()) && isModerator(chatId)) {
                    // TODO
                    sendSimpleMessage(chatId, "Ця команда поки що немає змісту, вона запрацює із розширенням функціоналу :)");
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
            String day = Day.getDayById(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).getName();

            // check operations
            try {
                long operationId = Long.parseLong(data);

                // if bot in group
                if (update.getCallbackQuery().getMessage().getChat().getType().equals("group")
                        || update.getCallbackQuery().getMessage().getChat().getType().equals("supergroup")) {
                    showQueueByScheduleIdCallback(chatId, messageId, operationId, day);
                    return;
                }

                Participant participant = participantDAO.getParticipantByChatId(chatId);

                if (participant != null) {
                    String operation = participant.getOperation();
                    if (operation.equals(Command.QUEUE.getCommand())) {
                        addParticipantToQueueByScheduleIdCallback(chatId, messageId, operationId, participant, day);
                    } else if (operation.equals(Command.DEQUEUE.getCommand())) {
                        removeParticipantFromQueueByScheduleIdCallback(chatId, messageId, operationId, participant, day);
                    } else if (operation.equals(Command.WATCH.getCommand())) {
                        showQueueByScheduleIdCallback(chatId, messageId, operationId, day);
                    } else if (operation.equals(Command.REMOVE.getCommand())) {
                        showQueueParticipantsToRemoveByScheduleIdCallback(chatId, messageId, operationId, day);
                    } else if (operation.equals(Command.REMOVE_PARTICIPANT.getCommand())) {
                        deleteParticipantFromQueue(chatId, messageId, operationId, participant);
                    }
                }
            } catch (NumberFormatException | ObjectNotFoundException e) {
                logger.debug(e.getMessage());
            }
        }
    }

    private void deleteParticipantFromQueue(long chatId, long messageId, long queueId, Participant participant) {

        Queue queue = queueDAO.getQueueById(queueId);

        Participant removedParticipant = queue.getParticipant();
        if (configuration.getTelegram().getModerators().contains(removedParticipant.getChatId())) {
            answerCallback(chatId, messageId, "Ти не можеш видаляти модераторів!");
            return;
        }

        queueDAO.changeParticipantStatus(queueId, Status.REMOVED.getStatus() + " " + Status.REMOVED.getEmoji());

        // notify removed participants about changes
        sendSimpleMessage(removedParticipant.getChatId(), "Тебе було видалено модератором з черги '" + queue.getSchedule().getSubject().getName() + "'\uD83D\uDE33");

        answerCallback(chatId, messageId, "Я видалив учасника "
                + (removedParticipant.getTag() != null ? "@" + removedParticipant.getTag() : removedParticipant.getName())
                + " з черги '" + queue.getSchedule().getSubject().getName() + "'");
        participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
    }

    private void showQueueParticipantsToRemoveByScheduleIdCallback(long chatId, long messageId, long scheduleId, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(scheduleId, day);
        if (schedule != null) {
            List<Queue> queueList = queueDAO.getQueueList();
            if (!queueList.isEmpty()) {
                // sort queue by participants enter date
                queueList.sort(compareByEnterDate);

                Map<Long, String> queueParticipants = queueList.stream()
                        .filter(queue -> queue.getSchedule().getId() == scheduleId) // filter by required schedule
                        .collect(Collectors.toMap(Queue::getId, part -> {
                            String tag = part.getParticipant().getTag();
                            return (tag != null ? "@" + tag : part.getParticipant().getName());
                        }));

                answerCallbackWithInlineButtons(chatId, messageId, "\uD83D\uDEABОбери учасника якого ти хочеш виключити з черги\uD83D\uDEAB", queueParticipants);
                Participant participant = participantDAO.getParticipantByChatId(chatId);
                participantDAO.updateParticipantOperationStatus(participant.getId(), Command.REMOVE_PARTICIPANT.getCommand());
            } else {
                answerCallback(chatId, messageId, "В черзі '" + schedule.getSubject().getName() + "' нікого немає");
            }
        }
    }

    private void showQueueByScheduleIdCallback(long chatId, long messageId, long scheduleId, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(scheduleId, day);
        if (schedule != null) {
            List<Queue> queueList = queueDAO.getQueueList();
            if (!queueList.isEmpty()) {
                // sort queue by participants enter date
                queueList.sort(compareByEnterDate);

                List<String> queueParticipants = queueList.stream()
                        .filter(queue -> queue.getSchedule().getId() == scheduleId) // filter by required schedule
                        .map(part -> {
                            String tag = part.getParticipant().getTag();
                            return (tag != null ? "@" + tag : part.getParticipant().getName()) + " " + part.getStatus();
                        }) // map participant to string
                        .collect(Collectors.toList());

                if (!queueParticipants.isEmpty()) {
                    StringBuilder queue = new StringBuilder("Черга '" + schedule.getSubject().getName() + " " + schedule.getHour() + "':\n");
                    for (int i = 0; i < queueParticipants.size(); i++) {
                        queue.append(i + 1)
                                .append(". ")
                                .append(queueParticipants.get(i))
                                .append("\n");
                    }
                    answerCallback(chatId, messageId, queue.toString());
                } else {
                    answerCallback(chatId, messageId, "В черзі '" + schedule.getSubject().getName() + "' немає нікого\uD83D\uDC40, будь першим надіславши команду /queue");
                }
            } else {
                answerCallback(chatId, messageId, "В черзі '" + schedule.getSubject().getName() + "' немає нікого\uD83D\uDC40, будь першим надіславши команду /queue");
            }

            // update participant choice watch + schedule id
            Participant participant = participantDAO.getParticipantByChatId(chatId);
            participantDAO.updateParticipantOperationStatus(participant.getId(), Command.WATCH.getCommand() + " " + schedule.getId());
        }
    }

    private void removeParticipantFromQueueByScheduleIdCallback(long chatId, long messageId, long scheduleId, Participant participant, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(scheduleId, day);
        if (schedule != null) {

            // check if queue are ready to be opened
            if (isOpenToQueue(schedule)) {
                answerCallback(chatId, messageId, "Доступ до цієї черги закритий. Черга відкривається о " + new Time(schedule.getHour().getTime() - openTimeInMilliseconds));
                return;
            }

            // find all queues
            List<Queue> queueList = queueDAO.getQueueList();
            if (queueList != null) {
                // filter queue by schedule
                List<Queue> queueParticipants = queueList.stream()
                        .filter(queue -> queue.getSchedule().getId() == scheduleId)
                        .collect(Collectors.toList());

                if (!queueParticipants.isEmpty()) {
                    try {
                        // filter participant if participant in queue and not removed from this queue
                        Queue queueToRemove = filterParticipantToRemove(participant, queueParticipants);

                        if (isDequeued(queueToRemove) || isRemoved(queueToRemove)) {
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

        participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
    }

    private void addParticipantToQueueByScheduleIdCallback(long chatId, long messageId, long scheduleId, Participant participant, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(scheduleId, day);
        if (schedule != null) {

            // check if queue are ready to be opened
            if (isOpenToQueue(schedule)) {
                answerCallback(chatId, messageId, "Доступ до цієї черги закритий. Черга відкривається о " + new Time(schedule.getHour().getTime() - openTimeInMilliseconds));
                return;
            }

            List<Queue> queueList = filterQueueByScheduleIdAndParticipantId(scheduleId, participant.getId());

            // if participant didn't enter queue add to queue
            // else change status
            if (queueList.isEmpty()) {
                createQueueEntity(participant, schedule);
                answerCallback(chatId, messageId, "Я успішно додав тебе до черги " + schedule.getSubject().getName() + "\uD83D\uDC4C \n" +
                        "Щоб вийти з неї надішли /dequeue");
            } else {
                Queue queue = queueList.get(0);

                if (isRemoved(queue)) {
                    answerCallback(chatId, messageId, "Тебе було видалено модератором☹");
                    return;
                }

                // if participant already in queue
                if (queue.getStatus().equals(Status.QUEUE.getStatus() + " " + Status.QUEUE.getEmoji())) {
                    answerCallback(chatId, messageId, "Ти вже є в черзі '" + schedule.getSubject().getName() + "'");
                    return;
                }
                queueDAO.changeParticipantStatus(queue.getId(), Status.QUEUE.getStatus() + " " + Status.QUEUE.getEmoji());
                queueDAO.setNowTime(queue.getId());
                answerCallback(chatId, messageId, "Я відновив тебе в черзі '" + schedule.getSubject().getName() + "'");
            }
        } else {
            answerCallback(chatId, messageId, "Такої події на сьогодні немає\uD83E\uDD37\u200D♂");
        }

        participantDAO.updateParticipantOperationStatus(participant.getId(), Command.NONE.getCommand());
    }

    private void removeParticipantFromQueueByScheduleId(long chatId, long scheduleId, Participant participant, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(scheduleId, day);
        if (schedule != null) {

            // check if queue are ready to be opened
            if (isOpenToQueue(schedule)) {
                sendSimpleMessage(chatId, "Доступ до цієї черги закритий. Черга відкривається о " + new Time(schedule.getHour().getTime() - openTimeInMilliseconds));
                return;
            }

            // find all queues
            List<Queue> queueList = queueDAO.getQueueList();
            if (queueList != null) {
                // filter queue by schedule
                List<Queue> queueParticipants = queueList.stream()
                        .filter(queue -> queue.getSchedule().getId() == scheduleId)
                        .collect(Collectors.toList());
                if (!queueParticipants.isEmpty()) {
                    try {
                        // filter participant if participant in queue and not removed from this queue
                        Queue queueToRemove = filterParticipantToRemove(participant, queueParticipants);

                        if (isDequeued(queueToRemove) || isRemoved(queueToRemove)) {
                            sendSimpleMessage(chatId, "Ти не береш участь в черзі '" + schedule.getSubject().getName() + "'");
                            return;
                        }

                        queueDAO.changeParticipantStatus(queueToRemove.getId(), Status.DEQUEUE.getStatus() + " " + Status.DEQUEUE.getEmoji());
                        sendSimpleMessage(chatId, "Я видалив тебе з цієї черги\uD83D\uDC4D \n" +
                                "Щоб записатись у неї знову надішли /queue");
                    } catch (IndexOutOfBoundsException e) {
                        sendSimpleMessage(chatId, "Тебе немає в цій черзі\uD83D\uDE44");
                    }
                } else {
                    sendSimpleMessage(chatId, "Ця черга порожня\uD83D\uDEAB");
                }
            } else {
                sendSimpleMessage(chatId, "Ця черга порожня\uD83D\uDEAB");
            }
        } else {
            sendSimpleMessage(chatId, "Такої події на сьогодні немає\uD83E\uDD37\u200D♂");
        }
    }

    private void addParticipantToQueueByScheduleId(long chatId, long scheduleId, Participant participant, String day) {
        Schedule schedule = scheduleDAO.getScheduleByIdAndDay(scheduleId, day);
        if (schedule != null) {

            // check if queue are ready to be opened
            if (isOpenToQueue(schedule)) {
                sendSimpleMessage(chatId, "Доступ до цієї черги закритий. Черга відкривається о " + new Time(schedule.getHour().getTime() - openTimeInMilliseconds));
                return;
            }

            List<Queue> queueList = filterQueueByScheduleIdAndParticipantId(scheduleId, participant.getId());

            // if participant didn't enter queue add to queue
            // else change status
            if (queueList.isEmpty()) {
                createQueueEntity(participant, schedule);
                sendSimpleMessage(chatId, "Я успішно додав тебе до черги " + schedule.getSubject().getName() + "\uD83D\uDC4C \n" +
                        "Щоб вийти з неї надішли /dequeue");
            } else {
                Queue queue = queueList.get(0);

                if (isRemoved(queue)) {
                    sendSimpleMessage(chatId, "Тебе було видалено модератором☹");
                    return;
                }

                // if participant already in queue
                if (queue.getStatus().equals(Status.QUEUE.getStatus() + " " + Status.QUEUE.getEmoji())) {
                    sendSimpleMessage(chatId, "Ти вже є в черзі '" + schedule.getSubject().getName() + "'");
                    return;
                }
                queueDAO.changeParticipantStatus(queue.getId(), Status.QUEUE.getStatus() + " " + Status.QUEUE.getEmoji());
                queueDAO.setNowTime(queue.getId());
                sendSimpleMessage(chatId, "Я відновив тебе в черзі '" + schedule.getSubject().getName() + "'");
            }
        } else {
            sendSimpleMessage(chatId, "Такої події на сьогодні немає\uD83E\uDD37\u200D♂");
        }
    }

    private List<Queue> filterQueueByScheduleIdAndParticipantId(long scheduleId, long participantId) {
        return queueDAO.getQueueList().stream()
                .filter(queue -> queue.getSchedule().getId() == scheduleId) // filter queue by schedule
                .filter(queue -> Objects.equals(queue.getParticipant().getId(), participantId)) // filter by participant
                .collect(Collectors.toList());
    }

    private Queue filterParticipantToRemove(Participant participant, List<Queue> queueParticipants) {
        return queueParticipants.stream()
                .filter(queue -> Objects.equals(queue.getParticipant().getId(), participant.getId()))
                .collect(Collectors.toList())
                .get(0);
    }

    private void createQueueEntity(Participant participant, Schedule schedule) {
        Queue queue = new Queue();
        queue.setParticipant(participant);
        queue.setSchedule(schedule);
        queue.setStatus(Status.QUEUE.getStatus() + " " + Status.QUEUE.getEmoji());
        queue.setEnter_date(new Date());
        queueDAO.addToQueue(queue);
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
            if (operation.equals(Command.WATCH.getCommand())) {
                message = message +
                        "\n➖ ➖ ➖ ➖ ➖ ➖ ➖ ➖ ➖ ➖ ➖ ➖" +
                        "\nПісля вибору черги для перегляду можеш надіслати мені /queue або /dequeue, і я виконаю відповідну дію одразу\uD83C\uDF1A";
            }
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

    private boolean isOpenToQueue(Schedule schedule) {
        long scheduleTime = schedule.getHour().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.add(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
        calendar.add(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
        calendar.add(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        long currentTime = calendar.getTime().getTime();
        return currentTime < scheduleTime - openTimeInMilliseconds;
    }

    private boolean isRemoved(Queue queue) {
        return queue.getStatus().equals(Status.REMOVED.getStatus() + " " + Status.REMOVED.getEmoji());
    }

    private boolean isDequeued(Queue queueToRemove) {
        return queueToRemove.getStatus().equals(Status.DEQUEUE.getStatus() + " " + Status.DEQUEUE.getEmoji());
    }

    private boolean isModerator(long chatId) {
        return configuration.getTelegram().getModerators().contains(chatId);
    }
}
