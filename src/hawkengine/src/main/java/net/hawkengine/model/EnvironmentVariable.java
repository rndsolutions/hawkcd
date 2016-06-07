//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

public class EnvironmentVariable {
	public EnvironmentVariable(String name, String value, boolean isSecured) throws Exception {
		this(name, value);
		this.setIsSecured(isSecured);
	}

	public EnvironmentVariable(String name, String value) throws Exception {
		this.setName(name);
		this.setValue(value);
	}

	public EnvironmentVariable() throws Exception {
	}

	private String __Name;

	public String getName() {
		return __Name;
	}

	public void setName(String value) {
		__Name = value;
	}

	private String __Value;

	public String getValue() {
		return __Value;
	}

	public void setValue(String value) {
		__Value = value;
	}

	private boolean __IsSecured;

	public boolean getIsSecured() {
		return __IsSecured;
	}

	public void setIsSecured(boolean value) {
		__IsSecured = value;
	}

}
