package io.hawkcd.db.mongodb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import io.hawkcd.db.IDbRepository;
import io.hawkcd.model.Entity;
import io.hawkcd.model.MaterialDefinition;
import io.hawkcd.model.TaskDefinition;
import io.hawkcd.utilities.deserializers.MaterialDefinitionAdapter;
import io.hawkcd.utilities.deserializers.TaskDefinitionAdapter;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.ws.rs.NotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import io.hawkcd.model.enums.*;

public class MongoDbRepository<T extends Entity> implements IDbRepository<T> {
    private static final Logger LOGGER = Logger.getLogger(MongoDbRepository.class);
    private MongoCollection collection;
    private Type entryType;
    private Gson jsonConverter;
    private MongoDatabase mongoDatabase;

    public MongoDbRepository(Class<T> entry) {
        this.entryType = entry;
        this.jsonConverter = new GsonBuilder()
                .registerTypeAdapter(TaskDefinition.class, new TaskDefinitionAdapter())
                .registerTypeAdapter(MaterialDefinition.class, new MaterialDefinitionAdapter())
                .create();

        this.mongoDatabase = MongoDbManager.getInstance().getDb();
        this.collection = this.mongoDatabase.getCollection(this.entryType.getTypeName());
    }

    public MongoDbRepository(Class entry, MongoCollection mockedMongoCollection) {
        this.entryType = entry;
        this.jsonConverter = new GsonBuilder().create();
        this.collection = mockedMongoCollection;
    }

    @Override
    public T getById(String id) {
        if (id == null) {
            return null;
        }

        T result;
        try {
            //construct the filter
            UUID uuid = UUID.fromString(id);
            BasicDBObject bObj = new BasicDBObject("id", uuid);

            //execute the query against the db
            Document document = (Document) collection.find(eq("id", id)).first();
            if (document != null) {
                String json = document.toJson();
                result = this.jsonConverter.fromJson(json, this.entryType);
                return result;
            }

            return null;
        } catch (NotFoundException e) {
            LOGGER.error(e);
            throw e;
        }
    }

