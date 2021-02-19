package dao;

import entity.Participant;
import entity.Schedule;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public final class ParticipantDAO {

    private static ParticipantDAO instance;
    private static Session session;

    public static ParticipantDAO getInstance() {
        session = HibernateUtil.currentSession();
        return instance != null ? instance : new ParticipantDAO();
    }

    public void addParticipant(Participant participant) {
        final Transaction transaction = session.beginTransaction();
        session.save(participant);
        transaction.commit();
    }

    public Participant getParticipantById(long id) {
        return session.load(Participant.class, id);
    }

    public Participant getParticipantByChatId(long chatId) {
        final Query query = session.createQuery("from Participant where chat_id = " + chatId);
        return (Participant) query.uniqueResult();
    }

    public Participant getParticipantByTag(String tag) {
        final Query query = session.createQuery("from Participant where tag = '" + tag + "'");
        return (Participant) query.uniqueResult();
    }

    public List<Participant> getAllParticipants() {
        final Query query = session.createQuery("from Participant");
        return (List<Participant>) query.list();
    }

    public void updateParticipantOperationStatus(long id, String operation) {
        Participant participant = session.load(Participant.class, id);
        participant.setOperation(operation);
    }

    public void removeParticipant(long id) {
        final Transaction transaction = session.beginTransaction();
        Participant participant = session.load(Participant.class, id);
        session.delete(participant);
        transaction.commit();
    }
}
