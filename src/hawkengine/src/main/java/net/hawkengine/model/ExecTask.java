//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

public class ExecTask extends TaskBase {
	public ExecTask() throws Exception {
		this.setType(TaskType.Exec);
	}

	private String __Command;

	public String getCommand() {
		return __Command;
	}

	public void setCommand(String value) {
		__Command = value;
	}

	private String[] __Arguments;

	public String[] getArguments() {
		return __Arguments;
	}

	public void setArguments(String[] value) {
		__Arguments = value;
	}

	private String __LookUpCommands;

	public String getLookUpCommands() {
		return __LookUpCommands;
	}

	public void setLookUpCommands(String value) {
		__LookUpCommands = value;
	}

	private String __WorkingDirectory;

	public String getWorkingDirectory() {
		return __WorkingDirectory;
	}

	public void setWorkingDirectory(String value) {
		__WorkingDirectory = value;
	}

	private boolean __IgnoreErrors;

	public boolean getIgnoreErrors() {
		return __IgnoreErrors;
	}

	public void setIgnoreErrors(boolean value) {
		__IgnoreErrors = value;
	}

}
