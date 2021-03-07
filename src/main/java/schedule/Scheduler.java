package schedule;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public final class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Logger.class);

    public static void executeScheduling() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();

            org.quartz.Scheduler scheduler = schedulerFactory.getScheduler();

            JobDetail queueClearJob = newJob(QueueClearJob.class).withIdentity("QueueClearJob").build();
            JobDetail callbackClearJob = newJob(CallbackClearJob.class).withIdentity("CallbackClearJob").build();

            Trigger queueClearTrigger = newTrigger()
                    .withIdentity("QueueClearTrigger", "Queue")
                    .withSchedule(dailyAtHourAndMinute(23, 59))
                    .forJob(queueClearJob)
                    .build();

            Trigger callbackClearTrigger = newTrigger()
                    .withIdentity("CallbackClearTrigger", "Callback")
                    .withSchedule(dailyAtHourAndMinute(23, 59))
                    .forJob(callbackClearJob)
                    .build();

            scheduler.scheduleJob(queueClearJob, queueClearTrigger);
            scheduler.scheduleJob(callbackClearJob, callbackClearTrigger);

            scheduler.start();
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
