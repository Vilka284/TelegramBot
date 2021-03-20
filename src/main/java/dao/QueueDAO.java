package dao;

import entity.Queue;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.Date;
import java.util.List;

public final class QueueDAO {

    private static QueueDAO instance;
    private static Session session;

    public static QueueDAO getInstance() {
        session = HibernateUtil.currentSession();
        return instance != null ? instance : new QueueDAO();
    }

    public Queue getQueueById(long id) {
        return session.load(Queue.class, id);
    }

    public List<Queue> getQueueList() {
        final Query query = session.createQuery("from Queue");
        return (List<Queue>) query.list();
    }

    public void addToQueue(Queue queue) {
        final Transaction transaction = session.beginTransaction();
        try {
            session.save(queue);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public void removeFromQueue(long id) {
        final Transaction transaction = session.beginTransaction();
        try {
            Queue queue = getQueueById(id);
            session.delete(queue);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public void changeParticipantStatus(long id, String status) {
        final Transaction transaction = session.beginTransaction();
        try {
            Queue queue = getQueueById(id);
            queue.setStatus(status);
            session.save(queue);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public void setNowTime(long id) {
        final Transaction transaction = session.beginTransaction();
        try {
            Queue queue = getQueueById(id);
            queue.setEnter_date(new Date());
            session.save(queue);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public void clearQueue() {
        final Transaction transaction = session.beginTransaction();
        try {
            final Query query = session.createQuery("delete from Queue");
            query.executeUpdate();
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }
}
