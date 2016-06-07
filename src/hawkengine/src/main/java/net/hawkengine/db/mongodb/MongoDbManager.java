package net.hawkengine.db.mongodb;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDbManager {
    public final static String HOST = "localhost";
    public final static int PORT = 27017;
    private static MongoDbManager ourInstance = null;
    public DB db;

    private MongoDbManager() {
        initDatabase();
    }

    public synchronized static MongoDbManager getInstance() {

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

            db = mongoClient.getDB("hawkDbMongo");

            System.out.println("Successfully connected to MongoDB");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " +
                    e.getMessage());
        }
    }
}
