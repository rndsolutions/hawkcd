package net.hawkengine.agent.services.interfaces;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface IFileManagementService {

    void generateDirectory(File file);

    String unzipFile(String filePath, String destination);

    String initiateFile(File file,InputStream stream, String filePath);

    List<File> getFiles(String rootPath, String wildCardPattern);

    String zipFiles(String zipFilePath, List<File> files, String filesRootPath, boolean includeRootPath);

    File generateUniqueFile(String filePath, String fileExtension);

    String deleteFile(String filePath);

    String deleteFilesInDirectory(String directoryPath);

    String deleteDirectoryRecursively(String directoryPath);

    String getRootPath(String fullPath);

    String getPattern(String rootPath, String fullPath);

    String pathCombine(String... args);

    public String normalizePath(String filePath);

    public String urlCombine(String... args);
}
