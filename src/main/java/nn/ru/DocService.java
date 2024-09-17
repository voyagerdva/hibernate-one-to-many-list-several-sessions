package nn.ru;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class DocService {

    private SessionFactory sessionFactory;

    public DocService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void insertDocsWithGenerateSeries(Long dirId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        // Нативный SQL-запрос для вставки данных через generate_series()
        String sql = "INSERT INTO docs (title, dir_id) " +
                "SELECT 'Report ' || s, :dirId " +
                "FROM generate_series(1, 100000) AS s";

        // Выполняем нативный запрос
        Query query = session.createNativeQuery(sql);
        query.setParameter("dirId", dirId);
        query.executeUpdate();

        // Завершаем транзакцию
        transaction.commit();
        session.close();
    }
}