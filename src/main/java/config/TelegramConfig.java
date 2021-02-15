package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class TelegramConfig {
    private BotConfig bot;
    private OwnerConfig owner;
}
