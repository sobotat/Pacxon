package pacxon.lib.api.entity;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import lombok.*;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import pacxon.lib.api.MapClient;
import pacxon.lib.api.NPCClient;

import java.util.Collections;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NPCEntity {

    private Long id;

    private int positionX;
    private int positionY;
    private String direction;
    private int spawnDelay;
    private int bonusSpeed;
    private String type;

    public static NPCClient getClient() {
        return JAXRSClientFactory.create("http://localhost:8080", NPCClient.class,
                Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE)
                        .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)),
                new HashMap<>(), true);
    }
}
