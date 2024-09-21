package nn.ru;

import nn.ru.entity.Dir;
import nn.ru.entity.Doc;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DocRepository {

    private static SessionFactory sessionFactory;
    private static Session session;
    private Transaction transaction;

    public SessionFactory getSessionFactory() {
        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Doc.class);
        configuration.addAnnotatedClass(Dir.class);

        sessionFactory = configuration.buildSessionFactory();
        return sessionFactory;
    }


    public Session getSession() {
        return sessionFactory.openSession();

    }

    public Transaction getTransaction() {
        return session.beginTransaction();
    }

    public void sessionClose() {
        session.close();
    }

    public void transactionCommit() {
        transaction.commit();
    }
}
