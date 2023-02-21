package pe.com.nttdbank.service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pe.com.nttdbank.model.AccountRestClient;
import pe.com.nttdbank.model.ClientRestClient;
import pe.com.nttdbank.model.CreditRestClient;
import pe.com.nttdbank.model.DebitCardRestClient;
import pe.com.nttdbank.model.ProductBalances;
import pe.com.nttdbank.model.User;
import pe.com.nttdbank.repository.UserRepository;
import pe.com.nttdbank.restclient.IAccountRestClient;
import pe.com.nttdbank.restclient.IClientRestClient;
import pe.com.nttdbank.restclient.IDebitCardRestClient;

@ApplicationScoped
public class UserService implements HealthCheck {

    @Inject
    Logger logger;

    @Inject
    UserRepository userRepository;

    @RestClient
    IClientRestClient clientRestClient;

    @RestClient
    IAccountRestClient accounttRestClient;

    @RestClient
    IDebitCardRestClient debitCardRestClient;

    @Inject 
    ReactiveMongoClient mongoClient;

    @Transactional
    public User createUser(User user) throws URISyntaxException, Exception  { // [validate that this works]

        if(findClient(clientRestClient.findAllClients(), user.identificationDocumentNumber, user.identificationDocumentType) == null){

            throw new NotFoundException("The identification document number " + user.identificationDocumentNumber + " doesn't exists");
        }

        ClientRestClient client = findClient(clientRestClient.findAllClients(), user.identificationDocumentNumber, 
                                             user.identificationDocumentType);

        if(findAccount(accounttRestClient.findAllAccounts(), client.clientId) == null) {

            throw new NotFoundException("The client " + client.clientId + " doesn't have an associated account");
        }

        AccountRestClient account = findAccount(accounttRestClient.findAllAccounts(), client.clientId);

        if(findDebitCard(debitCardRestClient.findAllDebitCards(), account.accountId, user) == null) {

            throw new NotFoundException("The debit card details aren't correct");
        }

        // validate that the user does not exist (by the id)
        if (user.userId != null) {

            throw new NotFoundException("the user id " + user.userId + " already exist");
        }

        // validate that the debit card is not registered yet [needs to implement calling the card microservice]
        if (userRepository.find("debitCardNumber", user.debitCardNumber).count() != 0) {

            throw new Exception("the debit card number " + user.debitCardNumber + " already exist"); 
        }

        // validate that the card is current and active [needs to implement]

        userRepository.persist(user);
        System.out.println("User created successfully."); // [improve this message, don't use printlb]

        return user;
    };

    public Uni<List<User>> listAllUsers() {

        return getCollection().find()
                .map(doc -> {
                    User user = new User();
                    user.debitCardNumber = doc.getString("debitCardNumber");
                    user.debitCardDueDate = doc.getString("debitCardDueDate");
                    user.debitCardValidationCode = doc.getString("debitCardValidationCode");
                    user.identificationDocumentType = doc.getString("identificationDocumentType");
                    user.identificationDocumentNumber = doc.getString("identificationDocumentNumber");
                    return user;
                }).collect().asList();

        // return userRepository.listAll(Sort.by("userId"));
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

    public ClientRestClient findClient(List<ClientRestClient> clientList, String documentIdentity, String documentIdentityType) {

        ClientRestClient client = clientList.stream().filter(x -> x.documentIdentity.equalsIgnoreCase(documentIdentity)).collect(Collectors.toList()).get(0);

        if(client != null) {
            if(client.documentIdentityType.equalsIgnoreCase(documentIdentityType)){
                return client;
            }
        }

        return null;
    }

    public AccountRestClient findAccount(List<AccountRestClient> accountList, Integer clientId) {

        AccountRestClient account = accountList.stream().filter(x -> x.clientId == clientId && x.isMain.equals(true)).collect(Collectors.toList()).get(0);

        if(account != null) {

            return account;
        }

        return null;
    }

    public DebitCardRestClient findDebitCard(List<DebitCardRestClient> debitCardList, Integer accountId, User user) {

        DebitCardRestClient debitCard = debitCardList.stream().filter(x -> x.accountId == accountId && x.cardNumber.equalsIgnoreCase(user.debitCardNumber) 
                                        && x.expirationDate.equalsIgnoreCase(user.debitCardDueDate) && x.cardValidationCode.equalsIgnoreCase(user.debitCardValidationCode)).collect(Collectors.toList()).get(0);

        if(debitCard != null) {

            return debitCard;
        }

        return null;
    }

    public User findBydebitCardNumber(String debitCardNumber) {
        User user = this.userRepository.find("debitCardNumber", debitCardNumber).firstResult();
        if (user == null) {
            throw new NotFoundException("USUARIO NO REGISTRADO");
        }
        return user;
    }

    public Boolean AccesoApp(String debitCardNumber, Integer password) {
        boolean status = false;
        User user = this.findBydebitCardNumber(debitCardNumber);
        if (password.toString().equals(user.password.toString())) {
            status = true;
            this.call();
        }
        return status;
    }

    @Override
    public HealthCheckResponse call() {

        return HealthCheckResponse.up("Service Ok");
    }

    /* 
    public List<CreditRestClient> findCreditsByClientId(Integer clientId, List<CreditRestClient> creditList) {

        List<CreditRestClient> auxCreditRestClientList = creditList.stream().filter(x -> x.clientId == clientId).collect(Collectors.toList());

        return auxCreditRestClientList;
    }
    */

    private ReactiveMongoCollection<Document> getCollection(){
        return mongoClient.getDatabase("multichannel_system").getCollection("user");
    }
}
