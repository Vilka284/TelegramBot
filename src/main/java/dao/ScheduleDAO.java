package dao;

import entity.Schedule;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
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

    public Schedule getScheduleByIdAndDay(long id, String day) {
        final Query query = session.createQuery("from Schedule s where s.id = :id and s.day = :day");
        query.setLong("id", id);
        query.setString("day", day);
        return (Schedule) query.uniqueResult();
    }

    public Schedule getScheduleBySubjectId(long subjectId) {
        final Query query = session.createQuery("from Schedule s where s.subject.id = :id");
        query.setLong("id", subjectId);
        return (Schedule) query.uniqueResult();
    }

    public List<Schedule> getScheduleList() {
        final Query query = session.createQuery("from Schedule");
        return (List<Schedule>) query.list();
    }

    public void addSchedule(Schedule schedule) {
        final Transaction transaction = session.beginTransaction();
        session.save(schedule);
        transaction.commit();
    }

    public List<Schedule> getScheduleListByDay(String day) {
        final Query query = session.createQuery("from Schedule s where s.day = :day");
        query.setString("day", day);
        return (List<Schedule>) query.list();
    }
}
