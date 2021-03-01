package enumeration;

import java.util.Arrays;

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

    public static Day getDayById(int id) {
        return Arrays.stream(Day.values())
                .filter(day -> day.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No such day exists"));
    }
}
