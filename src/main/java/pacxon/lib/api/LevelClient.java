package pacxon.lib.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pacxon.lib.api.entity.LevelEntity;

import java.util.List;

@Path("/level")
public interface LevelClient {

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String createLevel(LevelEntity level);

    @POST
    @Path("createNew")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String createNewLevel(LevelEntity level);

    @POST
    @Path("get")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    LevelEntity getLevel(String id);

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    List<LevelEntity> getLevels();

    @GET
    @Path("/getCount")
    @Produces(MediaType.TEXT_PLAIN)
    int getNumberOfLevel();
}