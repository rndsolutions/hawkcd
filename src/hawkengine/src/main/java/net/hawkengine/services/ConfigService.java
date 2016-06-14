package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Environment;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.Server;

public class ConfigService implements IConfigService{
	
	private IDbRepository<Pipeline> repository;
	
	public ConfigService() {
		// TODO Auto-generated constructor stub
	}
		
	public ConfigService(IDbRepository<Pipeline> repository){
		this.repository = repository;
	}
	
	@Override
	public IDbRepository<Pipeline> getPipelineConfigRepository() {
		// TODO Auto-generated method stub
		return null;				
	}

	@Override
	public void setPipelineConfigRepository(IDbRepository<Pipeline> value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDbRepository<Agent> getAgentConfigRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAgentConfigRepository(IDbRepository<Agent> value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDbRepository<Environment> getEnvironmentConfigRepository() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEnvironmentConfigRepository(IDbRepository<Environment> value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Pipeline> getAllPipelines() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pipeline getPipelineByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addPipeline(Pipeline pipeline) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deletePipeline(Pipeline pipeline) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updatePipeline(String pipelineName, Pipeline newPipeline) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Pipeline> getPipelineUpstream(String pipelineName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Pipeline> getPipelineDownstream(String pipelineName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<PipelineGroup> getAllPipelineGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelineGroup getPipelineGroup(String pipelineGroupName) {
		
		PipelineGroup g = new PipelineGroup();
		g.setName("this mocked pipeline group");
				
		return g;
	}

	@Override
	public String addPipelineGroup(PipelineGroup pipelineGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deletePipelineGroup(String pipelineGroupName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updatePipelineGroup(String pipelineGroupName, PipelineGroup newPipelineGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<MaterialDefinition> getAllMaterials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MaterialDefinition getMaterialByName(String pipelineName, String materialName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addMaterial(String pipelineName, MaterialDefinition materialToAdd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateMaterial(String pipelineName, String materialName, MaterialDefinition newMaterial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteMaterial(String pipelineName, String materialName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Agent> getAllAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent getAgentById(String Id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addAgent(Agent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteAgent(Agent agent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateAgent(String agentId, Agent newAgent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Server getServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Server getServerById(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addServer(Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteServer(Server server) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateServer(String serverId, Server newServer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Environment> getAllEnvironments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addEnvironment(Environment environment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteEnvironment(Environment environment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateEnvironment(String agentId, Environment newEnvironment) {
		// TODO Auto-generated method stub
		return null;
	}

}
