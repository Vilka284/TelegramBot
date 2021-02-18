package schedule;

import dao.QueueDAO;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueClearJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(Logger.class);
    private final QueueDAO queueDAO = QueueDAO.getInstance();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        queueDAO.clearQueue();
        logger.info("Queue clear job finished successfully");
    }
}
