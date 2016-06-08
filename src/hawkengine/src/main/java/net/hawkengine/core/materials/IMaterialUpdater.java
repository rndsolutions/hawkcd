//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:14 PM
//

package net.hawkengine.core.materials;

import net.hawkengine.model.Material;
import net.hawkengine.model.MaterialChange;

public interface IMaterialUpdater {
	MaterialChange getLatestMaterialChange(Material material) throws Exception;

	MaterialChange getLatestDbChange(Material material) throws Exception;

	boolean areLatestAndDbTheSame(MaterialChange latestChange, MaterialChange dbChange) throws Exception;

}
