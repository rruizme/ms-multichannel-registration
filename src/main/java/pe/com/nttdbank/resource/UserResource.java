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
import pe.com.nttdbank.model.User;
import pe.com.nttdbank.repository.UserRepository;
import pe.com.nttdbank.service.UserService;

@Path("/user-registration")
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
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<User>> list() {
        return userService.listAllUsers();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findUserById(@PathParam("id") String id) {

        User user = userService.findUserById(id);

        return user != null ? Response.ok(user).build()
                : Response.status(Status.NOT_FOUND).build();
        // return Response.ok(user).build();
    }

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
}
