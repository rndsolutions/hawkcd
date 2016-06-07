
package net.hawkengine.model;

import java.util.ArrayList;

public class CommandTask {
	private String __Command;

	public String getCommand() {
		return __Command;
	}

	public void setCommand(String value) {
		__Command = value;
	}

	private ArrayList<String> __Arguments;

	public ArrayList<String> getArguments() {
		return __Arguments;
	}

	public void setArguments(ArrayList<String> value) {
		__Arguments = value;
	}

	private RunIf __RunIf = RunIf.Passed;

	public RunIf getRunIf() {
		return __RunIf;
	}

	public void setRunIf(RunIf value) {
		__RunIf = value;
	}

}
