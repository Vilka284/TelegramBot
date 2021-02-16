package dao;

import entity.Participant;
import entity.Queue;
import entity.Schedule;

public class QueueDAO {

    private static QueueDAO instance;

    public static QueueDAO getInstance() {
        return instance != null ? instance : new QueueDAO();
    }

    public Queue getQueueBySchedule(Schedule schedule) {
        // TODO
        return null;
    }

    public void addToQueue(Schedule schedule, Participant participant) {
        // TODO
    }

    public void removeFromQueue(Schedule schedule, Participant participant) {
        // TODO
    }

    public void changeParticipantStatus(Schedule schedule, Participant participant, String status) {
        // TODO
    }

    public void clearQueue(Schedule schedule) {
        // TODO
    }
}
