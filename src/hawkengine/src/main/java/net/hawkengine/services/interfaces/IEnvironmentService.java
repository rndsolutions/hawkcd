//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services.interfaces;

import net.hawkengine.model.Environment;
import net.hawkengine.model.ServiceResult;

public interface IEnvironmentService extends ICrudService<Environment> {
	ServiceResult getAllEnvironments();

	ServiceResult addEnvironment(Environment environment);

	ServiceResult deleteEnvironment(String environmentId);

	ServiceResult updateEnvironment(Environment environment);

}
