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
    private final String relativeScriptsPath = "/src/main/resources/scripts/";
    private final String absoluteCurrentDirectoryPath = System.getProperty("user.dir");

    private final MessageSender messageSender = new MessageSender();
    private final CommandExecutorService commandExecutor = new CommandExecutorService();

    public void sendStatus(long chatId) {
        String temperature = "\uD83C\uDF21 Температура: " + getTemperature();
        String workingTime = "\n⏱ Час роботи: " + (int) ((currentTimeMillis() - startTime) / millisToHour) + " годин.";
        String calledTimes = "\n\uD83D\uDD3B Викликано: " + Bot.calledTimes + " раз.";
        String answeredTimes = "\n\uD83D\uDD3A Відповів: " + Bot.answeredTimes + " раз.";
        messageSender.sendMessage(chatId, "✉️Статус бота ✉\n\n"
                + temperature
                + workingTime
                + calledTimes
                + answeredTimes);
    }

    // This logs retrieving adapted for raspberry pi bot unit file
    // Use your personal logging method
    public void sendLogs(long chatId) {
        String fileName = "logs.txt";
        String scriptName = "logs.sh";
        commandExecutor.executeCommand("/bin/bash " + absoluteCurrentDirectoryPath + relativeScriptsPath + scriptName);
        File file = new File(absoluteCurrentDirectoryPath + relativeScriptsPath + fileName);
        messageSender.sendFile(chatId, file, fileName);
    }

    private String getTemperature() {
        String scriptName = "temp.sh";
        return commandExecutor.executeCommandWithResult("/bin/bash " + absoluteCurrentDirectoryPath + relativeScriptsPath + scriptName);

    }
}