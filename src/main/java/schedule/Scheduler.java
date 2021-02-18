package schedule;

import org.quartz.*;
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

            JobDetail jobDetail = newJob(QueueClearJob.class).withIdentity("QueueClearJob").build();

            Trigger trigger = newTrigger()
                    .withIdentity("QueueClearTrigger", "Queue")
                    .withSchedule(dailyAtHourAndMinute(0, 0))
                    .forJob(jobDetail)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
