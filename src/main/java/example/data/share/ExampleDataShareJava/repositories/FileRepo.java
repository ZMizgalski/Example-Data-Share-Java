package example.data.share.ExampleDataShareJava.repositories;


import example.data.share.ExampleDataShareJava.model.file.File;
import example.data.share.ExampleDataShareJava.payload.responses.file.FileModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepo extends MongoRepository<File, String> {
    Page<FileModel> findAllByUsername(String name, Pageable pageable);

    void deleteAllByUsername(String username);

    Boolean existsByUsername(String username);
}
