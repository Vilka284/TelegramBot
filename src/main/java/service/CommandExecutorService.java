package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandExecutorService {

    protected final Logger logger = LoggerFactory.getLogger(Logger.class);

    public String executeCommandWithResult(String command) {
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

    public void executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            lookForErrors(process);
            process.waitFor();
            process.destroy();
        } catch (Exception ignored) {
        }
    }

    private void lookForErrors(Process process) throws IOException {
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        // Read any errors from the attempted command
        String error = stdError.readLine();
        if (error != null) {
            logger.error("Command execution error: " + error);
            String s = null;
            while ((s = stdError.readLine()) != null) {
                logger.error(s);
            }
        }
    }
}