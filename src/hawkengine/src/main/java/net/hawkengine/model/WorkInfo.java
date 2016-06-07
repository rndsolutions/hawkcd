//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

import java.util.ArrayList;
import java.util.UUID;

public class WorkInfo {
	public WorkInfo() throws Exception {
		this.setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
	}

	private UUID __PipelineId;

	public UUID getPipelineId() {
		return __PipelineId;
	}

	public void setPipelineId(UUID value) {
		__PipelineId = value;
	}

	private int __PipelineExecutionID;

	public int getPipelineExecutionID() {
		return __PipelineExecutionID;
	}

	public void setPipelineExecutionID(int value) {
		__PipelineExecutionID = value;
	}

	private String __PipelineName;

	public String getPipelineName() {
		return __PipelineName;
	}

	public void setPipelineName(String value) {
		__PipelineName = value;
	}

	private String __PipelineEnvironmentName;

	public String getPipelineEnvironmentName() {
		return __PipelineEnvironmentName;
	}

	public void setPipelineEnvironmentName(String value) {
		__PipelineEnvironmentName = value;
	}

	private String __PipelineTriggerReason;

	public String getPipelineTriggerReason() {
		return __PipelineTriggerReason;
	}

	public void setPipelineTriggerReason(String value) {
		__PipelineTriggerReason = value;
	}

	private String __LabelTemplate;

	public String getLabelTemplate() {
		return __LabelTemplate;
	}

	public void setLabelTemplate(String value) {
		__LabelTemplate = value;
	}

	private String __StageId;

	public String getStageId() {
		return __StageId;
	}

	public void setStageId(String value) {
		__StageId = value;
	}

	private int __StageExecutionID;

	public int getStageExecutionID() {
		return __StageExecutionID;
	}

	public void setStageExecutionID(int value) {
		__StageExecutionID = value;
	}

	private String __StageName;

	public String getStageName() {
		return __StageName;
	}

	public void setStageName(String value) {
		__StageName = value;
	}

	private String __StageTriggerReason;

	public String getStageTriggerReason() {
		return __StageTriggerReason;
	}

	public void setStageTriggerReason(String value) {
		__StageTriggerReason = value;
	}

	private boolean __ShouldFetchMaterials;

	public boolean getShouldFetchMaterials() {
		return __ShouldFetchMaterials;
	}

	public void setShouldFetchMaterials(boolean value) {
		__ShouldFetchMaterials = value;
	}

	private Job __Job;

	public Job getJob() {
		return __Job;
	}

	public void setJob(Job value) {
		__Job = value;
	}

	private ArrayList<Material> __Materials;

	public ArrayList<Material> getMaterials() {
		return __Materials;
	}

	public void setMaterials(ArrayList<Material> value) {
		__Materials = value;
	}

	private ArrayList<EnvironmentVariable> __EnvironmentVariables;

	public ArrayList<EnvironmentVariable> getEnvironmentVariables() {
		return __EnvironmentVariables;
	}

	public void setEnvironmentVariables(ArrayList<EnvironmentVariable> value) {
		__EnvironmentVariables = value;
	}

}
