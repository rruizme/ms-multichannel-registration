package pe.com.nttdbank.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import pe.com.nttdbank.model.User;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<User, Integer> {

}
