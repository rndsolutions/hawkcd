package net.hawkengine.db.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.model.configuration.DatabaseConfig;
import net.hawkengine.model.enums.DatabaseType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MongoDbManager {
    public static final DatabaseConfig mongoDbConfig = ServerConfiguration.getConfiguration().getDatabaseConfigs().get(DatabaseType.MONGODB);
    private static MongoDatabase db;
    private static MongoDbManager mongoDbManager;
    private static MongoClient mongoClient;
    private static ServerAddress serverAddress;

    private MongoDbManager() {
        this.initDatabase();
        db = getDb();
    }

    public static synchronized MongoDbManager getInstance() {

        if (mongoDbManager == null) {
            mongoDbManager = new MongoDbManager();
        }
        return mongoDbManager;
    }

    public MongoClient getClient() {
        return mongoClient;
    }

    public MongoDatabase getDb() {
        return mongoClient.getDatabase(mongoDbConfig.getName());
    }

    private void initDatabase() {
        serverAddress = new ServerAddress(mongoDbConfig.getHost(), mongoDbConfig.getPort());
        List<ServerAddress> seeds = new ArrayList<>();
        seeds.add(serverAddress);
        List<MongoCredential> credentials = new ArrayList<>();
        credentials.add(MongoCredential.createCredential(mongoDbConfig.getUsername(), mongoDbConfig.getName(), mongoDbConfig.getPassword().toCharArray()));
        mongoClient = new MongoClient(seeds, credentials);

    }

    private void insertAdminCredentials(String userName, String password, String[] roles, String databaseName) {
        MongoDatabase adminDb = mongoClient.getDatabase(databaseName);
        Map<String, Object> commandArguments = new BasicDBObject();
        commandArguments.put("createUser", userName);
        commandArguments.put("pwd", password);
        commandArguments.put("roles", roles);
        BasicDBObject command = new BasicDBObject(commandArguments);
        adminDb.runCommand(command);
    }

    private boolean rootUserExists(String name, String password, String databaseName) {
        //TODO check if root user of the db exists, if not add it.
        return false;
    }
}