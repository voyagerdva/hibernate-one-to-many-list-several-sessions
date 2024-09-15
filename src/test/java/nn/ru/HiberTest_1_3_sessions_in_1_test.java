package nn.ru;

import nn.ru.entity.Dir;
import nn.ru.entity.Doc;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;


public class HiberTest_1_3_sessions_in_1_test {
    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    // Выполняется один раз перед всеми тестами
    @BeforeAll
    static void setUpAll() {
        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Doc.class);
        configuration.addAnnotatedClass(Dir.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    // Выполняется перед каждым тестом для открытия сессии и транзакции
    @BeforeEach
    void setUp() {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        // Создание таблиц перед тестом
        truncateTables();
    }


    // Выполняется после каждого теста для завершения транзакции, удаления таблиц и закрытия сессии
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

        // Удаление таблиц после теста
        Transaction transaction = session.beginTransaction();
        truncateTables();
        transaction.commit();

        if (session != null) {
            session.close();
        }
    }


    // Очистка таблиц
    private void truncateTables() {
        String truncateItemTable = "truncate dirs CASCADE";
        String truncateDocTable = "truncate docs CASCADE"; // Внешний ключ с каскадным удалением
        session.createNativeQuery(truncateDocTable).executeUpdate();
        session.createNativeQuery(truncateItemTable).executeUpdate();
    }

// ------------------------------------------------------------

    @Test
    void test_add_2docs_save_get() {

        Dir dir = new Dir();

        for (int i = 0; i < 10; i++) {
            Doc doc = new Doc("city");
            dir.addDocToItem(doc);
        }

        session.save(dir); // сейвы для doc1-2 нужны только если НЕ выставлен cascade=ALL. Если выставлен, то можно без них
        transaction.commit();
        session.close();

        // 2-ая сессия - для вычитывания:
        Session session2 = sessionFactory.openSession();
        Transaction transaction2 = session2.beginTransaction();

        Dir dir2 = session2.get(Dir.class, dir.getId());
        System.out.println(dir2);
        System.out.println(dir2.getDocs());
        transaction2.commit();
        session2.close();

        // 3-ая сессия - для апдейта:
        Session session3 = sessionFactory.openSession();
        Transaction transaction3 = session3.beginTransaction();

        Dir dir3 = session3.get(Dir.class, dir2.getId());
        System.out.println(dir3.getDocs());
        Doc oldDoc = dir3.getDocs().get(7);


        for (Doc doc : dir3.getDocs()) {
            if (doc == oldDoc) {
                oldDoc.setTitle("Street");
                doc = oldDoc;

            }
        }

        session3.update(dir3);

        transaction3.commit();
        session3.close();




//        Doc doc1 = session.get(Doc.class, 1L);
//        Doc doc2 = session.get(Doc.class, 2L);
//        Dir dir = session.get(Dir.class, 7L);
//        dir.addDocToItem(doc1);
//        dir.addDocToItem(doc2);
//        session.save(dir);
//        System.out.println(dir);




        // можно, конечно, использовать поля класса - session, transaction, но хочется для наглядности
        // ввести session3, transaction2 - новые локальные имена внутри метода, чтобы видно было
        // как перекидывается контекст между одной и второй сессиями, транзакциями и кто в какой момент
        // еще открыт, кто где требует закрытия и т.д.


    }

// ------------------------------------------------------

    @Test
    void test_add_2docs_and_get() {
        Doc doc1 = session.get(Doc.class, 1L);
        Doc doc2 = session.get(Doc.class, 2L);

        Dir dir = session.get(Dir.class, 7L);

        System.out.println("\n\n\n\n\n\n\n==================");
        System.out.println("\n" + doc1 + "\n" + doc2);
        System.out.println("\n\n" + dir);
        System.out.println("\n==================\n\n\n\n\n\n\n");
    }

    @Test
    void test_add_2docs_and_update() {
        Doc doc1 = session.get(Doc.class, 1L);
        Doc doc2 = session.get(Doc.class, 2L);

        Dir dir = session.get(Dir.class, 7L);


        dir.addDocToItem(doc1);
        dir.addDocToItem(doc2);

        session.save(dir);

        System.out.println(dir);
    }

    @Test
    void test_delete_doc1() {
        Doc doc1 = session.get(Doc.class, 2L);
        session.delete(doc1);

        Dir dir = session.get(Dir.class, 7L);
        System.out.println(dir);
    }

    @Test
    void test_delete_doc1_doc2() {
        Doc doc1 = session.get(Doc.class, 3L);
        Doc doc2 = session.get(Doc.class, 4L);

        session.delete(doc1);
        session.delete(doc2);

        Dir dir = session.get(Dir.class, 7L);

        System.out.println(dir);
    }

    @Test
    void test_delete_dir() {
        Dir dir = session.get(Dir.class, 11L);
        session.delete(dir);
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
