package example.data.share.ExampleDataShareJava.repositories;

import example.data.share.ExampleDataShareJava.model.token.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepo extends MongoRepository<Token, String> {
    Token findByToken(String token);

    void deleteByEmail(String email);

    boolean existsByEmail(String resetPasswordEmail);

    Token findByEmail(String resetPasswordEmail);
}
