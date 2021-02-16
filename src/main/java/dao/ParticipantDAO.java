package dao;

import com.github.fluent.hibernate.transformer.FluentHibernateResultTransformer;
import entity.Participant;
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
        return session.load(Participant.class, chatId);
    }

    public List<Participant> getAllParticipants() {
        final Query query = session.createQuery("from Participant");
        return (List<Participant>) query
                .setResultTransformer(new FluentHibernateResultTransformer(Participant.class))
                .list();
    }

    public void removeParticipant(long id) {
        final Transaction transaction = session.beginTransaction();
        Participant participant = session.load(Participant.class, id);
        session.delete(participant);
        transaction.commit();
    }
}
