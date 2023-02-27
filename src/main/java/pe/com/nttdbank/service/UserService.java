package pe.com.nttdbank.service;

import java.net.URISyntaxException;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.jboss.logging.Logger;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pe.com.nttdbank.model.User;
import pe.com.nttdbank.repository.UserRepository;

@ApplicationScoped
public class UserService implements HealthCheck {

    @Inject
    Logger logger;

    @Inject
    UserRepository userRepository;

    @Inject 
    ReactiveMongoClient mongoClient;

    @Transactional
    public User createUser(User user) throws URISyntaxException, Exception  { // [validate that this works]

        userRepository.persist(user);
        this.call();

        return user;
    };

    public Uni<List<User>> listAllUsers() {

        return getCollection().find()
                .map(doc -> {
                    User user = new User();
                    user.kindOfPerson = doc.getInteger("kindOfPerson");
                    user.identificationDocumentType = doc.getString("identificationDocumentType");
                    user.identificationDocumentNumber = doc.getString("identificationDocumentNumber");
                    user.name = doc.getString("name");
                    user.email = doc.getString("email");
                    user.phone = doc.getString("phone");
                    return user;
                }).collect().asList();
    };

    public User findUserById(String id) {

        // validate that the user exist (by the id)
        //if (userRepository.findByUserId(id) == null) {

        //    throw new NotFoundException("the user id " + id + " doesn't exist");  
        //}

        ObjectId userId = new ObjectId(id);

        return userRepository.findById(userId); 
    };

       
    @Transactional
    public User updateUser(String id, User user) { // [evaluate if this method is necessary because the card data should not change]

        user.userId = new ObjectId(id);

        if (userRepository.findById((new ObjectId(id))) != null) {

            // ogger.info("[RRM] valida que existe el usuario: " + userRepository.findById((new ObjectId(id))));

            user.userId = new ObjectId(id);

            userRepository.update(user);

            return user;
        }

        throw new NotFoundException("the user id " + user.userId + " doesn't exist");
    };

    @Transactional
    public void deleteUser(Integer id) { // [needs to implement]

    }

    @Override
    public HealthCheckResponse call() {

        return HealthCheckResponse.up("Service Ok");
    }

    private ReactiveMongoCollection<Document> getCollection(){
        return mongoClient.getDatabase("digital_wallet_system").getCollection("user");
    }
}
