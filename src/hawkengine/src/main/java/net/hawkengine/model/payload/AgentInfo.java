//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model.payload;

import java.util.UUID;

import net.hawkengine.model.AgentExecutionState;

public class AgentInfo {
	private UUID __AgentId;

	public UUID getAgentId() {
		return this.__AgentId;
	}

	public void setAgentId(UUID value) {
		this.__AgentId = value;
	}

	private String __Name;

	public String getName() {
		return this.__Name;
	}

	public void setName(String value) {
		this.__Name = value;
	}

	private String __IPAddress;

	public String getIPAddress() {
		return this.__IPAddress;
	}

	public void setIPAddress(String value) {
		this.__IPAddress = value;
	}

	private String __Sandbox;

	public String getSandbox() {
		return this.__Sandbox;
	}

	public void setSandbox(String value) {
		this.__Sandbox = value;
	}

	private AgentExecutionState __State = AgentExecutionState.Idle;

	public AgentExecutionState getState() {
		return this.__State;
	}

	public void setState(AgentExecutionState value) {
		this.__State = value;
	}

}
