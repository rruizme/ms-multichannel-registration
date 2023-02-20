package pe.com.nttdbank.service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pe.com.nttdbank.model.AccountRestClient;
import pe.com.nttdbank.model.ClientRestClient;
import pe.com.nttdbank.model.DebitCardRestClient;
import pe.com.nttdbank.model.User;
import pe.com.nttdbank.repository.UserRepository;
import pe.com.nttdbank.restclient.IAccountRestClient;
import pe.com.nttdbank.restclient.IClientRestClient;
import pe.com.nttdbank.restclient.IDebitCardRestClient;

@ApplicationScoped
public class UserService {

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

        logger.info("[RRM] account.accountId: " + account.accountId);
        logger.info("[RRM] account.clientId: " + account.clientId);
        logger.info("[RRM] account.isMain: " + account.isMain);

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

    public List<User> listAllUsers() {

        logger.info("[RRM] findAllClients: " + clientRestClient.findAllClients());
        logger.info("[RRM] findAllDebitCards: " + debitCardRestClient.findAllDebitCards());

        return userRepository.listAll(Sort.by("userId"));
    };

    // public List<User> listAllActiveUsers() {

    //     return userRepository.list("state", Sort.by("a_userId"), 1);
    // }

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

        logger.info("[RRM] ingresa a updateUser de la capa Service");

        user.userId = new ObjectId(id);

        if (userRepository.findById((new ObjectId(id))) != null) {

            logger.info("[RRM] valida que existe el usuario: " + userRepository.findById((new ObjectId(id))));

            user.userId = new ObjectId(id);
            logger.info("[RRM] setea userId: " + user.userId);
            userRepository.update(user);
            logger.info("[RRM] realiza el update(user)");

            return user;
        }

        throw new NotFoundException("the user id " + user.userId + " doesn't exist");
    };


     /* [PRUEBA UPDATE] 
    @Transactional
    public User updateUser(User user) { // [evaluate if this method is necessary because the card data should not change]

        if (userRepository.findById((new ObjectId(user.userId.toString()))) != null) {

            User userToUpdate = userRepository.findById(new ObjectId(user.userId.toString()));
            userToUpdate.debitCardNumber = user.debitCardNumber;
            userToUpdate.debitCardDueDate = user.debitCardDueDate;
            userToUpdate.debitCardValidationCode = user.debitCardValidationCode;
            userToUpdate.identificationDocumentType = user.identificationDocumentType;
            userToUpdate.identificationDocumentNumber = user.identificationDocumentNumber;

    

            return userToUpdate;
        }

        throw new NotFoundException("the user id " + user.userId + " doesn't exist");
    };
    */


    @Transactional
    public void deleteUser(Integer id) { // [needs to implement]

    }

    public ClientRestClient findClient(List<ClientRestClient> clientList, String documentIdentity, String documentIdentityType) {

        logger.info("[RRM] findClient");

        ClientRestClient client = clientList.stream().filter(x -> x.documentIdentity.equalsIgnoreCase(documentIdentity)).collect(Collectors.toList()).get(0);

        logger.info("[RRM] obtuvo el cliente");

        if(client != null) {
            if(client.documentIdentityType.equalsIgnoreCase(documentIdentityType)){
                return client;
            }
        }

        return null;
    }

    public AccountRestClient findAccount(List<AccountRestClient> accountList, Integer clientId) {

        AccountRestClient account = accountList.stream().filter(x -> x.clientId == clientId && x.isMain.equals(true)).collect(Collectors.toList()).get(0);

        logger.info("[RRM] obtuvo la cuenta");

        if(account != null) {

            return account;
        }

        return null;
    }

    public DebitCardRestClient findDebitCard(List<DebitCardRestClient> debitCardList, Integer accountId, User user) {

        List<DebitCardRestClient> debitCardlistPrueba = debitCardRestClient.findAllDebitCards();

        logger.info("[RRM] count: " + debitCardlistPrueba.size());

        logger.info("[RRM] 0: " + debitCardlistPrueba.get(0).cardNumber);

        DebitCardRestClient debitCard = debitCardList.stream().filter(x -> x.accountId == accountId && x.cardNumber.equalsIgnoreCase(user.debitCardNumber) 
                                        && x.expirationDate.equalsIgnoreCase(user.debitCardDueDate) && x.cardValidationCode.equalsIgnoreCase(user.debitCardValidationCode)).collect(Collectors.toList()).get(0);

        logger.info("[RRM] obtuvo debit card");

        if(debitCard != null) {

            return debitCard;
        }

        return null;
    }

    /* 
    public boolean validateDebitCard(List<DebitCard> debitCardList, String cardNumber, String expirationDate, String cardValidationCode) {

        DebitCard debitCard = debitCardList.stream().filter(x -> x.documentIdentity.equalsIgnoreCase(documentIdentity)).collect(Collectors.toList()).get(0);

        if(client != null) {
            if(client.documentIdentityType.equalsIgnoreCase(documentIdentityType)){
                return true;
            }
        }

        return false;
    }
    */
}
