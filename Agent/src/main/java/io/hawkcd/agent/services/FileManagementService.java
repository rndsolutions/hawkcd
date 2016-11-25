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

package io.hawkcd.agent.services;

import io.hawkcd.agent.services.interfaces.IFileManagementService;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
    public String zipFiles(String zipFilePath, List<File> files, String filesRootPath, boolean includeRootPath) {

        String errorMessage = null;
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FAST);
        parameters.setEncryptionMethod(Zip4jConstants.ENC_NO_ENCRYPTION);
        parameters.setDefaultFolderPath(filesRootPath);
        parameters.setIncludeRootFolder(includeRootPath);

        try {
            ZipFile zipFile = new ZipFile(zipFilePath);

            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file.isFile()) {
                    zipFile.addFile(file, parameters);
                }
                if (file.isDirectory()) {
                    zipFile.addFolder(file, parameters);
                }
            }
        } catch (ZipException e) {
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    public void generateDirectory(File file) {
        file.getParentFile().mkdirs();
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
            errorMessage = e.getMessage();
        }

        return errorMessage;
    }

    @Override
    public String initiateFile(File file, InputStream stream, String filePath) {
        String errorMessage = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                errorMessage = e.getMessage();
                e.printStackTrace();
            }
        }

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
    public String deleteFilesInDirectory(String directoryPath) {
        String errorMessage = null;
        if ((directoryPath == null) || (directoryPath == "")) {
            return errorMessage = "Directory Path arguments is empty or null!";
        }

        File directory = new File(directoryPath);
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
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

        if (pattern.isEmpty() || pattern.equals("/") || pattern.equals("\\")) {
            pattern = "**";
        }

        pattern = this.normalizePath(pattern);

        return StringUtils.strip(pattern, "/");
    }

    @Override
    public List<File> getFiles(String rootPath, String wildCardPattern) {
        List<File> allFiles = new ArrayList<>();
        File file = new File(rootPath);
        if (file.isFile()) {
            allFiles.add(file);
            return allFiles;
        }

        if (!wildCardPattern.equals("")) {
            rootPath = rootPath.replace(wildCardPattern, "");
        }

        DirectoryScanner scanner = new DirectoryScanner();
        try {
            scanner.setBasedir(rootPath);
            scanner.setIncludes(new String[]{wildCardPattern});
            scanner.scan();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (wildCardPattern.equals("**")) {
            File directory = scanner.getBasedir();
            allFiles.add(directory);

            return allFiles;
        }

        String[] files = scanner.getIncludedFiles();
        for (String f : files) {
            allFiles.add(new File(rootPath, f));
        }

        return allFiles;
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

        return StringUtils.replace(filePath, "\\", "/");
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
}
