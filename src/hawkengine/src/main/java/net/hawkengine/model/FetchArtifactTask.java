//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

//[SD] I think we need a bool property to indicate if the request is for a file or folder. Although the agent will download it in any case because it will use the fetch\zip method. We need it to know SrcDir or SrcFile is the source.
public class FetchArtifactTask extends TaskBase {
	public FetchArtifactTask() throws Exception {
		this.setType(TaskType.FetchArtifact);
	}

	/// <summary>
	/// This value can either be: 1. the name of upstream pipeline on which the
	/// pipeline of the job depends on.
	/// The pipeline should be added as a dependency under <materials>, or
	/// 2. the hierarchy of an ancestor pipeline of the current pipeline.
	/// Example,
	/// The value "BuildPipeline/AcceptancePipeline" denotes that the fetch task
	/// attempts to fetch artifacts
	/// from its ancestor 'BuildPipeline'. The given hierarchy denotes that the
	/// current pipeline depends
	/// on 'AcceptancePipeline' which in turn depends on 'BuildPipeline' using
	/// the dependency
	/// material definition given under materials. Defaults to current pipeline
	/// if not specified.
	/// </summary>
	private String __Pipeline;

	public String getPipeline() {
		return __Pipeline;
	}

	public void setPipeline(String value) {
		__Pipeline = value;
	}

	/**
	 * The name of the stage to fetch artifacts from
	 */
	private String __Stage;

	public String getStage() {
		return __Stage;
	}

	public void setStage(String value) {
		__Stage = value;
	}

	/**
	 * The name of the job to fetch artifacts from
	 */
	private String __Job;

	public String getJob() {
		return __Job;
	}

	public void setJob(String value) {
		__Job = value;
	}

	/**
	 * The path of the artifact directory of a specific job, relative to the
	 * sandbox directory. If the directory does not exist, the job is failed
	 */
	private String __Source;

	public String getSource() {
		return __Source;
	}

	public void setSource(String value) {
		__Source = value;
	}

	private String __Destination;

	public String getDestination() {
		return __Destination;
	}

	public void setDestination(String value) {
		__Destination = value;
	}

}
