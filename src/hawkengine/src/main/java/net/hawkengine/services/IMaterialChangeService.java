//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.MaterialChange;

public interface IMaterialChangeService {
	ArrayList<MaterialChange> getAllMaterialChanges(MaterialDefinition material) throws Exception;

	MaterialChange getLatestMaterialChange(MaterialDefinition material) throws Exception;

}
