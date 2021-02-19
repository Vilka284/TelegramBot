package enumeration;

public enum Command {

    START("/start", "Почни роботу зі мною цією командою\n", false),
    WATCH("/watch", "Дивитись чергу\n", true),
    QUEUE("/queue", "Записатись в чергу\n", true),
    DEQUEUE("/dequeue", "Вийти з черги", true),
    HELP("/help", "", false),
    NONE("none", "", false);

    private final String command;
    private final String help;
    private final boolean visible;

    Command(String command, String help, boolean visible) {
        this.help = help;
        this.command = command;
        this.visible = visible;
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
}
