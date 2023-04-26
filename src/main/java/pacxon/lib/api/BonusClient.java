package pacxon.lib.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pacxon.lib.api.entity.BonusEntity;
import pacxon.lib.api.entity.MapEntity;

import java.util.List;

@Path("/bonus")
public interface BonusClient {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    long createBonus(BonusEntity bonus);

    @POST
    @Path("/createAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String createBonuses(List<BonusEntity> bonuses);

    @POST
    @Path("/get")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    BonusEntity createBonus(long id);

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    List<BonusEntity> getBonuses();
}
