package example.data.share.ExampleDataShareJava.repositories;


import example.data.share.ExampleDataShareJava.model.role.ERole;
import example.data.share.ExampleDataShareJava.model.role.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);

    Role findRoleByName(ERole name);
}
