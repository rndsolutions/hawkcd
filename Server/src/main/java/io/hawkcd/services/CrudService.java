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

import io.hawkcd.Config;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.Entity;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.enums.DatabaseType;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.services.interfaces.ICrudService;

import java.util.List;

public abstract class CrudService<T extends Entity> extends Service<T> implements ICrudService<T> {
    public final DatabaseType DATABASE_TYPE = Config.getConfiguration().getDatabaseType();

    private IDbRepository<T> repository;

    public IDbRepository<T> getRepository() {
        return this.repository;
    }

    public void setRepository(IDbRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public ServiceResult getById(String id) {
        T dbObject = this.getRepository().getById(id);

        ServiceResult result;
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, NotificationType.SUCCESS, "retrieved successfully");
        } else {
            result = super.createServiceResult(dbObject, NotificationType.ERROR, "not found");
        }

        return result;
    }

    @Override
    public ServiceResult getAll() {
        List<T> dbObjects = this.getRepository().getAll();

        ServiceResult result = super.createServiceResultArray(dbObjects, NotificationType.SUCCESS, "retrieved successfully");

        return result;
    }

    @Override
    public ServiceResult add(T entity) {
        T dbObject = this.getRepository().add(entity);

        ServiceResult result;
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, NotificationType.SUCCESS, "created successfully");
        } else {
            result = super.createServiceResult(dbObject, NotificationType.ERROR, "already exists");
        }

        return result;
    }

    @Override
    public ServiceResult update(T entity) {
        if (entity == null) {
            return super.createServiceResult(entity, NotificationType.ERROR, "not found");
        }
        T dbObject = this.getRepository().update(entity);

        ServiceResult result;
        if (dbObject != null) {
            result = super.createServiceResult(dbObject, NotificationType.SUCCESS, "updated successfully");
        } else {
            result = super.createServiceResult(dbObject, NotificationType.ERROR, "not found");
        }

        return result;
    }

    @Override
    public ServiceResult delete(T entity) {
        T dbObject = this.getRepository().delete(entity.getId());

        ServiceResult result = new ServiceResult();
        if (dbObject == null) {
            result = super.createServiceResult((T) result.getObject(), NotificationType.ERROR, "not found");
        } else {
            result = super.createServiceResult((T) dbObject, NotificationType.SUCCESS, "deleted successfully");
        }

        result.setObject(dbObject);

        return result;
    }
}
