package dao;

import entity.Participant;
import entity.WatchCallback;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public final class WatchCallbackDAO {
    private static WatchCallbackDAO instance;
    private static Session session;

    public static WatchCallbackDAO getInstance() {
        session = HibernateUtil.currentSession();
        return instance != null ? instance : new WatchCallbackDAO();
    }

    public List<WatchCallback> getAllCallbacks() {
        final Query query = session.createQuery("from WatchCallback");
        return (List<WatchCallback>) query.list();
    }

    public void clearAllCallbacks() {
        final Transaction transaction = session.beginTransaction();
        final Query query = session.createQuery("delete from WatchCallback");
        query.executeUpdate();
        transaction.commit();
    }

    public WatchCallback getCallbackByParticipantId(long id) {
        final Query query = session.createQuery("from WatchCallback where participant.id = :id");
        query.setLong("id", id);
        return (WatchCallback) query.uniqueResult();
    }

    public void addWatchCallback(WatchCallback callback) {
        final Transaction transaction = session.beginTransaction();
        session.save(callback);
        transaction.commit();
    }

    public void updateWatchCallback(WatchCallback callback) {
        final Transaction transaction = session.beginTransaction();
        session.update(callback);
        session.flush();
        transaction.commit();
    }
}
