package example.data.share.ExampleDataShareJava.repositories;


import example.data.share.ExampleDataShareJava.model.content.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentRepo extends MongoRepository<Content, String> {
    Content findContentById(String id);
}
