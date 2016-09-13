package net.hawkengine.services.interfaces;

import net.hawkengine.model.configuration.filetree.Directory;

import java.io.File;
import java.io.InputStream;

public interface IFileManagementService {
    String unzipFile(String filePath, String destination);

    String streamToFile(InputStream stream, String filePath);

    File[] getFiles(String rootPath, String wildCardPattern);

    String zipFiles(String zipFilePath, File[] files, String filesRootPath, boolean includeRootPath);

    File generateUniqueFile(String filePath, String fileExtension);

    String deleteFile(String filePath);

    String deleteDirectoryRecursively(String directoryPath);

    String getRootPath(String fullPath);

    String getPattern(String rootPath, String fullPath);

    String pathCombine(String... args);

    String normalizePath(String filePath);

    String urlCombine(String... args);

    String getAbsolutePath(String path);

    Directory getFileNames(File parentDirectory);

}
