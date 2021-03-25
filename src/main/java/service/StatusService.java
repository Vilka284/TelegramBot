package service;

import bot.Bot;
import bot.MessageSender;

import java.io.File;

import static config.Configuration.startTime;
import static java.lang.System.currentTimeMillis;

/*
    Send bot status (temperature of raspberry pi, work time, statistics. etc)
 */
public class StatusService {

    private final long millisToHour = 3600000;

    private final MessageSender messageSender = new MessageSender();
    private final CommandExecutorService commandExecutor = new CommandExecutorService();

    public void sendStatus(long chatId) {
        String temperature = "\uD83C\uDF21 Температура: " + commandExecutor.executeCommandWithResult("vcgencmd measure_temp");
        String workingTime = "\n⏱ Час роботи: " + (int) ((currentTimeMillis() - startTime) / millisToHour) + " годин.";
        String calledTimes = "\n\uD83D\uDD3B Викликано: " + Bot.calledTimes + " раз.";
        String answeredTimes = "\n\uD83D\uDD3A Відповів: " + Bot.answeredTimes + " раз.";
        messageSender.sendMessage(chatId, "✉️Статус бота ✉\n\n"
                + temperature
                + workingTime
                + calledTimes
                + answeredTimes);
    }

    public void sendLogs(long chatId) {
        String fileName = "logs.txt";
        String scriptName = "logs.sh";
        String path = System.getProperty("user.dir");
        String scriptPath = "/src/main/resources/scripts/";
        commandExecutor.executeCommand("/bin/bash " + path + scriptPath + scriptName);
        File file = new File(path + scriptPath + fileName);
        messageSender.sendFile(chatId, file, fileName);
    }
}