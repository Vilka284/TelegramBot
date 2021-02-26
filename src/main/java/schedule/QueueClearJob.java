package schedule;

import dao.QueueDAO;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QueueClearJob extends AbstractJob {

    private final QueueDAO queueDAO = QueueDAO.getInstance();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        queueDAO.clearQueue();
        messageSender.sendMessage(configuration.getTelegram().getOwner().getChatId(), "✉️Чергу очищено ✉", true);
        logger.info("Queue clear job finished successfully");
    }
}
