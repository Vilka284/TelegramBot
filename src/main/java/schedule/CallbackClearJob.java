package schedule;

import dao.WatchCallbackDAO;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CallbackClearJob extends AbstractJob {

    private final WatchCallbackDAO watchCallbackDAO = WatchCallbackDAO.getInstance();

    @Override
    public void execute(JobExecutionContext context) {
        watchCallbackDAO.clearAllCallbacks();
        logger.info("Callback clear job finished successfully");
    }
}
