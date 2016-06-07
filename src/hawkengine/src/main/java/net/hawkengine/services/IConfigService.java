//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;

public interface IConfigService {
			
	IDbRepository<Pipeline> getPipelineConfigRepository() throws Exception;

	void setPipelineConfigRepository(IDbRepository<Pipeline> value) throws Exception;

	IDbRepository<Agent> getAgentConfigRepository() throws Exception;

	void setAgentConfigRepository(IDbRepository<Agent> value) throws Exception;

	IDbRepository<Environment> getEnvironmentConfigRepository() throws Exception;

	void setEnvironmentConfigRepository(IDbRepository<Environment> value) throws Exception;

	ArrayList<Pipeline> getAllPipelines() throws Exception;

	Pipeline getPipelineByName(String name) throws Exception;

	String addPipeline(Pipeline pipeline) throws Exception;

	String deletePipeline(Pipeline pipeline) throws Exception;

	String updatePipeline(String pipelineName, Pipeline newPipeline) throws Exception;

	ArrayList<Pipeline> getPipelineUpstream(String pipelineName) throws Exception;

	ArrayList<Pipeline> getPipelineDownstream(String pipelineName) throws Exception;

	ArrayList<PipelineGroup> getAllPipelineGroups() throws Exception;

	PipelineGroup getPipelineGroup(String pipelineGroupName) throws Exception;

	String addPipelineGroup(PipelineGroup pipelineGroup) throws Exception;

	String deletePipelineGroup(String pipelineGroupName) throws Exception;

	String updatePipelineGroup(String pipelineGroupName, PipelineGroup newPipelineGroup) throws Exception;

	ArrayList<Material> getAllMaterials() throws Exception;

	Material getMaterialByName(String pipelineName, String materialName) throws Exception;

	String addMaterial(String pipelineName, Material materialToAdd) throws Exception;

	String updateMaterial(String pipelineName, String materialName, Material newMaterial) throws Exception;

	String deleteMaterial(String pipelineName, String materialName) throws Exception;

	ArrayList<Agent> getAllAgents() throws Exception;

	Agent getAgentById(String Id) throws Exception;

	String addAgent(Agent agent) throws Exception;

	String deleteAgent(Agent agent) throws Exception;

	String updateAgent(String agentId, Agent newAgent) throws Exception;

	Server getServer() throws Exception;

	Server getServerById(Object id) throws Exception;

	String addServer(Server server) throws Exception;

	String deleteServer(Server server) throws Exception;

	String updateServer(String serverId, Server newServer) throws Exception;

	ArrayList<Environment> getAllEnvironments() throws Exception;

	String addEnvironment(Environment environment) throws Exception;

	String deleteEnvironment(Environment environment) throws Exception;

	String updateEnvironment(String agentId, Environment newEnvironment) throws Exception;

}
