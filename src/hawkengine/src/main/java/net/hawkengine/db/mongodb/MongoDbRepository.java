package net.hawkengine.db.mongodb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.*;
import com.mongodb.util.JSON;
import net.hawkengine.db.IDbRepository;
import net.hawkengine.model.DbEntry;

import javax.ws.rs.NotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MongoDbRepository<T extends DbEntry> implements IDbRepository<T> {

    private DB mongoDatabase;
    private DBCollection collection;
    private Type entryType;

    private Gson jsonConverter;

    public MongoDbRepository(Class<T> entry) {
        this.entryType = entry;

        this.jsonConverter = new GsonBuilder().create();
        this.mongoDatabase = MongoDbManager.getInstance().db;
        this.collection = mongoDatabase.getCollection(this.entryType.getTypeName());
    }

    public MongoDbRepository(Class entry, DBCollection mockedMongoCollection) {
        this.entryType = entry;

        this.jsonConverter = new GsonBuilder().create();
        this.collection = mockedMongoCollection;
    }

    @Override
    public T getById(String id) {
        T result;
        try {
            BasicDBObject query = new BasicDBObject("id", id);
            DBCursor documents = this.collection.find(query);

            if (documents.size() > 0) {
                String document = JSON.serialize(documents.next());

                result = jsonConverter.fromJson(document, this.entryType);

                return result;
            }
            return null;
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<T> getAll() {
        T resultElement;
        try {
            DBCursor documents = this.collection.find();

            List<T> result = new ArrayList<>();

            for (DBObject document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public T add(T entry) {
        if (getById(entry.getId()) == null) {
            try {
                String entryToJson = jsonConverter.toJson(entry);
                DBObject myDoc = (DBObject) JSON.parse(entryToJson);

                this.collection.insert(myDoc);

                return entry;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }else {
            return null;
            //throw new Exception("Entry already present");
        }
    }

    @Override
    public T update(T entry) {
        try {
            BasicDBObject newDocument = (BasicDBObject) JSON.parse(jsonConverter.toJson(entry));

            BasicDBObject searchQuery = new BasicDBObject().append("id", entry.getId());

            this.collection.findAndModify(searchQuery, newDocument);

            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return entry;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            BasicDBObject searchQuery = new BasicDBObject().append("id", id);
            this.collection.findAndRemove(searchQuery);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
