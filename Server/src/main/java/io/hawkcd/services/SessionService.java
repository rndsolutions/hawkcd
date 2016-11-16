package io.hawkcd.services;

import io.hawkcd.db.DbRepositoryFactory;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.Pipeline;
import io.hawkcd.model.SessionDetails;

/**
 * Created by rado on 15.11.16.
 */
public class SessionService  extends CrudService<SessionDetails> {
    private static final Class CLASS_TYPE = SessionDetails.class;

    public SessionService(){
        IDbRepository repository = DbRepositoryFactory.create(DATABASE_TYPE, CLASS_TYPE);
        super.setRepository(repository);
        super.setObjectType(CLASS_TYPE.getSimpleName());
    }

}
