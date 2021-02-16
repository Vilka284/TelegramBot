package dao;

import entity.Schedule;
import entity.Subject;

import java.util.List;

public class ScheduleDAO {

    private static ScheduleDAO instance;

    public static ScheduleDAO getInstance() {
        return instance != null ? instance : new ScheduleDAO();
    }

    public Schedule getScheduleById(long id) {
        // TODO
        return null;
    }

    public Schedule getScheduleBySubject(Subject subject) {
        // TODO
        return null;
    }

    public List<Schedule> getScheduleList() {
        // TODO
        return null;
    }

    public void addSchedule(Schedule schedule) {
        // TODO
    }
}
