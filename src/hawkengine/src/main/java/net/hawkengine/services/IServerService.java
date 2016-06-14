//
// Translated by CS2J (http://www.cs2j.com): 5/1/2016 4:17:15 PM
//

package net.hawkengine.services;

import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.Server;

public interface IServerService extends IDbRepository<Server> {
	Server getServer();

	Server getServerById(Object id);

	String addServer(Server server);

	String deleteServer(Server server);

	String updateServer(String serverId, Server newServer);

}
