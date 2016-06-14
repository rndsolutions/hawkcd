package net.hawkengine.db.mongodb;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public final class MongoDbManager {
    public static final String HOST = "localhost";
    public static final int PORT = 27017;
    private static MongoDbManager ourInstance;
    @SuppressWarnings("PublicField")
    public DB db;

    private MongoDbManager() {
        this.initDatabase();
    }

    public static synchronized  MongoDbManager getInstance() {

        if (ourInstance == null) {
            ourInstance = new MongoDbManager();
        }
        return ourInstance;
    }

    private void initDatabase() {
        try {
            // Connect to mongodb server on localhost
            MongoClient mongoClient = new MongoClient(HOST,
                    PORT);

            this.db = mongoClient.getDB("hawkDbMongo");

            System.out.println("Successfully connected to MongoDB");

        } catch (RuntimeException e) {
            System.err.println(e.getClass().getName() + ": " +
                    e.getMessage());
        }
    }
}
