import bot.Bot;
import dao.ParticipantDAO;
import dao.ScheduleDAO;
import entity.Participant;
import entity.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import config.ConfigurationMapper;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class Main {

    private static final ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final Logger logger = LoggerFactory.getLogger(Logger.class);
    private static ParticipantDAO participantDAO = ParticipantDAO.getInstance();

    public static void main(String[] args) {
        configurationMapper.run();
        Participant participant = new Participant();
        participant.setTag("crinitus_vulpi");
        participant.setChatId(0L);
        participant.setOperation("none");
        participantDAO.addParticipant(participant);
        /*try {
            TelegramBotsApi telegramBotApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }*/
    }
}
