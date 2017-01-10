/*
 * Copyright (C) 2016 R&D Solutions Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hawkcd.http;

import io.hawkcd.model.*;
import io.hawkcd.model.enums.JobStatus;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.TaskType;
import io.hawkcd.model.payload.JsTreeFile;
import io.hawkcd.services.AgentService;
import io.hawkcd.services.FileManagementService;
import io.hawkcd.services.PipelineService;
import io.hawkcd.services.interfaces.IFileManagementService;
import io.hawkcd.services.interfaces.IPipelineService;
import io.hawkcd.utilities.SchemaValidator;
import io.hawkcd.utilities.constants.ConfigurationConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/agents")
@Api(value = "/agents", description = "Web Services to browse entities")
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
                .entity(result.getEntity())
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
                .entity(result.getEntity())
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
                .entity(result.getEntity())
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
                    .entity(result.getEntity())
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
        Pipeline pipeline = (Pipeline) this.pipelineService.getById(job.getPipelineId()).getEntity();

        Stage stage = pipeline.getStages()
                    .stream()
                    .filter(s -> s.getId().equals(job.getStageId()))
                    .findFirst()
                    .orElse(null);

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
                pipeline.setArtifactsFileStructure(new ArrayList<>(Arrays.asList(artifactDirectory)));
            }

            Agent agent = (Agent) this.agentService.getById(job.getAssignedAgentId()).getEntity();
            agent.setAssigned(false);
            ServiceResult result = this.agentService.update(agent);
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
                    .entity(result.getEntity())
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
            Agent agentFromDb = (Agent) result.getEntity();
            if (result.getNotificationType() == NotificationType.ERROR) {
                agent.setLastReportedTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
                result = this.agentService.add(agent);

                return Response.status(Status.OK)
                        .entity(result.getEntity())
                        .build();
            }

            agentFromDb.setHostName(agent.getHostName());
            agentFromDb.setLastReportedTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            agentFromDb.setOperatingSystem(agent.getOperatingSystem());
            agentFromDb.setIpAddress(agent.getIpAddress());
            agentFromDb.setRootPath(agent.getRootPath());
            agentFromDb.setRunning(agent.isRunning());
            agentFromDb.setConnected(agent.isConnected());
            agentFromDb.setName(agent.getName());

            result = this.agentService.update(agentFromDb);

            if (result.getNotificationType() == NotificationType.ERROR) {
                return Response.status(Status.BAD_REQUEST)
                        .entity(result.getMessage())
                        .type(MediaType.TEXT_HTML)
                        .build();
            } else {

                return Response.status(Status.OK)
                        .entity(result.getEntity())
                        .build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST)
                    .entity(isValid)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
    }

//    @DELETE
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/{agentId}")
//    public Response deleteAgent(@PathParam("agentId") String agentId) {
////        ServiceResult result = this.agentService.delete(agentId);
////        if (result.getNotificationType() == NotificationType.ERROR) {
////            return Response.status(Status.NOT_FOUND)
////                    .entity(result.getMessage())
////                    .type(MediaType.TEXT_HTML)
////                    .build();
////        }
////        return Response.status(Status.NO_CONTENT)
////                .build();
//    }
}