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

import io.hawkcd.utilities.SchemaValidator;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.TaskDefinitionService;
import io.hawkcd.services.interfaces.ITaskDefinitionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/task-definitions")
@Consumes("application/json")
@Produces("application/json")
public class TaskDefinitionController {
    private ITaskDefinitionService taskDefinitionService;
    private SchemaValidator schemaValidator;

    public TaskDefinitionController() {
        this.taskDefinitionService = new TaskDefinitionService();
        this.schemaValidator = new SchemaValidator();
    }

    public TaskDefinitionController(ITaskDefinitionService taskDefinitionService) {
        this.taskDefinitionService = taskDefinitionService;
        this.schemaValidator = new SchemaValidator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTaskDefinitions() {
        ServiceResult result = this.taskDefinitionService.getAll();
        return Response.status(Status.OK)
                .entity(result.getEntity())
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{taskDefinitionId}")
    public Response getTaskDefinitionById(@PathParam("taskDefinitionId") String taskDefinitionId) {
        ServiceResult result = this.taskDefinitionService.getById(taskDefinitionId);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return Response.status(Status.NOT_FOUND)
                    .entity(result.getMessage())
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.status(Status.OK)
                .entity(result.getEntity())
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewTaskDefinition(TaskDefinition taskDefinition) {
        String isValid = this.schemaValidator.validate(taskDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.taskDefinitionService.addTask(taskDefinition);
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
    public Response updateTaskDefinition(TaskDefinition taskDefinition) {
        String isValid = this.schemaValidator.validate(taskDefinition);
        if (isValid.equals("OK")) {
            ServiceResult result = this.taskDefinitionService.updateTask(taskDefinition);
            if (result.getNotificationType() == NotificationType.ERROR) {
                return Response.status(Status.BAD_REQUEST)
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

//    @DELETE
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("/{taskDefinitionId}")
//    public Response deleteTaskDefinition(@PathParam("taskDefinitionId") String taskDefinitionId) {
//        ServiceResult result = this.taskDefinitionService.delete(taskDefinitionId);
//        if (result.getNotificationType() == NotificationType.ERROR) {
//            return Response.status(Status.BAD_REQUEST)
//                    .entity(result.getMessage())
//                    .type(MediaType.TEXT_HTML)
//                    .build();
//        }
//        return Response.status(Status.NO_CONTENT)
//                .entity(result.getMessage())
//                .build();
//    }
}
