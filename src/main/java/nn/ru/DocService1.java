package nn.ru;

import nn.ru.entity.Dir;
import nn.ru.entity.Doc;

public class DocService1 {

    DocRepository repository = new DocRepository();

    public void insertWithGenerateSeries(Long id) {
        repository.insertWithGenerateSeries(id);
    }

    public void createNewSessionFactory() {
        repository.createNewSessionFactory();
    }

    public void saveWithCommitAndClose(Dir dir) {
        repository.sessionSave(dir);
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
        if (repository.getSessionFactory() != null) {
            repository.sessionFactoryClose();
        }

    }

    public Dir getEntity(Dir dir) {
        return repository.getEntity(dir);
    }

    public void updateEntity(Dir dir) {
        repository.updateEntity(dir);
    }

    public void commitAndClose() {
        repository.transactionCommit();
        repository.sessionClose();
    }

    public void deleteEntity(Doc doc) {
        repository.deleteEntity(doc);
    }
}