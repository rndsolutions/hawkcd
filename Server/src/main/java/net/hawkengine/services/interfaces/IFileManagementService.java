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

package net.hawkengine.services.interfaces;

import net.hawkengine.model.configuration.filetree.JsTreeFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface IFileManagementService {
    String unzipFile(String filePath, String destination);

    String streamToFile(InputStream stream, String filePath);

    List<File> getFiles(String rootPath, String wildCardPattern);

    String zipFiles(String zipFilePath, List<File> files, String filesRootPath, boolean includeRootPath);

    File generateUniqueFile(String filePath, String fileExtension);

    String deleteFile(String filePath);

    String deleteDirectoryRecursively(String directoryPath);

    String getRootPath(String fullPath);

    String getPattern(String rootPath, String fullPath);

    String pathCombine(String... args);

    String normalizePath(String filePath);

    String urlCombine(String... args);

    String getAbsolutePath(String path);

    JsTreeFile getFileNames(File parentDirectory);
}
