package pacxon.lib.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import pacxon.lib.api.entity.BonusEntity;
import pacxon.lib.api.entity.NPCEntity;

import java.util.List;

@Path("/npc")
public interface NPCClient {

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    long createNPC(NPCEntity npc);

    @POST
    @Path("createAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String createNPCs(List<NPCEntity> npcs);

    @POST
    @Path("get")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    NPCEntity createNPC(long id);

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    List<NPCEntity> getNPCs();
}
