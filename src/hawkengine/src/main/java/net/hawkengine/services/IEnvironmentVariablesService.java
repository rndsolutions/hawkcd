//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.model.EnvironmentVariable;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.Stage;
import net.hawkengine.model.Task;

public interface IEnvironmentVariablesService {
	ArrayList<EnvironmentVariable> getOverriddenEnvironentVariables(
			ArrayList<EnvironmentVariable>... environmentVariableCollections);

	ArrayList<EnvironmentVariable> getPredifinedEnvironentVariables(Pipeline pipeline, Stage stage, JobDefinition job)
			;

	void passEnvironmentVariablesToTask(Task task, ArrayList<EnvironmentVariable> environmentVariables)
			;

}
