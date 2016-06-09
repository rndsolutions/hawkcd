package net.hawkengine.services;

import java.util.ArrayList;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Agent;
import net.hawkengine.model.Environment;
import net.hawkengine.model.Material;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.PipelineGroup;
import net.hawkengine.model.Server;

public class ConfigService implements IConfigService{
	
	private IDbRepository<PipelineDefinition> repository;
	
	public ConfigService() {
		// TODO Auto-generated constructor stub
	}
		
	public ConfigService(IDbRepository<PipelineDefinition> repository){
		this.repository = repository;
	}
	
	@Override
	public IDbRepository<PipelineDefinition> getPipelineConfigRepository() throws Exception {
		// TODO Auto-generated method stub
		return null;				
	}

	@Override
	public void setPipelineConfigRepository(IDbRepository<PipelineDefinition> value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDbRepository<Agent> getAgentConfigRepository() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAgentConfigRepository(IDbRepository<Agent> value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDbRepository<Environment> getEnvironmentConfigRepository() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEnvironmentConfigRepository(IDbRepository<Environment> value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<PipelineDefinition> getAllPipelines() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelineDefinition getPipelineByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addPipeline(PipelineDefinition pipelineDefinition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deletePipeline(PipelineDefinition pipelineDefinition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updatePipeline(String pipelineName, PipelineDefinition newPipelineDefinition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<PipelineDefinition> getPipelineUpstream(String pipelineName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<PipelineDefinition> getPipelineDownstream(String pipelineName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<PipelineGroup> getAllPipelineGroups() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelineGroup getPipelineGroup(String pipelineGroupName) throws Exception {
		
		PipelineGroup g = new PipelineGroup();
		g.setName("this mocked pipeline group");
				
		return g;
	}

	@Override
	public String addPipelineGroup(PipelineGroup pipelineGroup) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deletePipelineGroup(String pipelineGroupName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updatePipelineGroup(String pipelineGroupName, PipelineGroup newPipelineGroup) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Material> getAllMaterials() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Material getMaterialByName(String pipelineName, String materialName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addMaterial(String pipelineName, Material materialToAdd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateMaterial(String pipelineName, String materialName, Material newMaterial) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteMaterial(String pipelineName, String materialName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Agent> getAllAgents() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Agent getAgentById(String Id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addAgent(Agent agent) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteAgent(Agent agent) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateAgent(String agentId, Agent newAgent) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Server getServer() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Server getServerById(Object id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addServer(Server server) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteServer(Server server) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateServer(String serverId, Server newServer) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Environment> getAllEnvironments() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addEnvironment(Environment environment) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteEnvironment(Environment environment) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateEnvironment(String agentId, Environment newEnvironment) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
