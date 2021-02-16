package dao;

import com.github.fluent.hibernate.transformer.FluentHibernateResultTransformer;
import entity.Participant;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class ParticipantDAO {

    private static ParticipantDAO instance;

    public static ParticipantDAO getInstance() {
        return instance != null ? instance : new ParticipantDAO();
    }

    public void addParticipant(Participant participant) {
        final Session session = HibernateUtil.currentSession();
        final Transaction transaction = session.beginTransaction();
        session.save(participant);
        transaction.commit();
        session.close();
    }

    public Participant getParticipantById(long id) {
        final Session session = HibernateUtil.currentSession();
        Participant participant = session.load(Participant.class, id);
        session.close();
        return participant;
    }

    public Participant getParticipantByChatId(long chatId) {
        final Session session = HibernateUtil.currentSession();
        Participant participant = session.load(Participant.class, chatId);
        session.close();
        return participant;
    }

    public List<Participant> getAllParticipants() {
        final Session session = HibernateUtil.currentSession();
        final Query query = session.createQuery("from Participant");
        List<Participant> participants = query
                .setResultTransformer(new FluentHibernateResultTransformer(Participant.class))
                .list();
        session.close();
        return participants;
    }

    public void removeParticipant(long id) {
        final Session session = HibernateUtil.currentSession();
        final Transaction transaction = session.beginTransaction();
        Participant participant = session.load(Participant.class, id);
        session.delete(participant);
        transaction.commit();
        session.close();
    }
}
