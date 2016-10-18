package net.hawkengine.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.core.utilities.deserializers.TaskDefinitionAdapter;
import net.hawkengine.model.TaskDefinition;
import net.hawkengine.services.FileManagementService;
import net.hawkengine.services.interfaces.IFileManagementService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Path("/Artifacts/{pipelineName}")
public class ArtifactController {
    private IFileManagementService fileManagementService;
    private String basePath;
    private String outputFolder;
    private Gson jsonConverter;
    private File zipFile;

    public ArtifactController() {
        this.fileManagementService = new FileManagementService();
        this.basePath = System.getProperty("user.dir");
        this.outputFolder = this.basePath + File.separator + "Temp" + File.separator;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
        File tempFolder = new File(this.outputFolder);
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }
    }

    public ArtifactController(IFileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .create();
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.zipFile.delete();
    }

    @POST
    @Path("/{pipelineExecutionId}/upload-artifact")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unzipFile(@PathParam("pipelineName") String pipelineName,
                              @PathParam("pipelineExecutionId") String pipelineExecutionID,
                              @QueryParam("destination") String destination,
                              InputStream uploadedInputStream) {
        this.zipFile = new File(this.basePath + File.separator + "Temp" + File.separator + UUID.randomUUID() + ".zip");

        String errorMessage = this.fileManagementService.streamToFile(uploadedInputStream, this.zipFile.getPath());
        if (errorMessage != null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        String artifactsFolder = ServerConfiguration.getConfiguration().getArtifactsDestination();
        this.outputFolder = this.basePath + File.separator + artifactsFolder + File.separator + pipelineName + File.separator + pipelineExecutionID + File.separator + destination;

        errorMessage = this.fileManagementService.unzipFile(this.zipFile.getPath(), this.outputFolder);
        if (errorMessage != null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .build();
    }

    @Path("/{pipelineExecutionId}/fetch-artifact")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response zipFile(String directory) {
        directory = this.fileManagementService.normalizePath(directory);
        directory = this.basePath + File.separator + ServerConfiguration.getConfiguration().getArtifactsDestination() + File.separator + directory;
        String rootPath = this.fileManagementService.getRootPath(directory);
        String wildCardPattern = this.fileManagementService.getPattern(rootPath, directory);

        if (rootPath.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        List<File> files = this.fileManagementService.getFiles(rootPath, wildCardPattern);

        if (files == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        this.outputFolder = this.basePath + File.separator + "Temp" + File.separator;

        this.zipFile = this.fileManagementService.generateUniqueFile(this.outputFolder, "zip");

        String errorMessage = this.fileManagementService.zipFiles(zipFile.getPath(), files, rootPath, false);

        if (errorMessage != null) {
            this.zipFile.delete();

            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(zipFile)
                .build();
    }

    @Path("/{pipelineExecutionId}/{artifactSource:.*}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/force-download")
    public Response getArtifact(@PathParam("pipelineName") String pipelineName,
                                @PathParam("pipelineExecutionId") String pipelineExecutionID,
                                @PathParam("artifactSource") String artifactSource) {

        artifactSource = this.fileManagementService.normalizePath(artifactSource);

        String directory = this.basePath + File.separator + ServerConfiguration.getConfiguration().getArtifactsDestination() + File.separator + pipelineName + File.separator + pipelineExecutionID + File.separator + artifactSource;

        File fileToReturn = new File(directory);

        if (!fileToReturn.exists()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(fileToReturn)
                .build();
    }
}