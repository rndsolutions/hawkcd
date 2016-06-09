//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Environment;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.services.interfaces.ICrudService;

public interface IEnvironmentService extends ICrudService<Environment> {
	ServiceResult getAllEnvironments();

	ServiceResult addEnvironment(Environment environment);

	ServiceResult deleteEnvironment(String environmentId);

	ServiceResult updateEnvironment(Environment environment);

}
