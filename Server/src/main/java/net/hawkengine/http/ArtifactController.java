package net.hawkengine.http;

import net.hawkengine.services.FileManagementService;
import net.hawkengine.services.interfaces.IFileManagementService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Path("/Artifacts/{pipelineName}/{stageName}/{jobName}")
public class ArtifactController {
    private IFileManagementService fileManagementService;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addZip(@PathParam("pipelineName") String pipelineName,
                           @PathParam("stageName") String stageName,
                           @PathParam("jobName") String jobName,
                           File zipFile) {

        String basePath = System.getProperty("user.dir");

        String outputFolder = basePath + "\\Artifacts\\" + pipelineName + "\\" + stageName + "\\" + jobName;
        byte[] buffer = new byte[1024];

        try {

            //create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {

                String fileName = zipEntry.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

//                System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }

                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();

//            System.out.println("Done");

        } catch (IOException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }
        return Response.status(Response.Status.OK)
                .build();
    }

    @Path("/fetch-artifact")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response createZip(String directory) {
        this.fileManagementService = new FileManagementService();

        String rootPath = this.fileManagementService.getRootPath(directory);
        String wildCardPattern = this.fileManagementService.getPattern(rootPath, directory);

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

        String basePath = System.getProperty("user.dir");

        String outputFolder = basePath + "\\Temp\\";

        File zipFile = this.fileManagementService.generateUniqueFile(outputFolder, "zip");

        String errorMessage = this.fileManagementService.zipFiles(zipFile.getPath(), files, rootPath, false);

            if (errorMessage != null) {
            zipFile.delete();
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.TEXT_HTML)
                        .build();
        }

        return Response.status(Response.Status.OK)
                .entity(zipFile)
                .build();
    }
}
