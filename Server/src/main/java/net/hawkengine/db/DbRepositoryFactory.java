package net.hawkengine.db;

import net.hawkengine.db.redis.RedisRepository;
import net.hawkengine.model.enums.DatabaseType;

public class DbRepositoryFactory {
    public static IDbRepository create(DatabaseType databaseType, Class classType) {
        switch (databaseType) {
            case REDIS:
                return new RedisRepository(classType);
            default:
                return null;
        }
    }
}
