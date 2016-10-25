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

package net.hawkengine.agent.services;

import com.sun.jersey.api.client.Client;
import net.hawkengine.agent.services.interfaces.IFileManagementService;
import net.hawkengine.agent.services.interfaces.IMaterialService;
import net.hawkengine.agent.services.interfaces.ISecurityService;

public abstract class MaterialService implements IMaterialService {
    protected IFileManagementService fileManagementService;
    protected ISecurityService securityService;
    protected Client restClient;

    public MaterialService() {
        this.fileManagementService = new FileManagementService();
        this.securityService = new SecurityService();
        this.restClient = new Client();
    }

    public IFileManagementService getFileManagementService() {
        return fileManagementService;
    }

    public void setFileManagementService(FileManagementService fileManagementService) {
        this.fileManagementService = fileManagementService;
    }

    public ISecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
