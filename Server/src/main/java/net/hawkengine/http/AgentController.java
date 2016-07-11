package net.hawkengine.http;

import net.hawkengine.core.utilities.EndpointConnector;
import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.*;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/agents")
@Consumes("application/json")
@Produces("application/json")
public class AgentController {
    private AgentService agentService;
    private SchemaValidator schemaValidator;
    private IPipelineService pipelineService;

    public AgentController() {
        this.agentService = new AgentService();
        this.schemaValidator = new SchemaValidator();
        this.pipelineService = new PipelineService();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAgents() {
        ServiceResult result = this.agentService.getAll();
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{agentId}")
    public Response getById(@PathParam("agentId") String agentId) {
        ServiceResult result = this.agentService.getById(agentId);
        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND)
                    .entity(result)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{agentId}/work")
    public Response getWork(@PathParam("agentId") String agentId) {
        ServiceResult result = this.agentService.getWorkInfo(agentId);
        return Response.status(Status.OK)
                .entity(result.getObject())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAgent(Agent agent) {
        String isValid = this.schemaValidator.validate(agent);
        if (isValid.equals("OK")) {
            ServiceResult result = this.agentService.add(agent);
            if (result.hasError()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            }

            return Response.status(Status.OK)
                    .entity(result.getObject())
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/work")
    public Response addWork(Job job) {

        // TODO: Move logic into JobService
        if (job == null) {
            return Response.status(Status.OK).build();
        }

        if ((job.getStatus() == JobStatus.PASSED) || (job.getStatus() == JobStatus.FAILED)) {
            Agent agent = (Agent) this.agentService.getById(job.getAssignedAgentId()).getObject();
            agent.setRunning(false);
            agent.setAssigned(false);
            ServiceResult result = this.agentService.update(agent);
            EndpointConnector.passResultToEndpoint(this.getClass().getSimpleName(), "update", result);
        }

        Pipeline pipeline = (Pipeline) this.pipelineService.getById(job.getPipelineId()).getObject();

        Stage stage = pipeline.getStages().stream().filter(s -> s.getId().equals(job.getStageId())).findFirst().orElse(null);

        List<Job> jobs = stage.getJobs();
        int lengthOfJobs = jobs.size();
        for (int i = 0; i < lengthOfJobs; i++) {
            Job currentJob = jobs.get(i);
            if (currentJob.getId().equals(job.getId())) {
                jobs.set(i, job);
                stage.setJobs(jobs);
                break;
            }
        }

        List<Stage> stages = pipeline.getStages();
        int lengthOfStages = stages.size();
        for (int i = 0; i < lengthOfStages; i++) {
            Stage currentStage = stages.get(i);
            if (currentStage.getId().equals(stage.getId())) {
                stages.set(i, stage);
                pipeline.setStages(stages);
                break;
            }
        }

        this.pipelineService.update(pipeline);

        return Response.status(Status.OK).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{agentId}")
    public Response updateAgent(Agent agent) {
        String isValid = this.schemaValidator.validate(agent);
        if (isValid.equals("OK")) {
            ServiceResult result = this.agentService.update(agent);
            if (result.hasError()) {
                result = this.agentService.add(agent);
            }

            if (result.hasError()) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            } else {
                return Response.status(Status.OK)
                        .entity(result.getObject())
                        .build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{agentId}")
    public Response deleteAgent(@PathParam("agentId") String agentId) {
        ServiceResult result = this.agentService.delete(agentId);
        if (result.hasError()) {
            return Response.status(Status.NOT_FOUND)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        } else {
            return Response.status(Status.NO_CONTENT)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }
}