package io.hawkcd.services;

import io.hawkcd.core.config.Config;
import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.Server;
import io.hawkcd.model.ServiceResult;
import io.hawkcd.model.configuration.Configuration;

import javax.ws.rs.NotSupportedException;

/**
 * Created by rado on 28.11.16.
 */
public class ServerService extends CrudService<Server> {

    private static final Class CLASS_TYPE = Server.class;
    private static ServerService serverService;
    private Server server;

    public ServerService(){
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

    public static ServerService getInstance() {
        if (serverService == null) {
            serverService = new ServerService();
            return serverService;
        } else {
            return serverService;
        }
    }

    @Override
    public ServiceResult add(Server server){
        ServiceResult result = super.add(server);
        this.server = (Server) result.getEntity();
        return result;
    }
    @Override
    public ServiceResult update(Server server){
        ServiceResult result = super.update(server);
        this.server = (Server) result.getEntity();
        return  result;
    }
    @Override
    public ServiceResult delete(Server server){
        throw  new NotSupportedException("Delete Operation on Server object is not supported");
    }

    public Server getServer() {
        return server;
    }

}
