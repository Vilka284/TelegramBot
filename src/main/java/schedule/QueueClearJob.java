package schedule;

import bot.AbstractBot;
import dao.QueueDAO;
import dao.ScheduleDAO;
import entity.Queue;
import entity.Schedule;
import enumeration.Day;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class QueueClearJob extends AbstractJob {

    private final QueueDAO queueDAO = QueueDAO.getInstance();
    private final ScheduleDAO scheduleDAO = ScheduleDAO.getInstance();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String day = Day.getDayById(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).getName();
        List<Schedule> schedules = scheduleDAO.getScheduleListByDay(day);
        List<Queue> queueList = queueDAO.getQueueList();
        queueList.sort(AbstractBot.getQueueComparatorByEnterDate());
        // clear queue
        queueDAO.clearQueue();

        if (!schedules.isEmpty()) {
            StringBuilder message = new StringBuilder("✉ Чергу очищено ✉\n\n");

            for (Schedule schedule : schedules) {
                List<String> queueParticipants = queueList.stream()
                        .filter(queue -> queue.getSchedule().getId().equals(schedule.getId()))
                        .map(part -> {
                            String tag = part.getParticipant().getTag();
                            return (tag != null ? "@" + tag : part.getParticipant().getName()) + " " + part.getStatus();
                        }) // map participant to string
                        .collect(Collectors.toList());

                message.append("♻️Черга: '")
                        .append(schedule.getSubject().getName())
                        .append(" ")
                        .append(schedule.getHour())
                        .append("'♻\n");

                if (queueParticipants.size() > 0) {
                    for (int i = 0; i < queueParticipants.size(); i++) {
                        message.append(i + 1)
                                .append(". ")
                                .append(queueParticipants.get(i))
                                .append("\n");
                    }
                } else {
                    message.append("Порожня\uD83E\uDD37\u200D♂\n");
                }

                message.append("\n\n");
            }
            messageSender.sendMessage(configuration.getTelegram().getOwner().getChatId(), message.toString(), true);
        } else {
            messageSender.sendMessage(configuration.getTelegram().getOwner().getChatId(), "✉ Черга порожня ✉");
        }
        logger.info("Queue clear job finished successfully");
    }
}
