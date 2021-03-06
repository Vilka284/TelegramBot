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

    @Override
    public String toString() {
        return '\t' + bot.getUsername() + ": " + bot.getToken() + '\n'
                + "\tList of moderators: " + moderators.toString() + '\n';
    }
}
