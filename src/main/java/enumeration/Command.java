package enumeration;

public enum Command {

    START("/start", "Почни роботу зі мною цією командою\n", true),
    WATCH("/watch", "Дивитись чергу\n", true),
    QUEUE("/queue", "Записатись в чергу\n", true),
    DEQUEUE("/dequeue", "Вийти з черги", true),
    HELP("/help", "", false);

    private final String command;
    private final String help;
    private final boolean show;

    Command(String command, String help, boolean show) {
        this.help = help;
        this.command = command;
        this.show = show;
    }

    public String getCommand() {
        return command;
    }

    public String getHelp() {
        return help;
    }

    public boolean getShow() {
        return show;
    }
}
