package pe.com.nttdbank.restclient;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import pe.com.nttdbank.model.CreditRestClient;

@RegisterRestClient
@Path("/credits")
public interface ICreditRestClient {

    @GET
    List<CreditRestClient> findAllCredits();
}
