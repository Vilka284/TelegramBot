package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class TelegramConfig {
    private BotConfig bot;
    private OwnerConfig owner;
    private List<Long> moderators;
}
