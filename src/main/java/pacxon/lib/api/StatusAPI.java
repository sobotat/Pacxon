package pacxon.lib.api;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import java.util.Collections;
import java.util.HashMap;

@Path("/api")
public interface StatusAPI {

    @GET
    @Path("/status")
    @Produces(MediaType.TEXT_PLAIN)
    boolean getStatus();

    static StatusAPI getClient() {
        return JAXRSClientFactory.create("http://localhost:8080", StatusAPI.class,
                Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE)
                        .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)),
                new HashMap<>(), true);
    }
}