    @Override
    public List<T> getAll() {
        T resultElement;
        List<T> result = new ArrayList<>();
        try {
            FindIterable documents = this.collection.find();
            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = this.jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }

    @Override
    public T add(T entry) {
        if (entry == null) {
            return null;
        }

        if (this.getById(entry.getId()) == null) {
            try {
                String entryToJson = this.jsonConverter.toJson(entry);
                Document document = Document.parse(entryToJson);
                this.collection.insertOne(document);
                return entry;
            } catch (RuntimeException e) {
                LOGGER.error(e);
            }
        } else {
            return null;
        }

        return null;
    }

    @Override
    public T update(T entry) {
        if (entry == null) {
            return null;
        }

        try {
            String entryToJson = this.jsonConverter.toJson(entry);
            Document document = Document.parse(entryToJson);

            UpdateResult updateResult = this.collection.replaceOne(eq("id", document.get("id")), document);

            if (updateResult.getMatchedCount() == 1) { // means one record updated
                return entry;
            }

            return null; //either none or many records updated, so consider the operation not successful.
        } catch (RuntimeException e) {
            LOGGER.error(e);
            return null;
        }
    }

    @Override
    public T delete(String id) {
        if (id == null) {
            return null;
        }

        T result;
        try {
            //construct the filter
            UUID uuid = UUID.fromString(id);
            BasicDBObject query = new BasicDBObject("id", uuid);

            Document document = (Document) this.collection.findOneAndDelete(eq("id", id));
            if (document != null) {
                String dd = document.toJson();
                result = this.jsonConverter.fromJson(dd, this.entryType);
                return result;
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
            return null;
        }
    }

    public List<T> getByQuantity(String definitionId, Integer quantity, String firstId) {
        T resultElement;
        List<T> result = new ArrayList<>();

        try {
            FindIterable documents;
            BasicDBObject queryFilter = new BasicDBObject("pipelineDefinitionId", definitionId);
            BasicDBObject sortFilter = new BasicDBObject("executionId", -1);

            if(firstId.isEmpty() || firstId == null || firstId.equals("undefined"))
            {
                documents = collection.find(queryFilter).sort(sortFilter).limit(quantity);
            }
            else
            {
                Document lastPipe = (Document) collection.find(eq("id", firstId)).first();
                Integer execId = (Integer)lastPipe.get("executionId");

                List<BasicDBObject> queryExpression = new ArrayList<BasicDBObject>();

                queryExpression.add(queryFilter);
                queryExpression.add(new BasicDBObject("executionId", new BasicDBObject("$lt", execId)));
                queryFilter = new BasicDBObject();
                queryFilter.put("$and", queryExpression);

                documents = collection.find(queryFilter).sort(sortFilter).limit(quantity);
            }

            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = this.jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }

    public List<T> getAllByDefinitionId(String definitionId) {
        if (definitionId == null) {
            return null;
        }

        T resultElement;
        List<T> result = new ArrayList<>();
        try {
            FindIterable documents = this.collection.find(eq("pipelineDefinitionId", definitionId));
            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = this.jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }

    public List<T> getAllUpdatedUnpreparedPipelinesInProgress() {
        T resultElement;
        List<T> result = new ArrayList<>();
        try {
            BasicDBObject queryFilter = new BasicDBObject();
            List<BasicDBObject> queryExpression = new ArrayList<BasicDBObject>();
            List<BasicDBObject> partialQueryExpresion = new ArrayList<BasicDBObject>();

            partialQueryExpresion.add(new BasicDBObject("areMaterialsUpdated", true));
            partialQueryExpresion.add(new BasicDBObject("isPrepared", false));

            queryExpression.add(new BasicDBObject("$and", partialQueryExpresion));
            queryExpression.add(new BasicDBObject("status", PipelineStatus.IN_PROGRESS.toString()));

            queryFilter.put("$and", queryExpression);
            BasicDBObject sortFilter = new BasicDBObject("startTime", 1);

            FindIterable documents = collection.find(queryFilter).sort(sortFilter);

            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = this.jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }

    public  List<T> getAllPreparedAwaitingPipelines(){
        T resultElement;
        List<T> result = new ArrayList<>();
        try {
            BasicDBObject queryFilter = new BasicDBObject();
            List<BasicDBObject> queryExpresion = new ArrayList<BasicDBObject>();

            queryExpresion.add(new BasicDBObject("isPrepared", true));
            queryExpresion.add(new BasicDBObject("status", PipelineStatus.AWAITING.toString()));

            queryFilter.put("$and", queryExpresion);
            BasicDBObject sortFilter = new BasicDBObject("startTime", 1);

            FindIterable documents = collection.find(queryFilter).sort(sortFilter);

            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = this.jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }

    public List<T> getAllPreparedPipelinesInProgress(){
        T resultElement;
        List<T> result = new ArrayList<>();
        try {
            BasicDBObject queryFilter = new BasicDBObject();
            List<BasicDBObject> queryExpresion = new ArrayList<BasicDBObject>();

            queryExpresion.add(new BasicDBObject("isPrepared", true));
            queryExpresion.add(new BasicDBObject("status", PipelineStatus.IN_PROGRESS.toString()));

            queryFilter.put("$and", queryExpresion);
            BasicDBObject sortFilter = new BasicDBObject("startTime", 1);

            FindIterable documents = collection.find(queryFilter).sort(sortFilter);

            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = this.jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }

    public List<T> getAllNonupdatedPipelines(){
        T resultElement;
        List<T> result = new ArrayList<>();
        try {
            BasicDBObject queryFilter = new BasicDBObject("areMaterialsUpdated", false);
            BasicDBObject sortFilter = new BasicDBObject("startTime", 1);
            FindIterable documents = collection.find(queryFilter).sort(sortFilter);

            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = this.jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }

    public T GetLastPipeline(String definitionId){
        T result = null;
        try {
            BasicDBObject queryFilter = new BasicDBObject("pipelineDefinitionId", definitionId);
            BasicDBObject sortFilter = new BasicDBObject("executionId", -1);
            FindIterable documents = collection.find(queryFilter).sort(sortFilter).limit(1);

            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                result = this.jsonConverter.fromJson(documentToJson, this.entryType);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }

    public List<T> getAllPipelineArtifactDTOs(String searchCriteria, Integer numberOfPipelines, Integer skip, String pipelineId){
        T resultElement;
        List<T> result = new ArrayList<>();

        try {
            FindIterable documents;
            BasicDBObject queryFilter = new BasicDBObject();
            queryFilter.put("pipelineDefinitionName", Pattern.compile(searchCriteria, Pattern.CASE_INSENSITIVE));

            BasicDBObject sortFilter = new BasicDBObject("pipelineDefinitionId", -1);
            sortFilter.append("executionId", -1);

            if(pipelineId.isEmpty() || pipelineId == null || pipelineId.equals("undefined"))
            {
                documents = collection.find(queryFilter).sort(sortFilter).limit(numberOfPipelines);
            }
            else
            {
                documents = collection.find(queryFilter).sort(sortFilter).skip(skip).limit(numberOfPipelines);
            }

            for (Object document : documents) {
                String documentToJson = JSON.serialize(document);
                resultElement = this.jsonConverter.fromJson(documentToJson, this.entryType);
                result.add(resultElement);
            }
        } catch (RuntimeException e) {
            LOGGER.error(e);
        }

        return result;
    }
}