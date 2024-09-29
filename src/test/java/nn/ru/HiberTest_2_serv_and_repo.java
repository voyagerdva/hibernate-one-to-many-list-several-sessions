package nn.ru;

import nn.ru.entity.Dir;
import nn.ru.entity.Doc;
import org.junit.jupiter.api.*;


public class HiberTest_2_serv_and_repo {

    public static DocService1 docService = new DocService1();

    @BeforeAll
    static void setUpAll() {
        docService.createNewSessionFactory();
    }

    @BeforeEach
    void setUp() {
        docService.prepareAllBefore();
    }


// ------------------------------------------------------------

    @Test
    void test_add_100000docs_save_get_update_with_Service_and_Repository() {

        Dir dir = new Dir();
        docService.saveWithCommitAndClose(dir);

        // Вставляем 100,000 записей через generate_series
        docService.insertWithGenerateSeries(dir.getId());

        // ---------- 2-ая сессия - для вычитывания: ----------------------
        docService.beginNewSessionAndTransaction();

        Dir dir2 = docService.getEntity(dir);

        System.out.println(dir2.getDocs().get(99997));
        docService.commitAndClose();
//
        // ----------  3-ая сессия - для апдейта: ----------------------
        docService.beginNewSessionAndTransaction();
        Long idDocForManipulations = 99997L;
        Long idDocForUpdate = null;

        Dir dir3 = docService.getEntity(dir);
        Doc oldDoc = dir3.getDocs().get(Math.toIntExact(idDocForManipulations));

        for (Doc doc : dir3.getDocs()) {
            if (doc == oldDoc) {
                oldDoc.setTitle("Street");
            }
        }

        docService.updateEntity(dir3);
        docService.commitAndClose();


        // ----------  4-ая сессия - для delete doc[99997] : ----------------------
        docService.beginNewSessionAndTransaction();
        Doc docForDelete = null;

        Dir dir4 = docService.getEntity(dir);

        for (Doc doc : dir4.getDocs()) {
            if (doc.getTitle().equals("Street")) {
                docForDelete = doc;
            }
        }

        dir4.getDocs().remove(docForDelete); // Удалить doc из коллекции
        docService.deleteEntity(docForDelete); // Удалить doc из БД
        docService.commitAndClose();

        // ----------  5-ая сессия - для добавления новых 100000 doc: ----------------------

        // Вставляем еще 100,000 записей через generate_series
        docService.insertWithGenerateSeries(dir.getId());
        System.out.println("======================");

    }


// ------------------------------------------------------

    @AfterEach
    void tearDown() {
        docService.switchOffAfterEach();
    }


    // Выполняется один раз после всех тестов для закрытия фабрики сессий
    @AfterAll
    static void tearDownAll() {
        docService.switchOffAfterAll();
    }

}
