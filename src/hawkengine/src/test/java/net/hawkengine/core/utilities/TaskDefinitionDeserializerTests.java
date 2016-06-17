package net.hawkengine.core.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.hawkengine.core.utilities.deserializers.TaskDefinitionDeserializer;
import net.hawkengine.model.ExecTask;
import net.hawkengine.model.FetchArtifactTask;
import net.hawkengine.model.FetchMaterialTask;
import net.hawkengine.model.UploadArtifactTask;
import net.hawkengine.model.enums.MaterialType;
import net.hawkengine.model.enums.RunIf;
import net.hawkengine.model.enums.TaskType;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TaskDefinitionDeserializerTests {

    private static TaskDefinitionDeserializer mockedDeserializer;
    private static Gson jsonConverter;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUp() {
        jsonConverter = new Gson();
        mockedDeserializer = new TaskDefinitionDeserializer();
    }

    @Test
    public void deserializeExecTask_validJSON_correctObject() {
        //Arrange
        String jsonAsString = "{\n" +
                "\"type\": \"EXEC\",\n" +
                "\"name\": \"execTask\",\n" +
                "\"command\": \"testMethod\",\n" +
                "\"lookUpCommands\": \"testLookUp\",\n" +
                "\"workingDirectory\": \"testDirectory\",\n" +
                "\"isIgnoringErrors\": \"true\",\n" +
                "\"runIfCondition\": \"PASSED\"\n" +
                "}";
        JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

        //Act
        ExecTask actualResult = (ExecTask) this.mockedDeserializer.deserialize(jsonElement,null,null);

        //Assert
        Assert.assertNotNull(actualResult);
        Assert.assertEquals(TaskType.EXEC, actualResult.getType());
        Assert.assertEquals("testMethod", actualResult.getCommand());
        Assert.assertEquals("testLookUp",actualResult.getLookUpCommands());
        Assert.assertEquals("testDirectory",actualResult.getWorkingDirectory());
        Assert.assertTrue(actualResult.isIgnoringErrors());
        Assert.assertEquals(RunIf.PASSED,actualResult.getRunIfCondition());
    }

    @Test
    public void deserializeFetchArtifactTask_validJSON_correctObject() {
        //Arrange
        String jsonAsString = "{\n" +
                "\"type\": \"FETCH_ARTIFACT\",\n" +
                "\"name\": \"fetchArtifact\",\n" +
                "\"pipeline\": \"testPipe\",\n" +
                "\"stage\": \"one\",\n" +
                "\"job\": \"first\",\n" +
                "\"source\": \"mySource\",\n" +
                "\"destination\": \"dist\"\n" +
                "}";
        JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

        //Act
        FetchArtifactTask actualResult = (FetchArtifactTask) this.mockedDeserializer.deserialize(jsonElement,null,null);

        //Assert
        Assert.assertNotNull(actualResult);
        Assert.assertEquals(TaskType.FETCH_ARTIFACT, actualResult.getType());
        Assert.assertEquals("fetchArtifact", actualResult.getName());
        Assert.assertEquals("testPipe",actualResult.getPipeline());
        Assert.assertEquals("one",actualResult.getStage());
        Assert.assertEquals("first",actualResult.getJob());
        Assert.assertEquals("mySource",actualResult.getSource());
    }

    @Test
    public void deserializeFetchMaterialTask_validJSON_correctObject() {
        //Arrange
        String jsonAsString = "{\n" +
                "\"type\": \"FETCH_MATERIAL\",\n" +
                "\"materialName\": \"fetchMaterial\",\n" +
                "\"pipelineName\": \"testPipe\",\n" +
                "\"materialType\": \"GIT\"\n" +
                "}";
        JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

        //Act
        FetchMaterialTask actualResult = (FetchMaterialTask) this.mockedDeserializer.deserialize(jsonElement,null,null);

        //Assert
        Assert.assertNotNull(actualResult);
        Assert.assertEquals(TaskType.FETCH_MATERIAL, actualResult.getType());
        Assert.assertEquals("fetchMaterial",actualResult.getMaterialName());
        Assert.assertEquals("testPipe",actualResult.getPipelineName());
        Assert.assertEquals(MaterialType.GIT,actualResult.getMaterialType());
    }

    @Test
    public void deserializeUploadArtifactTask_validJSON_correctObject() {
        //Arrange
        String jsonAsString = "{\n" +
                "\"type\": \"UPLOAD_ARTIFACT\",\n" +
                "\"source\": \"mySource\",\n" +
                "\"destination\": \"myDestination\"\n" +
                "}";
        JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

        //Act
        UploadArtifactTask actualResult = (UploadArtifactTask) this.mockedDeserializer.deserialize(jsonElement,null,null);

        //Assert
        Assert.assertNotNull(actualResult);
        Assert.assertEquals(TaskType.UPLOAD_ARTIFACT, actualResult.getType());
        Assert.assertEquals("mySource",actualResult.getSource());
        Assert.assertEquals("myDestination",actualResult.getDestination());
    }

    @Test
    public void deserialize_withInvalidTaskType_properExceptionAndMessage(){
        //Arrange
        String jsonAsString = "{\n" +
                "\"type\": \"nonExisting\"\n" +
                "}";
        JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

        //Act
        this.expectedException.expect(JsonParseException.class);
        this.expectedException.expectMessage("Invalid Task Definition type!");
        this.mockedDeserializer.deserialize(jsonElement,null,null);
    }

    @Test
    public void deserialize_nonExistingProperty_properExceptionAndMessage(){
        //Arrange
        String jsonAsString = "{\n" +
                "\"nonExisting\": \"nonExisting\"\n" +
                "}";
        JsonElement jsonElement = this.jsonConverter.fromJson(jsonAsString, JsonElement.class);

        //Act
        this.expectedException.expect(JsonParseException.class);
        this.expectedException.expectMessage("Field type is null!");
        this.mockedDeserializer.deserialize(jsonElement,null,null);
    }
}