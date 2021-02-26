package schedule;

import bot.MessageSender;
import config.Configuration;
import config.ConfigurationHolder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJob implements Job {

    public final Logger logger = LoggerFactory.getLogger(Logger.class);
    public final MessageSender messageSender = MessageSender.getInstance();
    public final Configuration configuration = ConfigurationHolder.getConfiguration();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

    }
}
