package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;
import net.hawkengine.model.dto.UserDto;
import net.hawkengine.services.Service;

public interface IUserService extends ICrudService<User> {

    ServiceResult getByEmailAndPassword(String email, String password);

    ServiceResult getByEmail(String email);

    ServiceResult addUserWithoutProvider(User user);

    ServiceResult changeUserPassword(UserDto user, String newPasword, String oldPassword);
}
