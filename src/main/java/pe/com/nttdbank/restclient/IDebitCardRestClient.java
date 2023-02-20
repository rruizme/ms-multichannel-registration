package pe.com.nttdbank.restclient;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import pe.com.nttdbank.model.DebitCardRestClient;

@RegisterRestClient
@Path("/debit-cards")
public interface IDebitCardRestClient {

    @GET
    List<DebitCardRestClient> findAllDebitCards();
}


