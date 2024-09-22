package nn.ru;

import nn.ru.entity.Dir;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DocService1 {

    DocRepository repository = new DocRepository();

    public void insertWithGenerateSeries(Long id) {
        repository.insertWithGenerateSeries(id);
    }

    public void createNewSessionFactory() {
        repository.createNewSessionFactory();
    }

    public SessionFactory getSessionFactory() {
        return repository.getSessionFactory();
    }

    public void createNewSession() {
        repository.createNewSession();
    }

    public Session getSession() {
        return repository.getSession();
    }

    public void sessionClose() {
        repository.sessionClose();
    }

    public void createNewTransaction() {
        repository.createNewTransaction();
    }

    public Transaction getTransaction() {
        return repository.getTransaction();
    }

    public void transactionCommit() {
        repository.transactionCommit();
    }


    public void saveWithCommitAndClose(Dir dir) {
        repository.sessionSave(dir);
    }

    public void sessionFactoryClose() {
        repository.sessionFactoryClose();
    }

    public void cleanUp() {
        repository.truncateTables();
    }

    public void beginNewSessionAndTransaction() {
        repository.beginSessionAndTransaction();
    }

    public void prepareAllBefore() {
        beginNewSessionAndTransaction();
        cleanUp();
    }

    public void switchOffAfterEach() {
        repository.switchOffAfterEach();
    }

    public void switchOffAfterAll() {
        if (getSessionFactory() != null) {
            sessionFactoryClose();
        }

    }

    public Dir getEntity(Dir dir) {
        return repository.getEntity(dir);
    }
}