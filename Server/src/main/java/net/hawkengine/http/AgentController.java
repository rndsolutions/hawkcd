package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Job;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.Stage;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.ws.EndpointConnector;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/agents")
public class AgentController {
    private AgentService agentService;
    private SchemaValidator schemaValidator;
    private IPipelineService pipelineService;

    public AgentController() {
        this.agentService = new AgentService();
        this.schemaValidator = new SchemaValidator();
        this.pipelineService = new PipelineService();
    }

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
        this.schemaValidator = new SchemaValidator();
        this.pipelineService = new PipelineService();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAgents() throws Throwable {
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
                    .type(MediaType.TEXT_HTML)
                    .entity(result.getMessage())
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
        if (result.hasError()){
            return Response.status(Status.BAD_REQUEST)
                    .entity(result.getMessage())
                    .build();
        }
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

            return Response.status(Status.CREATED)
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
            EndpointConnector.passResultToEndpoint(AgentService.class.getSimpleName(), "update", result);
        }

        Pipeline pipeline = (Pipeline) this.pipelineService.getById(job.getPipelineId()).getObject();

        Stage stage = pipeline.getStages().stream().filter(s -> s.getId().equals(job.getStageId())).findFirst().orElse(null);

        List<Job> jobs = stage.getJobs();
        int lengthOfJobs = jobs.size();
        for (int i = 0; i < lengthOfJobs; i++) {
            Job currentJob = jobs.get(i);
            if (currentJob.getId().equals(job.getId())) {
                if (currentJob.getStatus() != JobStatus.CANCELED) {
                    jobs.set(i, job);
                    stage.setJobs(jobs);
                    break;
                }
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
    public Response updateAgent(Agent agent) {
        String isValid = this.schemaValidator.validate(agent);
        if (isValid.equals("OK")) {
            ServiceResult result = this.agentService.update(agent);
            if (result.hasError()) {
                return Response.status(Status.NOT_FOUND)
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
    @Path("/{agentId}/report")
    public Response reportAgent(Agent agent) {
        String isValid = this.schemaValidator.validate(agent);
        if (isValid.equals("OK")) {
            ServiceResult result = this.agentService.getById(agent.getId());
            Agent agentFromDb = (Agent) result.getObject();
            if (result.hasError()) {
                result = this.agentService.add(agent);

                return Response.status(Status.OK)
                        .entity(result.getObject())
                        .build();
            }

            agentFromDb.setHostName(agent.getHostName());
            agentFromDb.setLastReportedTime(agent.getLastReportedTime());
            agentFromDb.setOperatingSystem(agent.getOperatingSystem());
            agentFromDb.setIpAddress(agent.getIpAddress());
            agentFromDb.setRootPath(agent.getRootPath());
            agentFromDb.setRunning(agent.isRunning());
            agentFromDb.setName(agent.getName());

            result = this.agentService.update(agentFromDb);

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
        }
            return Response.status(Status.NO_CONTENT)
                    .build();
    }
}