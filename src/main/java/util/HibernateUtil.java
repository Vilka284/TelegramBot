package util;

import config.ConfigurationHolder;
import config.ConnectionConfig;
import entity.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {

    public static final ThreadLocal<Session> session = new ThreadLocal<>();
    private static final SessionFactory sessionFactory;

    static {
        try {
            //sessionFactory = new Configuration().configure().buildSessionFactory();
            Configuration configuration = new Configuration();
            Properties settings = new Properties();
            ConnectionConfig connection = ConfigurationHolder.getConfiguration().getConnection();

            settings.put(Environment.DRIVER, connection.getDriver());
            settings.put(Environment.URL, connection.getUrl());
            settings.put(Environment.USER, connection.getUsername());
            settings.put(Environment.PASS, connection.getPassword());
            settings.put(Environment.DIALECT, connection.getDialect());

            settings.put(Environment.SHOW_SQL, "true");

            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

            settings.put(Environment.HBM2DDL_AUTO, "update");

            configuration.setProperties(settings);

            configuration.addAnnotatedClass(Participant.class);
            configuration.addAnnotatedClass(Subject.class);
            configuration.addAnnotatedClass(Schedule.class);
            configuration.addAnnotatedClass(Queue.class);
            configuration.addAnnotatedClass(WatchCallback.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException ex) {
            throw new RuntimeException("Configuration problem: " + ex.getMessage(), ex);
        }
    }

    public static Session currentSession() throws HibernateException {
        Session s = session.get();
        if (s == null) {
            s = sessionFactory.openSession();
            session.set(s);
        }
        return s;
    }

    public static void closeSession() throws HibernateException {
        Session s = session.get();
        session.set(null);
        if (s != null)
            s.close();
    }
}
