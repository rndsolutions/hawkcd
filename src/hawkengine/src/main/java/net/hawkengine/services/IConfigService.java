package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.*;

public interface IConfigService {
			
	IDbRepository<Pipeline> getPipelineConfigRepository();

	void setPipelineConfigRepository(IDbRepository<Pipeline> value);

	IDbRepository<Agent> getAgentConfigRepository();

	void setAgentConfigRepository(IDbRepository<Agent> value);

	IDbRepository<Environment> getEnvironmentConfigRepository();

	void setEnvironmentConfigRepository(IDbRepository<Environment> value);

	ArrayList<Pipeline> getAllPipelines();

	Pipeline getPipelineByName(String name);

	String addPipeline(Pipeline pipeline);

	String deletePipeline(Pipeline pipeline);

	String updatePipeline(String pipelineName, Pipeline newPipeline);

	ArrayList<Pipeline> getPipelineUpstream(String pipelineName);

	ArrayList<Pipeline> getPipelineDownstream(String pipelineName);

	ArrayList<PipelineGroup> getAllPipelineGroups();

	PipelineGroup getPipelineGroup(String pipelineGroupName);

	String addPipelineGroup(PipelineGroup pipelineGroup);

	String deletePipelineGroup(String pipelineGroupName);

	String updatePipelineGroup(String pipelineGroupName, PipelineGroup newPipelineGroup);

	ArrayList<MaterialDefinition> getAllMaterials();

	MaterialDefinition getMaterialByName(String pipelineName, String materialName);

	String addMaterial(String pipelineName, MaterialDefinition materialToAdd);

	String updateMaterial(String pipelineName, String materialName, MaterialDefinition newMaterial);

	String deleteMaterial(String pipelineName, String materialName);

	ArrayList<Agent> getAllAgents();

	Agent getAgentById(String Id);

	String addAgent(Agent agent);

	String deleteAgent(Agent agent);

	String updateAgent(String agentId, Agent newAgent);

	Server getServer();

	Server getServerById(Object id);

	String addServer(Server server);

	String deleteServer(Server server);

	String updateServer(String serverId, Server newServer);

	ArrayList<Environment> getAllEnvironments();

	String addEnvironment(Environment environment);

	String deleteEnvironment(Environment environment);

	String updateEnvironment(String agentId, Environment newEnvironment);

}
