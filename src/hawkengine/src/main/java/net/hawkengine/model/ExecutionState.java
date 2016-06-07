//
// Translated by CS2J (http://www.cs2j.com): 4/24/2016 12:58:55 AM
//

package net.hawkengine.model;

public enum ExecutionState {
	// public enum AgentState
	// {
	// Pending,
	// Enabled,
	// Disabled,
	// Idle,
	// ExecutingJob
	// }
	// [Flags]
	None,
	// = 0,
	Scheduled,
	// = 1,
	Running,
	// = 2,
	Completed,
	// = 3
	Paused, Canceled, Failed, Incomplete
}
