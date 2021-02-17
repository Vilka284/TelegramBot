package dao;

import entity.Queue;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

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

    public List<Queue> getAllQueues() {
        final Query query = session.createQuery("from Queue");
        return (List<Queue>) query.list();
    }

    public void addToQueue(Queue queue) {
        final Transaction transaction = session.beginTransaction();
        session.save(queue);
        transaction.commit();
    }

    public void removeFromQueue(long id) {
        final Transaction transaction = session.beginTransaction();
        Queue queue = getQueueById(id);
        session.delete(queue);
        transaction.commit();
    }

    public void changeParticipantStatus(long id, String status) {
        final Transaction transaction = session.beginTransaction();
        Queue queue = getQueueById(id);
        queue.setStatus(status);
        session.save(queue);
        transaction.commit();
    }

    public void clearQueue() {
        final Transaction transaction = session.beginTransaction();
        final Query query = session.createQuery("delete from Queue");
        query.executeUpdate();
        transaction.commit();
    }
}
