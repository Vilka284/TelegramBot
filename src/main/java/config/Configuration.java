package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.System.currentTimeMillis;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Configuration {

    public static final Long startTime = currentTimeMillis();

    private ConnectionConfig connection;
    private TelegramConfig telegram;

    @Override
    public String toString() {
        return "Configuration:\n" +
                "Connection: \n" + connection + "\n" +
                "Telegram: \n" + telegram + "\n";
    }
}
