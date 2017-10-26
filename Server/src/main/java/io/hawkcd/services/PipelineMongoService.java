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


package io.hawkcd.services;

import io.hawkcd.core.config.Config;
import io.hawkcd.db.mongodb.MongoDbRepository;
import io.hawkcd.model.Entity;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.DatabaseType;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.interfaces.IPipelineMongoService;

import java.util.List;

public class PipelineMongoService<T extends Entity> extends Service<T> implements IPipelineMongoService {

    public final DatabaseType DATABASE_TYPE = Config.getConfiguration().getDatabaseType();

    private MongoDbRepository<T> mongoRepository;

    public MongoDbRepository<T> getMongoRepository() {
        return this.mongoRepository;
    }

    public void setMongoRepository(MongoDbRepository<T> repository) {
        this.mongoRepository = repository;
    }

    @Override
    public ServiceResult getByQuantity(String definitionId, Integer quantity, String firstId) {
        List<T> dbObjects =  this.getMongoRepository().getByQuantity(definitionId, quantity, firstId);

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult getByDefinitionId(String definitionId) {
        List<T> dbObjects =  this.getMongoRepository().getAllByDefinitionId(definitionId);

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult getAllUpdatedUnpreparedPipelinesInProgress() {
        List<T> dbObjects =  this.getMongoRepository().getAllUpdatedUnpreparedPipelinesInProgress();

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    public ServiceResult getAllPreparedAwaitingPipelines(){
        List<T> dbObjects =  this.getMongoRepository().getAllPreparedAwaitingPipelines();

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult getAllPreparedPipelinesInProgress(){
        List<T> dbObjects =  this.getMongoRepository().getAllPreparedPipelinesInProgress();

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult getAllNonupdatedPipelines(){
        List<T> dbObjects = this.getMongoRepository().getAllNonupdatedPipelines();

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public T GetLastPipeline(String definitionId){
        T result = this.getMongoRepository().GetLastPipeline(definitionId);

        return result;
    }

    @Override
    public ServiceResult getAllPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines, Integer skip, String pipelineId){
        List<T> dbObjects = this.getMongoRepository().getAllPipelineArtifactDTOs(searchCriteria, numberOfPipelines, skip, pipelineId);

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }
}
