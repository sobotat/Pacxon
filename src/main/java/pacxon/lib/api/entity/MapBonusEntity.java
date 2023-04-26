package pacxon.lib.api.entity;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import lombok.*;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import pacxon.lib.api.BonusClient;
import pacxon.lib.api.MapBonusClient;

import java.util.Collections;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MapBonusEntity {

    private Long id;

    private int positionX;
    private int positionY;

    private BonusEntity bonus;

    public static MapBonusClient getClient() {
        return JAXRSClientFactory.create("http://localhost:8080", MapBonusClient.class,
                Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE)
                        .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)),
                new HashMap<>(), true);
    }
}
