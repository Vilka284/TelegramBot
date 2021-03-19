package service;

import bot.Bot;
import bot.MessageSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static config.Configuration.startTime;
import static java.lang.System.currentTimeMillis;

/*
    Send bot status (temperature of raspberry pi, work time, statistics. etc)
 */
public class StatusService {

    private final MessageSender messageSender = new MessageSender();

    public void sendStatus(long chatId) {
        String temperature = "\uD83C\uDF21 Температура: " + executeCommand("vcgencmd measure_temp");
        String workingTime = "\n⏱ Час роботи: " + (int) ((currentTimeMillis() - startTime) / 3600000) + " годин.";
        String calledTimes = "\n\uD83D\uDD3B Викликано: " + Bot.calledTimes + " раз.";
        String answeredTimes = "\n\uD83D\uDD3A Відповів: " + Bot.answeredTimes + " раз.";
        messageSender.sendMessage(chatId, "✉️Статус бота ✉\n\n"
                + temperature
                + workingTime
                + calledTimes
                + answeredTimes);
    }

    private String executeCommand(String command) {
        StringBuilder message = new StringBuilder();
        try {
            String nextString;
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((nextString = reader.readLine()) != null)
                message.append(nextString).append("\n");
            process.waitFor();
            if (process.exitValue() != 0) {
                return "Помилка виконання команди\n";
            }
            process.destroy();
        } catch (Exception ignored) {
        }
        return message.toString();
    }
}
