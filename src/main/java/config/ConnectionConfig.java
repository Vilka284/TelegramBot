package config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class ConnectionConfig {
    private String url;
    private int poolSize;
    private String username;
    private String password;
}
