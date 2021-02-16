package enumeration;

public enum Status {

    QUEUE("В черзі", "\uD83D\uDFE2"),
    DEQUEUE("Не в черзі", "❌"),
    COMPLETE("Здав", "✅");

    private final String status;
    private final String emoji;

    Status(String status, String emoji) {
        this.status = status;
        this.emoji = emoji;
    }

    public String getStatus() {
        return status;
    }

    public String getEmoji() {
        return emoji;
    }
}
