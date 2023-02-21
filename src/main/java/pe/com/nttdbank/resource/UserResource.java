package pe.com.nttdbank.resource;

import java.net.URI;
import java.util.List;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import pe.com.nttdbank.model.Login;
import pe.com.nttdbank.model.User;
import pe.com.nttdbank.repository.UserRepository;
import pe.com.nttdbank.service.UserService;

@Path("/user")
public class UserResource {

    @Inject
    Logger logger;

    @Inject
    UserRepository userRepository;

    @Inject
    UserService userService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createUser(User user) throws Exception {

        userService.createUser(user);

        return Response.created(new URI("/" + user.userId)).build();
    }

    @GET
    @Path("/findBydebitCard/{debitCardNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response findBydebitCardNumber(@PathParam("debitCardNumber") String debitCardNumber) {
        User user = this.userService.findBydebitCardNumber(debitCardNumber);

        return user != null ? Response.ok(user).build()
                : Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("/accesoApp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response AccesoApp(Login login) {

        boolean status = this.userService.AccesoApp(login.debitCardNumber, login.password);

        return Response.ok(status).status(Status.ACCEPTED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<User>> list() {
        return userService.listAllUsers();
    }

    /* 
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllUsers() {

        return Response.ok(userService.listAllUsers()).build();
    }
    */

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserById(@PathParam("id") String id) {

        User user = userService.findUserById(id);

        return user != null ? Response.ok(user).build()
                : Response.status(Status.NOT_FOUND).build();
        // return Response.ok(user).build();
    }

    // [evaluate to use this method for some user field]
    // @GET
    // @Path("/search/{debitCardNumber}")
    // public Response search(@PathParam("debitCardNumber") String debitCardNumber)
    // {
    // User user = repository.findByDebitCardNumber(debitCardNumber);
    // return user != null ? Response.ok(user).build()
    // : Response.status(Status.NOT_FOUND).build();
    // }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateUser(@PathParam("id") String id, User user) {

        logger.info("[RRM] userId: " + user.userId);
        user.userId = new ObjectId(id);
        logger.info("[RRM] userId: " + user.userId);
        userRepository.update(user);
        logger.info("[RRM] pasó");


        return Response.ok(user).build();
    }

    /* [PRUEBA UPDATE]
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateUser(@PathParam("id") String id, User user) {

        logger.info("[RRM] user.userId 1: " + user.userId);
        user.userId = new ObjectId(id);
        logger.info("[RRM] user.userId 2: " + user.userId);
        
        // User userToUpdate = userService.updateUser(user);
        userService.updateUser(user);

        logger.info("[RRM] user.debitCardDueDate: " + user.debitCardDueDate);
        logger.info("[RRM] user.debitCardNumber: " + user.debitCardNumber);
        logger.info("[RRM] user.debitCardValidationCode: " + user.debitCardValidationCode);
        logger.info("[RRM] user.identificationDocumentNumber: " + user.identificationDocumentNumber);
        logger.info("[RRM] user.identificationDocumentType: " + user.identificationDocumentType);

        return Response.ok(userService.updateUser(user)).build();

    }
    */

    // @DELETE
    // @Path("/{id}")
    // public Response deleteUser(@PathParam("id") String id) {
    // User user = userService.findById(id);
    // repository.delete(user);
    // return Response.noContent().build();
    // }
}
