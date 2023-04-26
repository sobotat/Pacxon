package pacxon.lib.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pacxon.lib.api.entity.BonusEntity;
import pacxon.lib.api.entity.MapBonusEntity;

import java.util.List;

@Path("/map-bonus")
public interface MapBonusClient {

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    long createMapBonus(MapBonusEntity mapBonus);

    @POST
    @Path("createAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String createMapBonuses(List<MapBonusEntity> mapBonuses);

    @POST
    @Path("get")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    MapBonusEntity createMapBonus(long id);

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    List<MapBonusEntity> getMapBonuses();
}
