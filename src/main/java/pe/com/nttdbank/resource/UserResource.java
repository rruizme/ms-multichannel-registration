package pe.com.nttdbank.resource;

import java.net.URI;

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
import pe.com.nttdbank.model.User;
import pe.com.nttdbank.service.UserService;

@Path("/user")
public class UserResource {

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllUsers() {

        return Response.ok(userService.listAllUsers()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserById(@PathParam("id") Integer id) {

        User user = userService.findUserById(id);

        return user != null ? Response.ok(user).build()
                            : Response.status(Status.NOT_FOUND).build();
        // return Response.ok(user).build();
    }

    // [evaluate to use this method for some user field]
    //@GET
    //@Path("/search/{debitCardNumber}")
    //public Response search(@PathParam("debitCardNumber") String debitCardNumber) {
    //    User user = repository.findByDebitCardNumber(debitCardNumber);
    //    return user != null ? Response.ok(user).build()
    //            : Response.status(Status.NOT_FOUND).build();
    //}

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateUser(@PathParam("id") Integer id, User user) {

        user.userId = id;
        userService.updateUser(user);

        return Response.ok(user).build();

    }

    // @DELETE
    // @Path("/{id}")
    // public Response delete(@PathParam("id") String id) {
        // User user = userService.findById(id);
        // repository.delete(user);
        // return Response.noContent().build();
    // }
}
