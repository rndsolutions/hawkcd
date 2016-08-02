
package net.hawkengine.services.interfaces;

import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.User;

public interface IUserService extends ICrudService<User> {

    ServiceResult getByEmailAndPassword(String email, String password);

    ServiceResult addUserWithoutProvider(User user);
}