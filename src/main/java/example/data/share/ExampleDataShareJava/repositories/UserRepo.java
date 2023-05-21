package example.data.share.ExampleDataShareJava.repositories;


import example.data.share.ExampleDataShareJava.model.role.ERole;
import example.data.share.ExampleDataShareJava.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Query("{'roles': {$ne: ?0}}")
    Page<User> findByRolesNotIn(ERole role, Pageable pageable);

    User findUserById(String name);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    User findByEmail(String email);
}
