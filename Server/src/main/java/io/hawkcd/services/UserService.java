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

import io.hawkcd.core.Message;
import io.hawkcd.core.MessageDispatcher;
import io.hawkcd.core.security.Authorization;
import io.hawkcd.core.security.AuthorizationFactory;
import io.hawkcd.core.subscriber.Envelope;
import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.User;
import io.hawkcd.model.dto.UserDto;
import io.hawkcd.model.enums.NotificationType;
import io.hawkcd.model.enums.PermissionScope;
import io.hawkcd.model.enums.PermissionType;
import io.hawkcd.services.interfaces.IUserService;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

public class UserService extends CrudService<User> implements IUserService {
    private static final Class CLASS_TYPE = User.class;

    public UserService() {
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public UserService(IDbRepository repository) {
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.VIEWER)
    public ServiceResult getById(String userId) {
        return super.getById(userId);
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.NONE)
    public ServiceResult getAll() {
        return super.getAll();
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult add(User user) {
        ServiceResult result = this.getByEmail(user.getEmail());
        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }
        String password = user.getPassword();
        String hashedPassword = DigestUtils.sha256Hex(password);
        user.setPassword(hashedPassword);
        return super.add(user);
    }

    @Override
//    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult update(User user) {
        ServiceResult result = super.update(user);
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        String methodName = ste[1].getMethodName();
        String className = this.getClass().getSimpleName();

        Message message = AuthorizationFactory.getAuthorizationManager().constructAuthorizedMessage(result, className, methodName);

        MessageDispatcher.dispatchIncomingMessage(message);

        List<String> ids = new ArrayList<>();
        ids.add(user.getId());
        Envelope envelopе = new Envelope(ids);
        message = new Message(envelopе);
        message.setUserUpdate(true);
        MessageDispatcher.dispatchIncomingMessage(message);

        return result;
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult delete(User user) {
        return super.delete(user);
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult getByEmailAndPassword(String email, String password) {
        List<User> users = (List<User>) this.getAll().getEntity();

        User user = users
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user == null) {
            return super.createServiceResult(user, NotificationType.ERROR, "'s email and password doesn't match");
        } else {
            return super.createServiceResult(user, NotificationType.SUCCESS, "retrieved successfully");
        }
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult getByEmail(String email) {
        List<User> users = (List<User>) this.getAll().getEntity();

        User user = users
                .stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (user != null) {
            return super.createServiceResult(user, NotificationType.ERROR, "with this email already exists");
        } else {
            return super.createServiceResult(user, NotificationType.SUCCESS, "does not exist");
        }
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult addUserWithoutProvider(User user) {
        return this.add(user);
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult changeUserPassword(UserDto user, String newPassword, String oldPassword) {
        String hashedPassword = DigestUtils.sha256Hex(oldPassword);
        ServiceResult result = this.getByEmailAndPassword(user.getUsername(), hashedPassword);

        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }
        User userToUpdate = (User) result.getEntity();
        String hashedPasswordToUpdateUser = DigestUtils.sha256Hex(newPassword);
        userToUpdate.setPassword(hashedPasswordToUpdateUser);

        return this.update(userToUpdate);
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult resetUserPassword(User user) {
        String hashedPassword = DigestUtils.sha256Hex(user.getPassword());

        ServiceResult result = this.getById(user.getId());

        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }
        user.setPassword(hashedPassword);
        return this.update(user);
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult disableUser(String id) {
        ServiceResult result = this.getById(id);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }

        User user = (User) result.getEntity();
        user.setEnabled(false);
        return this.update(user);
    }

    @Override
    @Authorization(scope = PermissionScope.SERVER, type = PermissionType.ADMIN)
    public ServiceResult enableUser(String id) {
        ServiceResult result = this.getById(id);
        if (result.getNotificationType() == NotificationType.ERROR) {
            return result;
        }

        User user = (User) result.getEntity();
        user.setEnabled(true);
        return this.update(user);
    }


}
