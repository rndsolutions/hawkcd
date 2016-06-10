//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.model.MaterialDefinition;

public interface IMaterialService {
	ArrayList<MaterialDefinition> getAllMaterials() throws Exception;

	MaterialDefinition getMaterialByName(String pipelineName, String materialName) throws Exception;

	String addMaterial(String pipelineName, MaterialDefinition materialToAdd) throws Exception;

	String updateMaterial(String pipelineName, String materialName, MaterialDefinition newMaterial) throws Exception;

	String deleteMaterial(String pipelineName, String materialName) throws Exception;
}
