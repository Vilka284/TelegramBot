package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Configuration {
    private ConnectionConfig connection;
    private TelegramConfig telegram;

    @Override
    public String toString() {
        return "Configuration:\n" +
                "Connection: \n" + connection + "\n" +
                "Telegram: \n" + telegram + "\n";
    }
}
