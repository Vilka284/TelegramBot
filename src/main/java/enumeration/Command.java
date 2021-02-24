package enumeration;

public enum Command {

    // participant commands
    START("/start", "Почни роботу зі мною цією командою\n", false, false),
    WATCH("/watch", "Дивитись чергу\n", true, false),
    QUEUE("/queue", "Записатись в чергу\n", true, false),
    DEQUEUE("/dequeue", "Вийти з черги", true, false),
    STOP("/stop", "Більше не писати тобі", false, false),
    HELP("/help", "", false, false),

    // moderator commands
    REMOVE("/remove", "Видаляй учасників з черги цією командою\n", true, true),
    BAN("/ban", "Видаляй учасників цією командою назавжди\n", true, true),

    NONE("none", "", false, false);

    private final String command;
    private final String help;
    private final boolean visible;
    private final boolean moderator;

    Command(String command, String help, boolean visible, boolean moderator) {
        this.help = help;
        this.command = command;
        this.visible = visible;
        this.moderator = moderator;
    }

    public String getCommand() {
        return command;
    }

    public String getHelp() {
        return help;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isModerator() {
        return moderator;
    }
}
