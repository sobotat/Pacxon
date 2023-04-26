package pacxon.lib;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import pacxon.lib.api.MapClient;
import pacxon.lib.api.entity.MapEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Log4j2
class MapTest {

    @Test
    public void test1(){
        List<MapEntity> maps = getMaps();

        for(MapEntity map: maps){
            log.info(map.toString());
        }
    }

    private static List<MapEntity> getMaps() {
        return getMapClient().getMaps();
    }

    private static MapClient getMapClient() {
        return JAXRSClientFactory.create("http://localhost:8080", MapClient.class,
                Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE)
                        .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)),
                new HashMap<>(), true);
    }

}