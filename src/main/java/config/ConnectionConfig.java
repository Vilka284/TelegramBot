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
    private String dialect;
    private String driver;

    @Override
    public String toString() {
        return "\tURL: " + url + '\n' +
                "\tUsername: " + username + '\n' +
                "\tPassword: " + password + '\n' +
                "\tDialect: " + dialect + '\n' +
                "\tDriver: " + driver + '\n';
    }
}
