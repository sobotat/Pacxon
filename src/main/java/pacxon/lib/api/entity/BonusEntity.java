package pacxon.lib.api.entity;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import lombok.*;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import pacxon.lib.api.BonusClient;
import pacxon.lib.api.MapClient;

import java.util.Collections;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BonusEntity {

    private Long id;

    private int time;
    private int spawnDelay;
    private int speed;
    private String type;

    public static BonusClient getClient() {
        return JAXRSClientFactory.create("http://localhost:8080", BonusClient.class,
                Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE)
                        .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)),
                new HashMap<>(), true);
    }
}
