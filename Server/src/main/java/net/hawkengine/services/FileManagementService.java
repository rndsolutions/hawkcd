package net.hawkengine.services;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.model.configuration.filetree.JsTreeFile;
import net.hawkengine.services.interfaces.IFileManagementService;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileManagementService implements IFileManagementService {

    @Override
    public String zipFiles(String zipFilePath, File[] files, String filesRootPath, boolean includeRootPath) {

        String errorMessage = null;
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FAST);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_NO_ENCRYPTION);
        parameters.setDefaultFolderPath(filesRootPath);
        parameters.setIncludeRootFolder(includeRootPath);

        try {
            ZipFile zipFile = new ZipFile(zipFilePath);

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile()) {
                    zipFile.addFile(file, parameters);
                }
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }

        return errorMessage;
    }

    @Override
    public File generateUniqueFile(String filePath, String fileExtension) {

        File directory = new File(filePath);

        if (!directory.exists()) {
            directory.mkdirs();
        }
        String zipFileName = UUID.randomUUID() + "." + fileExtension;
        String zipFilePath = Paths.get(filePath, zipFileName).toString();
        zipFilePath = this.normalizePath(zipFilePath);
        File file = new File(zipFilePath);

        return file;
    }

    @Override
    public String unzipFile(String filePath, String destination) {

        String errorMessage = null;
        try {
            ZipFile zipFile = new ZipFile(filePath);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    @Override
    public String streamToFile(InputStream stream, String filePath) {

        String errorMessage = null;
        try {
            byte[] bytes = IOUtils.toByteArray(stream);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    @Override
    public String deleteFile(String filePath) {
        String errorMessage = null;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        return errorMessage;
    }

    @Override
    public String deleteDirectoryRecursively(String directoryPath) {

        String errorMessage = null;

        File directory = new File(directoryPath);

        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            errorMessage = e.getMessage();
            return errorMessage;
        }

        return errorMessage;
    }

    @Override
    public String getRootPath(String path) {

        String rootPath = path;

        rootPath = this.normalizePath(path);

        File file = new File(rootPath);

        int wildCardCharIndex = path.indexOf('*');

        if (wildCardCharIndex != -1) {
            rootPath = path.substring(0, wildCardCharIndex - 1);
            return rootPath;
        }

        if (file.isFile()) {
            rootPath = file.getAbsolutePath().replace(file.getName(), "");
            FilenameUtils.normalizeNoEndSeparator(rootPath);
            return rootPath;
        }

        if (file.isDirectory()) {
            rootPath = file.getAbsolutePath();
            return rootPath;
        }

        return "";
    }

    @Override
    public String getPattern(String rootPath, String path) {

        String pattern = path.replace(rootPath, "");

        if (pattern.isEmpty()) {
            pattern = "**";
        }

        pattern = this.normalizePath(pattern);

        return StringUtils.strip(pattern, "/");
    }

    @Override
    public File[] getFiles(String rootPath, String wildCardPattern) {

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(this.normalizePath(rootPath));
        scanner.setIncludes(new String[]{wildCardPattern});
        scanner.scan();
        String[] allPaths = scanner.getIncludedFiles();

        List<File> allFiles = new ArrayList<File>();
        String[] directories = scanner.getIncludedDirectories();
        for (String directory : directories) {
            allFiles.add(new File(rootPath, directory));
        }

        for (int i = 0; i < allPaths.length; i++) {
            File file = new File(rootPath, allPaths[i]);
            if (!allFiles.contains(file.getParentFile())) {
                allFiles.add(file);
            }
        }

        if (allFiles.size() == 0) {
            return null;
        }
        return allFiles.toArray(new File[]{});
    }

    @Override
    public String pathCombine(String... args) {
        String output = "";
        for (String arg : args) {
            arg = this.normalizePath(arg);
            output = FilenameUtils.concat(output, arg);
        }
        FilenameUtils.normalizeNoEndSeparator(output);

        return output;
    }

    @Override
    public String normalizePath(String filePath) {

        filePath = StringUtils.replace(filePath, "/", File.separator);
        return StringUtils.replace(filePath, "\\", File.separator);
    }

    @Override
    public String urlCombine(String... args) {

        String output = null;

        StringBuilder argsHolder = new StringBuilder();
        for (String arg : args) {
            arg = this.normalizePath(arg);
            argsHolder.append(arg);
            argsHolder.append("/");
        }
        output = argsHolder.toString();
        output = StringUtils.stripEnd(output, "/");

        return output;
    }

    @Override
    public String getAbsolutePath(String path) {

        String rootPath = this.normalizePath(path);

        File file = new File(rootPath);

        if (file.isFile()) {
            rootPath = file.getAbsolutePath();
            FilenameUtils.normalizeNoEndSeparator(rootPath);
            return rootPath;
        }

        if (file.isDirectory()) {
            rootPath = file.getAbsolutePath();
            return rootPath;
        }

        return "";
    }

    @Override
    public JsTreeFile getFileNames(File parentDirectory) {
        JsTreeFile directory = new JsTreeFile();

        directory.setText(parentDirectory.getName());
        directory.setType("folder");
        String directoryPath = parentDirectory.getAbsolutePath().substring(System.getProperty("user.dir").length() + 1);
        directory.setPath(directoryPath);

        List<JsTreeFile> childs = new ArrayList<>();

        File[] files = parentDirectory.listFiles();

        Boolean hasArtifacts = files == null ? false : true;

        if(hasArtifacts){
            for (File file : files){
                if(file.isDirectory()){
                    childs.add(this.getFileNames(file));
                } else {
                    JsTreeFile currentFile = new JsTreeFile();
                    currentFile.setText(file.getName());
                    currentFile.setType("file");
                    String filePath = parentDirectory.getAbsolutePath().substring(System.getProperty("user.dir").length() + 1);
                    currentFile.setPath(filePath);
                    childs.add(currentFile);
                }
            }
        }

        directory.setChildren(childs);
        return directory;
    }
}
