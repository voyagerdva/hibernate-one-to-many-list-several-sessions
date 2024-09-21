package nn.ru;

import nn.ru.entity.Dir;
import nn.ru.entity.Doc;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;


public class HiberTest_2_serv_and_repo {

    private static SessionFactory sessionFactory;
    private static Session session;
    private static Transaction transaction;

    public static DocService1 docService = new DocService1();

    @BeforeAll
    static void setUpAll() {
        sessionFactory = docService.docRepository.getSessionFactory();
    }

    @BeforeEach
    void setUp() {
        session = docService.docRepository.getSession();
        transaction = docService.docRepository.getTransaction();

        truncateTables();
    }


    @AfterEach
    void tearDown() {

        if (session != null && transaction != null) { // так таботает
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
        }

//        if (session.getSessionFactory().isClosed()) { // так не работает
//            session = sessionFactory.openSession();
//            transaction = session.beginTransaction();
//        }

        if (transaction != null) {
            transaction.commit();
        }

        Transaction transaction = session.beginTransaction();
        truncateTables();
        transaction.commit();

        if (session != null) {
            docService.docRepository.sessionClose();
        }
    }


    // Очистка таблиц
    private void truncateTables() {
        String truncateItemTable = "truncate dirs CASCADE";
        String truncateDocTable = "truncate docs CASCADE"; // Внешний ключ с каскадным удалением
        session.createNativeQuery(truncateDocTable).executeUpdate();
        session.createNativeQuery(truncateItemTable).executeUpdate();

        // Сброс автоинкремента для таблицы docs
        String resetDocIdSequence = "ALTER SEQUENCE docs_id_seq RESTART WITH 1";

        // Сброс автоинкремента для таблицы dirs
        String resetDirIdSequence = "ALTER SEQUENCE dirs_id_seq RESTART WITH 1";

        // Выполнение очистки и сброса последовательностей
        session.createNativeQuery(resetDocIdSequence).executeUpdate();
        session.createNativeQuery(resetDirIdSequence).executeUpdate();
    }

// ------------------------------------------------------------

    @Test
    void test_add_100000docs_save_get_update_with_Service_and_Repository() {

        docService = new DocService1();

        Dir dir = new Dir();
        docService.docRepository.getSession().save(dir);

        docService.docRepository.transactionCommit();
        docService.docRepository.sessionClose();

        // Вставляем 100,000 записей через generate_series
        docService.insertDocsWithGenerateSeries(dir.getId());

        // ---------- 2-ая сессия - для вычитывания: ----------------------
        Session session2 = sessionFactory.openSession();
        Transaction transaction2 = session2.beginTransaction();

        Dir dir2 = session2.get(Dir.class, dir.getId());
        System.out.println(dir2.getDocs().get(99997));
        transaction2.commit();
        session2.close();

        // ----------  3-ая сессия - для апдейта: ----------------------
        Session session3 = sessionFactory.openSession();
        Transaction transaction3 = session3.beginTransaction();

        Dir dir3 = session3.get(Dir.class, dir2.getId());
        System.out.println();
        Doc oldDoc = dir3.getDocs().get(99997);
        System.out.println();

        Long idDocForDelete = null;

        for (Doc doc : dir3.getDocs()) {
            if (doc == oldDoc) {
                oldDoc.setTitle("Street");
                doc = oldDoc;
                idDocForDelete = doc.getId();
                System.out.println(idDocForDelete);
            }
        }

        session3.update(dir3);


        transaction3.commit();
        session3.close();


        // ----------  4-ая сессия - для delete doc[99997] : ----------------------
        Session session4 = sessionFactory.openSession();
        Transaction transaction4 = session4.beginTransaction();

        Dir dir4 = session4.get(Dir.class, dir2.getId());
        System.out.println(dir4.getDocs());
        Doc docForDelete = session4.get(Doc.class, idDocForDelete);

        // Удалить doc из коллекции
        dir4.getDocs().remove(docForDelete);
        session4.delete(docForDelete);


        transaction4.commit();
        session4.close();


        // ----------  5-ая сессия - для добавления новых 100000 doc: ----------------------


        // Вставляем еще 100,000 записей через generate_series
        docService.insertDocsWithGenerateSeries(dir.getId());
        System.out.println("======================");






        // можно, конечно, переиспользовать поля класса - session, transaction - для новых сессии и транзакции,
        // но хочется для наглядности ввести session3, transaction2 - новые локальные имена внутри метода, чтобы видно было
        // как перекидывается контекст между одной и второй сессиями, транзакциями и кто в какой момент
        // еще открыт, кто где требует закрытия и т.д.


    }


// ------------------------------------------------------

    // Выполняется один раз после всех тестов для закрытия фабрики сессий
    @AfterAll
    static void tearDownAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

}
