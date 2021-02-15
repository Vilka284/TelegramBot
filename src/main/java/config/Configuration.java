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
}
