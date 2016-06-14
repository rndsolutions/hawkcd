//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.model.MaterialDefinition;

public interface IMaterialService {
	ArrayList<MaterialDefinition> getAllMaterials();

	MaterialDefinition getMaterialByName(String pipelineName, String materialName);

	String addMaterial(String pipelineName, MaterialDefinition materialToAdd);

	String updateMaterial(String pipelineName, String materialName, MaterialDefinition newMaterial);

	String deleteMaterial(String pipelineName, String materialName);
}
