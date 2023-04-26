package pacxon.lib.api.entity;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import lombok.*;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import pacxon.lib.api.NPCClient;
import pacxon.lib.api.PlayerClient;

import java.util.Collections;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerEntity {

    private Long id;

    private int positionX;
    private int positionY;
    private int speed;

    public static PlayerClient getClient() {
        return JAXRSClientFactory.create("http://localhost:8080", PlayerClient.class,
                Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE)
                        .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)),
                new HashMap<>(), true);
    }
}
