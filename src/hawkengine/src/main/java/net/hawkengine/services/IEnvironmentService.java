//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Environment;

public interface IEnvironmentService extends IDbRepository<Environment> {
	ArrayList<Environment> getAllEnvironments() throws Exception;

	String addEnvironment(Environment environment) throws Exception;

	String deleteEnvironment(Environment environment) throws Exception;

	String updateEnvironment(String environmentName, Environment newEnvironment) throws Exception;

}
