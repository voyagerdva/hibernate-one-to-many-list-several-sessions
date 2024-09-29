package nn.ru;

import nn.ru.entity.Dir;
import nn.ru.entity.Doc;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class DocRepository {

    private static SessionFactory sessionFactory;
    private static Session session;
    private Transaction transaction;

    //-------------------------------------------------------------

    public void createNewSessionFactory() {
        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Dir.class);
        configuration.addAnnotatedClass(Doc.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void createNewSession() {
        session = sessionFactory.openSession();
    }

    public Session getSession() {
        return session;
    }

    public void sessionClose() {
        session.close();
    }

    public void createNewTransaction() {
        transaction = session.beginTransaction();
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void transactionCommit() {
        transaction.commit();
    }

    public void insertWithGenerateSeries(Long idd) {

        if (getSession() != null || getTransaction() != null) { // так таботает
            createNewSession();
            createNewTransaction();
        }

        // Нативный SQL-запрос для вставки данных через generate_series()
        String sql = "INSERT INTO docs (title, dir_id) " +
                "SELECT 'Report ' || s, :idd " +
                "FROM generate_series(1, 100000) AS s";

        // Выполняем нативный запрос
        Query query = getSession().createNativeQuery(sql);
        query.setParameter("idd", idd);
        query.executeUpdate();

        // Завершаем транзакцию
        getTransaction().commit();
        getSession().close();
    }

    public void sessionSave(Dir dir) {
        getSession().save(dir);
        transaction.commit();
        session.close();
    }

    public void sessionFactoryClose() {
        getSessionFactory().close();
    }

    // Очистка таблиц
    public void truncateTables() {
        String truncateItemTable = "truncate dirs CASCADE";
        String truncateDocTable = "truncate docs CASCADE"; // Внешний ключ с каскадным удалением
        getSession().createNativeQuery(truncateDocTable).executeUpdate();
        getSession().createNativeQuery(truncateItemTable).executeUpdate();

        // Сброс автоинкремента для таблицы docs
        String resetDocIdSequence = "ALTER SEQUENCE docs_id_seq RESTART WITH 1";

        // Сброс автоинкремента для таблицы dirs
        String resetDirIdSequence = "ALTER SEQUENCE dirs_id_seq RESTART WITH 1";

        // Выполнение очистки и сброса последовательностей
        getSession().createNativeQuery(resetDocIdSequence).executeUpdate();
        getSession().createNativeQuery(resetDirIdSequence).executeUpdate();
    }

    public void beginSessionAndTransaction() {
        createNewSession();
        createNewTransaction();
    }

    public void switchOffAfterEach() {
        if (getSession() != null && getTransaction() != null) { // так таботает
            createNewSession();
            createNewTransaction();
        }

//        if (session.getSessionFactory().isClosed()) { // так не работает
//            session = sessionFactory.openSession();
//            transaction = session.beginTransaction();
//        }

        if (getTransaction() != null) {
            transactionCommit();
        }

        createNewTransaction();
        truncateTables();
        transactionCommit();

        if (getSession() != null) {
            sessionClose();
        }

    }

    public Dir getEntity(Dir dir) {
        return getSession().get(Dir.class, dir.getId());
    }

    public void updateEntity(Dir dir) {
        getSession().update(dir);
    }

    public void deleteEntity(Doc doc) {
        getSession().delete(doc);
    }
}
