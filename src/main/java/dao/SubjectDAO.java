package dao;

import entity.Subject;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public final class SubjectDAO {

    private static SubjectDAO instance;
    private static Session session;

    public static SubjectDAO getInstance() {
        session = HibernateUtil.currentSession();
        return instance != null ? instance : new SubjectDAO();
    }

    public void addSubject(Subject subject) {
        final Transaction transaction = session.beginTransaction();
        try {
            session.save(subject);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public Subject getSubjectById(long id) {
        return session.load(Subject.class, id);
    }

    public List<Subject> getAllSubjects() {
        final Query query = session.createQuery("from Subject");
        return (List<Subject>) query.list();
    }
}
