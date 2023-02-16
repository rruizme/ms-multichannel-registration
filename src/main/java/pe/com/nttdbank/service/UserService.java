package pe.com.nttdbank.service;

import java.net.URISyntaxException;
import java.util.List;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import pe.com.nttdbank.model.User;
import pe.com.nttdbank.repository.UserRepository;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public User createUser(User user) throws URISyntaxException, Exception  { // [validate that this works]

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

        return userRepository.listAll(Sort.by("userId"));
    };

    // public List<User> listAllActiveUsers() {

    //     return userRepository.list("state", Sort.by("a_userId"), 1);
    // }

    public User findUserById(Integer id) {

        // validate that the user exist (by the id)
        //if (userRepository.findByUserId(id) == null) {

        //    throw new NotFoundException("the user id " + id + " doesn't exist");  
        //}

        return userRepository.findById(id); 
    };

    @Transactional
    public User updateUser(User user) { // [evaluate if this method is necessary because the card data should not change]

        if (userRepository.findById((user.userId)) != null) {

            User userToUpdate = userRepository.findById(user.userId);
            userToUpdate.debitCardNumber = user.debitCardNumber;
            userToUpdate.debitCardDueDate = user.debitCardDueDate;
            userToUpdate.debitCardValidationCode = user.debitCardValidationCode;
            userToUpdate.identificationDocumentType = user.identificationDocumentType;
            userToUpdate.identificationDocumentNumber = user.identificationDocumentNumber;

            return user;
        }

        throw new NotFoundException("the user id " + user.userId + " doesn't exist");
    };

    @Transactional
    public void deleteUser(Integer id) { // [needs to implement]

    };
}
