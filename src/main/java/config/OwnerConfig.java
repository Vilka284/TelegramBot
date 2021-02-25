package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class OwnerConfig {
    private String name;
    private Long chatId;
    private String tag;
}
