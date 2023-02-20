package pe.com.nttdbank.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pe.com.nttdbank.model.User;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {

}
