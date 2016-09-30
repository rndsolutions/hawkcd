package net.hawkengine.http;

import net.hawkengine.core.utilities.SchemaValidator;
import net.hawkengine.core.utilities.constants.ConfigurationConstants;
import net.hawkengine.model.*;
import net.hawkengine.model.configuration.filetree.JsTreeFile;
import net.hawkengine.model.enums.JobStatus;
import net.hawkengine.model.enums.NotificationType;
import net.hawkengine.model.enums.TaskType;
import net.hawkengine.services.AgentService;
import net.hawkengine.services.FileManagementService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.interfaces.IFileManagementService;
import net.hawkengine.services.interfaces.IPipelineService;
import net.hawkengine.ws.EndpointConnector;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/agents")
public class AgentController {
    private AgentService agentService;
    private IFileManagementService fileManagementService;
    private SchemaValidator schemaValidator;
    private IPipelineService pipelineService;

    public AgentController() {
        this.agentService = new AgentService();
        this.schemaValidator = new SchemaValidator();
        this.pipelineService = new PipelineService();
        this.fileManagementService = new FileManagementService();
    }

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
        this.schemaValidator = new SchemaValidator();
        this.pipelineService = new PipelineService();
        this.fileManagementService = new FileManagementService();
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
        if (result.getNotificationType() == NotificationType.ERROR) {
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
        if (result.getNotificationType() == NotificationType.ERROR) {
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
            if (result.getNotificationType() == NotificationType.ERROR) {
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

        PipelineService.lock.lock();
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

        if ((job.getStatus() == JobStatus.PASSED) || (job.getStatus() == JobStatus.FAILED)) {
            boolean hasUploadArtifact = false;
            for (Task task : job.getTasks()) {
                if (task.getType() == TaskType.UPLOAD_ARTIFACT) {
                    hasUploadArtifact = true;
                    break;
                }
            }

            if (hasUploadArtifact) {
                String artifactsDirectory = System.getProperty("user.dir") + File.separator +
                        ConfigurationConstants.PROPERTY_ARTIFACTS_DESTINATION + File.separator +
                        pipeline.getPipelineDefinitionName() + File.separator + pipeline.getExecutionId();
                JsTreeFile artifactDirectory = this.fileManagementService.getFileNames(new File(artifactsDirectory));
                pipeline.setArtifactsFileStructure(new ArrayList<JsTreeFile>(Arrays.asList(artifactDirectory)));
            }

            Agent agent = (Agent) this.agentService.getById(job.getAssignedAgentId()).getObject();
            agent.setAssigned(false);
            ServiceResult result = this.agentService.update(agent);
            EndpointConnector.passResultToEndpoint(AgentService.class.getSimpleName(), "update", result);
        }

        this.pipelineService.update(pipeline);
        PipelineService.lock.unlock();

        return Response.status(Status.OK).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAgent(Agent agent) {
        String isValid = this.schemaValidator.validate(agent);
        if (isValid.equals("OK")) {
            ServiceResult result = this.agentService.update(agent);
            if (result.getNotificationType() == NotificationType.ERROR) {
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
            if (result.getNotificationType() == NotificationType.ERROR) {
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

            if (result.getNotificationType() == NotificationType.ERROR) {
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
        if (result.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.NOT_FOUND)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.status(Status.NO_CONTENT)
                .build();
    }
}