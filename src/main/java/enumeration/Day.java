package enumeration;

public enum Day {
    SUNDAY(1, "Sun"),
    MONDAY(2, "Mon"),
    TUESDAY(3, "Tue"),
    WEDNESDAY(4, "Wed"),
    THURSDAY(5, "Thu"),
    FRIDAY(6, "Fri"),
    SATURDAY(7, "Sat");

    private int id;
    private String name;

    Day(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
