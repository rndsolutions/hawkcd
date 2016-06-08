//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:14 PM
//

package net.hawkengine.core;

/**
 * Represents a HawakEngine server component
 */
public interface IServerComponent {
	void configure() throws Exception;

	void start() throws Exception;

	void stop() throws Exception;

}
