package dao;

import entity.Schedule;
import org.hibernate.Query;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;

public final class ScheduleDAO {

    private static ScheduleDAO instance;
    private static Session session;

    public static ScheduleDAO getInstance() {
        session = HibernateUtil.currentSession();
        return instance != null ? instance : new ScheduleDAO();
    }

    public Schedule getScheduleById(long id) {
        return session.load(Schedule.class, id);
    }

    public Schedule getScheduleBySubjectId(long subjectId) {
        final Query query = session.createQuery("from Schedule s where s.subject.id = '" + subjectId + "'");
        return (Schedule) query.uniqueResult();
    }

    public List<Schedule> getScheduleList() {
        final Query query = session.createQuery("from Schedule");
        return (List<Schedule>) query.list();
    }

    public void addSchedule(Schedule schedule) {
        // TODO
    }
}
