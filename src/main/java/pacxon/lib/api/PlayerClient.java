package pacxon.lib.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pacxon.lib.api.entity.NPCEntity;
import pacxon.lib.api.entity.PlayerEntity;

import java.util.List;

@Path("/player")
public interface PlayerClient {

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    long createPlayer(PlayerEntity player);

    @POST
    @Path("get")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    PlayerEntity createPlayer(long id);

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    List<PlayerEntity> getPlayers();
}
