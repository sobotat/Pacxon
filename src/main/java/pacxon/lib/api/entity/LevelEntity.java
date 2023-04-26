package pacxon.lib.api.entity;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import lombok.*;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import pacxon.lib.api.BonusClient;
import pacxon.lib.api.LevelClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LevelEntity {

    private String id;

    MapEntity map;
    PlayerEntity player;

    List<NPCEntity> npcs;

    List<MapBonusEntity> bonuses;

    public static LevelClient getClient() {
        return JAXRSClientFactory.create("http://localhost:8080", LevelClient.class,
                Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE)
                        .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)),
                new HashMap<>(), true);
    }
}
