package net.hawkengine.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.model.payload.UploadArtifactInfo;
import net.hawkengine.services.FileManagementService;
import net.hawkengine.services.interfaces.IFileManagementService;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/Artifacts/{pipelineName}")
public class ArtifactController {
    private IFileManagementService fileManagementService;
    private String basePath;
    private String outputFolder;
    private Gson jsonConverter;

    public ArtifactController() {
        this.fileManagementService = new FileManagementService();
        this.basePath = System.getProperty("user.dir");
        this.outputFolder = this.basePath + File.separator + "Temp" + File.separator;
        this.fileManagementService.deleteDirectoryRecursively(this.outputFolder);
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
    }

    public ArtifactController(IFileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
    }

    @POST
    @Path("/{pipelineExecutionID}/upload-artifact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unzipFile(@PathParam("pipelineName") String pipelineName,
                              @PathParam("pipelineExecutionID") String pipelineExecutionID,
                              String uploadArtifactInfoAsString) {
        String artifactsFolder = ServerConfiguration.getConfiguration().getArtifactsDestination();
        UploadArtifactInfo uploadArtifactInfo = this.jsonConverter.fromJson(uploadArtifactInfoAsString, UploadArtifactInfo.class);
        if (uploadArtifactInfo.getDestination() != null){
            String destination = this.fileManagementService.normalizePath(uploadArtifactInfo.getDestination());
            this.outputFolder = destination + File.separator + pipelineName + File.separator + pipelineExecutionID;
        } else {
            this.outputFolder = this.basePath + File.separator + artifactsFolder + File.separator + pipelineName + File.separator + pipelineExecutionID;
        }

        String errorMessage = this.fileManagementService.unzipFile(uploadArtifactInfo.getZipFile().getPath(), this.outputFolder);

        if (errorMessage != null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.status(Response.Status.OK)
                .build();
    }

    @Path("/{stageName}/{jobName}/fetch-artifact")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response zipFile(String directory) {
        directory = ServerConfiguration.getConfiguration().getArtifactsDestination() + File.separator + directory;
        String fullPath = this.fileManagementService.getAbsolutePath(directory);
        String rootPath = this.fileManagementService.getRootPath(fullPath);
        String wildCardPattern = this.fileManagementService.getPattern(rootPath, fullPath);

        if (rootPath.isEmpty()) {

            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        File[] files = this.fileManagementService.getFiles(rootPath, wildCardPattern);

        if (files == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        this.outputFolder = this.basePath + File.separator + "Temp" + File.separator;

        File zipFile = this.fileManagementService.generateUniqueFile(this.outputFolder, "zip");

        String errorMessage = this.fileManagementService.zipFiles(zipFile.getPath(), files, rootPath, false);

        if (errorMessage != null) {
            zipFile.delete();

            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(zipFile)
                .build();
    }
}
