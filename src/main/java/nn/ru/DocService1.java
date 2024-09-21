package nn.ru;

import org.hibernate.query.Query;

public class DocService1 {

    DocRepository docRepository = new DocRepository();

    public void insertDocsWithGenerateSeries(Long dirId) {

        // Нативный SQL-запрос для вставки данных через generate_series()
        String sql = "INSERT INTO docs (title, dir_id) " +
                "SELECT 'Report ' || s, :dirId " +
                "FROM generate_series(1, 100000) AS s";

        // Выполняем нативный запрос
        Query query = docRepository.getSession().createNativeQuery(sql);
        query.setParameter("dirId", dirId);
        query.executeUpdate();

        // Завершаем транзакцию
        docRepository.transactionCommit();
        docRepository.sessionClose();
    }
}